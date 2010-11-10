package models;

import javax.persistence.Embeddable;

@Embeddable
public class LoginInformation {

	//Initialization block
	{
		unsuccessfullLoginCount = 0;
		successfulLoginCount = 0L;
		unsuccessfullLoginCountBeforeSuccessfulLogin = 0;
		loginToken = "";
	}
	
	private String loginToken;
	private Long lastSuccessfulLogin;
	private Long successfulLoginCount;
	private Integer unsuccessfullLoginCount;
	private Integer unsuccessfullLoginCountBeforeSuccessfulLogin;
	private Long lastUnsuccessfulLogin;
	private String lastLoginClientIP;

	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}

	public String getLoginToken() {
		return loginToken;
	}

	public void setLastSuccessfulLogin(Long lastSuccessfulLogin) {
		this.lastSuccessfulLogin = lastSuccessfulLogin;
	}

	public Long getLastSuccessfulLogin() {
		return lastSuccessfulLogin;
	}

	public void setSuccessfulLoginCount(Long successfulLoginCount) {
		this.successfulLoginCount= successfulLoginCount;
	}

	public Long getSuccessfulLoginCount() {
		return successfulLoginCount;
	}

	public void setUnsuccessfullLoginCount(Integer unsuccessfullLoginCount) {
		this.unsuccessfullLoginCount = unsuccessfullLoginCount;
	}

	public Integer getUnsuccessfullLoginCount() {
		return unsuccessfullLoginCount;
	}

	public void setUnsuccessfullLoginCountBeforeSuccessfulLogin(
			Integer unsuccessfullLoginCountBeforeSuccessfulLogin) {
		this.unsuccessfullLoginCountBeforeSuccessfulLogin = unsuccessfullLoginCountBeforeSuccessfulLogin;
	}

	public Integer getUnsuccessfullLoginCountBeforeSuccessfulLogin() {
		return unsuccessfullLoginCountBeforeSuccessfulLogin;
	}

	public void setLastUnsuccessfulLogin(Long lastUnsuccessfulLogin) {
		this.lastUnsuccessfulLogin = lastUnsuccessfulLogin;
	}

	public Long getLastUnsuccessfulLogin() {
		return lastUnsuccessfulLogin;
	}

	public void setLastLoginClientIP(String lastLoginClientIP) {
		this.lastLoginClientIP = lastLoginClientIP;
	}

	public String getLastLoginClientIP() {
		return lastLoginClientIP;
	}

}
