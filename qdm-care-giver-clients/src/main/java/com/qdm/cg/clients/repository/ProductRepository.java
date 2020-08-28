package com.qdm.cg.clients.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cg.clients.entity.Product;
import com.qdm.cg.clients.entity.Reports;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	
	public List<Product> findByClientId(long clientId);
}
