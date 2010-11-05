package models;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import models.enums.UserType;

import play.Logger;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Codec;
import sun.security.provider.MD5;
import validators.PhoneNumber;
import validators.Unique;

@Entity
public class User extends Model {

	{
		setUserType(UserType.USER);
		setActive(false);
		
	}
	
	@Required
	@MaxSize(60)
	@MinSize(6)
	private String name;
	
	@Required
	private Boolean active;
	
	@Required
	@Email
	@Unique(message="validation.email.unique")
	private String email;
	
	@Transient
	@Required
	@MinSize(5)
	private String password;
	
	private String passwordHash;
	
	@PhoneNumber
	private String phone;
	
	@PhoneNumber
	private String mobile;
	
	private UserType userType;
	
	private String activationUUID;
	
		
	
	@SuppressWarnings("unused")
	@PrePersist
	private void prepareNewUser() {
		this.passwordHash = Codec.hexMD5(this.password+this.email);
		this.activationUUID = UUID.randomUUID().toString();
	}
	
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

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public UserType getUserType() {
		return userType;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Boolean isActive() {
		return active;
	}

	public void setActivationUUID(String activationUID) {
		this.activationUUID = activationUID;
	}

	public String getActivationUUID() {
		return activationUUID;
	}
}
