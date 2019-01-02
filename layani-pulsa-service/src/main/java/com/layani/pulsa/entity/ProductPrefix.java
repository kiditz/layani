package com.layani.pulsa.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "ps_product_prefix")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public class ProductPrefix {
	@ManyToOne
	@JoinColumn(name = "provider_prefix_id", referencedColumnName = "id")
	private Provider providerPrefixId;
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product productId;

	@JsonProperty
	public Provider getProviderPrefixId() {
		return providerPrefixId;
	}

	public void setProviderPrefixId(Provider providerPrefixId) {
		this.providerPrefixId = providerPrefixId;
	}

	@JsonProperty
	public Product getProductId() {
		return productId;
	}

	public void setProductId(Product productId) {
		this.productId = productId;
	}
}