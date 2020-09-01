package com.qdm.cg.clients.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.qdm.cg.clients.dto.ClientActivityDto;
import com.qdm.cg.clients.dto.ClientActivityResponse;
import com.qdm.cg.clients.dto.ClientActivitySummaryDto;
import com.qdm.cg.clients.dto.ClientInfoDto;
import com.qdm.cg.clients.dto.ClientReportResponse;
import com.qdm.cg.clients.dto.EquipmentDto;
import com.qdm.cg.clients.dto.IssueDetailDto;
import com.qdm.cg.clients.dto.IssueDto;
import com.qdm.cg.clients.dto.IssueListResponse;
import com.qdm.cg.clients.dto.IssueStatus;
import com.qdm.cg.clients.dto.ProductRatingDto;
import com.qdm.cg.clients.dto.ProductRatingResponse;
import com.qdm.cg.clients.dto.ProductsDto;
import com.qdm.cg.clients.dto.RecommendationsDto;
import com.qdm.cg.clients.dto.RecommendationsTrackResponse;
import com.qdm.cg.clients.dto.RecommendedProductsDto;
import com.qdm.cg.clients.dto.RecommendedProductsResponse;
import com.qdm.cg.clients.dto.ReportsDto;
import com.qdm.cg.clients.dto.TimeLine;
import com.qdm.cg.clients.entity.Activity;
import com.qdm.cg.clients.entity.ClientDetails;
import com.qdm.cg.clients.entity.Equipment;
import com.qdm.cg.clients.entity.Issues;
import com.qdm.cg.clients.entity.Product;
import com.qdm.cg.clients.entity.Reports;
import com.qdm.cg.clients.enums.ManageClientsConstants;
import com.qdm.cg.clients.enums.StatusEnum;
import com.qdm.cg.clients.exceptionhandler.NoIssueFoundException;
import com.qdm.cg.clients.repository.ActivityRepository;
import com.qdm.cg.clients.repository.ClientDetailsRepository;
import com.qdm.cg.clients.repository.EquipmentRepository;
import com.qdm.cg.clients.repository.IssuesRepository;
import com.qdm.cg.clients.repository.ProductRepository;
import com.qdm.cg.clients.repository.ReportsRepository;
import com.qdm.cg.clients.response.ResponseInfo;

@Service
public class ManageClientService {

	@Autowired
	ModelMapper modelMapper;
	@Autowired
	IssuesRepository issuesRepository;
	@Autowired
	ReportsRepository reportsRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	EquipmentRepository equipmentRepository;
	@Autowired
	ActivityRepository activityRepository;

	@Autowired
	ClientDetailsRepository clientDetailsRepository;

	public ResponseInfo getClientReport(long clientId, Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		List<ReportsDto> reportsList = new ArrayList<ReportsDto>();
		Page<Reports> pagedResult = reportsRepository.findByClientId(clientId, paging);
		List<Reports> entityList = pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<Reports>();
		for (Reports entity : entityList) {
			reportsList.add(modelMapper.map(entity, ReportsDto.class));
		}
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(ClientReportResponse.builder().reports(reportsList).total_reports(reportsList.size()).build())
				.build();
	}

	public ResponseInfo getIssueDetail(long issueId) {
		Issues isssue = issuesRepository.findById(issueId)
				.orElseThrow(() -> new NoIssueFoundException(issueId + " Issue ID not found."));
		Long id = isssue.getClientId();

		ClientDetails clientDetails = clientDetailsRepository.findById(id.intValue())
				.orElseThrow(() -> new NoIssueFoundException("No ID found"));

		// System.out.println("check"+clientDetails.toString());
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/clients/downloadFile/" + clientDetails.getUploadPhoto().getId()).toUriString();

		IssueDetailDto issueDetails = modelMapper.map(isssue, IssueDetailDto.class);
		issueDetails.setProfile_pic(fileDownloadUri);
		return ResponseInfo.builder().status("Success").status_code(200).message("").data(issueDetails).build();
	}

	public ResponseInfo getIssueList(long clientId, Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		int openCount = 0, resolvedCount = 0, pendingCount = 0;
		Page<Issues> pagedResult = issuesRepository.findByClientId(clientId, paging);
		List<Issues> issueList = pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<Issues>();
		List<IssueDto> issuedto = new ArrayList<>();
		for (Issues entity : issueList) {
			issuedto.add(modelMapper.map(entity, IssueDto.class));
		}
		for (IssueDto dto : issuedto) {

			if (null != dto.getIssue_status()
					&& dto.getIssue_status().equalsIgnoreCase(ManageClientsConstants.open_status)) {
				openCount++;
			} else if (null != dto.getIssue_status()
					&& dto.getIssue_status().equalsIgnoreCase(ManageClientsConstants.resolved_status)) {
				resolvedCount++;
			} else if (null != dto.getIssue_status()
					&& dto.getIssue_status().equalsIgnoreCase(ManageClientsConstants.pending_status)) {
				pendingCount++;
			}
		}
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(IssueListResponse.builder().open_count(openCount).pending_count(pendingCount)
						.resolved_count(resolvedCount).issues_enum(StatusEnum.values()).issue_list(issuedto).build())
				.build();

	}

	public ResponseInfo modifyIssueStatus(IssueStatus issueStatus) {

		Issues issues = issuesRepository.findById(issueStatus.getIssue_id())
				.orElseThrow(() -> new NoIssueFoundException(issueStatus.getIssue_id() + "  Issue ID was Not found."));

		issues.setIssue_status(issueStatus.getIssue_status());
		issuesRepository.save(issues);
		return ResponseInfo.builder().status("Success").status_code(200).message("").build();

	}

	public ResponseInfo getRecommendations(long clientId, Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<Equipment> pagedResult = equipmentRepository.findByClientId(clientId, paging);
		List<Equipment> equipments = pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<Equipment>();
		List<EquipmentDto> equipmentList = new ArrayList<EquipmentDto>();
		for (Equipment equipment : equipments) {
			equipmentList.add(modelMapper.map(equipment, EquipmentDto.class));
		}
		Page<Product> pagedProdResult = productRepository.findByClientId(clientId, paging);
		List<Product> products = pagedProdResult.hasContent() ? pagedProdResult.getContent() : new ArrayList<Product>();
		List<ProductsDto> productList = new ArrayList<ProductsDto>();
		for (Product product : products) {
			productList.add(modelMapper.map(product, ProductsDto.class));
		}
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(RecommendationsDto.builder().equipments(equipmentList).products(productList).build()).build();

	}

	public ResponseInfo getProductRatings(long clientId) {
		List<Product> productsList = productRepository.findByClientId(clientId);
		List<ProductRatingDto> productRatings = new ArrayList<ProductRatingDto>();
		for (Product product : productsList) {
			productRatings.add(modelMapper.map(product, ProductRatingDto.class));
		}
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(ProductRatingResponse.builder().ratings_list(productRatings).build()).build();

	}

	public ResponseInfo getRecommendedProductList(long clientId, Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);

		Page<Product> pagedProdResult = productRepository.findByClientId(clientId, paging);
		List<Product> products = pagedProdResult.hasContent() ? pagedProdResult.getContent() : new ArrayList<Product>();
		List<RecommendedProductsDto> recommendedProductList = new ArrayList<RecommendedProductsDto>();
		for (Product product : products) {
			recommendedProductList.add(modelMapper.map(product, RecommendedProductsDto.class));
		}
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(RecommendedProductsResponse.builder().recommended_products_list(recommendedProductList).build())
				.build();
	}

	public ResponseInfo getClientActivity(String event, long clientId, Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<Activity> pagedResult = activityRepository.findByClientId(clientId, paging);
		List<Activity> activityList = pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<Activity>();
		;
		List<ClientActivityDto> activityDtoList = new ArrayList<ClientActivityDto>();
		for (Activity activity : activityList) {
			activityDtoList.add(modelMapper.map(activity, ClientActivityDto.class));
		}

		if (event == null) {
			return ResponseInfo.builder().status("Success").status_code(200).message("")
					.data(ClientActivityResponse.builder().activities(activityDtoList).build()).build();
		}
		List<ClientActivityDto> pastActivityList = new ArrayList<ClientActivityDto>();
		List<ClientActivityDto> upcomingActivityList = new ArrayList<ClientActivityDto>();
		String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
		SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		for (ClientActivityDto activity : activityDtoList) {
			try {
				Date d1 = sdfo.parse(date);
				Date d2 = sdfo.parse(activity.getDate_time());
				if (d1.compareTo(d2) > 0) {
					pastActivityList.add(activity);
				} else {
					upcomingActivityList.add(activity);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if ("past".equals(event)) {
			return ResponseInfo.builder().status("Success").status_code(200).message("")
					.data(ClientActivityResponse.builder().activities(pastActivityList).build()).build();

		} else if ("upcoming".equals(event)) {
			return ResponseInfo.builder().status("Success").status_code(200).message("")
					.data(ClientActivityResponse.builder().activities(upcomingActivityList).build()).build();

		}
		return null;

	}

	public ResponseInfo getActivitySummary(long activityId) {
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new NoIssueFoundException("No ID found"));

		ClientDetails clientDetails = clientDetailsRepository.findById((int) activity.getClientId())
				.orElseThrow(() -> new NoIssueFoundException("No ID found"));

		ClientInfoDto clientInfo = modelMapper.map(clientDetails, ClientInfoDto.class);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/clients/downloadFile/" + clientDetails.getUploadPhoto().getId()).toUriString();
		clientInfo.setClient_name(clientDetails.getName());
		clientInfo.setGender(clientDetails.getGender().name());
		clientInfo.setMobile_no(String.valueOf(clientDetails.getMobilenumber()));
		clientInfo.setProfile_pic(fileDownloadUri);

		ClientActivitySummaryDto clientSummary = modelMapper.map(activity, ClientActivitySummaryDto.class);
		clientSummary.setClient_name(clientDetails.getName());
		clientSummary.setClient_info(clientInfo);

		return ResponseInfo.builder().status("Success").status_code(200).message("").data(clientSummary).build();

	}

	// done

	public ResponseInfo getRecommendedProductTrack(long productId) {
		RecommendationsTrackResponse response = new RecommendationsTrackResponse();
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new NoIssueFoundException("Product ID not found"));

		response = modelMapper.map(product, RecommendationsTrackResponse.class);
		List<TimeLine> timeLine = new ArrayList<TimeLine>();
		timeLine.add(new TimeLine("Recommended", "2020-08-26T05:27:51Z", true));
		timeLine.add(new TimeLine("Consent Received", "2020-08-27T05:27:51Z", true));
		timeLine.add(new TimeLine("Purchased", "2020-08-28T05:27:51Z", true));
		timeLine.add(new TimeLine("Delivered", "2020-08-29T05:27:51Z", false));
		timeLine.add(new TimeLine("Demoed", "2020-08-31T05:27:51Z", false));
		response.setTimeline(timeLine);
		return ResponseInfo.builder().status("Success").status_code(200).message("").data(response).build();

	}

	public void addProduct(Product product) {
		productRepository.save(product);
	}

	public void addEquipment(Equipment equipment) {
		equipmentRepository.save(equipment);
	}

	public void addIssues(Issues issue) {
		issuesRepository.save(issue);
	}

	public void addReports(Reports reports) {
		reportsRepository.save(reports);
	}

	public void addActivity(Activity activity) {
		activityRepository.save(activity);
	}

}