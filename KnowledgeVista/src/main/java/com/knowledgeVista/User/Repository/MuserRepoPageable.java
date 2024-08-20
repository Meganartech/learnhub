package com.knowledgeVista.User.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserDto;

public interface MuserRepoPageable extends PagingAndSortingRepository<Muser, Long> {
//	 @Query("SELECT u FROM Muser u WHERE u.role.roleName = :rolename")
//	    Page<Muser> findByRoleName(@Param("rolename") String roleName, Pageable pageable);
//	 
//	 @Query("SELECT u FROM Muser u WHERE u.role.roleName = :rolename AND u.institutionName = :institutionname")
//	 Page<Muser> findByRoleNameAndInstitutionName(@Param("rolename") String roleName, @Param("institutionname") String institutionName,Pageable pageable);

	 @Query("SELECT new com.knowledgeVista.User.MuserDto(u.userId, u.username, u.email, u.phone, u.isActive, u.dob, u.skills,u.institutionName) " +
	           "FROM Muser u WHERE u.role.roleName = :rolename")
	    Page<MuserDto> findByRoleName(@Param("rolename") String roleName, Pageable pageable);

	    @Query("SELECT new com.knowledgeVista.User.MuserDto(u.userId, u.username, u.email, u.phone, u.isActive, u.dob, u.skills,u.institutionName) " +
	           "FROM Muser u WHERE u.role.roleName = :rolename AND u.institutionName = :institutionname")
	    Page<MuserDto> findByRoleNameAndInstitutionName(@Param("rolename") String roleName, @Param("institutionname") String institutionName, Pageable pageable);

	    
	 @Query("SELECT new com.knowledgeVista.User.MuserDto(u2.userId, u2.username, u2.email, u2.phone, u2.isActive, u2.dob, u2.skills ,u2.institutionName) " +
	           "FROM Muser u1 " +
	           "JOIN u1.allotedCourses c " +
	           "JOIN c.users u2 " +
	           "WHERE u1.email = :email " +
	           "GROUP BY u2.userId, u2.username, u2.email, u2.phone, u2.isActive, u2.dob, u2.skills,u2.institutionName")
	    Page<MuserDto> findStudentsOfTrainer(@Param("email") String email, Pageable pageable);

}
