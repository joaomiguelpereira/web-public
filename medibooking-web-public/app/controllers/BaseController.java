package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotations.authorization.RequiresUserSession;
import models.BusinessAdministrator;
import models.User;
import models.enums.UserType;
import constants.Constants;
import constants.SessionValuesConstants;
import play.Logger;
import play.classloading.enhancers.ControllersEnhancer.ControllerSupport;
import play.classloading.enhancers.LocalvariablesNamesEnhancer.LocalVariablesNamesTracer;
import play.classloading.enhancers.LocalvariablesNamesEnhancer.LocalVariablesSupport;
import play.data.validation.Error;
import play.data.validation.Validation;
import play.db.jpa.Model;
import play.i18n.Messages;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;
import play.mvc.With;
import play.mvc.Http.Cookie;

public class BaseController extends Controller {

	// private static final int NUMBER_ROWS_PER_PAGE = 5;
	/**
	 * Current logged in user. Each Thread has its own copy
	 */
	protected static ThreadLocal<User> currentUser = new ThreadLocal<User>();

	protected static void logValidationErrors() {

		Map<String, List<play.data.validation.Error>> errors = Validation
				.current().errorsMap();

		for (List<play.data.validation.Error> theErrors : errors.values()) {
			for (Error error : theErrors) {
				Logger.debug("Error on " + error.getKey() + ":"
						+ error.message());
			}
		}

	}

	/**
	 * Check if a valid session exists
	 * 
	 * @return True if a valid session exists, false otherwise
	 */
	protected static boolean hasSession() {

		return session.contains(SessionValuesConstants.LOGIN_TOKEN)
				&& session.contains(SessionValuesConstants.LOGIN_EMAIL)
				&& session.contains(SessionValuesConstants.USER_TYPE);
	}

	protected static BusinessAdministrator getCurrentAdministrator() {

		if (currentUser.get() instanceof BusinessAdministrator) {
			return (BusinessAdministrator) currentUser.get();
		} else {
			throw new RuntimeException("Expecting current user to be an "
					+ BusinessAdministrator.class.getName() + " but is "
					+ currentUser.get().getClass().getName());
		}

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
					.get(SessionValuesConstants.REMEMBER_ME);
			Cookie cookieLgToken = request.cookies
					.get(SessionValuesConstants.REMEMBER_ME_TOKEN);

			if (cookieEmail != null && cookieLgToken != null) {

				// get the value for email
				String cookieEmailValue = cookieEmail.value;
				String[] tmpTokens = cookieEmailValue
						.split(SessionValuesConstants.COOKIE_SIGNED_VAL_SEPARATOR);
				String email = tmpTokens.length == 2 ? tmpTokens[1] : null;
				String emailSigned = tmpTokens.length == 2 ? tmpTokens[0]
						: null;
				// get the value for login token
				String cookieLgTokenValue = cookieLgToken.value;

				tmpTokens = cookieLgTokenValue
						.split(SessionValuesConstants.COOKIE_SIGNED_VAL_SEPARATOR);

				String lgToken = tmpTokens.length == 2 ? tmpTokens[1] : null;
				String lgTokenSigned = tmpTokens.length == 2 ? tmpTokens[0]
						: null;

				if (!Crypto.sign(lgToken).equals(lgTokenSigned)
						|| !Crypto.sign(email).equals(emailSigned)) {
					clearAuthenticatedUserSessionData();
					error("Invalid Cookie signature");

				}
				if (email != null) {
					// try to find the user
					User user = User.find("byEmail", email).first();
					if (user != null
							&& user.getLoginInformation().getLoginToken() != null
							&& user.getLoginInformation().getLoginToken()
									.equals(lgToken)) {
						createAuthenticateUserSessionData(user);
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

		if (hasSession()) {

			User aCurrentUser = User.find("byEmail",
					session.get(SessionValuesConstants.LOGIN_EMAIL)).first();

			currentUser.set(aCurrentUser);

			if (currentUser.get() != null
					&& (!currentUser
							.get()
							.getUserType()
							.equals(UserType.valueOf(session
									.get(SessionValuesConstants.USER_TYPE))) || !currentUser
							.get()
							.getLoginInformation()
							.getLoginToken()
							.equals(session
									.get(SessionValuesConstants.LOGIN_TOKEN)))) {

				// Also clear login data
				clearAuthenticatedUserSessionData();
			}
		}
	}

	/**
	 * Check authorization to the action
	 */
	@Before
	protected static void checkActionAuthorization() {

		// Get requested Action/Controller

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

				flashError("user.not.authorized");
				Application.index();
				// forbidden(Messages.get("insufficent.privileges"));
			} else {
				// save current location
				flash.put(Constants.FLASH_LAST_URL,
						request.method == "GET" ? request.url : "/");
				Users.login();
			}

		}
	}

	protected static void redirectToLastRequestedResource() {
		String url = flash.get(Constants.FLASH_LAST_URL);
		if (url == null) {
			url = "/";
		}
		redirect(url);

	}

	/**
	 * Remove all auto login cookies from client and clear current user
	 */
	protected static void clearAuthenticatedUserSessionData() {

		// set the cookie value to ""
		response.removeCookie(SessionValuesConstants.REMEMBER_ME);
		// set the cookie value to ""

		response.removeCookie(SessionValuesConstants.REMEMBER_ME_TOKEN);
		// remove the cookie from map
		response.cookies.remove(SessionValuesConstants.REMEMBER_ME);

		// remove the cookie from map
		response.cookies.remove(SessionValuesConstants.REMEMBER_ME_TOKEN);
		session.remove(SessionValuesConstants.LOGIN_EMAIL);
		session.remove(SessionValuesConstants.LOGIN_TOKEN);
		session.remove(SessionValuesConstants.USER_TYPE);

		// Let me be here, please!!!!???
		currentUser.set(null);
	}

	/**
	 * Create required session data for authenticated user
	 * 
	 * @param user
	 */
	protected static void createAuthenticateUserSessionData(User user) {
		// set user login in session
		session.put(SessionValuesConstants.LOGIN_TOKEN, user
				.getLoginInformation().getLoginToken());
		session.put(SessionValuesConstants.LOGIN_EMAIL, user.getEmail());
		session.put(SessionValuesConstants.USER_TYPE, user.getUserType()
				.toString());

	}

	/**
	 * Shortcut to put a message into error flash
	 * 
	 * @param i18nKey
	 *            the i18nKey containing the message
	 */
	protected static void flashError(String i18nKey) {
		flash.error(Messages.get(i18nKey));
	}

	/**
	 * Shortcut to put a message into success flash
	 * 
	 * @param i18nKey
	 *            the i18nKey containing the message
	 */
	protected static void flashSuccess(String i18nKey) {

		flash.success(Messages.get(i18nKey));
	}

	/**
	 * Shortcut to put a message into warning flash
	 * 
	 * @param i18nKey
	 *            the i18nKey containing the message
	 */

	protected static void flashWarning(String i18nKey) {

		flash.put("warning", Messages.get(i18nKey));
	}

}
