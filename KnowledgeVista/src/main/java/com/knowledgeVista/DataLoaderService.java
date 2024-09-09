package com.knowledgeVista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserRoles;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.Repository.MuserRoleRepository;


import java.io.InputStream;
import java.time.LocalDate;

@Service
public class DataLoaderService {

    @Autowired
    private MuserRoleRepository muserRoleRepository;
   @Autowired 
    private MuserRepositories muserrepositories;
    @PostConstruct
    @Transactional
    public void init() {
        try {
            loadRoles();
             loadUsers(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoles() {
        try {
            InputStream is = getClass().getResourceAsStream("/roles.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList roleNodes = doc.getElementsByTagName("role");
            for (int i = 0; i < roleNodes.getLength(); i++) {
                Element roleElement = (Element) roleNodes.item(i);
                String roleName = roleElement.getElementsByTagName("roleName").item(0).getTextContent();
                boolean isActive = Boolean.parseBoolean(roleElement.getElementsByTagName("isActive").item(0).getTextContent());

                // Create MuserRoles object
                MuserRoles role = new MuserRoles();
                role.setRoleName(roleName);
                role.setIsActive(isActive);

                // Check if role already exists
                if (muserRoleRepository.findByRoleName(roleName).isEmpty()) {
                    muserRoleRepository.save(role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions as per your application's requirements
        }
    }

    
    private void loadUsers() {
        try {
            InputStream is = getClass().getResourceAsStream("/users.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList userNodes = doc.getElementsByTagName("user");
         

            for (int i = 0; i < userNodes.getLength(); i++) {
                Element userElement = (Element) userNodes.item(i);
                String username = userElement.getElementsByTagName("username").item(0).getTextContent();
                String password = userElement.getElementsByTagName("psw").item(0).getTextContent();
                String email = userElement.getElementsByTagName("email").item(0).getTextContent();
                LocalDate dob = LocalDate.parse(userElement.getElementsByTagName("dob").item(0).getTextContent());
                String phone = userElement.getElementsByTagName("phone").item(0).getTextContent();
                String skills = userElement.getElementsByTagName("skills").item(0).getTextContent();
                String countryCode = userElement.getElementsByTagName("countryCode").item(0).getTextContent();
                String roleName = userElement.getElementsByTagName("role").item(0).getTextContent();
                String institutionName=userElement.getElementsByTagName("institutionName").item(0).getTextContent();
                // Check if user with the same email already exists
                if (muserrepositories.findByEmail(email).isEmpty()) {
                    // Find or create the role
                    MuserRoles role = muserRoleRepository.findByRoleName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

                    // Create new user instance
                    Muser newUser = new Muser();
                    newUser.setUsername(username);
                    newUser.setPsw(password);
                    newUser.setEmail(email);
                    newUser.setDob(dob);
                    newUser.setPhone(phone);
                    newUser.setSkills(skills);
                    newUser.setCountryCode(countryCode);
                    newUser.setInstitutionName(institutionName);
                    newUser.setRole(role); // Set the role instance
                    newUser.setIsActive(true); // Assuming default active status

                    muserrepositories.save(newUser);
                }
            }

            // Save all valid users at once
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions as per your application's requirements
        }
    }


}
