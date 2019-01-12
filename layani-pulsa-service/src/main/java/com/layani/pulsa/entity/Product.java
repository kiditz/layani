package com.layani.pulsa.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import com.layani.pulsa.entity.Product;

@Entity
@Table(name = "ps_product")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public class Product {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PS_PRODUCT_ID_SEQ")
	@SequenceGenerator(name = "PS_PRODUCT_ID_SEQ", sequenceName = "ps_product_id_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	@Column(name = "name")
	@Basic(optional = false)
	private String name;
	@Column(name = "code")
	@Basic(optional = false)
	@Size(min = 1, max = 60)
	private String code;
	@Column(name = "nominal")
	@Basic(optional = false)
	private BigDecimal nominal;
	@Column(name = "provider_id")
	@Basic(optional = false)
	private Long providerId;
	@Column(name = "active")
	private Boolean active;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;
	@ManyToMany(mappedBy = "products")
	private Set<PartnerProduct> partnerProducts;

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
	public BigDecimal getNominal() {
		return nominal;
	}

	public void setNominal(BigDecimal nominal) {
		this.nominal = nominal;
	}

	@JsonProperty
	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
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
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	// @JsonProperty
	public Set<PartnerProduct> getPartnerProducts() {
		return partnerProducts;
	}

	public void setPartnerProducts(Set<PartnerProduct> partnerProducts) {
		this.partnerProducts = partnerProducts;
	}

}