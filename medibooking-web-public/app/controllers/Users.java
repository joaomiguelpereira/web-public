package controllers;

import java.util.List;

import javax.inject.Inject;

import notifiers.UserMailer;

import play.Logger;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;

import services.UserService;
import models.Office;
import models.OfficeAdministrator;
import models.User;
import models.enums.UserType;

public class Users extends Application {

	@Inject
	private static UserService userService;

	/**
	 * Blank form to create a new user
	 * 
	 * @param userType
	 */
	public static void blank(UserType userType) {
		render(userType);
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
		
		// find the user
		User user = User.find("activationUUID=?", activationKey.trim()).first();
		if (null != user) {
			if (user.isActive()) {
				flashWarning("user.already.activated");
			} else {
				user.setActive(true);
				user.save();
				flashSuccess("user.activated");
			}
		} else {
			flashError("user.invalid.activation.key");
		}
		
		Application.index();

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
