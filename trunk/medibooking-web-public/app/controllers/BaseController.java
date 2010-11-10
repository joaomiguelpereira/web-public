package controllers;

import models.User;
import constants.CookieValuesConstants;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Http.Cookie;

public class BaseController extends Controller {
	/**
	 * Check if a valid session exists
	 * 
	 * @return True if a valid session exists, false otherwise
	 */
	protected static boolean hasSession() {
		return session.contains(CookieValuesConstants.LOGIN_TOKEN)
				&& session.contains(CookieValuesConstants.LOGIN_EMAIL);
	}

	/**
	 * check if a "remember me" cookie exists in client and login user if yes
	 */
	@Before
	protected static void doAutoLogin() {

		// Get the cookie
		boolean authenticationSuccessfull = false;

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
						authenticationSuccessfull = true;

					}
				}

			}
			if (!authenticationSuccessfull) {
				removeAutoLoginCookies();
			}

		}
	}

	/**
	 * Remove all auto login cookies from client
	 */
	protected static void removeAutoLoginCookies() {

		response.removeCookie(CookieValuesConstants.REMEMBER_ME);
		response.removeCookie(CookieValuesConstants.REMEMBER_ME_TOKEN);
		response.cookies.remove(CookieValuesConstants.REMEMBER_ME);
		response.cookies.remove(CookieValuesConstants.REMEMBER_ME_TOKEN);

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
