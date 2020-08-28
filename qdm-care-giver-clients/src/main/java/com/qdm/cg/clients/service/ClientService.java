package com.qdm.cg.clients.service;

import java.util.List;
import java.util.Set;

import com.qdm.cg.clients.dto.ClientRegisterationDTO;
import com.qdm.cg.clients.entity.ClientDetails;
import com.qdm.cg.clients.entity.UploadProfile;

public interface ClientService {

	ClientDetails clientRegisteration(ClientRegisterationDTO clientDetails);

	List<ClientDetails> getAllClientsDetails();

	ClientDetails getClientByClientId(Integer clientid);

	ClientDetails updateClientSubscriptions(Integer clientId, Set<Integer> subscriptionList);

	UploadProfile getFile(int fileId);

	List<ClientDetails> getAllClients(Integer pageNo, Integer pageSize);
}
