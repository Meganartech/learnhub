package com.knowledgeVista.Batch.Assignment.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.knowledgeVista.Batch.Batch;
import com.knowledgeVista.Batch.Assignment.Assignment;
import com.knowledgeVista.Batch.Assignment.Submission;
import com.knowledgeVista.Batch.Assignment.Repo.AssignmentQuesstionRepo;
import com.knowledgeVista.Batch.Assignment.Repo.AssignmentRepo;
import com.knowledgeVista.Batch.Assignment.Repo.AssignmentSheduleRepo;
import com.knowledgeVista.Batch.Assignment.Repo.SubmissionRepo;
import com.knowledgeVista.Batch.Repo.BatchRepository;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@Service
public class AssignmentService2 {
	@Autowired
	private AssignmentRepo assignmentRepo;
	@Autowired
	private AssignmentQuesstionRepo QuestionRepo;
	@Autowired
	private CourseDetailRepository courseDetailRepo;
	@Autowired
	private MuserRepositories muserRepo;
	@Autowired
	private BatchRepository batchRepo;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private AssignmentSheduleRepo sheduleRepo;
	@Autowired
	private SubmissionRepo submissionRepo;
	private static final Logger logger = LoggerFactory.getLogger(AssignmentService2.class);

	public ResponseEntity<?> getAssignmentsBybatchId(String token, Long batchId) {
		try {
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser user = optionalUser.get();
			String role = user.getRole().getRoleName();
			if ("USER".equals(role)) {
				Optional<Batch> opbatch = batchRepo.findById(batchId);
				if (opbatch.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Batch Not Found");
				}
				Batch batch = opbatch.get();
				if (user.getEnrolledbatch().contains(batch)) {
					List<Map<String, Object>> assignments = assignmentRepo.getAssignmentSchedulesByBatchId(batchId);
					return ResponseEntity.ok(assignments);
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("Access denied: You are not enrolled in this batch.");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Access denied: Only Users Can Access This Page");
			}
		} catch (Exception e) {
			logger.error("Error occured in Getting Assignment" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			// TODO: handle exception
		}
	}

	public ResponseEntity<?> GetAssignmentByAssignmentIdForSubmission(String token, Long assignmentId, Long batchId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}

			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}

			Muser user = optionalUser.get();
			if (!"USER".equals(user.getRole().getRoleName())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You Cannot Access This Page");
			}

			Optional<Assignment> opassignment = assignmentRepo.findById(assignmentId);
			if (opassignment.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Assignment Not Found");
			}

			Assignment assignment = opassignment.get();
			CourseDetail course = assignment.getCourseDetail();

			if (!course.getUsers().contains(user)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Not Enrolled in the Course");
			}

			// Clean the assignment object
			assignment.setCourseDetail(null);
			assignment.setSchedules(null);
			if (assignment.getQuestions() != null) {
				assignment.getQuestions().forEach(q -> q.setAssignment(null));
			}

			// Fetch existing submission if present
			Optional<Submission> existingSubmission = submissionRepo
					.findByBatchIdAndAssignmentIdAndUserIdAndIsGradedFalse(assignmentId, user.getUserId(), batchId);

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("assignment", assignment);
			if (existingSubmission.isPresent()) {
				Submission submission = existingSubmission.get();
				submission.setBatch(null);
				submission.setAssignment(null);
				submission.setUser(null);
				responseMap.put("existingSubmission", submission);
			} else {
				responseMap.put("existingSubmission", null);
			}
			return ResponseEntity.ok(responseMap);

		} catch (Exception e) {
			logger.error("Error Fetching Assignment", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error Occurred while Fetching Assignment: " + e.getMessage());
		}
	}

	public ResponseEntity<?> SubmitAssignment(String token, Long assignmentId, Long scheduleId, Long batchId,
			Map<Long, String> answers) {
		try {
// Step 1: Validate Token
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}

// Step 2: Get user from token
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}

			Muser user = optionalUser.get();

// Step 3: Check if the user has role USER
			if (!"USER".equals(user.getRole().getRoleName())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Only users with role USER can submit assignments");
			}

// Step 4: Fetch Assignment and Batch
			Optional<Assignment> optionalAssignment = assignmentRepo.findById(assignmentId);
			Optional<Batch> optionalBatch = batchRepo.findById(batchId);

			if (optionalAssignment.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Invalid assignment ID");
			}

			if (optionalBatch.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Batch Not Found");
			}

			Assignment assignment = optionalAssignment.get();
			Batch batch = optionalBatch.get();

// Step 5: Check if user is enrolled in the batch
			if (!user.getEnrolledbatch().contains(batch)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Access Denied: You are not enrolled in this batch");
			}

// Step 6: Check for existing submission
			Optional<Submission> existingSubmission = submissionRepo
					.findByBatchIdAndAssignmentIdAndUserIdAndIsGradedFalse(assignmentId, user.getUserId(), batchId);

			if (existingSubmission.isPresent()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Assignment is already graded or submitted. You cannot submit it again.");
			}

// Step 7: Determine Submission Status
			LocalDate currentDate = LocalDate.now();
			LocalDate scheduleDate = sheduleRepo.findSheduleDateByAssignmentIDAndbatchID(batchId, assignmentId);

			if (scheduleDate == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Schedule date not found");
			}

			Submission.SubmissionStatus status;
			if (currentDate.isEqual(scheduleDate)) {
				status = Submission.SubmissionStatus.SUBMITTED;
			} else if (currentDate.isAfter(scheduleDate)) {
				status = Submission.SubmissionStatus.LATE_SUBMISSION;
			} else {
				status = Submission.SubmissionStatus.SUBMITTED; // Early submissions are still valid
			}

// Step 8: Create and Save Submission
			Submission submission = new Submission();
			submission.setAssignment(assignment);
			submission.setBatch(batch);
			submission.setUser(user);
			submission.setAnswers(answers);
			submission.setSubmittedAt(LocalDateTime.now());
			submission.setSubmissionStatus(status);
			submission.setGraded(false);

			submissionRepo.save(submission);

			return ResponseEntity.ok("Assignment Submitted Successfully");

		} catch (Exception e) {
			logger.error("Error Submitting Assignment: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while submitting the assignment");
		}
	}
}
