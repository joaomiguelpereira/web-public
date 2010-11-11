package controllers;

import java.util.List;

import javax.inject.Inject;

import constants.Constants;
import constants.SessionValuesConstants;

import notifiers.UserMailer;

import play.Logger;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Crypto;

import services.UserService;
import models.Office;
import models.OfficeAdministrator;
import models.User;
import models.enums.UserType;

public class Users extends Application {

	/**
	 * Blank form to create a new user
	 * 
	 * @param userType
	 */
	public static void blank(UserType userType) {

		if (!hasSession()) {
			render(userType);
		} else {
			flashWarning("login.session.exists");
			Application.index();
		}

	}

	public static void logout() {
		// if the user is not currently logged in
		if (session.get(SessionValuesConstants.LOGIN_EMAIL) == null) {
			flashWarning("logout.nologinsession");
			render("@Application.index");
		} else {
			String email = session.get(SessionValuesConstants.LOGIN_EMAIL);
			User user = User.find("byEmail", email).first();
			if (user != null) {
				user.invalidateLoginToken();
			}
			
			clearAuthenticatedUserSessionData();

			flashSuccess("logout.successfull");
			Application.index();

		}
	}

	/**
	 * Action to login a user, given his/her email and password
	 * 
	 * @param email
	 *            User's email
	 * @param password
	 *            User's password
	 * @param keepLogged
	 *            if true, user will be auto-logged during 30 days
	 */
	public static void authenticate(String email, String password,
			boolean keepLogged) {
		Logger.debug("Keeplogged:" + keepLogged);
		Logger.debug("URL HERE NOW IS: "+flash.get(Constants.FLASH_LAST_URL));
		// verify the presence of email
		validation.required(email, Messages.get("login.invalid.email"));
		validation.required(password, Messages.get("login.invalid.password"));
		if (validation.hasErrors()) {
			flashError("login.invalid.data");
			flash.keep(Constants.FLASH_LAST_URL);
			render("@login");
		}

		// get the user by email
		User user = User.find("email=?", email).first();
		if (user == null) {
			response.status = 401;
			flashError("login.user.notFound");
			flash.keep(Constants.FLASH_LAST_URL);
			render("@login", email);
		} else if (user.isActive()) {
			String loginToken = user.authenticate(password,
					request.remoteAddress);
			if (loginToken == null) {
				flashError("login.unsuccessful");
				response.status = 401;
				flash.keep(Constants.FLASH_LAST_URL);
				render("@login", email);
			} else {
				createAuthenticateUserSessionData(user);

				if (keepLogged) {

					response.setCookie(
							SessionValuesConstants.REMEMBER_ME,
							Crypto.sign(user.getEmail())
									+ SessionValuesConstants.COOKIE_SIGNED_VAL_SEPARATOR
									+ user.getEmail(),
							SessionValuesConstants.REMEMBER_ME_DURATION);

					response.setCookie(
							SessionValuesConstants.REMEMBER_ME_TOKEN,
							Crypto.sign(user.getLoginInformation()
									.getLoginToken())
									+ SessionValuesConstants.COOKIE_SIGNED_VAL_SEPARATOR
									+ user.getLoginInformation()
											.getLoginToken(),
							SessionValuesConstants.REMEMBER_ME_DURATION);

				}

				
				flashSuccess("login.successful");
				//Redirect to last requested location
				redirectToLastRequestedResource();
				
			}
		} else {
			flashError("login.innactive.user");
			redirectToLastRequestedResource();
		}
	}


	public static void login() {
		
		if (!hasSession()) {
			Logger.debug("URL HER IS: "+flash.get(Constants.FLASH_LAST_URL));
			flash.keep(Constants.FLASH_LAST_URL);
			render();
		} else {
			flashWarning("login.session.exists");
			Application.index();
		}
	}

	/**
	 * Render the registration confirmation page. The user in this stage is
	 * inactive
	 * 
	 * @param userType
	 */
	public static void userCreatedConfirmation(UserType userType) {
		render(userType);
	}

	public static void activateUser(String activationKey) {

		boolean success = false;
		// find the user
		User user = User.find("activationUUID=?", activationKey.trim()).first();
		if (null != user) {
			if (user.isActive()) {
				flashWarning("user.already.activated");
			} else {
				user.setActive(true);
				user.save();
				flashSuccess("user.activated");
				success = true;
			}
		} else {
			flashError("user.invalid.activation.key");
		}

		if (success) {
			Users.nextStepsAfterActivation(user.getUserType());
		} else {
			Application.index();
		}

	}

	public static void nextStepsAfterActivation(UserType userType) {
		render(userType);
	}

	/**
	 * List all users
	 */
	public static void index() {
		List<User> users = User.find("userType=?", UserType.USER).fetch();
		List<OfficeAdministrator> officeAdmins = OfficeAdministrator.findAll();

		render(users, officeAdmins);

	}

	/**
	 * Save the user
	 * 
	 * @param user
	 * @param userType
	 * @param emailConfirmation
	 * @param passwordConfirmation
	 * @param termsAgreement
	 */
	public static void save(@Valid User user, UserType userType,
			String emailConfirmation, String passwordConfirmation,
			boolean termsAgreement) {
		// WA
		validation.equals(emailConfirmation, user.getEmail()).message(
				Messages.get("validation.emails.notMatch"));
		validation.equals(passwordConfirmation, user.getPassword()).message(
				Messages.get("validation.passwords.notMatch"));
		validation.isTrue(termsAgreement).message(
				"validation.accept.termsAndConditions");

		if (validation.hasErrors()) {
			flashError("partner.register.error");
			render("@blank", user, userType, termsAgreement, emailConfirmation);
		}
		User savedUser = null;
		switch (userType) {

		case OFFICE_ADMIN:
			savedUser = new OfficeAdministrator(user).save();
			break;

		default:
			savedUser = user.save();
			break;
		}
		// send activation email to user's email
		UserMailer.activateAccount(savedUser);
		flashSuccess("user.register.success");
		render("@userCreatedConfirmation", userType, savedUser);

	}

}
