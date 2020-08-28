package com.qdm.cs.usermanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.qdm.cs.usermanagement.constants.ResponseConstants;
import com.qdm.cs.usermanagement.dto.FormDataDTO;
import com.qdm.cs.usermanagement.entity.CareCoordinator;
import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.entity.Skills;
import com.qdm.cs.usermanagement.entity.UploadProfile;
import com.qdm.cs.usermanagement.enums.Status;
import com.qdm.cs.usermanagement.response.ResponseInfo;
import com.qdm.cs.usermanagement.response.ResponseType;
import com.qdm.cs.usermanagement.service.CareCoordinatorService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = { "/careCoordinator" })
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
@Slf4j
public class CareCoordinatorController {

	@Autowired
	CareCoordinatorService careCoordinatorService;

	@PostMapping(value = "/add", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> addCareCoordinator(FormDataDTO formDataDTO) throws IOException {
		ResponseEntity response = null;
		try {
			CareCoordinator careCoordinator = careCoordinatorService.addCareCoordinator(formDataDTO);
			log.info("Care Giver Created Successfully With CareGiver_Id : " + careCoordinator.getCareCoordinatorId());
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.CREATED);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At addCareCoordinator : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/list/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getCareCoordinator(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(value = "careCoordinatorName",required = false) String careCoordinatorName) {
		ResponseEntity response = null;
		List<CareCoordinator> careCoordinatorList;
		List<CareCoordinator> careCoordinatorListCount;

		try {
			if (careCoordinatorName == null) {
				careCoordinatorList = careCoordinatorService.getCareCoordinator(pageNo, pageSize);
				careCoordinatorListCount = careCoordinatorService.getCareCoordinatorListCount();
			} else {
				careCoordinatorList = careCoordinatorService.searchCareCoordinator(pageNo, pageSize,
						careCoordinatorName);
				careCoordinatorListCount = careCoordinatorService.searchCareCoordinatorListCount(careCoordinatorName);
			}
			List<Object> coordinator = new ArrayList<>();
			Map<String, Object> careCoordinatorResponse = new HashMap<>();

			for (CareCoordinator careCoordinatorData : careCoordinatorList) {
				List<Category> category = careCoordinatorService.getCategoryListById(careCoordinatorData.getCategory());
				List<Object> categoryList = new ArrayList<>();
				for (Category categoryData : category) {
					if (categoryData != null) {
						Map<String, Object> categoryMap = new HashMap<>();
						categoryMap.put("label", categoryData.getCategoryName());
						categoryMap.put("value", categoryData.getCategoryId());
						categoryList.add(categoryMap);
					}
				}

				JSONObject careGivers = new JSONObject();
				careGivers.put("count", careCoordinatorData.getCareGiversCount());
				careGivers.put("name", "CareGivers");

				JSONObject clients = new JSONObject();
				clients.put("count", careCoordinatorData.getClientsCount());
				clients.put("name", "Clients");

				JSONArray jsonarr = new JSONArray();
				jsonarr.add(careGivers);
				jsonarr.add(clients);

				careCoordinatorResponse.put("total_count", careCoordinatorListCount.size());
				careCoordinatorResponse.put("offset", pageNo);

				Map<String, Object> careCoordinatorDatas = new HashMap<>();
				careCoordinatorDatas.put("id", careCoordinatorData.getCareCoordinatorId());
				careCoordinatorDatas.put("name", careCoordinatorData.getCareCoordinatorName());
				careCoordinatorDatas.put("isactive", careCoordinatorData.getActiveStatus());
				careCoordinatorDatas.put("service", "");
				if (careCoordinatorData.getUploadPhoto() != null) {
					String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/careCoordinator/downloadFile/" + careCoordinatorData.getUploadPhoto().getId())
							.toUriString();
					careCoordinatorDatas.put("profile_pic", fileDownloadUri);
				} else {
					careCoordinatorDatas.put("profile_pic", "");
				}
				careCoordinatorDatas.put("category", categoryList);
				careCoordinatorDatas.put("orderList", jsonarr);
				coordinator.add(careCoordinatorDatas);
				careCoordinatorResponse.put("list", coordinator);

			}
			log.info("Get All CareCoordinator Records - Total Count : " + careCoordinatorResponse.size());
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", careCoordinatorResponse), HttpStatus.OK);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At getCareCoordinator : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/details/get/{careCoordinatorId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> editCareGiverDetails(@PathVariable("careCoordinatorId") int careCoordinatorId) {
		ResponseEntity response = null;
		try {

			Optional<CareCoordinator> careCoordinator = careCoordinatorService
					.getCareCoordinatorById(careCoordinatorId);
			Map<String, Object> careCoordinatorRecord = new HashMap<>();
			if (careCoordinator.isPresent()) {
				CareCoordinator careCoordinatorData = careCoordinator.get();
				List<Category> category = careCoordinatorService.getCategoryListById(careCoordinatorData.getCategory());
				List<Object> categoryList = new ArrayList<>();
				for (Category categoryData : category) {
					if (categoryData != null) {
						Map<String, Object> categoryMap = new HashMap<>();
						categoryMap.put("label", categoryData.getCategoryName());
						categoryMap.put("value", categoryData.getCategoryId());
						categoryList.add(categoryMap);
					}
				}
				
				List<Skills> skills = careCoordinatorService.getSkillsListById(careCoordinatorData.getSkills());
				List<Object> skillsList = new ArrayList<>();
				for (Skills skillsData : skills) {
					if (skillsData != null) {
						Map<String, Object> skillsMap = new HashMap<>();
						skillsMap.put("label", skillsData.getSkillName());
						skillsMap.put("value", skillsData.getSkillId());
						skillsList.add(skillsMap);
					}
				}
				
				careCoordinatorRecord.put("id", careCoordinatorData.getCareCoordinatorId());
				careCoordinatorRecord.put("name", careCoordinatorData.getCareCoordinatorName());
				careCoordinatorRecord.put("isactive", careCoordinatorData.getActiveStatus());
				if (careCoordinatorData.getUploadPhoto() != null) {
					String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/careCoordinator/downloadFile/" + careCoordinatorData.getUploadPhoto().getId())
							.toUriString();

					careCoordinatorRecord.put("profile_pic", fileDownloadUri);
				} else {
					careCoordinatorRecord.put("profile_pic", "");

				}
				careCoordinatorRecord.put("mobile_no_isd_code", careCoordinatorData.getMobileNoISDCode());
				careCoordinatorRecord.put("mobile_no", careCoordinatorData.getMobileNo());
				careCoordinatorRecord.put("email", careCoordinatorData.getEmailId());
				careCoordinatorRecord.put("address", careCoordinatorData.getAddress());
				careCoordinatorRecord.put("clients_count", careCoordinatorData.getClientsCount());
				careCoordinatorRecord.put("upcoming_activities_count", 0);
				careCoordinatorRecord.put("average_rating", 0);
				careCoordinatorRecord.put("upcoming_activities_count", 0);
				careCoordinatorRecord.put("skills", skillsList);
				careCoordinatorRecord.put("category", categoryList);
				careCoordinatorRecord.put("care_provider", "");

				log.info("Get CareGiver Records By CareGiverId " + careCoordinatorId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", careCoordinatorRecord), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareGiver Found with Id : " + careCoordinatorId);
				response = new ResponseEntity(
						new ResponseInfo(ResponseType.NOT_FOUND.getResponseMessage(),
								ResponseType.NOT_FOUND.getResponseCode(), "", careCoordinatorRecord),
						HttpStatus.NOT_FOUND);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At editCareGiverDetails : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping(value = "/modify", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> updateCareCoordinator(FormDataDTO formDataDTO) throws IOException {
		ResponseEntity response = null;
		if (formDataDTO.getCareCoordinatorId() == 0) {
			log.info(ResponseConstants.Care_Giver_Id);
			response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
					ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
			return response;
		}
		try {
			CareCoordinator careCoordinator = careCoordinatorService.updateCareCoordinator(formDataDTO);
			if (careCoordinator != null) {
				log.info("Updated Care Coordinator Successfully with Id : " + careCoordinator.getCareCoordinatorId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareCoordinator Found with Id : " + formDataDTO.getCareCoordinatorId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateCareCoordinator : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping("/downloadFile/{fileId:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable int fileId, HttpServletRequest request) {
		UploadProfile databaseFile = careCoordinatorService.getFile(fileId);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(databaseFile.getFileType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + databaseFile.getFileName() + "\"")
				.body(new ByteArrayResource(databaseFile.getData()));
	}

	@PutMapping("/updateClientsCount/{careCoordinatorId}/{clientsCount}")
	public ResponseEntity<?> updateClientsCount(@PathVariable("careCoordinatorId") long careCoordinatorId,
			@PathVariable("clientsCount") int clientsCount) {
		ResponseEntity response = null;

		try {
			CareCoordinator careCoordinator = careCoordinatorService.updateClientsCount(careCoordinatorId,
					clientsCount);
			if (careCoordinator != null) {
				log.info("Updated Clients Count Successfully with Id : " + careCoordinatorId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareCoordinator Found with Id : " + careCoordinatorId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateClientsCount : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping("/updateCareGiversCount/{careCoordinatorId}/{careGiversCount}")
	public ResponseEntity<?> updateCareGiversCount(@PathVariable("careCoordinatorId") long careCoordinatorId,
			@PathVariable("careGiversCount") int careGiversCount) {
		ResponseEntity response = null;
		try {
			CareCoordinator careCoordinator = careCoordinatorService.updateCareGiversCount(careCoordinatorId,
					careGiversCount);
			if (careCoordinator != null) {
				log.info("Updated CareGivers Count Successfully with Id : " + careCoordinatorId);

				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareCoordinator Found with Id : " + careCoordinatorId);

				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateCareGiversCount : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping("/updateCareCoordinatorAvailabilityStatus/{careCoordinatorId}/{activeStatus}")
	public ResponseEntity<?> updateCareCoordinatorAvailabilityStatus(
			@PathVariable("careCoordinatorId") long careCoordinatorId,
			@PathVariable("activeStatus") Status activeStatus) {
		ResponseEntity response = null;
		try {
			CareCoordinator careCoordinator = careCoordinatorService.updateClientsActiveStatus(careCoordinatorId,
					activeStatus);
			if (careCoordinator != null) {
				log.info("Updated Availability Status Successfully with Id : " + careCoordinatorId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No CareCoordinator Found with Id : " + careCoordinatorId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
						ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At updateClientsActiveStatus : " + e.getMessage());

			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

}
