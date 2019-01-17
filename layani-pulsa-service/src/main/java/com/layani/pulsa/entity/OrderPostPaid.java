package com.layani.pulsa.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Id;
import javax.persistence.Column;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.validation.constraints.Size;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ps_order_post_paid")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public class OrderPostPaid {

	@Id
	@Column(name = "order_id")
	private Long orderId;
	@Column(name = "adm_cost")
	@Basic(optional = false)
	private BigDecimal admCost;
	@Column(name = "customer_name")
	@Basic(optional = false)
	private String customerName;
	@Column(name = "num_of_trx")
	@Basic(optional = false)
	private Long numOfTrx;
	@Column(name = "post_paid_amount")
	@Basic(optional = false)
	private BigDecimal postPaidAmount;
	@Column(name = "post_paid_month")
	@Size(min = 1, max = 20)
	private String postPaidMonth;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;
	@Column(name = "bill_amount")
	@Basic(optional = false)
	private BigDecimal billAmount;

	@JsonProperty
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@JsonProperty
	public BigDecimal getAdmCost() {
		return admCost;
	}

	public void setAdmCost(BigDecimal admCost) {
		this.admCost = admCost;
	}

	@JsonProperty
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@JsonProperty
	public Long getNumOfTrx() {
		return numOfTrx;
	}

	public void setNumOfTrx(Long numOfTrx) {
		this.numOfTrx = numOfTrx;
	}

	@JsonProperty
	public BigDecimal getPostPaidAmount() {
		return postPaidAmount;
	}

	public void setPostPaidAmount(BigDecimal postPaidAmount) {
		this.postPaidAmount = postPaidAmount;
	}

	@JsonProperty
	public String getPostPaidMonth() {
		return postPaidMonth;
	}

	public void setPostPaidMonth(String postPaidMonth) {
		this.postPaidMonth = postPaidMonth;
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
	public BigDecimal getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}
}