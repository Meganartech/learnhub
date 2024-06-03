package com.knowledgeVista.Payments;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderuserRepo extends JpaRepository<Orderuser,Long>{
	Optional<Orderuser> findByOrderId(String orderId);

}
