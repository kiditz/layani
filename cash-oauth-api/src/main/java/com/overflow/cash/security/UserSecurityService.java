package com.overflow.cash.security;

import com.overflow.cash.model.UserCredentials;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserSecurityService extends UserDetailsService {
	UserCredentials loadUserByUsername(String username);
}
