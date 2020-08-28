package com.qdm.cg.clients.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cg.clients.entity.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	public List<Activity> findByClientId(long clientId);

}
