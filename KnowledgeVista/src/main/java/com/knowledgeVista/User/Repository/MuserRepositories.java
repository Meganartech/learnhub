package com.knowledgeVista.User.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserDto;
import com.knowledgeVista.User.MuserProfileDTO;
import com.knowledgeVista.User.MuserRequiredDto;


@Repository
public interface MuserRepositories extends JpaRepository<Muser,Long> {


	@Query("SELECT u FROM Muser u WHERE u.email = ?1")
  Optional<Muser> findByEmail(String email);
	

	@Query("SELECT u.institutionName FROM Muser u WHERE u.email = ?1")
  String findinstitutionByEmail(String email);
	 @Query("SELECT new com.knowledgeVista.User.MuserDto(u.userId, u.username, u.email, u.phone, u.isActive, u.dob, u.skills, u.institutionName) " +
	           "FROM Muser u " +
	           "WHERE u.email = :email")
	    Optional<MuserDto> findDetailsByEmailforSysadmin(@Param("email") String email);
	
	  @Query("SELECT new com.knowledgeVista.User.MuserDto(u.userId, u.username, u.email, u.phone, u.isActive, u.dob, u.skills, u.institutionName) " +
	           "FROM Muser u " +
	           "WHERE u.email = :email AND u.institutionName = :institutionName")
	    Optional<MuserDto> findDetailsByEmailAndInstitution(@Param("email") String email,
	                                                        @Param("institutionName") String institutionName);
	  
	  @Query("SELECT new com.knowledgeVista.User.MuserRequiredDto(u.userId, u.username, u.email, u.phone, u.isActive, u.dob, u.skills, u.institutionName,u.profile, u.countryCode) " +
	           "FROM Muser u " +
	           "WHERE u.email = :email AND u.institutionName = :institutionName")
	    Optional<MuserRequiredDto> findDetailandProfileByEmailAndInstitution(@Param("email") String email,
	                                                        @Param("institutionName") String institutionName);
	  
@Query("SELECT new com.knowledgeVista.User.MuserProfileDTO(u.profile, u.countryCode, u.role.roleName ) " +
	           "FROM Muser u " +
	           "JOIN u.role r " +
	           "WHERE u.email = :email AND u.institutionName = :institutionName")
	    Optional<MuserProfileDTO> findProfileAndCountryCodeAndRoleByEmailAndInstitutionName(
	        @Param("email") String email,
	        @Param("institutionName") String institutionName
	    );

@Query("SELECT new com.knowledgeVista.User.MuserProfileDTO(u.profile, u.countryCode, u.role.roleName ) " +
        "FROM Muser u " +
        "JOIN u.role r " +
        "WHERE u.email = :email ")
 Optional<MuserProfileDTO> findProfileAndCountryCodeAndRoleByEmail(
     @Param("email") String email
 );

	@Query("SELECT u FROM Muser u WHERE u.username = ?1")
	  Optional<Muser> findByname(String username);
	
	@Query("SELECT u FROM Muser u WHERE u.email = ?1 AND u.institutionName = ?2")
  Optional<Muser> findByEmailandInstitutionName(String email, String institutionName);
	
	@Query("SELECT u FROM Muser u WHERE u.userId = ?1 AND u.institutionName = ?2")
	  Optional<Muser> findByuserIdandInstitutionName(Long userId, String institutionName);

	
	@Query("SELECT u from Muser u WHERE u.institutionName = ?1")
	Optional<Muser>findByInstitutionName(String institutionName);
	
	   
	   
	   @Query("SELECT u FROM Muser u WHERE u.role.roleName = :rolename AND u.institutionName = :institutionname")
		List<Muser> findByRoleNameAndInstitutionName(@Param("rolename") String roleName, @Param("institutionname") String institutionName);
    
	   @Query("SELECT COUNT(u) FROM Muser u WHERE u.role.roleName = :rolename  AND u.institutionName = :institutionname")
	    Long countByRoleNameandInstitutionName(@Param("rolename") String roleName, @Param("institutionname") String institutionName);

	    @Query("SELECT COUNT(u) FROM Muser u WHERE u.role.roleName = :rolename")
	    Long countByRoleName(@Param("rolename") String roleName);
	    
	    @Query("SELECT isActive FROM Muser u WHERE u.role.roleName = :rolename  AND u.institutionName = :institutionname")
	    Boolean getactiveResultByInstitutionName(@Param("rolename") String roleName, @Param("institutionname") String institutionName);

	    @Query("SELECT u.email FROM Muser u WHERE u.institutionName = :institutionname AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
	    List<String> findEmailsByEmailContainingIgnoreCase(@Param("email") String email,@Param("institutionname") String institution);

	    
	    
	    @Query("SELECT DISTINCT u2.email " +
	            "FROM Muser u1 " +
	            "JOIN u1.allotedCourses c " +
	            "JOIN c.users u2 " +
	            "WHERE u1.email = :email " +
	            "AND u2.email LIKE %:query%")
	     List<String> findEmailsInAllotedCoursesByUserEmail(@Param("email") String email, @Param("query") String query);

	    @Query("SELECT new com.knowledgeVista.User.MuserDto(u2.userId, u2.username, u2.email, u2.phone, u2.isActive, u2.dob, u2.skills) " +
	            "FROM Muser u1 " +
	            "JOIN u1.allotedCourses c " +
	            "JOIN c.users u2 " +
	            "WHERE u1.email = :email " +
	            "GROUP BY u2.userId, u2.username, u2.email, u2.phone, u2.isActive, u2.dob, u2.skills,u2.institutionName")
	     List<MuserDto> findStudentsOfTrainer(@Param("email") String email);
}



