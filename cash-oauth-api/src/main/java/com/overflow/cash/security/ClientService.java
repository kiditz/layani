package com.overflow.cash.security;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface ClientService extends ClientDetailsService {
	@Override
	public ClientDetails loadClientByClientId(String clientId);
}
