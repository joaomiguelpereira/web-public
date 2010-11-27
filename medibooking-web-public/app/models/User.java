package models;

import java.util.UUID;

import javax.persistence.Embedded;
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

	
	private String resetPasswordSecret;
	
	@Required
	@MaxSize(60)
	@MinSize(6)
	private String name;

	@Embedded
	private LoginInformation loginInformation;

	@Required
	private Boolean active;

	@Required
	@Email
	@Unique(message = "validation.email.unique")
	private String email;

	@Transient
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
		// do it if it's a new entity
		if (!this.isPersistent()) {
			
			
			this.passwordHash = generatePasswordHash(this.password, this.email);
			this.setActivationUUID(UUID.randomUUID().toString());
			this.setLoginInformation(new LoginInformation());
		}
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

	public void setActivationUUID(String activationUUID) {
		this.activationUUID = activationUUID;
	}

	public String getActivationUUID() {
		return activationUUID;
	}

	/**
	 * Authenticate a user based on password
	 * 
	 * @param password
	 *            The password
	 * @param clientIP
	 *            The ip where the request was made
	 * @return an authentication token if everything went ok, null otherwise
	 */
	public String authenticate(String password, String clientIP) {
		// verify if the password is the same
		String loginToken = null;
		Logger.debug("Trying to authenticate user: " + this.email
				+ " from IP: " + clientIP);
		String passwordHash = Codec.hexMD5(password + this.email);

		if (this.passwordHash.equals(passwordHash) && this.isActive()) {
			// create new authentication token
			loginToken = UUID.randomUUID().toString();
			this.loginInformation.setLoginToken(loginToken);
			this.loginInformation.setLastSuccessfulLogin(System
					.currentTimeMillis());

			this.loginInformation.setSuccessfulLoginCount(this.loginInformation
					.getSuccessfulLoginCount() + 1);

			this.loginInformation
					.setUnsuccessfullLoginCountBeforeSuccessfulLogin(0);
			Logger.debug("Authentication successful");
		} else {
			this.loginInformation.setLoginToken(null);
			this.loginInformation
					.setUnsuccessfullLoginCount(this.loginInformation
							.getUnsuccessfullLoginCount() + 1);
			this.loginInformation
					.setUnsuccessfullLoginCountBeforeSuccessfulLogin(this.loginInformation
							.getUnsuccessfullLoginCountBeforeSuccessfulLogin() + 1);
			this.loginInformation.setLastUnsuccessfulLogin(System
					.currentTimeMillis());
			Logger.debug("Authentication unsuccessful");
		}
		this.loginInformation.setLastLoginClientIP(clientIP);
		//Empty recoverPasswordSecret if any
		if ( this.resetPasswordSecret != null ) {
			this.resetPasswordSecret = null;
		}
		this.save();
		return loginToken;
	}

	public void setLoginInformation(LoginInformation loginInformation) {
		this.loginInformation = loginInformation;
	}

	public LoginInformation getLoginInformation() {
		return loginInformation;
	}

	public void invalidateLoginToken() {
		this.loginInformation.setLoginToken(null);
		this.save();

	}

	public static String generatePasswordHash(String password,
			String email) {
		return Codec.hexMD5(password + email);

	}

	public void setResetPasswordSecret(String resetPasswordSecret) {
		this.resetPasswordSecret = resetPasswordSecret;
	}

	public String getResetPasswordSecret() {
		return resetPasswordSecret;
	}

}
