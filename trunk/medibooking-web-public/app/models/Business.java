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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;


import play.Logger;
import play.data.validation.CheckWith;
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
public class Business extends BaseModel {
	
	{
		this.administrators = new ArrayList<BusinessAdministrator>();
	}
	
	
	@Required
	@Unique
    @MaxSize(60)
    @MinSize(4)
	private String name;
	
	@MaxSize(500)
	private String shortIntroduction;
	
	private Boolean active;
	
	@OneToMany
	private List<Email> emails;
	
	@OneToMany
	private List<Phone> phones;
	
	@OneToMany
	private List<WebSite> webSites;
	
	@ManyToMany
	private List<BusinessAdministrator> administrators;
	
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
	public Business() {
		
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}


	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean isActive() {
		return active;
	}
	public void setAdministrators(List<BusinessAdministrator> administrators) {
		this.administrators = administrators;
	}
	public List<BusinessAdministrator> getAdministrators() {
		return administrators;
	}
	public void addAdministrator(BusinessAdministrator oa) {
		this.administrators.add(oa);
		
	}
	
	public int getAdminCount() {
		return this.administrators.size();
	}
	public void setShortIntroduction(String shortIntroduction) {
		this.shortIntroduction = shortIntroduction;
	}
	public String getShortIntroduction() {
		return shortIntroduction;
	}
	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}
	public List<Email> getEmails() {
		return emails;
	}
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}
	public List<Phone> getPhones() {
		return phones;
	}
	public void setWebSites(List<WebSite> webSites) {
		this.webSites = webSites;
	}
	public List<WebSite> getWebSites() {
		return webSites;
	}
	
	

	
	

}
