package com.knowledgeVista.License;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface licenseRepository extends JpaRepository<License, Long> {

}
