package controllers;

import java.util.List;

import javax.inject.Inject;

import annotations.authorization.RequiresEmptyUserSession;
import annotations.authorization.RequiresUserSession;

import constants.Constants;
import constants.SessionValuesConstants;

import notifiers.UserMailer;

import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Crypto;

import services.UserService;
import models.Business;
import models.BusinessAdministrator;
import models.User;
import models.enums.UserType;

public class Users extends BaseController {

	/**
	 * Blank form to create a new user
	 * 
	 * @param userType
	 */
	@RequiresEmptyUserSession
	public static void blank(UserType userType) {
		render(userType);
	}

	@RequiresEmptyUserSession
	public static void recoverPassword() {
		render();
	}

	@RequiresEmptyUserSession
	public static void changeResetedPassword(String resetPasswordSecret) {
		//Just before shoing the screen, check if the secret is valid
		User user = User.find("byResetPasswordSecret", resetPasswordSecret).first();
		if ( user == null ) {
			flashError("controllers.users.changeResetedPassword.fail");
			Application.index();
		}
		render(resetPasswordSecret);
	}
	@RequiresEmptyUserSession
	public static void saveNewPasswordAfterReset(String newPassword, String newPasswordConfirmation,String resetPasswordSecret) {
		//check if any of the params is invalid
		if (newPassword == null || newPasswordConfirmation == null ) {

			validation.addError("newPassword",
					"models.user.newPassword.invalid");
			flashError("controllers.users.saveNewPasswordAfterReset.fail");
			render("@changeResetedPassword", newPassword, newPasswordConfirmation,
					resetPasswordSecret);

		}
		if (newPassword.length() < 5) {

			validation.addError("newPassword",
					"models.user.newPassword.invalid");
			flashError("controllers.users.saveNewPasswordAfterReset.fail");
			render("@changeResetedPassword", newPassword, newPasswordConfirmation,
					resetPasswordSecret);
		}

		if (!newPassword.equals(newPasswordConfirmation)) {

			validation.addError("newPasswordConfirmation",
					"models.user.newPassword.confirmation");
			flashError("controllers.users.saveNewPasswordAfterReset.fail");
			render("@changeResetedPassword", newPassword, newPasswordConfirmation,
					resetPasswordSecret);

		}
		if ( resetPasswordSecret==null) {
			flashError("controllers.users.saveNewPasswordAfterReset.fail");
			flash.keep();
			Application.index();
		}
		//Try to find the user 
		User user = User.find("byResetPasswordSecret",resetPasswordSecret).first();
		if ( user==null) {
			flashError("controllers.users.saveNewPasswordAfterReset.fail");
			flash.keep();
			Application.index();			
		}
		
		user.setPasswordHash(User.generatePasswordHash(newPassword, user.getEmail()));
		user.setResetPasswordSecret(null);
		user.save();
		flashSuccess("controllers.users.saveNewPasswordAfterReset.success");
		flash.keep();
		Users.login();
		
	}
	@RequiresEmptyUserSession
	public static void resetPassword(String email) {
		//Find the user 
		User user = User.find("byEmail", email).first();
		if (user != null) {
			//create a resetPasswordSecret
			user.setResetPasswordSecret(Codec.hexMD5(user.getEmail()+user.getName()+System.currentTimeMillis()+Codec.UUID()));
			user.save();
			UserMailer.resetPassword(user);
			flashSuccess("controllers.users.resetPassword.success");
			flash.keep();
			Application.index();
		} else {
			flashError("controllers.users.resetPassword.fail");
			validation.addError("email", "not.found");
			render("@recoverPassword", email);
		}
	}
	
	@RequiresUserSession
	public static void saveNewPassword(String originalPassword,
			String newPassword, String newPasswordConfirmation) {

		if (newPassword == null || newPasswordConfirmation == null
				|| originalPassword == null) {

			validation.addError("newPassword",
					"models.user.newPassword.invalid");
			flashError("controllers.users.saveNewPassword.fail");

			render("@changePassword", originalPassword, newPassword,
					newPasswordConfirmation);

		}
		if (newPassword.length() < 5) {
			validation.addError("newPassword",
					"models.user.newPassword.invalid");
			flashError("controllers.users.saveNewPassword.fail");
			render("@changePassword", originalPassword, newPassword,
					newPasswordConfirmation);
		}

		if (!newPassword.equals(newPasswordConfirmation)) {
			validation.addError("newPassword",
					"models.user.newPassword.confirmation");
			flashError("controllers.users.saveNewPassword.fail");
			render("@changePassword", originalPassword, newPassword,
					newPasswordConfirmation);

		}

		User user = currentUser.get();
		// Check if the originalPassword is the same as the hash stored in DB
		String passwordHash = User.generatePasswordHash(originalPassword,
				user.getEmail());
		if (passwordHash.equals(user.getPasswordHash())) {
			user.setPasswordHash(User.generatePasswordHash(newPassword,
					user.getEmail()));
			user.save();
			flashSuccess("controllers.users.saveNewPassword.sucess");
			Users.view();
		} else {
			flashError("controllers.users.saveNewPassword.fail");
			validation.addError("originalPassword",
					Messages.get("models.user.wrongPassword"));

			render("@changePassword", originalPassword, newPassword,
					newPasswordConfirmation);
		}

	}

	@RequiresUserSession
	public static void changePassword() {
		User user = currentUser.get();
		render(user);
	}

	@RequiresUserSession
	public static void save() {
		User user = currentUser.get();
		// Don't allow to change email nor useType
		if (params.all().containsKey("user.email")
				|| params.all().containsKey("user.userType")) {
			flashError("controllers.users.save.fail");
			render("@edit", user);

		}
		Binder.bind(user, "user", params.all());
		if (user.validateAndSave()) {
			flashSuccess("controllers.users.save.sucess");
			flash.keep();
			Users.view();
		} else {
			flashError("controllers.users.save.fail");
			logValidationErrors();
			render("@edit", user);
		}

	}

	/**
	 * Show the account screen
	 */
	@RequiresUserSession
	public static void view() {
		User user = currentUser.get();
		render(user);
	}

	@RequiresUserSession
	public static void edit() {
		User user = currentUser.get();
		render(user);
	}

	/**
	 * Logout a user
	 */
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
				// Redirect to last requested location
				redirectToLastRequestedResource();

			}
		} else {
			flashError("login.innactive.user");
			redirectToLastRequestedResource();
		}
	}

	public static void login() {

		if (!hasSession()) {

			flash.keep(Constants.FLASH_LAST_URL);
			render();
		} else {
			flashWarning("empty.userSession.required.fail");
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
		User user = User.find("byActivationUUID", activationKey.trim()).first();
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

	/**
	 * Acrion called after successfull account activation
	 * 
	 * @param userType
	 */
	public static void nextStepsAfterActivation(UserType userType) {
		render(userType);
	}

	/**
	 * List all users
	 */
	@RequiresUserSession(userTypes = UserType.ADMIN)
	public static void index() {
		List<User> users = User.find("byUserType", UserType.USER).fetch();
		List<BusinessAdministrator> businessesAdmins = BusinessAdministrator
				.findAll();

		render(users, businessesAdmins);

	}

	/**
	 * Create new User
	 * 
	 * @param user
	 * @param userType
	 * @param emailConfirmation
	 * @param passwordConfirmation
	 * @param termsAgreement
	 */
	public static void create(@Valid User user, UserType userType,
			String emailConfirmation, String passwordConfirmation,
			boolean termsAgreement) {
		// WA
		validation.equals(emailConfirmation, user.getEmail()).message(
				Messages.get("validation.emails.notMatch"));
		validation.required(user.getPassword());
		validation.equals(passwordConfirmation, user.getPassword()).message(
				Messages.get("validation.passwords.notMatch"));
		validation.isTrue(termsAgreement).message(
				"validation.accept.termsAndConditions");

		if (validation.hasErrors()) {
			flashError("controllers.users.create.error");
			render("@blank", user, userType, termsAgreement, emailConfirmation);
		}
		User savedUser = null;
		switch (userType) {

		case BUSINESS_ADMIN:
			savedUser = new BusinessAdministrator(user).save();
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
