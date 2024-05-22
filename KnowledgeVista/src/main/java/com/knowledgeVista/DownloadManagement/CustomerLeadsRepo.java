package com.knowledgeVista.DownloadManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerLeadsRepo extends JpaRepository<CustomerLeads, Long> {

}
