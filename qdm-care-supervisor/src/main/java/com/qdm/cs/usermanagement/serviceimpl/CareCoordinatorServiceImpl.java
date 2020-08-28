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
import com.qdm.cs.usermanagement.entity.CareCoordinator;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.repository.CareCoordinatorRepository;
import com.qdm.cs.usermanagement.repository.CategoryRepository;
import com.qdm.cs.usermanagement.repository.SkillsRepository;
import com.qdm.cs.usermanagement.repository.UploadProfileRepository;
import com.qdm.cs.usermanagement.service.CareCoordinatorService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CareCoordinatorServiceImpl implements CareCoordinatorService {

	CareCoordinatorRepository careCoordinatorRepository;
	CategoryRepository categoryRepository;
	ModelMapper modelMapper;
	UploadProfileRepository uploadProfileRepository;
	SkillsRepository skillsRepository;

	@Autowired
	public CareCoordinatorServiceImpl(CareCoordinatorRepository careCoordinatorRepository,
			CategoryRepository categoryRepository, ModelMapper modelMapper,
			UploadProfileRepository uploadProfileRepository, SkillsRepository skillsRepository) {
		super();
		this.careCoordinatorRepository = careCoordinatorRepository;
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
		this.uploadProfileRepository = uploadProfileRepository;
		this.skillsRepository = skillsRepository;
	}

	@Override
	public List<CareCoordinator> getCareCoordinator(Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<CareCoordinator> pagedResult = careCoordinatorRepository.findAll(paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareCoordinator>();
	}

	@Override
	public Optional<CareCoordinator> getCareCoordinatorById(long careCoordinatorId) {
		return careCoordinatorRepository.findById(careCoordinatorId);
	}

	@Override
	public CareCoordinator updateClientsCount(long careCoordinatorId, int clientsCount) {
		Optional<CareCoordinator> careGiverUpdateClientsCount = careCoordinatorRepository.findById(careCoordinatorId);
		if (careGiverUpdateClientsCount.isPresent()) {
			careGiverUpdateClientsCount.get().setClientsCount(clientsCount);
			return careCoordinatorRepository.save(careGiverUpdateClientsCount.get());
		}
		return careGiverUpdateClientsCount.get();
	}

	@Override
	public CareCoordinator updateClientsActiveStatus(long careCoordinatorId, Status activeStatus) {
		Optional<CareCoordinator> careGiverUpdateData = careCoordinatorRepository.findById(careCoordinatorId);
		if (careGiverUpdateData.isPresent()) {
			careGiverUpdateData.get().setCareCoordinatorId(careCoordinatorId);
			careGiverUpdateData.get().setActiveStatus(activeStatus);
			return careCoordinatorRepository.save(careGiverUpdateData.get());
		}
		return careGiverUpdateData.get();
	}

	@Override
	public CareCoordinator updateCareGiversCount(long careCoordinatorId, int careGiversCount) {
		Optional<CareCoordinator> careGiverUpdateClientsCount = careCoordinatorRepository.findById(careCoordinatorId);
		if (careGiverUpdateClientsCount.isPresent()) {
			careGiverUpdateClientsCount.get().setCareCoordinatorId(careCoordinatorId);
			careGiverUpdateClientsCount.get().setCareGiversCount(careGiversCount);
			return careCoordinatorRepository.save(careGiverUpdateClientsCount.get());
		}
		return careGiverUpdateClientsCount.get();
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
	public CareCoordinator addCareCoordinator(FormDataDTO formDataDTO) {
		CareCoordinator careCoordinator = modelMapper.map(formDataDTO, CareCoordinator.class);
		if (careCoordinator.getUploadPhoto() != null) {
			try {
				String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
				UploadProfile uploadProfile = UploadProfile.builder().fileName(fileName)
						.fileType(formDataDTO.getUploadPhoto().getContentType())
						.data(formDataDTO.getUploadPhoto().getBytes()).size(formDataDTO.getUploadPhoto().getSize())
						.build();
				careCoordinator.setUploadPhoto(uploadProfile);
			} catch (IOException e) {
				log.error("Error Occured In Care Coordinator Service Add Care Coordinator With Id "
						+ careCoordinator.getCareCoordinatorId());
			}
		}
		return careCoordinatorRepository.save(careCoordinator);
	}

	@Override
	public CareCoordinator updateCareCoordinator(FormDataDTO formDataDTO) {
		Optional<CareCoordinator> careCoordinatorUpdateData = careCoordinatorRepository
				.findById(formDataDTO.getCareCoordinatorId());
		if (careCoordinatorUpdateData.isPresent()) {
			careCoordinatorUpdateData.get()
					.setActiveStatus(formDataDTO.getActiveStatus() != null ? formDataDTO.getActiveStatus()
							: careCoordinatorUpdateData.get().getActiveStatus());
			careCoordinatorUpdateData.get().setAddress(formDataDTO.getAddress() != null ? formDataDTO.getAddress()
					: careCoordinatorUpdateData.get().getAddress());
			careCoordinatorUpdateData.get().setCareCoordinatorName(
					formDataDTO.getCareCoordinatorName() != null ? formDataDTO.getCareCoordinatorName()
							: careCoordinatorUpdateData.get().getCareCoordinatorName());
			careCoordinatorUpdateData.get()
					.setCareGiversCount(formDataDTO.getCareGiversCount() != 0 ? formDataDTO.getCareGiversCount()
							: careCoordinatorUpdateData.get().getCareGiversCount());
			careCoordinatorUpdateData.get().setCategory(formDataDTO.getCategory() != null ? formDataDTO.getCategory()
					: careCoordinatorUpdateData.get().getCategory());
			careCoordinatorUpdateData.get()
					.setClientsCount(formDataDTO.getClientsCount() != 0 ? formDataDTO.getClientsCount()
							: careCoordinatorUpdateData.get().getClientsCount());
			careCoordinatorUpdateData.get().setEmailId(formDataDTO.getEmailId() != null ? formDataDTO.getEmailId()
					: careCoordinatorUpdateData.get().getEmailId());
			careCoordinatorUpdateData.get().setMobileNo(formDataDTO.getMobileNo() != 0 ? formDataDTO.getMobileNo()
					: careCoordinatorUpdateData.get().getMobileNo());
			careCoordinatorUpdateData.get().setSkills(formDataDTO.getSkills() != null ? formDataDTO.getSkills()
					: careCoordinatorUpdateData.get().getSkills());

			if (formDataDTO.getUploadPhoto() != null) {
				String fileName = StringUtils.cleanPath(formDataDTO.getUploadPhoto().getOriginalFilename());
				try {
					careCoordinatorUpdateData.get()
							.setUploadPhoto(new UploadProfile(formDataDTO.getUploadPhoto().getOriginalFilename(),
									formDataDTO.getUploadPhoto().getContentType(),
									formDataDTO.getUploadPhoto().getBytes(), formDataDTO.getUploadPhoto().getSize()));
				} catch (IOException e) {
					log.info("Error Occured at UpdateCareCoordinator Photo Upload");
					e.printStackTrace();
				}
				CareCoordinator careCoordinatorUpdated = careCoordinatorRepository
						.save(careCoordinatorUpdateData.get());
				return careCoordinatorUpdated;
			} else {
				careCoordinatorUpdateData.get().setUploadPhoto(careCoordinatorUpdateData.get().getUploadPhoto());
			}
		}
		return careCoordinatorUpdateData.get();
	}

	@Override
	public List<CareCoordinator> getCareCoordinatorListCount() {
		return careCoordinatorRepository.findAll();
	}

	@Override
	public UploadProfile getFile(int fileId) {
		return uploadProfileRepository.findById(fileId).get();
	}

	@Override
	public List<CareCoordinator> searchCareCoordinator(Integer pageNo, Integer pageSize, String careCoordinatorName) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<CareCoordinator> pagedResult = careCoordinatorRepository
				.findByCareCoordinatorName(careCoordinatorName.toLowerCase(), paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<CareCoordinator>();
	}

	@Override
	public List<CareCoordinator> searchCareCoordinatorListCount(String careCoordinatorName) {
		return careCoordinatorRepository.findByCareCoordinatorName(careCoordinatorName.toLowerCase());
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
