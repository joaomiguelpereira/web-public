package models;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Embeddable
public class Address{
	
	@Required
	@MaxSize(100)
	@MinSize(5)
	private String addressLineOne;
	
	@MaxSize(100)
	@MinSize(4)
	private String addressLineTwo;
	
	@Required
	@MaxSize(60)
	@MinSize(8)
	private String postalCode;

	@Required
	@MaxSize(50)
	@MinSize(4)
	private String city;
		
	@MaxSize(50)
	@MinSize(2)
	private String country;

	public void setAddressLineOne(String addressLineOne) {
		this.addressLineOne = addressLineOne;
	}

	public String getAddressLineOne() {
		return addressLineOne;
	}

	public void setAddressLineTwo(String addressLineTwo) {
		this.addressLineTwo = addressLineTwo;
	}

	public String getAddressLineTwo() {
		return addressLineTwo;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}


}
