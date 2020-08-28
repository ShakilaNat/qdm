package com.qdm.cg.clients.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.qdm.cg.clients.dto.ClientRegisterationDTO;
import com.qdm.cg.clients.dto.SubscriptionsDTO;
import com.qdm.cg.clients.entity.ClientDetails;
import com.qdm.cg.clients.entity.Subscriptions;
import com.qdm.cg.clients.entity.UploadProfile;
import com.qdm.cg.clients.repository.SubscriptionsRepository;
import com.qdm.cg.clients.response.ResponseInfo;
import com.qdm.cg.clients.response.ResponseType;
import com.qdm.cg.clients.service.ClientService;
import com.qdm.cg.clients.service.SubscriptionsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = { "clients" })
@Slf4j
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })

public class ClientController {
	@Autowired
	ClientService clientsService;

	@Autowired
	SubscriptionsService subscriptionsService;

	@Autowired
	SubscriptionsRepository subscriptionsRepository;

	@PostMapping(value = "/registeration")
	public ResponseEntity<?> clientRegisteration(ClientRegisterationDTO clientDetails) {

		ResponseEntity response = null;
		try {
			ClientDetails clientRegisteration = clientsService.clientRegisteration(clientDetails);
			log.info("Client Created Successfully With ClientId : " + clientRegisteration.getId());
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.CREATED);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At Adding Client : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/list/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getAllClientsDetails(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize) {
		ResponseEntity response = null;
		try {
			List<ClientDetails> clientstotal = clientsService.getAllClientsDetails();
			List<ClientDetails> clients = clientsService.getAllClients(pageNo,pageSize);
			List<Object> list = new ArrayList<Object>();

			Map<String, Object> getClients=new HashMap<String, Object>();
			getClients.put("offset",pageNo);
			getClients.put("total_count",clientstotal.size());
			for (ClientDetails clientsData : clients) {
				String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
						.path("/clients/downloadFile/" + clientsData.getUploadPhoto().getId()).toUriString();

				Map<String, Object> map = new HashMap();
				map.put("id", clientsData.getId());
				map.put("name", clientsData.getName());
				map.put("is_active", clientsData.getIsActive());
				map.put("upcoming_activity", "Initial asessment");
				map.put("gender", clientsData.getGender());
				map.put("age", clientsData.getAge());
				map.put("gender", clientsData.getGender());
				map.put("contact", clientsData.getMobilenumber());
				map.put("status_type", "Needs medical attention");
				map.put("profile_pic", fileDownloadUri);
				map.put("email", clientsData.getEmailid());
				list.add(map);
				getClients.put("list",list);
			}

			log.info("Clients List " + list);
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", getClients), HttpStatus.OK);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At getClientsLists : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/details/get/{clientid}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getClientByClientId(@PathVariable("clientid") Integer clientid) {
		ResponseEntity response = null;
		try {
			ClientDetails clientsData = clientsService.getClientByClientId(clientid);

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/clients/downloadFile/" + clientsData.getUploadPhoto().getId()).toUriString();

			if (clientsData != null) {
				Set<Integer> subs = clientsData.getSubscriptions();
				ArrayList<Subscriptions> sub = new ArrayList<Subscriptions>();
				for (Integer integer : subs) {
					Subscriptions subb = subscriptionsRepository.findBySubscriptionId(integer);
					sub.add(subb);
				}

				Map<String, Object> bodyTemp = new HashMap();
				bodyTemp.put("vital_sign", "Body Temperature");
				bodyTemp.put("value", clientsData.getBodyTemperature());
				bodyTemp.put("measure_unit", "Celsius");
				bodyTemp.put("color_code", "#DC143C");

				Map<String, Object> bloodPressure = new HashMap();
				bloodPressure.put("vital_sign", "Blood Pressure");
				bloodPressure.put("value", clientsData.getBloodPressure());
				bloodPressure.put("measure_unit", "Normal");
				bloodPressure.put("color_code", "#008000");

				Map<String, Object> pulseRate = new HashMap();
				pulseRate.put("vital_sign", "Pulse Rate");
				pulseRate.put("value", clientsData.getPulseRate());
				pulseRate.put("measure_unit", "Bpm");
				pulseRate.put("color_code", "#008000");
				
				Map<String, Object> respiration = new HashMap();
				respiration.put("vital_sign", "Respiration Rate");
				respiration.put("value", clientsData.getRespirationRate());
				respiration.put("measure_unit", "Bpm");
				respiration.put("color_code", "#FCFC0B");

				List<Object> vitalsign = new ArrayList();
				vitalsign.add(bodyTemp);
				vitalsign.add(bloodPressure);
				vitalsign.add(pulseRate);
				vitalsign.add(respiration);

				Map<String, Object> emergency_contact = new HashMap();
				emergency_contact.put("contact_name", clientsData.getRelativeName());
				emergency_contact.put("mobile_no", clientsData.getRelativeMobilenumber());
				emergency_contact.put("mobile_no_isd_code", clientsData.getRelativeMobileISDcode());

				Map<String, Object> clientDetails = new HashMap();
				clientDetails.put("id", clientsData.getId());
				clientDetails.put("name", clientsData.getName());
				clientDetails.put("is_active", clientsData.getIsActive());
				clientDetails.put("upcoming_activity", "Initial asessment");
				clientDetails.put("age", clientsData.getAge());
				clientDetails.put("gender", clientsData.getGender());
				clientDetails.put("mobile_no_isd_code", clientsData.getMobilenumberISDcode());
				clientDetails.put("mobile_no", clientsData.getMobilenumber());
				clientDetails.put("status_type", "");
				clientDetails.put("profile_pic", fileDownloadUri);
				clientDetails.put("email", clientsData.getEmailid());
				clientDetails.put("user_type", "Pro");
				clientDetails.put("emergency_contact", emergency_contact);
				clientDetails.put("recent_vital_signs", vitalsign);
				clientDetails.put("subscriptions", sub);
				log.info("ClientById " + clientDetails);
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", clientDetails), HttpStatus.OK);
				return response;
			} else {
				log.info("No Clients Found with Id :  " + clientid);
				response = new ResponseEntity(new ResponseInfo(ResponseType.NOT_FOUND.getResponseMessage(),
						ResponseType.NOT_FOUND.getResponseCode(), "", null), HttpStatus.NOT_FOUND);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At getClientByClientId : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PostMapping(value = "/subscription/add", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> addSubscriptions(@RequestBody SubscriptionsDTO subscriptionsDTO) {
		ResponseEntity response = null;
		try {
			Subscriptions subscription = subscriptionsService.addSubscriptions(subscriptionsDTO);
			log.info("Subscription Created Successfully With subscriptionId : " + subscription.getSubscriptionId());
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.CREATED);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At AddSubscription : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@PutMapping(value = "/subscription/modify", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> updateClientSubscriptions(@RequestParam("clientId") Integer clientId,
			@RequestParam("subscriptionList") Set<Integer> subscriptionList) {
		ResponseEntity response = null;
		if (clientId == 0) {
			log.error("Client Id is Empty");
			response = new ResponseEntity(new ResponseInfo(ResponseType.BAD_REQUEST.getResponseMessage(),
					ResponseType.BAD_REQUEST.getResponseCode(), "", null), HttpStatus.BAD_REQUEST);
			return response;
		}
		try {
			ClientDetails clients = clientsService.updateClientSubscriptions(clientId, subscriptionList);
			if (clients != null) {
				log.info("Client Updated Successfully with Id " + clients.getId());
				response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
						ResponseType.SUCCESS.getResponseCode(), "", null), HttpStatus.OK);
				return response;
			} else {
				log.info("No Clients Found with Id :  " + clientId);
				response = new ResponseEntity(new ResponseInfo(ResponseType.NOT_FOUND.getResponseMessage(),
						ResponseType.NOT_FOUND.getResponseCode(), "", null), HttpStatus.NOT_FOUND);
				return response;
			}
		} catch (Exception e) {
			log.error("Error Occured At UpdateClientSubscriptions : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "Try Again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@GetMapping(value = "/subscription/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getSubscriptions() {
		ResponseEntity response = null;
		try {
			List<Subscriptions> subscriptions = subscriptionsService.getSubscriptions();
			log.info("Care Givers List " + subscriptions);
			response = new ResponseEntity(new ResponseInfo(ResponseType.SUCCESS.getResponseMessage(),
					ResponseType.SUCCESS.getResponseCode(), "", subscriptions), HttpStatus.OK);
			return response;
		} catch (Exception e) {
			log.error("Error Occured At getSubscriptionsList : " + e.getMessage());
			response = new ResponseEntity(new ResponseInfo(ResponseType.ERROR.getResponseMessage(),
					ResponseType.ERROR.getResponseCode(), "", null), HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}
	
	@GetMapping("/downloadFile/{fileId:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable int fileId, HttpServletRequest request) {
		UploadProfile databaseFile = clientsService.getFile(fileId);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(databaseFile.getFileType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + databaseFile.getFileName() + "\"")
				.body(new ByteArrayResource(databaseFile.getData()));
	}

}
