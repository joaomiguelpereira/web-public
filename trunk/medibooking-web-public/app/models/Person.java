package models;

import javax.persistence.Entity;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import validators.PhoneNumber;
import validators.Unique;

@Entity
public class Person extends Model {

	@Required
	@MaxSize(60)
	@MinSize(6)
	private String name;
	
	@Required
	@Email
	@Unique
	private String email;
	
	@Required
	@MinSize(5)
	private String password;
	
	@PhoneNumber
	private String phone;
	
	@PhoneNumber
	private String mobile;

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

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}
}
