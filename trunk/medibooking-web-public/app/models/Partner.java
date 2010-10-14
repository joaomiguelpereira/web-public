package models;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import models.enums.BusinessType;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.Model;

@Entity
public class Partner extends Model {
	
	@Required
    @MaxSize(60)
    @MinSize(4)
	private String name;
	
	@Required
	@Enumerated(EnumType.STRING)
	private BusinessType businessType;
	
	@Required
	@Embedded
	@Valid
	private Address address;
	
	public Partner(String name, BusinessType businessType, Address address) {
		super();
		this.address = address;
		this.name = name;
		this.setBusinessType(businessType);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}
	

}
