package com.overflow.cash.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author kiditz
 *
 */
@Entity
@Table(name = "co_client")
public class Client implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "co_client_id_seq")
	@SequenceGenerator(name = "co_client_id_seq", sequenceName = "co_client_id_seq", allocationSize = 1)
	private Long id;
	@Column(name = "client_id")
	private String clientId;
	@Column(name = "client_secret")
	private String clientSecret;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "co_client_scope", joinColumns = {@JoinColumn(name = "client_id", referencedColumnName = "client_id")})
	@Column(name = "scope")
	private Set<String> scope;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "co_client_grant", joinColumns = {@JoinColumn(name = "client_id", referencedColumnName = "client_id")})
	@Column(name = "grant_name")
	private Set<String> authorizedGrantTypes;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "co_client_redirect", joinColumns = {@JoinColumn(name = "client_id", referencedColumnName = "client_id")})
	@Column(name = "redirect_uri")
	private Set<String> registeredRedirectUri;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public Set<String> getScope() {
		return scope;
	}

	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	public Set<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	public Set<String> getRegisteredRedirectUri() {
		return registeredRedirectUri;
	}

	public void setRegisteredRedirectUri(Set<String> registeredRedirectUri) {
		this.registeredRedirectUri = registeredRedirectUri;
	}

}
