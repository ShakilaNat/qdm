package com.qdm.cs.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.qdm.cs.usermanagement.entity.CareCoordinator;

@Repository
public interface CareCoordinatorRepository extends JpaRepository<CareCoordinator, Long> {

	Optional<CareCoordinator> findByCareCoordinatorId(long careCoordinatorId);

	@Query("Select c from CareCoordinator c where lower(c.careCoordinatorName) like %:careCoordinatorName%")
	Page<CareCoordinator> findByCareCoordinatorName(String careCoordinatorName, Pageable paging);

	@Query("Select c from CareCoordinator c where lower(c.careCoordinatorName) like %:careCoordinatorName%")
	List<CareCoordinator> findByCareCoordinatorName(String careCoordinatorName);

}
