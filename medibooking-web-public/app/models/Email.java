package models;

import javax.persistence.Entity;

import play.data.validation.MaxSize;
import play.data.validation.Required;


@Entity
public class Email extends BaseModel {

	@Required
	@MaxSize(value=20)
	private String name;
	
	
	@play.data.validation.Email
	@Required
	private String email;
	@MaxSize(value=200)
	private String description;
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	
	
}
