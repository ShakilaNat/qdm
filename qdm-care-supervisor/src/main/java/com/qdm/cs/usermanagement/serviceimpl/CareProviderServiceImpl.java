package com.qdm.cs.usermanagement.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareProvider;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.repository.CareProviderRepository;
import com.qdm.cs.usermanagement.repository.CategoryRepository;
import com.qdm.cs.usermanagement.repository.SkillsRepository;
import com.qdm.cs.usermanagement.repository.UploadProfileRepository;
import com.qdm.cs.usermanagement.service.CareProviderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CareProviderServiceImpl implements CareProviderService {

	CareProviderRepository careProviderRepository;
	CategoryRepository categoryRepository;
	ModelMapper modelMapper;
	UploadProfileRepository uploadProfileRepository;
	SkillsRepository skillsRepository;

	@Autowired
	public CareProviderServiceImpl(CareProviderRepository careProviderRepository, CategoryRepository categoryRepository,
			ModelMapper modelMapper, UploadProfileRepository uploadProfileRepository,
			SkillsRepository skillsRepository) {
		super();
		this.careProviderRepository = careProviderRepository;
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
		this.uploadProfileRepository = uploadProfileRepository;
		this.skillsRepository = skillsRepository;
	}

	@Override
	public List<CareProvider> getCareProvider(Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<CareProvider> pagedResult = careProviderRepository.findAll(paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareProvider>();
	}

	@Override
	public Optional<CareProvider> getCareProviderById(long careProviderId) {
		return careProviderRepository.findById(careProviderId);
	}

	@Override
	public CareProvider updateClientsActiveStatus(long careProviderId, Status activeStatus) {
		Optional<CareProvider> careGiverUpdateData = careProviderRepository.findById(careProviderId);
		if (careGiverUpdateData.isPresent()) {
			careGiverUpdateData.get().setCareProviderId(careProviderId);
			careGiverUpdateData.get().setActiveStatus(activeStatus);
			return careProviderRepository.save(careGiverUpdateData.get());
		}
		return careGiverUpdateData.get();
	}

	@Override
	public CareProvider updateCareGiversCount(long careProviderId, int careGiversCount) {
		Optional<CareProvider> careGiverUpdateClientsCount = careProviderRepository.findById(careProviderId);
		if (careGiverUpdateClientsCount.isPresent()) {
			careGiverUpdateClientsCount.get().setCareProviderId(careProviderId);
			careGiverUpdateClientsCount.get().setCareGiversCount(careGiversCount);
			return careProviderRepository.save(careGiverUpdateClientsCount.get());
		}
		return careGiverUpdateClientsCount.get();
	}

	@Override
	public CareProvider updateProductsCount(long careProviderId, int productsCount) {
		Optional<CareProvider> ProductUpdateCount = careProviderRepository.findById(careProviderId);
		if (ProductUpdateCount.isPresent()) {
			ProductUpdateCount.get().setCareProviderId(careProviderId);
			ProductUpdateCount.get().setProductsCount(productsCount);
			return careProviderRepository.save(ProductUpdateCount.get());
		}
		return ProductUpdateCount.get();
	}

	@Override
	public List<Category> getCategoryListById(Collection<Integer> category) {
		List<Category> data = new ArrayList<>();
		for (Integer categoryData : category) {
			Category categoryList = categoryRepository.findByCategoryId(categoryData);
			data.add(categoryList);
		}
		return data;
	}

	@Override
	public CareProvider addCareProvider(FormDataDTO formDataDTO) throws IOException {
		CareProvider careProvider = modelMapper.map(formDataDTO, CareProvider.class);
		if (careProvider.getUploadPhoto() != null) {
			try {
				String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
				UploadProfile uploadProfile = UploadProfile.builder().fileName(fileName)
						.fileType(formDataDTO.getUploadPhoto().getContentType())
						.data(formDataDTO.getUploadPhoto().getBytes()).size(formDataDTO.getUploadPhoto().getSize())
						.build();
				careProvider.setUploadPhoto(uploadProfile);
			} catch (IOException e) {
				log.error("Error Occured In CareProviderService AddCareProvider ProfileUpload With Id : "
						+ careProvider.getCareProviderId());
			}
		}
		return careProviderRepository.save(careProvider);
	}

	@Override
	public CareProvider updateCareProvider(FormDataDTO formDataDTO) {
		Optional<CareProvider> careProviderUpdateDate = careProviderRepository
				.findById(formDataDTO.getCareProviderId());
		if (careProviderUpdateDate.isPresent()) {
			careProviderUpdateDate.get()
					.setActiveStatus(formDataDTO.getActiveStatus() != null ? formDataDTO.getActiveStatus()
							: careProviderUpdateDate.get().getActiveStatus());
			careProviderUpdateDate.get().setAddress(formDataDTO.getAddress() != null ? formDataDTO.getAddress()
					: careProviderUpdateDate.get().getAddress());
			careProviderUpdateDate.get()
					.setCareGiversCount(formDataDTO.getCareGiversCount() != 0 ? formDataDTO.getCareGiversCount()
							: careProviderUpdateDate.get().getCareGiversCount());
			careProviderUpdateDate.get()
					.setCareProviderName(formDataDTO.getCareProviderName() != null ? formDataDTO.getCareProviderName()
							: careProviderUpdateDate.get().getCareProviderName());
			careProviderUpdateDate.get().setCategory(formDataDTO.getCategory() != null ? formDataDTO.getCategory()
					: careProviderUpdateDate.get().getCategory());
			careProviderUpdateDate.get().setEmailId(formDataDTO.getEmailId() != null ? formDataDTO.getEmailId()
					: careProviderUpdateDate.get().getEmailId());
			careProviderUpdateDate.get()
					.setInChargesName(formDataDTO.getInChargesName() != null ? formDataDTO.getInChargesName()
							: careProviderUpdateDate.get().getInChargesName());
			careProviderUpdateDate.get().setMobileNo(formDataDTO.getMobileNo() != 0 ? formDataDTO.getMobileNo()
					: careProviderUpdateDate.get().getMobileNo());
			careProviderUpdateDate.get().setOfferings(formDataDTO.getOfferings() != null ? formDataDTO.getOfferings()
					: careProviderUpdateDate.get().getOfferings());
			careProviderUpdateDate.get().setSkills(formDataDTO.getSkills() != null ? formDataDTO.getSkills()
					: careProviderUpdateDate.get().getSkills());

			if (formDataDTO.getUploadPhoto() != null) {
				String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
				try {
					careProviderUpdateDate.get()
							.setUploadPhoto(new UploadProfile(formDataDTO.getUploadPhoto().getOriginalFilename(),
									formDataDTO.getUploadPhoto().getContentType(),
									formDataDTO.getUploadPhoto().getBytes(), formDataDTO.getUploadPhoto().getSize()));
				} catch (IOException e) {
					log.info("Error Occured at UpdateCareGiver Photo Upload");
					e.printStackTrace();
				}
				CareProvider careProviderUpdated = careProviderRepository.save(careProviderUpdateDate.get());
				return careProviderUpdated;
			} else {
				careProviderUpdateDate.get().setUploadPhoto(careProviderUpdateDate.get().getUploadPhoto());
			}
		}
		return careProviderUpdateDate.get();
	}

	@Override
	public List<CareProvider> getAllCareProviderListCount() {
		return careProviderRepository.findAll();
	}

	@Override
	public UploadProfile getFile(int fileId) {
		return uploadProfileRepository.findById(fileId).get();
	}

	@Override
	public List<CareProvider> searchCareProvider(Integer pageNo, Integer pageSize, String careProviderName) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<CareProvider> pagedResult = careProviderRepository.findByCareProviderName(careProviderName.toLowerCase(),
				paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareProvider>();
	}

	@Override
	public List<CareProvider> searchAllCareProviderListCount(String careProviderName) {
		return careProviderRepository.findByCareProviderName(careProviderName.toLowerCase());
	}

	@Override
	public List<Skills> getSkillsListById(Collection<Integer> skills) {
		List<Skills> data = new ArrayList<>();
		for (Integer skillData : skills) {
			Skills skillList = skillsRepository.findBySkillId(skillData);
			data.add(skillList);
		}
		return data;
	}
}
