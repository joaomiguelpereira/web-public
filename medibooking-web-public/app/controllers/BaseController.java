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

	/**
	 * Current logged in user. Each Thread has its own copy
	 */
	protected static ThreadLocal<User> currentUser = new ThreadLocal<User>();

	/**
	 * Check if a valid session exists
	 * @return True if a valid session exists, false otherwise
	 */
	protected static boolean hasSession() {
		return session.contains(CookieValuesConstants.LOGIN_TOKEN)
				&& session.contains(CookieValuesConstants.LOGIN_EMAIL)
				&& session.contains(CookieValuesConstants.USER_TYPE);
	}

	/**
	 * check if a "remember me" cookie exists in client and login user if yes
	 */
	@Before
	protected static void doAutoLogin() {
		Logger.debug("doAutoLogin");
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
						createAuthenticateUserSessionData(user);
						Logger.debug("Auto Login done");
						authenticationSuccessfull = true;

					}
				}

			}
			if (!authenticationSuccessfull) {
				clearAuthenticatedUserSessionData();
			}

		}
	}

	/**
	 * Set the current user
	 */
	@Before
	protected static void setCurrentUser() {
		Logger.debug("setCurrentUser");
		if (hasSession()) {

			User aCurrentUser = User.find("byEmail",
					session.get(CookieValuesConstants.LOGIN_EMAIL)).first();

			currentUser.set(aCurrentUser);

			if (currentUser.get() != null
					&& !currentUser
							.get()
							.getLoginInformation()
							.getLoginToken()
							.equals(session
									.get(CookieValuesConstants.LOGIN_TOKEN))) {
				
				//Also clear login data
				clearAuthenticatedUserSessionData();
			}
		}
	}

	/**
	 * Check authorization to the action
	 */
	@Before
	protected static void checkActionAuthorization() {
		Logger.debug("checkAuthorization");
		RequiresUserSession rus = getActionAnnotation(RequiresUserSession.class);
		boolean authorized = false;
		if (rus != null && currentUser.get() != null) {

			// check if user has what it needs
			for (UserType ut : rus.userTypes()) {
				if (ut.equals(currentUser.get().getUserType())) {
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
			if (currentUser.get() != null) {
				Application.index();
			} else {
				Users.login();
			}

		}
	}

	/**
	 * Remove all auto login cookies from client and clear current user
	 */
	protected static void clearAuthenticatedUserSessionData() {

		response.removeCookie(CookieValuesConstants.REMEMBER_ME); //set the cookie value to ""
		response.removeCookie(CookieValuesConstants.REMEMBER_ME_TOKEN); //set the cookie value to ""
		response.cookies.remove(CookieValuesConstants.REMEMBER_ME); //remove the key from the map
		response.cookies.remove(CookieValuesConstants.REMEMBER_ME_TOKEN); //remove the key from the map
		session.remove(CookieValuesConstants.LOGIN_EMAIL);
		session.remove(CookieValuesConstants.LOGIN_TOKEN);
		session.remove(CookieValuesConstants.USER_TYPE);

		//Let me be here, please!!!!???
		currentUser.set(null);
	}

	/**
	 * Create required session data for authenticated user
	 * @param user
	 */
	protected static void createAuthenticateUserSessionData(User user) {
		// set user login in session
		session.put(CookieValuesConstants.LOGIN_TOKEN, user
				.getLoginInformation().getLoginToken());
		session.put(CookieValuesConstants.LOGIN_EMAIL, user.getEmail());
		session.put(CookieValuesConstants.USER_TYPE, user.getUserType()
				.toString());
	}

	/**
	 * Shortcut to put a message into error flash
	 * @param i18nKey the i18nKey containing the message
	 */ 
	protected static void flashError(String i18nKey) {
		flash.clear();
		flash.error(Messages.get(i18nKey));
	}

	/**
	 * Shortcut to put a message into success flash
	 * @param i18nKey the i18nKey containing the message
	 */ 
	protected static void flashSuccess(String i18nKey) {
		flash.clear();
		flash.success(Messages.get(i18nKey));
	}

	/**
	 * Shortcut to put a message into warning flash
	 * @param i18nKey the i18nKey containing the message
	 */ 

	protected static void flashWarning(String i18nKey) {
		flash.clear();
		flash.put("warning", Messages.get(i18nKey));
	}

}
