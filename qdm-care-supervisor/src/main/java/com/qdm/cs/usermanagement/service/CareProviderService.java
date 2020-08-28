package com.qdm.cs.usermanagement.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareProvider;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;

public interface CareProviderService {

	List<CareProvider> getCareProvider(Integer pageNo,Integer pageSize);

	Optional<CareProvider> getCareProviderById(long careProviderId);

	CareProvider updateClientsActiveStatus(long careProviderId, Status activeStatus);

	CareProvider updateCareGiversCount(long careCoordinatorId, int careGiversCount);

	CareProvider updateProductsCount(long careProviderId, int productsCount);

	List<Category> getCategoryListById(Collection<Integer> category);

	CareProvider addCareProvider(FormDataDTO formDataDTO) throws IOException;

	CareProvider updateCareProvider(FormDataDTO formDataDTO) throws IOException;

	List<CareProvider> getAllCareProviderListCount();

	UploadProfile getFile(int fileId);

	List<CareProvider> searchCareProvider(Integer pageNo, Integer pageSize, String careProviderName);

	List<CareProvider> searchAllCareProviderListCount(String careProviderName);

	List<Skills> getSkillsListById(Collection<Integer> skills);

}
