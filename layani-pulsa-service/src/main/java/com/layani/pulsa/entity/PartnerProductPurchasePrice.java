package com.layani.pulsa.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ps_partner_product_purchase_price")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public class PartnerProductPurchasePrice {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PS_PARTNER_PRODUCT_PURCHASE_PRICE_ID_SEQ")
	@SequenceGenerator(name = "PS_PARTNER_PRODUCT_PURCHASE_PRICE_ID_SEQ", sequenceName = "ps_partner_product_purchase_price_id_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	@Column(name = "sell_price")
	@Basic(optional = false)
	private BigDecimal sellPrice;
	@Column(name = "flg_tax")
	@Basic(optional = false)
	@Size(min = 1, max = 1)
	private String flgTax;
	@Column(name = "tax_percentage")
	@Basic(optional = false)
	private BigDecimal taxPercentage;
	@Column(name = "start_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startAt;
	@Column(name = "end_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endAt;
	@Column(name = "active")
	@Basic(optional = false)
	private Boolean active;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;

	@ManyToOne
	@JoinColumn(name = "partner_product_id", referencedColumnName = "id")
	private PartnerProduct partnerProductId;

	@JsonProperty
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty
	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	@JsonProperty
	public String getFlgTax() {
		return flgTax;
	}

	public void setFlgTax(String flgTax) {
		this.flgTax = flgTax;
	}

	@JsonProperty
	public BigDecimal getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(BigDecimal taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	@JsonProperty
	public Date getStartAt() {
		return startAt;
	}

	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}

	@JsonProperty
	public Date getEndAt() {
		return endAt;
	}

	public void setEndAt(Date endAt) {
		this.endAt = endAt;
	}

	@JsonProperty
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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
	public PartnerProduct getPartnerProductId() {
		return partnerProductId;
	}

	public void setPartnerProductId(PartnerProduct partnerProductId) {
		this.partnerProductId = partnerProductId;
	}
}