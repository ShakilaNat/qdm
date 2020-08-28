package com.qdm.cg.clients.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cg.clients.entity.Issues;

@Repository
public interface IssuesRepository extends JpaRepository<Issues, Long> {
	
	public List<Issues> findByClientId(long clientId);

}
