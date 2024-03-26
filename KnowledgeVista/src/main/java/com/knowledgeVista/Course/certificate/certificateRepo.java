package com.knowledgeVista.Course.certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface certificateRepo extends JpaRepository<certificate, Long> {

}
