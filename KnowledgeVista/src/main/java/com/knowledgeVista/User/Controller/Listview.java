package com.knowledgeVista.User.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.Repository.MuserRoleRepository;

@RestController
@RequestMapping("/view")
@CrossOrigin
public class Listview {
	@Autowired
	private MuserRepositories muserrepositories;
	
	
//```````````````WORKING````````````````````````````````````

    @GetMapping("/users")
    public ResponseEntity<List<Muser>> getUsersByRoleName() {
        try {
            List<Muser> users = muserrepositories.findByRoleName("USER");
           
            users.forEach(user -> {
                byte[] decompressedImage = ImageUtils.decompressImage(user.getProfile());
                user.setProfile(decompressedImage);
                user.setCourses(null);
            });
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
//```````````````WORKING````````````````````````````````````
    @GetMapping("/users/{userId}")
    public ResponseEntity<Muser> getUserById(@PathVariable Long userId) {
        try {
            Muser user = muserrepositories.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
      
            byte[] decompressedImage = ImageUtils.decompressImage(user.getProfile());
            user.setProfile(decompressedImage);
            user.setCourses(null);

        	user.setAllotedCourses(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

//```````````````WORKING````````````````````````````````````
@GetMapping("/Trainer")
public ResponseEntity<List<Muser>> getTrainerByRoleName() {
    try {
        List<Muser> users = muserrepositories.findByRoleName("TRAINER");
       
        users.forEach(user -> {
        	user.setAllotedCourses(null);
            byte[] decompressedImage = ImageUtils.decompressImage(user.getProfile());
            user.setProfile(decompressedImage);
            user.setCourses(null);
        });
        return ResponseEntity.ok(users);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
}