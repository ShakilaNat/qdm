package com.qdm.cs.usermanagement.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareCoordinator;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;

public interface CareCoordinatorService {

	List<CareCoordinator> getCareCoordinator(Integer pageNo,Integer pageSize);

	Optional<CareCoordinator> getCareCoordinatorById(long careCoordinatorId);

	CareCoordinator updateClientsCount(long careCoordinatorId, int clientsCount);

	CareCoordinator updateClientsActiveStatus(long careCoordinatorId, Status activeStatus);

	CareCoordinator updateCareGiversCount(long careCoordinatorId, int careGiversCount);

	List<Category> getCategoryListById(Collection<Integer> category);

	CareCoordinator addCareCoordinator(FormDataDTO formDataDTO) throws IOException;

	CareCoordinator updateCareCoordinator(FormDataDTO formDataDTO) throws IOException;

	List<CareCoordinator> getCareCoordinatorListCount();

	UploadProfile getFile(int fileId);

	List<CareCoordinator> searchCareCoordinator(Integer pageNo, Integer pageSize, String careCoordinatorName);

	List<CareCoordinator> searchCareCoordinatorListCount(String careCoordinatorName);

	List<Skills> getSkillsListById(Collection<Integer> skills);

}
