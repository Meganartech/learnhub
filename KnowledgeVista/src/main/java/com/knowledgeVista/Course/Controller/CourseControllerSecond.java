package com.knowledgeVista.Course.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
@RequestMapping("/course")
@CrossOrigin
public class CourseControllerSecond {
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private JwtUtil jwtUtil;
}
