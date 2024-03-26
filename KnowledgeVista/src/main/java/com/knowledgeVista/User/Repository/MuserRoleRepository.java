package com.knowledgeVista.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.User.MuserRoles;
@Repository
public interface MuserRoleRepository extends JpaRepository<MuserRoles,Long> {
	@Query("SELECT u FROM MuserRoles u WHERE u.roleName = ?1")
	MuserRoles findByroleName(String roleName);

}
