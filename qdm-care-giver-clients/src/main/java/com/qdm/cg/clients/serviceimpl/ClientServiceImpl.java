package com.qdm.cg.clients.serviceimpl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qdm.cg.clients.dto.ClientRegisterationDTO;
import com.qdm.cg.clients.entity.ClientDetails;
import com.qdm.cg.clients.entity.Subscriptions;
import com.qdm.cg.clients.entity.UploadProfile;
import com.qdm.cg.clients.repository.ClientDetailsRepository;
import com.qdm.cg.clients.repository.SubscriptionsRepository;
import com.qdm.cg.clients.repository.UploadProfileRepository;
import com.qdm.cg.clients.service.ClientService;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

	@Autowired
	ClientDetailsRepository clientDetailsRepository;

	@Autowired
	SubscriptionsRepository subscriptionsRepository;

	@Autowired
	ModelMapper modelMapper;

	@Override
	public ClientDetails clientRegisteration(ClientRegisterationDTO clientDetails) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dobDate = formatter.format(clientDetails.getDob());
		try {
			Date d = formatter.parse(dobDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int date = c.get(Calendar.DATE);
			LocalDate l1 = LocalDate.of(year, month, date);
			LocalDate now1 = LocalDate.now();
			Period diff1 = Period.between(l1, now1);
			clientDetails.setAge(diff1.getYears());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ClientDetails clientsReg = modelMapper.map(clientDetails, ClientDetails.class);
		String fileName = StringUtils.cleanPath(clientDetails.getUploadPhoto().getOriginalFilename());
		UploadProfile uploadProfile = null;
		try {
			uploadProfile = UploadProfile.builder().fileName(fileName)
					.fileType(clientDetails.getUploadPhoto().getContentType())
					.data(clientDetails.getUploadPhoto().getBytes()).size(clientDetails.getUploadPhoto().getSize())
					.build();
		} catch (IOException e) {
			System.out.println(e);
		}
		clientsReg.setUploadPhoto(uploadProfile);
		return clientDetailsRepository.save(clientsReg);
	}

	@Override
	public List<ClientDetails> getAllClientsDetails() {
		return clientDetailsRepository.findAll();
	}

	@Override
	public ClientDetails getClientByClientId(Integer clientid) {
		Optional<ClientDetails> client = clientDetailsRepository.findById(clientid);
		if (client.isPresent()) {
			return client.get();
		} else {
			return null;
		}
	}

	@Override
	public ClientDetails updateClientSubscriptions(Integer clientId, Set<Integer> subscriptionList) {
		Optional<ClientDetails> client = clientDetailsRepository.findById(clientId);
		Set<Integer> subscriptionsList = new HashSet<>();
		if (client.isPresent()) {
			for (Integer subscriptions : client.get().getSubscriptions()) {
				subscriptionsList.add(subscriptions);
			}
		} else {
			return null;
		}
		for (Integer subscriptionListId : subscriptionList) {
			Subscriptions sub = subscriptionsRepository.findBySubscriptionId(subscriptionListId);
			subscriptionsList.add(sub.getSubscriptionId());
		}
		client.get().setSubscriptions(subscriptionsList);
		return clientDetailsRepository.save(client.get());
	}

	@Autowired
	private UploadProfileRepository dbFileRepository;

	public UploadProfile getFile(int fileId) {
		return dbFileRepository.findById(fileId).get();
	}

	@Override
	public List<ClientDetails> getAllClients(Integer pageNo, Integer pageSize) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		Page<ClientDetails> pagedResult = clientDetailsRepository.findAll(paging);
		return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<ClientDetails>();
	}

}
