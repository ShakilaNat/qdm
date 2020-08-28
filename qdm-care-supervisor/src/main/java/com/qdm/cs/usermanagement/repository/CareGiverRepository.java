package com.qdm.cs.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.qdm.cs.usermanagement.entity.CareGiver;

@Repository
public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {

	Optional<CareGiver> findByCareGiverId(long careGiverId);

	@Query("Select c from CareGiver c where lower(c.careGiverName) like %:careGiverName%")
	Page<CareGiver> findByCareGiverName(String careGiverName, Pageable paging);

	@Query("Select c from CareGiver c where lower(c.careGiverName) like %:careGiverName%")
	List<CareGiver> findByCareGiverName(String careGiverName);
	
}
