package controllers;

import annotations.authorization.RequiresUserSession;
import models.User;
import models.enums.UserType;
import constants.CookieValuesConstants;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;
import play.mvc.With;
import play.mvc.Http.Cookie;

public class BaseController extends Controller {

	protected static User currentUser = null;

	/**
	 * Check if a valid session exists
	 * 
	 * @return True if a valid session exists, false otherwise
	 */
	protected static boolean hasSession() {
		return session.contains(CookieValuesConstants.LOGIN_TOKEN)
				&& session.contains(CookieValuesConstants.LOGIN_EMAIL);
	}

	@Before
	protected static void setGlobalRenderArgs() {
		renderArgs.put("hasSession", hasSession());
	}

	/**
	 * check if a "remember me" cookie exists in client and login user if yes
	 */
	@Before
	protected static void doAutoLogin() {

		boolean authenticationSuccessfull = false;

		// Do it only if there's no session
		if (!hasSession()) {

			Cookie cookieEmail = request.cookies
					.get(CookieValuesConstants.REMEMBER_ME);
			Cookie cookieLgToken = request.cookies
					.get(CookieValuesConstants.REMEMBER_ME_TOKEN);

			if (cookieEmail != null && cookieLgToken != null) {

				// get the value for email
				String cookieEmailValue = cookieEmail.value;
				String[] tmpTokens = cookieEmailValue
						.split(CookieValuesConstants.COOKIE_SIGNED_VAL_SEPARATOR);
				String email = tmpTokens.length == 2 ? tmpTokens[1] : null;

				// get the value for login token
				String cookieLgTokenValue = cookieLgToken.value;

				tmpTokens = cookieLgTokenValue
						.split(CookieValuesConstants.COOKIE_SIGNED_VAL_SEPARATOR);

				String lgToken = tmpTokens.length == 2 ? tmpTokens[1] : null;

				if (email != null) {
					// try to find the user
					User user = User.find("byEmail", email).first();
					if (user != null
							&& user.getLoginInformation().getLoginToken() != null
							&& user.getLoginInformation().getLoginToken()
									.equals(lgToken)) {
						authenticateUser(user);
						Logger.debug("Auto Login done");
						authenticationSuccessfull = true;

					}
				}

			}
			if (!authenticationSuccessfull) {
				clearUserSessionData();
			}

	}
	}

@Before
	protected static void setCurrentUser() {
		if (hasSession()) {
			currentUser = User.find("byEmail",
					session.get(CookieValuesConstants.LOGIN_EMAIL)).first();
			
			if (currentUser != null
					&& !currentUser
							.getLoginInformation()
							.getLoginToken()
							.equals(session
									.get(CookieValuesConstants.LOGIN_TOKEN))) {
				currentUser = null;
			}
		}
	}

	@Before
	protected static void checkAuthorization() {
		RequiresUserSession rus = getActionAnnotation(RequiresUserSession.class);
		boolean authorized = false;
		if (rus != null && currentUser != null) {
			
			// check if user has what it needs
			for (UserType ut : rus.userTypes()) {
				if (ut.equals(currentUser.getUserType())) {
					authorized = true;
					break;
				}
			}

		} else if (rus == null) {
			authorized = true;
		}

		if (!authorized) {
			flashError("user.not.authorized");
			Logger.info("An attempt to access a forbiden resource from IP:"
					+ request.remoteAddress);
			if (currentUser!=null) {
				Application.index();
			} else {
				Users.login();
			}
			
		}
	}

	
	/**
	 * Remove all auto login cookies from client
	 */
	protected static void clearUserSessionData() {
		
		response.removeCookie(CookieValuesConstants.REMEMBER_ME);
		response.removeCookie(CookieValuesConstants.REMEMBER_ME_TOKEN);
		response.cookies.remove(CookieValuesConstants.REMEMBER_ME);
		response.cookies.remove(CookieValuesConstants.REMEMBER_ME_TOKEN);
		session.remove(CookieValuesConstants.LOGIN_EMAIL);
		session.remove(CookieValuesConstants.LOGIN_TOKEN);
		
		currentUser = null;
	}

	protected static void authenticateUser(User user) {
		// set user login in session
		session.put(CookieValuesConstants.LOGIN_TOKEN, user
				.getLoginInformation().getLoginToken());
		session.put(CookieValuesConstants.LOGIN_EMAIL, user.getEmail());
	}

	protected static void flashError(String i18nKey) {
		flash.clear();
		flash.error(Messages.get(i18nKey));
	}

	protected static void flashSuccess(String i18nKey) {
		flash.clear();
		flash.success(Messages.get(i18nKey));
	}

	protected static void flashWarning(String i18nKey) {
		flash.clear();
		flash.put("warning", Messages.get(i18nKey));
	}

}
