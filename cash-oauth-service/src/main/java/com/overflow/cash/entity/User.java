package com.overflow.cash.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "co_user")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "co_user_id_seq")
	@SequenceGenerator(name = "co_user_id_seq", sequenceName = "co_user_id_seq", allocationSize = 1)
	private Long id;
	@Basic(optional = false)
	@Column(name = "phone_number")
	@Size(min = 1, max = 20)
	private String phoneNumber;
	@Basic(optional = false)
	@Column(name = "username")
	@Size(min = 1, max = 60)
	private String username;
	@Basic(optional = false)
	@Column(name = "fullname")
	private String fullName;
	@Basic(optional = false)
	@Column(name = "hash_password")
	private byte[] hashedPassword;
	@Column(name = "account_non_expired")
	private Boolean accountNonExpired;
	@Column(name = "account_non_locked")
	private Boolean accountNonLocked;
	@Column(name = "credentials_non_expired")
	private Boolean credentialsNonExpired;
	@Column(name = "enabled")
	private Boolean enabled;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.EAGER)
	private List<Authority> userAuthorityList;

	public User() {
	}

	@JsonProperty
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@JsonProperty
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@JsonProperty
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@JsonProperty
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonProperty
	public byte[] getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(byte[] hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	@JsonProperty
	public Boolean getAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	@JsonProperty
	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@JsonProperty
	public Boolean getCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	@JsonProperty
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@JsonProperty
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@JsonProperty
	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}


	@JsonProperty
	public List<Authority> getUserAuthorityList() {
		return userAuthorityList;
	}

	public void setUserAuthorityList(List<Authority> userAuthorityList) {
		this.userAuthorityList = userAuthorityList;
	}
}