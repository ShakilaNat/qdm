package com.qdm.cg.clients.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cg.clients.entity.Reports;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Long> {

	
	public List<Reports> findByClientId(long clientId);
}
