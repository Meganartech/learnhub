package com.knowledgeVista.Batch.Assignment.Service;

import java.time.LocalDate;
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
import com.knowledgeVista.Batch.Assignment.AssignmentQuestion;
import com.knowledgeVista.Batch.Assignment.AssignmentSchedule;
import com.knowledgeVista.Batch.Assignment.Repo.AssignmentQuesstionRepo;
import com.knowledgeVista.Batch.Assignment.Repo.AssignmentRepo;
import com.knowledgeVista.Batch.Assignment.Repo.AssignmentSheduleRepo;
import com.knowledgeVista.Batch.Repo.BatchRepository;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@Service
public class AssignmentService {
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
	private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);

	public ResponseEntity<?> saveAssignment(String token, Assignment assignment, Long courseId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser addingUser = optionalUser.get();
			String role = addingUser.getRole().getRoleName();
			if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Save Assignment");
			}
			Optional<CourseDetail> optionalcourse = courseDetailRepo.findById(courseId);
			if (optionalcourse.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Course Not Found");
			}
			CourseDetail Course = optionalcourse.get();
			if ("ADMIN".equals(role)) {
				return saveAssignmentService(assignment, Course);
			} else if ("TRAINER".equals(role)) {
				if (Course.getTrainer().contains(addingUser)) {
					return saveAssignmentService(assignment, Course);
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This Course Was Not Assigned To You");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
			}
		} catch (Exception e) {
			logger.error("Error Saving Assignment", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error Occurred in Saving Assignment: " + e.getMessage());
		}
	}

	private ResponseEntity<?> saveAssignmentService(Assignment assignment, CourseDetail course) {
		try {
			assignment.setCourseDetail(course);
			if (assignment.getQuestions() != null) {
				assignment.getQuestions().forEach(question -> question.setAssignment(assignment));
			}
			assignmentRepo.save(assignment);
			return ResponseEntity.ok("Assignment Saved Successfully");
		} catch (Exception e) {
			logger.error("Error at SaveAssignment Service", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error at Saving Assignment: " + e.getMessage());
		}
	}

	public ResponseEntity<?> GetAllAssignmentByCourse(String token, Long courseId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser addingUser = optionalUser.get();
			String role = addingUser.getRole().getRoleName();
			if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Save Assignment");
			}
			String institutionName = addingUser.getInstitutionName();
			List<Map<String, Object>> assignments = assignmentRepo
					.findAssignmentDetailsWithoutCourseAndQuestions(courseId, institutionName);

			return ResponseEntity.ok(assignments);
		} catch (Exception e) {
			logger.error("Error Occured Getting Assignment By courseId" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	public ResponseEntity<?> DeleteAssignment(String token, Long AssignmentId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser addingUser = optionalUser.get();
			String role = addingUser.getRole().getRoleName();
			if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Save Assignment");
			}
			Optional<Assignment> opassignment = assignmentRepo.findById(AssignmentId);
			if (opassignment.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Assignment Not Found");
			}
			Assignment assignment = opassignment.get();
			CourseDetail Course = assignment.getCourseDetail();
			if ("ADMIN".equals(role)) {
				assignmentRepo.deleteById(AssignmentId);
				return ResponseEntity.ok("Assignment Deleted");
			} else if ("TRAINER".equals(role)) {
				if (Course.getTrainer().contains(addingUser)) {
					assignmentRepo.deleteById(AssignmentId);
					return ResponseEntity.ok("Assignment Deleted");
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This Course Was Not Assigned To You");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
			}
		} catch (Exception e) {
			logger.error("Error Saving Assignment", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error Occurred in Deleting Assignment: ");
		}
	}

	public ResponseEntity<?> GetAssignmentByAssignmentId(String token, Long AssignmentId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser addingUser = optionalUser.get();
			String role = addingUser.getRole().getRoleName();
			if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Edit Assignment");
			}
			Optional<Assignment> opassignment = assignmentRepo.findById(AssignmentId);
			if (opassignment.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Assignment Not Found");
			}
			Assignment assignment = opassignment.get();
			CourseDetail Course = assignment.getCourseDetail();
			if ("ADMIN".equals(role)) {
				assignment.setCourseDetail(null);
				assignment.setSchedules(null);
				if (assignment.getQuestions() != null) {
					assignment.getQuestions().forEach(q -> q.setAssignment(null));
				}
				return ResponseEntity.ok(assignment);
			} else if ("TRAINER".equals(role)) {
				if (Course.getTrainer().contains(addingUser)) {
					assignment.setCourseDetail(null);
					assignment.setSchedules(null);
					if (assignment.getQuestions() != null) {
						assignment.getQuestions().forEach(q -> q.setAssignment(null));
					}
					return ResponseEntity.ok(assignment);
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This Course Was Not Assigned To You");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
			}
		} catch (Exception e) {
			logger.error("Error Saving Assignment", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error Occurred in Saving Assignment: " + e.getMessage());
		}
	}

	public ResponseEntity<?> updateAssignment(String token, Assignment updated, Long AssignmentId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser addingUser = optionalUser.get();
			String role = addingUser.getRole().getRoleName();
			if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Save Assignment");
			}
			Optional<Assignment> opassignment = assignmentRepo.findById(AssignmentId);
			if (opassignment.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Assignment Not Found");
			}
			Assignment assignment = opassignment.get();
			CourseDetail Course = assignment.getCourseDetail();
			if ("ADMIN".equals(role)) {
				if (updated.getDescription() != null) {
					assignment.setDescription(updated.getDescription());
				}
				if (updated.getTitle() != null) {
					assignment.setTitle(updated.getTitle());
				}
				if (updated.getTotalMarks() != null) {
					assignment.setTotalMarks(updated.getTotalMarks());
				}
				assignmentRepo.save(assignment);
				return ResponseEntity.ok("Updated");
			} else if ("TRAINER".equals(role)) {
				if (Course.getTrainer().contains(addingUser)) {
					if (updated.getDescription() != null) {
						assignment.setDescription(updated.getDescription());
					}
					if (updated.getTitle() != null) {
						assignment.setTitle(updated.getTitle());
					}
					if (updated.getTotalMarks() != null) {
						assignment.setTotalMarks(updated.getTotalMarks());
					}
					assignmentRepo.save(assignment);
					return ResponseEntity.ok("Updated");
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This Course Was Not Assigned To You");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
			}
		} catch (Exception e) {
			logger.error("Error Saving Assignment", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error Occurred in Saving Assignment: " + e.getMessage());
		}
	}

	public ResponseEntity<?> DeleteAssignmentQuestionById(String token, Long questionId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser addingUser = optionalUser.get();
			String role = addingUser.getRole().getRoleName();
			if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Save Assignment");
			}
			Optional<AssignmentQuestion> opassignmentQuestion = QuestionRepo.findById(questionId);
			if (opassignmentQuestion.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Assignment Not Found");
			}
			AssignmentQuestion assignmentQuestion = opassignmentQuestion.get();
			CourseDetail Course = assignmentQuestion.getAssignment().getCourseDetail();
			if ("ADMIN".equals(role)) {
				QuestionRepo.deleteById(questionId);
				return ResponseEntity.ok("Deleted");
			} else if ("TRAINER".equals(role)) {
				if (Course.getTrainer().contains(addingUser)) {
					QuestionRepo.deleteById(questionId);
					return ResponseEntity.ok("Deleted");
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This Course Was Not Assigned To You");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
			}
		} catch (Exception e) {
			logger.error("Error Deleting Assignment", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error Occurred in Saving Assignment: " + e.getMessage());
		}
	}

	public ResponseEntity<?> updateAssignmentQuestion(String token, List<AssignmentQuestion> updated,
			Long AssignmentId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser addingUser = optionalUser.get();
			String role = addingUser.getRole().getRoleName();
			if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Save Assignment");
			}
			Optional<Assignment> opassignment = assignmentRepo.findById(AssignmentId);
			if (opassignment.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Assignment Not Found");
			}
			Assignment assignment = opassignment.get();
			CourseDetail Course = assignment.getCourseDetail();
			if ("ADMIN".equals(role)) {
				assignment.getQuestions().clear(); // this triggers orphan removal
				updated.forEach(question -> question.setAssignment(assignment));
				assignment.getQuestions().addAll(updated); // set the new questions
				assignmentRepo.save(assignment);
				return ResponseEntity.ok("Updated");
			} else if ("TRAINER".equals(role)) {
				if (Course.getTrainer().contains(addingUser)) {
					assignment.getQuestions().clear(); // this triggers orphan removal
					updated.forEach(question -> question.setAssignment(assignment));
					assignment.getQuestions().addAll(updated); // set the new questions
					assignmentRepo.save(assignment);

					return ResponseEntity.ok("Updated");
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This Course Was Not Assigned To You");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
			}
		} catch (Exception e) {
			logger.error("Error Saving Assignment", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error Occurred in Saving Assignment: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getAssignmentSheduleDetails(Long courseId, Long batchId, String token) {
		try {
			String role = jwtUtil.getRoleFromToken(token);
			String email = jwtUtil.getUsernameFromToken(token);
			boolean isalloted = false;

			if ("ADMIN".equals(role)) {
				isalloted = true;
			} else if ("TRAINER".equals(role)) {
				isalloted = muserRepo.FindAllotedOrNotByUserIdAndBatchId(email, batchId);
			}
			if (isalloted) {
				List<Map<String, Object>> shedule = assignmentRepo.getAssignmentSchedulesByCourseIdAndBatchId(courseId,
						batchId);
				return ResponseEntity.ok(shedule);
			}
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
		} catch (Exception e) {
			logger.error("error at GetSheduleQuizz" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	public ResponseEntity<?> SaveORUpdateSheduleAssignment(Long AssignmentId, Long batchId, LocalDate AssignmentDate,
			String token) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
			}
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> optionalUser = muserRepo.findByEmail(email);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
			}
			Muser addingUser = optionalUser.get();
			String role = addingUser.getRole().getRoleName();
			if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Save Assignment");
			}
			Optional<Batch> opbatch = batchRepo.findById(batchId);
			if (opbatch.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Batch Not Found");
			}
			Batch batch = opbatch.get();
			Optional<Assignment> opassignment = assignmentRepo.findById(AssignmentId);
			if (opassignment.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Assignment Not Found");
			}
			Assignment assignment = opassignment.get();
			CourseDetail Course = assignment.getCourseDetail();
			if ("ADMIN".equals(role)) {
				Optional<AssignmentSchedule> opshedule = sheduleRepo.findByAssignmentIdAndBatchId(batchId,
						AssignmentId);
				if (opshedule.isPresent()) {
					AssignmentSchedule shedule = opshedule.get();
					shedule.setAssignmentDate(AssignmentDate);
					sheduleRepo.save(shedule);
					return ResponseEntity.ok("Updated");
				} else {
					AssignmentSchedule shedule = new AssignmentSchedule();
					shedule.setAssignment(assignment);
					shedule.setBatch(batch);
					shedule.setAssignmentDate(AssignmentDate);
					sheduleRepo.save(shedule);
					return ResponseEntity.ok("Saved");
				}

			} else if ("TRAINER".equals(role)) {
				if (Course.getTrainer().contains(addingUser)) {
					Optional<AssignmentSchedule> opshedule = sheduleRepo.findByAssignmentIdAndBatchId(batchId,
							AssignmentId);
					if (opshedule.isPresent()) {
						AssignmentSchedule shedule = opshedule.get();
						shedule.setAssignmentDate(AssignmentDate);
						sheduleRepo.save(shedule);
						return ResponseEntity.ok("Updated");
					} else {
						AssignmentSchedule shedule = new AssignmentSchedule();
						shedule.setAssignment(assignment);
						shedule.setBatch(batch);
						shedule.setAssignmentDate(AssignmentDate);
						sheduleRepo.save(shedule);
						return ResponseEntity.ok("Saved");
					}
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This Course Was Not Assigned To You");
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
			}
		} catch (Exception e) {
			logger.error("error at GetSheduleQuizz" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

}
