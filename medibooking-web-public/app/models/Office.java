package models;

import java.util.ArrayList;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;

import models.enums.BusinessType;

import play.Logger;
import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.data.validation.Valid;
import play.db.jpa.Model;
import validators.PhoneNumber;
import validators.Unique;
import validators.UniqueCheck;

@Entity
public class Office extends BaseModel {
	
	{
		this.administrators = new ArrayList<OfficeAdministrator>();
	}
	
	@Required
	@Unique
    @MaxSize(60)
    @MinSize(4)
	private String name;
	
	@Required
	@Enumerated(EnumType.STRING)
	private BusinessType businessType;
	
	@Required
	@PhoneNumber
	private String phone1;

	
	@PhoneNumber
	private String phone2;
	
	@PhoneNumber
	private String fax;
	
	@PhoneNumber
	private String mobile1;
	
	@PhoneNumber
	private String mobile2;
	
	@Email
	@Unique
	private String email;
	
	@URL
	@Unique
	private String url;
	
	private Boolean active;
	
	@ManyToMany
	private List<OfficeAdministrator> administrators;
	
	@Required
	@Embedded
	@Valid
	private Address address;
	
	
	@SuppressWarnings("unused")
	@PrePersist
	private void beforeSave() {
		if ( !this.isPersistent() ) {
			this.setActive(false);
		}
		
	}
	/**
	 * Default constructor
	 */
	public Office() {
		
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

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFax() {
		return fax;
	}

	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}

	public String getMobile1() {
		return mobile1;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean isActive() {
		return active;
	}
	public void setAdministrators(List<OfficeAdministrator> administrators) {
		this.administrators = administrators;
	}
	public List<OfficeAdministrator> getAdministrators() {
		return administrators;
	}
	public void addAdministrator(OfficeAdministrator oa) {
		this.administrators.add(oa);
		
	}
	
	public int getAdminCount() {
		return this.administrators.size();
	}
	
	

	
	

}
