package com.knowledgeVista.DownloadManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.knowledgeVista.DownloadManagement.Customer_downloads;

@Repository
public interface CustomerdownloadRepo extends JpaRepository<Customer_downloads, Long> {

}
