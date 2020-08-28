package com.qdm.cs.usermanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.qdm.cs.usermanagement.entity.CareProvider;

@Repository
public interface CareProviderRepository extends JpaRepository<CareProvider, Long> {

	@Query("Select c from CareProvider c where lower(c.careProviderName) like %:careProviderName%")
	Page<CareProvider> findByCareProviderName(String careProviderName, Pageable paging);

	@Query("Select c from CareProvider c where lower(c.careProviderName) like %:careProviderName%")
	List<CareProvider> findByCareProviderName(String careProviderName);

}
