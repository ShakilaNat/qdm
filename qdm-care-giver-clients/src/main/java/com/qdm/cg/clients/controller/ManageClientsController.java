package com.qdm.cg.clients.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qdm.cg.clients.dto.IssueStatus;
import com.qdm.cg.clients.entity.Activity;
import com.qdm.cg.clients.entity.Equipment;
import com.qdm.cg.clients.entity.Issues;
import com.qdm.cg.clients.entity.Product;
import com.qdm.cg.clients.entity.Reports;
import com.qdm.cg.clients.response.ResponseInfo;
import com.qdm.cg.clients.serviceimpl.ManageClientService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/clients")
@Slf4j
public class ManageClientsController {
	@Autowired
	ManageClientService manageClientService;

	@GetMapping("/reports/get")
	public ResponseEntity<?> getReports(@RequestParam long clientId, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize) {
		log.info("Reports for Client ID " + clientId);
		ResponseInfo responseinfo = manageClientService.getClientReport(clientId, pageNo, pageSize);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/issues/list/get")
	public ResponseEntity<?> getIssuesList(@RequestParam long clientId,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
		ResponseInfo responseinfo = manageClientService.getIssueList(clientId, pageNo, pageSize);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/issues/detail/get")
	public ResponseEntity<?> getIssueDetail(@RequestParam long issueId) {
		ResponseInfo responseinfo = manageClientService.getIssueDetail(issueId);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@PutMapping("/issues/status/modify")
	public ResponseEntity<?> getDetailsList(@RequestBody IssueStatus issueStatus) {
		ResponseInfo responseinfo = manageClientService.modifyIssueStatus(issueStatus);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/recommendations/details/get")
	public ResponseEntity<?> getRecommendationsDetailsList(@RequestParam long clientId,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
		ResponseInfo responseinfo = manageClientService.getRecommendations(clientId, pageNo, pageSize);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/recommendations/products/list/get")
	public ResponseEntity<?> getRecommendationsProductsList(@RequestParam long clientId,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
		ResponseInfo responseinfo = manageClientService.getRecommendedProductList(clientId, pageNo, pageSize);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/recommendations/products/track/get")
	public ResponseEntity<?> getRecommendationsProductsTrack(@RequestParam String clientId,@RequestParam long productId) {
		ResponseInfo responseinfo = manageClientService.getRecommendedProductTrack(productId);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/products/ratings/get")
	public ResponseEntity<?> getRatingsList(@RequestParam long clientId) {
		ResponseInfo responseinfo = manageClientService.getProductRatings(clientId);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/activities/summary/get")
	public ResponseEntity<?> getActivitySummary(@RequestParam long activityId) {
		ResponseInfo responseinfo = manageClientService.getActivitySummary(activityId);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/activities/get")
	public ResponseEntity<?> getActivity(@RequestParam long clientId, @RequestParam(required = false) String event,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
		ResponseInfo responseinfo = manageClientService.getClientActivity(event, clientId, pageNo, pageSize);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@PostMapping("/add/product")
	public void addProduct(@RequestBody Product product) {
		manageClientService.addProduct(product);
	}

	@PostMapping("/add/equipment")
	public void addEquipment(@RequestBody Equipment equipment) {
		manageClientService.addEquipment(equipment);
	}

	@PostMapping("/add/issues")
	public void addIssues(@RequestBody Issues issue) {
		manageClientService.addIssues(issue);
	}

	@PostMapping("/add/reports")
	public void addReports(@RequestBody Reports reports) {
		manageClientService.addReports(reports);
	}

	@PostMapping("/add/activity")
	public void addActivity(@RequestBody Activity activity) {
		manageClientService.addActivity(activity);
	}

}
