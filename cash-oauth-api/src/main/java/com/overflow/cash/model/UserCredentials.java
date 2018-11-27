package com.overflow.cash.model;

import com.overflow.cash.entity.Authority;
import com.overflow.cash.entity.User;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UserCredentials implements UserDetails, CredentialsContainer, Cloneable {
	private static final long serialVersionUID = 1L;
	private User user;
	private List<UserAuthority> authorities = new ArrayList<>();


	public UserCredentials(User userPrincipal) {
		this.user = userPrincipal;
		List<Authority> authorityList = userPrincipal.getUserAuthorityList();
		authorityList.forEach(item -> {
			String authority = item.getAuthority();
			authorities.add(new UserAuthority(authority));
		});
	}

	@Override
	public void eraseCredentials() {
		this.user.setHashedPassword(null);
	}

	@Override
	public List<UserAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return new String(user.getHashedPassword(), StandardCharsets.UTF_8);
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return user.getAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return user.getAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return user.getAccountNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return user.getEnabled();
	}

}
