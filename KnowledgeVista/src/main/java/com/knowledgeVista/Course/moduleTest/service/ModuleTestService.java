package com.knowledgeVista.Course.moduleTest.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.VideoLessonDTO;
import com.knowledgeVista.Course.videoLessons;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Course.Repository.videoLessonRepo;
import com.knowledgeVista.Course.Test.Question;
import com.knowledgeVista.Course.moduleTest.MQuestion;
import com.knowledgeVista.Course.moduleTest.ModuleTest;
import com.knowledgeVista.Course.moduleTest.repo.MQuestionRepo;
import com.knowledgeVista.Course.moduleTest.repo.ModuleTestActivityRepo;
import com.knowledgeVista.Course.moduleTest.repo.ModuleTestAnswerRepo;
import com.knowledgeVista.Course.moduleTest.repo.moduleTestRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@Service
public class ModuleTestService {
	@Autowired
	private MuserRepositories muserRepository;
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private videoLessonRepo lessonRepo;
	@Autowired
	private moduleTestRepo moduletestRepo;
	@Autowired
	private MQuestionRepo MquestionRepo;
	@Autowired
	private ModuleTestActivityRepo moduletestActivityrepo;
	@Autowired
	private ModuleTestAnswerRepo moduleTestAnswerRepo;

	private static final Logger logger = LoggerFactory.getLogger(ModuleTestService.class);

	public ResponseEntity<?> searchLessons(String token, String Query, Long courseId) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			String role = jwtUtil.getRoleFromToken(token);
			String email = jwtUtil.getUsernameFromToken(token);
			String institution = muserRepository.findinstitutionByEmail(email);
			if (institution == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Institution Not Found");
			}
			if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
				List<VideoLessonDTO> lessons = lessonRepo.searchByTitle(Query, institution, courseId);
				return ResponseEntity.ok(lessons);
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User cannot Access This Page");
		} catch (Exception e) {
			logger.error("error at searchLesson" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	public ResponseEntity<?> SaveModuleTest(String token, List<Long> lessonIds, ModuleTest moduleTest, Long courseId) {
		try {
			// Extract user details from token
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
			}

			String role = jwtUtil.getRoleFromToken(token);
			String email = jwtUtil.getUsernameFromToken(token);
			String institution = muserRepository.findinstitutionByEmail(email);

			if (institution == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Institution Not Found");
			}

			// Check if user has permission
			if (!"ADMIN".equals(role) && !"TRAINER".equals(role)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User cannot access this page");
			}

			// Fetch lessons and course details
			List<videoLessons> lessons = lessonRepo.findByLessonIdsAndInstitutionName(lessonIds, institution);

			Optional<CourseDetail> courseOpt = coursedetailrepository.findByCourseIdAndInstitutionName(courseId,
					institution);
			if (courseOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Course not found");
			}
			CourseDetail course = courseOpt.get();
			// Associate module test with course and lessons
			moduleTest.setCourseDetail(course);
			moduleTest.setLessons(lessons);
			moduleTest.getQuestions().forEach(q -> q.setMtest(moduleTest));
			// Save module test and questions in a single transaction
			System.out.println("size");
			moduleTest.setMnoOfQuestions(Long.valueOf(moduleTest.getQuestions().size()));
			ModuleTest savedModuleTest = moduletestRepo.save(moduleTest);
			System.out.println(savedModuleTest.getMtestName());
			moduleTest.getQuestions().forEach(q -> q.setMtest(savedModuleTest));
			MquestionRepo.saveAll(moduleTest.getQuestions());

			return ResponseEntity.ok("Module test saved successfully");

		} catch (Exception e) {
			logger.error("Error in SaveModuleTest: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	public ResponseEntity<?> getModuleTestById(Long mtestId, String token) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
			}

			String role = jwtUtil.getRoleFromToken(token);
			if (!"ADMIN".equals(role) && !"TRAINER".equals(role)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User cannot access this page");
			}
			Optional<ModuleTest> optest = moduletestRepo.findModuleTestWithoutRelations(mtestId);
			if (optest.isPresent()) {
				ModuleTest test = optest.get();
				List<MQuestion> questions = MquestionRepo.findByModuleTestId(test.getMtestId());
				test.setQuestions(questions);
				return ResponseEntity.ok(test);
			} else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body("Module Test with Given Id " + mtestId + " is not  Found");
			}

		} catch (Exception e) {
			logger.error("Error At get Module test by Id" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	public ResponseEntity<?> getModuleTestListByCourseId(Long courseId, String token) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
			}

			String role = jwtUtil.getRoleFromToken(token);
			if (!"ADMIN".equals(role) && !"TRAINER".equals(role)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User cannot access this page");
			}
			List<ModuleTest> test = moduletestRepo.findModuleTestListWithoutRelations(courseId);

			return ResponseEntity.ok(test);

		} catch (Exception e) {
			logger.error("Error At get Module test by courseId" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	public ResponseEntity<?> addMoreModuleQuestion(Long mtestId, String questionText, String option1, String option2,
			String option3, String option4, String answer, String token) {
		try {
// Validate JWT token
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
			}

// Check user role
			String role = jwtUtil.getRoleFromToken(token);
			if (!"ADMIN".equals(role) && !"TRAINER".equals(role)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
			}

// Fetch ModuleTest by ID
			return moduletestRepo.findById(mtestId).map(test -> {
				test.setMnoOfQuestions(test.getMnoOfQuestions() + 1);

// Create and save new question
				MQuestion ques = new MQuestion(questionText, option1, option2, option3, option4, answer, test);
				MquestionRepo.save(ques);

				return ResponseEntity.ok("Question added successfully");
			}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Test not found"));

		} catch (Exception e) {
			logger.error("Error adding question: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
		}
	}

	public ResponseEntity<?> deleteModuleQuestion(List<Long> questionIds, String token, Long testId) {
		try {
			// Validate JWT token
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			String role = jwtUtil.getRoleFromToken(token);
			int deletedQuestionsCount = 0;

			if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
				for (Long id : questionIds) {
					if (MquestionRepo.existsById(id)) { // Check if the question exists before deleting
						MquestionRepo.deleteById(id);
						deletedQuestionsCount++;
					}
				}

				Optional<ModuleTest> optTest = moduletestRepo.findById(testId);
				if (optTest.isPresent()) {
					ModuleTest test = optTest.get();
					Long noOfQuestions = test.getMnoOfQuestions();

					noOfQuestions -= deletedQuestionsCount;

					if (noOfQuestions <= 0) {
						moduletestRepo.deleteById(testId); // Delete test if no questions are left
					} else {
						test.setMnoOfQuestions(noOfQuestions);
						moduletestRepo.save(test); // Update test question count
					}
				}

				return ResponseEntity.ok("Questions deleted successfully");
			}

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
		} catch (Exception e) {
			logger.error("Error deleting questions", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	public ResponseEntity<?> editModuleTest(Long testId, String testName, Long noOfAttempt, Double passPercentage,
			String token) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
			}
			String role = jwtUtil.getRoleFromToken(token);
			if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
				Optional<ModuleTest> optest = moduletestRepo.findById(testId);
				System.out.println("test" + testName);
				System.out.println("pass" + passPercentage);
				System.out.println("noofat" + noOfAttempt);
				if (optest.isPresent()) {
					ModuleTest test = optest.get();
					if (testName != null) {
						test.setMtestName(testName);
					}
					if (noOfAttempt != null) {
						test.setMnoOfAttempt(noOfAttempt);
					}
					if (passPercentage != null) {
						test.setMpassPercentage(passPercentage);
					}
					moduletestRepo.saveAndFlush(test);
					return ResponseEntity.ok().body("Module Test updated successfully");

				} else {
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body("test Not Found");
				}
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			;
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating Module test: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getModuleQuestion(Long questionId, String token) {
		try {
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			String role = jwtUtil.getRoleFromToken(token);
			if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
				MQuestion existingQuestion = MquestionRepo.findById(questionId).orElse(null);

				existingQuestion.setMtest(null);
				return ResponseEntity.ok(existingQuestion);
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Users Canot Access This Page");

		} catch (Exception e) {
			// Handle any unexpected exceptions here
			// You can log the error or return an appropriate response
			e.printStackTrace();
			logger.error("", e);
			;
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}

	}

	public ResponseEntity<?> updateModuleQuestion(Long questionId, String questionText, String option1, String option2,
			String option3, String option4, String answer, String token) {
		try {
// Validate JWT token
			if (!jwtUtil.validateToken(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			String role = jwtUtil.getRoleFromToken(token);
			if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
				Optional<MQuestion> opexistingQuestion = MquestionRepo.findById(questionId);
				if (opexistingQuestion.isPresent()) {
					MQuestion existingQuestion = opexistingQuestion.get();
					existingQuestion.setQuestionText(questionText);
					existingQuestion.setOption1(option1);
					existingQuestion.setOption2(option2);
					existingQuestion.setOption3(option3);
					existingQuestion.setOption4(option4);
					existingQuestion.setAnswer(answer);
					MquestionRepo.save(existingQuestion);
					return ResponseEntity.ok().body("Question updated successfully");
				}
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Question Not Found");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (Exception e) {

			e.printStackTrace();
			logger.error("", e);		
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message" + e.getMessage());
		}
	}

}
