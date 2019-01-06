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
@Table(name = "ps_order")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public class Order {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PS_ORDER_DETAIL_ID_SEQ")
	@SequenceGenerator(name = "PS_ORDER_DETAIL_ID_SEQ", sequenceName = "ps_order_detail_id_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	@Column(name = "msisdn")
	@Basic(optional = false)
	@Size(min = 1, max = 30)
	private String msisdn;
	@Column(name = "customer_id")
	private Long customerId;
	@Column(name = "outlet_id")
	@Basic(optional = false)
	private Long outletId;
	@Column(name = "sales_type")
	@Basic(optional = false)
	@Size(min = 1, max = 20)
	private String salesType;
	@Column(name = "reqid")
	private String reqid;
	@Column(name = "sell_price")
	@Basic(optional = false)
	private BigDecimal sellPrice;
	@Column(name = "purchase_price")
	@Basic(optional = false)
	private BigDecimal purchasePrice;
	@Column(name = "status")
	@Basic(optional = false)
	@Size(min = 1, max = 1)
	private String status;
	@Column(name = "order_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date orderAt;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;
	@Column(name = "remark")
	@Basic(optional = false)
	private String remark;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "sn")
	private String sn;

	@JsonProperty
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@JsonProperty
	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	@JsonProperty
	public Long getOutletId() {
		return outletId;
	}

	public void setOutletId(Long outletId) {
		this.outletId = outletId;
	}

	@JsonProperty
	public String getSalesType() {
		return salesType;
	}

	public void setSalesType(String salesType) {
		this.salesType = salesType;
	}

	@JsonProperty
	public String getReqid() {
		return reqid;
	}

	public void setReqid(String reqid) {
		this.reqid = reqid;
	}

	@JsonProperty
	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	@JsonProperty
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@JsonProperty
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty
	public Date getOrderAt() {
		return orderAt;
	}

	public void setOrderAt(Date orderAt) {
		this.orderAt = orderAt;
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
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@JsonProperty
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@JsonProperty
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

}