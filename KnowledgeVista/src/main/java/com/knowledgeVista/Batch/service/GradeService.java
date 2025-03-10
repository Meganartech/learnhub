package com.knowledgeVista.Batch.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
import com.knowledgeVista.Attendance.AttendanceService;
import com.knowledgeVista.Course.Quizz.Repo.QuizzattemptRepo;
import com.knowledgeVista.Course.Quizz.Repo.quizzRepo;
import com.knowledgeVista.Course.Test.Repository.MusertestactivityRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.knowledgeVista.Batch.Batch;
import com.knowledgeVista.Batch.GradeDto;
import com.knowledgeVista.Batch.GradeDto.GradeDtoWithUserDetails;
import com.knowledgeVista.Batch.Weightage.Weightage;
import com.knowledgeVista.Batch.Weightage.service.weightageService;
@Service
public class GradeService {
	
	private final JwtUtil jwtUtil;
    private final MuserRepositories muserRepo;
    private final AttendanceService attendanceService;
    private final MusertestactivityRepo testActivityRepo;
    private final QuizzattemptRepo quizzattemptRepo;
    private final weightageService weightageService;
    @Autowired private quizzRepo quizRepo;
    private static final Logger logger = LoggerFactory.getLogger(GradeService.class);

    // Constructor-based dependency injection
    public GradeService(JwtUtil jwtUtil, 
                        MuserRepositories muserRepo, 
                        AttendanceService attendanceService, 
                        MusertestactivityRepo testActivityRepo, 
                        QuizzattemptRepo quizzattemptRepo,
                        weightageService weightageService) {
        this.jwtUtil = jwtUtil;
        this.muserRepo = muserRepo;
        this.attendanceService = attendanceService;
        this.testActivityRepo = testActivityRepo;
        this.quizzattemptRepo = quizzattemptRepo;
        this.weightageService=weightageService;
    }
private GradeDto getGradesbyBatchId(Muser user,Weightage weightage,Long batchId,String batchName) {
	 
      String email=user.getEmail();
      // Quiz Score Calculation========================================================
      List<Long> quizzIdList = muserRepo.findQuizzIdsByUserEmail(email,batchId);
      List<Long> scheduledQuizzIds = quizRepo.getQuizzIDSheduledByUser(email, quizzIdList);
      Double quizzPercentage = quizzattemptRepo.getTotalScoreForUser(user.getUserId(), scheduledQuizzIds);
      
      double totalQuizzPercentage = scheduledQuizzIds.size() * 100;
      double quizPercentage100 = (quizzPercentage != null && totalQuizzPercentage > 0) 
                                  ? (quizzPercentage / totalQuizzPercentage) * 100 : 0.0;
      double weightedQuiz = round((quizPercentage100 * weightage.getQuizzWeightage()) / 100);

      // Test Score Calculation==============================================================
      List<Long> totalTestIds = muserRepo.findTestIdsByUserEmail(email, batchId);
      int totalTest = totalTestIds.size();
      Double result = testActivityRepo.getTotalPercentageForUserandTestIDs(email, totalTestIds);
      
      double totalPercentage = (result != null && totalTest > 0) 
                                ? (result / (totalTest * 100)) * 100 : 0.0;
      double weightedTest = round((totalPercentage * weightage.getTestWeightage()) / 100);

      // Attendance Score Calculation======================================================
      Double attendancePercentage = attendanceService.calculateAttendance(user.getUserId(),batchId);
      attendancePercentage = (attendancePercentage != null) ? attendancePercentage : 0.0;
      double weightedAttendance = round((attendancePercentage * weightage.getAttendanceWeightage()) / 100);

      // Total Score Calculation===================================================================
      double weightedAssignment=0.0;
      double totalScore = round(weightedTest + weightedQuiz + weightedAttendance+weightedAssignment);

      String res=totalScore>=weightage.getPassPercentage()?"PASS":"FAIL";
      GradeDto gradeDto = new GradeDto(batchName,weightedTest, weightedQuiz, weightedAttendance, totalScore, weightedAssignment,res);
     return gradeDto;
}

private GradeDto getGradesbyBatchId(Muser user,Weightage weightage,Long batchId,String batchName,double totalPercentage) {
	 
    String email=user.getEmail();
    // Quiz Score Calculation========================================================
    List<Long> quizzIdList = muserRepo.findQuizzIdsByUserEmail(email,batchId);
    List<Long> scheduledQuizzIds = quizRepo.getQuizzIDSheduledByUser(email, quizzIdList);
    Double quizzPercentage = quizzattemptRepo.getTotalScoreForUser(user.getUserId(), scheduledQuizzIds);
    
    double totalQuizzPercentage = scheduledQuizzIds.size() * 100;
    double quizPercentage100 = (quizzPercentage != null && totalQuizzPercentage > 0) 
                                ? (quizzPercentage / totalQuizzPercentage) * 100 : 0.0;
    double weightedQuiz = round((quizPercentage100 * weightage.getQuizzWeightage()) / 100);

    // Test Score Calculation==============================================================
   
    double weightedTest = round((totalPercentage * weightage.getTestWeightage()) / 100);

    // Attendance Score Calculation======================================================
    Double attendancePercentage = attendanceService.calculateAttendance(user.getUserId(),batchId);
    attendancePercentage = (attendancePercentage != null) ? attendancePercentage : 0.0;
    double weightedAttendance = round((attendancePercentage * weightage.getAttendanceWeightage()) / 100);

    // Total Score Calculation===================================================================
    double weightedAssignment=0.0;
    double totalScore = round(weightedTest + weightedQuiz + weightedAttendance+weightedAssignment);

    String res=totalScore>=weightage.getPassPercentage()?"PASS":"FAIL";
    GradeDto gradeDto = new GradeDto(batchName,weightedTest, weightedQuiz, weightedAttendance, totalScore, weightedAssignment,res);
   return gradeDto;
}

public ResponseEntity<?> getTestAndGradeAnalysis(String token,String stuemail,Long batchId){
	try{
		List<Long> totalTestIds = muserRepo.findTestIdsByUserEmail(stuemail, batchId);
	      int totalTest = totalTestIds.size();
	      Double result = testActivityRepo.getTotalPercentageForUserandTestIDs(stuemail, totalTestIds);
	      
	      double totalPercentage = (result != null && totalTest > 0) 
	                                ? (result / (totalTest * 100)) * 100 : 0.0;
	      Optional<Muser> opuser = muserRepo.findByEmail(stuemail);
	        if (opuser.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
	        }

	        Muser user = opuser.get();
	        if (!"USER".equals(user.getRole().getRoleName())) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Students Can Access This Page");
	        }
	        Weightage weightage = weightageService.getWeightage(user.getInstitutionName());
	        
	        GradeDto grade=getGradesbyBatchId(user, weightage, batchId, "default NAME",totalPercentage);
	        Map<String, Object> res = new HashMap<>();
	        res.put("grade", grade.getTotalScore());
	        res.put("result", grade.getResult());
	        res.put("test", totalPercentage);
	       return ResponseEntity.ok(res);
	      
	}catch (Exception e) {
		// TODO: handle exception
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
public ResponseEntity<?> getGrades(String token) {
    try {
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        String email = jwtUtil.getUsernameFromToken(token);
        Optional<Muser> opuser = muserRepo.findByEmail(email);
        if (opuser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
        }

        Muser user = opuser.get();
        if (!"USER".equals(user.getRole().getRoleName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Students Can Access This Page");
        }
        Weightage weightage = weightageService.getWeightage(user.getInstitutionName());
       List<GradeDto>grades=new ArrayList<GradeDto>();
       List<Batch>batches=user.getEnrolledbatch();
       for(Batch batch: batches) {
    	   GradeDto grade=getGradesbyBatchId(user, weightage, batch.getId(), batch.getBatchTitle());
    	   grades.add(grade);
       }
       Map<String, Object> res = new HashMap<>();
       res.put("grades", grades);
       res.put("weight", weightage);
       return ResponseEntity.ok(res);

    } catch (NullPointerException e) {
        logger.error("NullPointerException while fetching grades", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some required data is missing");
    } catch (ArithmeticException e) {
        logger.error("ArithmeticException while calculating grades", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Math error occurred during calculation");
    } catch (Exception e) {
        logger.error("Error Fetching Grade Points", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
    }
}
public ResponseEntity<?> getGradesofStudent(String token, String email, Long batchId) {
    try {
        String role = jwtUtil.getRoleFromToken(token);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        Optional<Muser> opuser = muserRepo.findByEmail(email);
        if (opuser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
        }

        Muser user = opuser.get();
        if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
            Weightage weightage = weightageService.getWeightage(user.getInstitutionName());

            // Use stream to find the batch
            Optional<Batch> matchingBatch = user.getEnrolledbatch()
                    .stream()
                    .filter(batch -> batch.getId().equals(batchId))
                    .findFirst();

            if (matchingBatch.isPresent()) {
                GradeDto grade = getGradesbyBatchId(user, weightage, matchingBatch.get().getId(), matchingBatch.get().getBatchTitle());
                GradeDtoWithUserDetails finalgrade=new GradeDtoWithUserDetails();
                finalgrade.setGradeDto(grade);
                finalgrade.setProfile(user.getProfile());
                finalgrade.setEmail(user.getEmail());
                finalgrade.setUserName(user.getUsername());
                Map<String, Object> res = new HashMap<>();
                res.put("grades", finalgrade);
                res.put("weight", weightage);
                return ResponseEntity.ok(res);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Batch not found");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Students cannot access this page");

    } catch (NullPointerException e) {
        logger.error("NullPointerException while fetching grades", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some required data is missing");
    } catch (ArithmeticException e) {
        logger.error("ArithmeticException while calculating grades", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Math error occurred during calculation");
    } catch (Exception e) {
        logger.error("Error Fetching Grade Points", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
    }
}
// Helper method to round to 2 decimal places
private double round(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
}


}
