package com.overflow.cash.security;



import com.overflow.cash.entity.Client;
import org.slerp.core.Domain;
import org.slerp.core.business.BusinessFunction;
import com.overflow.cash.model.Oauth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
public class DefaultClientService implements ClientService {
	@Autowired
	private BusinessFunction findClientByClientId;

	@Override
	public ClientDetails loadClientByClientId(String clientId) {
		Domain inputDomain = new Domain();
		inputDomain.put("clientId", clientId);
		Domain resultDomain = findClientByClientId.handle(inputDomain).getDomain("client");
		if (resultDomain == null)
			throw new ClientRegistrationException("client.id.not.found");
		Client client = resultDomain.convertTo(Client.class);
		return new Oauth2Client(client);
	}
}
