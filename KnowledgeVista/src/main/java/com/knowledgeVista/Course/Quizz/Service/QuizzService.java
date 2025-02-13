package com.knowledgeVista.Course.Quizz.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.knowledgeVista.Course.videoLessons;
import com.knowledgeVista.Course.Quizz.Quizz;
import com.knowledgeVista.Course.Quizz.Quizzquestion;
import com.knowledgeVista.Course.Quizz.Repo.QuizzQuestionRepo;
import com.knowledgeVista.Course.Quizz.Repo.quizzRepo;
import com.knowledgeVista.Course.Repository.videoLessonRepo;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@Service
public class QuizzService {
    
	 @Autowired
	 private JwtUtil jwtUtil;

		@Autowired
		private MuserRepositories muserRepository;
		@Autowired
		private QuizzQuestionRepo quizQuestionRepo;
		@Autowired
		private quizzRepo quizzRepo;
		@Autowired videoLessonRepo lessonsRepo;
	  	 private static final Logger logger = LoggerFactory.getLogger(QuizzService.class);
	
  public ResponseEntity<?>SaveQuizz(Long lessonId,Quizz quizzData,String token){
	  try {
		  if (!jwtUtil.validateToken(token)) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
          }
		  String role=jwtUtil.getRoleFromToken(token);
		  String email=jwtUtil.getUsernameFromToken(token);
		  if("ADMIN".equals(role)||"TRAINER".equals(role)) {
			
		  String institution=muserRepository.findinstitutionByEmail(email);
		  if(institution==null) {
			  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("institution Not Found");
		  }
		  Optional<videoLessons> less=lessonsRepo.findById(lessonId);
		  if(less.isPresent()) {
			  videoLessons lesson=less.get();
		  quizzData.setInstituionName(institution);
		  quizzData.setLessons(lesson);
		 Quizz Saved=quizzRepo.save(quizzData);
		List<Quizzquestion>Questions=quizzData.getQuizzquestions();
		for(Quizzquestion ques: Questions) {
			ques.setQuizz(Saved);
			quizQuestionRepo.save(ques);
		}
		Map<String,Object> res=new HashMap<String,Object>();
		res.put("quizzId", Saved.getQuizzId());
		res.put("quizname", Saved.getQuizzName());
		return ResponseEntity.ok(res);
		  }
		  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Lesson Not Found");
		  
		 
		  }else {
			  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are Not Authorized to Access This Page");
		  }
		  
	  }catch (Exception e) {
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
  }
  public ResponseEntity<?>AddMoreQuestionInQuizz(Long quizzId,Quizzquestion quizzquestion,String token){
	  try {
		  if (!jwtUtil.validateToken(token)) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
          }
		  String role=jwtUtil.getRoleFromToken(token);
		  String email=jwtUtil.getUsernameFromToken(token);
		  if("ADMIN".equals(role)||"TRAINER".equals(role)) {
			
		  String institution=muserRepository.findinstitutionByEmail(email);
		  if(institution==null) {
			  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("institution Not Found");
		  }
		  Optional<Quizz> quizz=quizzRepo.findById(quizzId);
		  if(quizz.isPresent()) {
			  Quizz savedquizz=quizz.get();
			quizzquestion.setQuizz(savedquizz);
			quizQuestionRepo.save(quizzquestion);
			return ResponseEntity.ok("Saved SuccessFully");
		}
		  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Quizz Not Found");
		  
	  }else {
			  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are Not Authorized to Access This Page");
		  }
		  
	  }catch (Exception e) {
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
  }

  public ResponseEntity<?>GetQuizz(Long quizzId,String token){
	  try {
		  if (!jwtUtil.validateToken(token)) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
          }
		  String role=jwtUtil.getRoleFromToken(token);
		  String email=jwtUtil.getUsernameFromToken(token);
			  Optional<Quizz> opquizz=quizzRepo.findById(quizzId);
			  if(opquizz.isPresent()) {
				  Quizz quizz=opquizz.get();
				  if("ADMIN".equals(role)) {
					  List<Quizzquestion> questions= quizQuestionRepo.findByQuizzId(quizzId);
					  return ResponseEntity.ok(questions);
				  }else {
					  Long courseID=quizz.getLessons().getCourseDetail().getCourseId();
					  boolean isalloted=muserRepository.FindAllotedOrNotByUserIdAndCourseId(email, courseID);
					  if(isalloted) {
						  List<Quizzquestion> questions= quizQuestionRepo.findByQuizzId(quizzId);
						  return ResponseEntity.ok(questions);
					  }else {
						  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You Cannot Access This Page");
					  }
				  }
			  }else {
				  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Quizz Found ");
			  }
		  
	  }catch (Exception e) {
		  logger.error("error at Getting Quizz"+e);
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
  }

  public ResponseEntity<?>GetQuizzQuestion(Long questionId,String token){
	  try {
		  if (!jwtUtil.validateToken(token)) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
          }
		  String role=jwtUtil.getRoleFromToken(token);
		  String email=jwtUtil.getUsernameFromToken(token);
			  Optional<Quizzquestion> opquest=quizQuestionRepo.findById(questionId);
			  if(opquest.isPresent()) {
				  Quizzquestion quest=opquest.get();
				  if("ADMIN".equals(role)) {
					  quest.setQuizz(null);
					  return ResponseEntity.ok(quest);
				  }else {
					  Long courseID=quest.getQuizz().getLessons().getCourseDetail().getCourseId();
					  boolean isalloted=muserRepository.FindAllotedOrNotByUserIdAndCourseId(email, courseID);
					  if(isalloted) {
						  quest.setQuizz(null);
						  return ResponseEntity.ok(quest);
					  }else {
						  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You Cannot Access This Page");
					  }
				  }
			  }else {
				  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Quizz Found ");
			  }
		  
	  }catch (Exception e) {
		  logger.error("error at Getting Quizz"+e);
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
  }
  public ResponseEntity<?>DeleteQuizzQuestion(List<Long>questionIds,Long quizzId ,String token){
	  try {
		  if (!jwtUtil.validateToken(token)) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
          }
		  String role=jwtUtil.getRoleFromToken(token);
		  String email=jwtUtil.getUsernameFromToken(token);
		  boolean isalloted=false;
			  Optional<Quizz> opquest=quizzRepo.findById(quizzId);
			  if(opquest.isPresent()) {
				  Quizz quizz=opquest.get();
				  if("ADMIN".equals(role)) {
					  isalloted=true;
				  }else if("TRAINER".equals(role)){
					  Long courseID=quizz.getLessons().getCourseDetail().getCourseId();
					   isalloted=muserRepository.FindAllotedOrNotByUserIdAndCourseId(email, courseID);
				  }
				  if(isalloted) {
					  quizQuestionRepo.deleteByQuestionIdsAndQuizzId(questionIds, quizzId);
					  Long remainingQuestions = quizQuestionRepo.countByQuizzId(quizzId);
					  System.out.println(remainingQuestions);
		                if (remainingQuestions == 0) {
		                	System.out.println("in delete");
		                	   quizzRepo.deleteQuizzById(quizzId);
		                       quizzRepo.flush();
		                }
					  return ResponseEntity.ok("Delted Successfully");
				  }
				  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("you Are Not allowed to access This Page");
			  }else {
				  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Quizz Found ");
			  }
		  
	  }catch (Exception e) {
		  logger.error("error at DELETING Quizz"+e);
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
  }


  public ResponseEntity<?>UpdateQuizzQuestion(Long questionId,Quizzquestion quizzquestion,String token){
	  try {
		  if (!jwtUtil.validateToken(token)) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
          }
		  String role=jwtUtil.getRoleFromToken(token);
		  String email=jwtUtil.getUsernameFromToken(token);
		  boolean isalloted=false;
			  Optional<Quizzquestion> opquest=quizQuestionRepo.findById(questionId);
			  if(opquest.isPresent()) {
				  Quizzquestion quest=opquest.get();
				  if("ADMIN".equals(role)) {
					  isalloted=true;
				  }else if("TRAINER".equals(role)){
					  Long courseID=quest.getQuizz().getLessons().getCourseDetail().getCourseId();
					   isalloted=muserRepository.FindAllotedOrNotByUserIdAndCourseId(email, courseID);
				  }
				  if(isalloted) {
					  quest.setAnswer(quizzquestion.getAnswer());
					  quest.setOption1(quizzquestion.getOption1());
					  quest.setOption2(quizzquestion.getOption2());
					  quest.setOption3(quizzquestion.getOption3());
					  quest.setOption4(quizzquestion.getOption4());
					  quest.setQuestionText(quizzquestion.getQuestionText());
					  quizQuestionRepo.save(quest);
					  return ResponseEntity.ok("updated Successfully");
				  }
				  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("you Are Not allowed to access This Page");
			  }else {
				  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Quizz Found ");
			  }
		  
	  }catch (Exception e) {
		  logger.error("error at Getting Quizz"+e);
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
  }

}
