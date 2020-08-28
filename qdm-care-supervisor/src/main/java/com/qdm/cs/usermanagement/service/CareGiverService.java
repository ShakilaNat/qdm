package com.qdm.cs.usermanagement.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareGiver;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;

public interface CareGiverService {

	List<CareGiver> getCareGiver(Integer pageNo, Integer pageSize);

	Optional<CareGiver> getCareGiverById(int careGiverId);

	CareGiver updateClientsCount(long careGiverId, int clientsCount);

	CareGiver updateClientsActiveStatus(long careGiverId, Status activeStatus);

	List<Category> getCategoryListById(Collection<Integer> category);

	CareGiver addCareGiver(FormDataDTO formDataDTO) throws IOException;

	CareGiver updateCareGiver(FormDataDTO formDataDTO) throws IOException;

	List<CareGiver> getAllCareGiversListCount();

	UploadProfile getFile(int fileId);

	List<CareGiver> searchCareGiver(Integer pageNo, Integer pageSize, String careGiverName);

	List<CareGiver> searchAllCareGiversListCount(String careGiverName);

	List<Skills> getSkillsListById(Collection<Integer> skills);

}
