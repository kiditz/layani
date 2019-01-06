package com.layani.pulsa.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.layani.pulsa.entity.PartnerProduct;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

@Entity
@Table(name = "ps_partner_product")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public class PartnerProduct {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PS_PARTNER_PRODUCT_ID_SEQ")
	@SequenceGenerator(name = "PS_PARTNER_PRODUCT_ID_SEQ", sequenceName = "ps_partner_product_id_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	@Column(name = "name")
	@Basic(optional = false)
	private String name;
	@Column(name = "code")
	@Basic(optional = false)
	@Size(min = 1, max = 60)
	private String code;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;
	@ManyToOne
	@JoinColumn(name = "partner_id", referencedColumnName = "id")
	private Partner partner;
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "ps_partner_product_has_product", joinColumns = @JoinColumn(name = "partner_product_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
	private Set<Product> products;

	@JsonProperty
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public Set<Product> getProducts() {
		return products;
	}

	@JsonProperty
	public Partner getPartner() {
		return partner;
	}
}