package models;

import javax.persistence.Entity;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import validators.PhoneNumber;

@Entity
public class Phone extends BaseModel{

	@Required
	@MaxSize(value=20)
	private String name;
	
	@Required
	@PhoneNumber
	private String phone;

	@MaxSize(value=200)
	private String description;
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
