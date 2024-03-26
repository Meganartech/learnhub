package com.knowledgeVista.User.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.User.Muser;


@Repository
public interface MuserRepositories extends JpaRepository<Muser,Long> {

	@Query("SELECT u FROM Muser u WHERE u.email = ?1")
  Optional<Muser> findByEmail(String email);
	
	
	   @Query("SELECT u FROM Muser u WHERE u.role.roleName = :rolename")
	    List<Muser> findByRoleName(@Param("rolename") String roleName);

	    @Query("SELECT COUNT(u) FROM Muser u WHERE u.role.roleName = :rolename")
	    Long countByRoleName(@Param("rolename") String roleName);
}



