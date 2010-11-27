package functional;

import groovy.swing.factory.ActionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.BusinessAdministrator;
import models.User;
import models.enums.UserType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import constants.SessionValuesConstants;
import controllers.Users;

import play.Logger;
import play.db.jpa.Model;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Crypto;
import play.libs.Mail.Mock;
import play.mvc.Http;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;
import play.mvc.Router.Route;
import play.mvc.Scope;
import play.mvc.Scope.Flash;
import play.test.Fixtures;
import play.test.FunctionalTest;

public class UsersTest extends ApplicationFunctionalTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
		Fixtures.load("users.yml");
		if (Flash.current() != null) {
			Flash.current().clear();
		}
		logoutCurrentUser();

	}

	@Test
	public void testShowEditForm() {

		// Log a user
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		Http.Response response = GET(Router.reverse("Users.edit"));
		assertNoErrorFlashed();
		assertNoWarningFlashed();
		assertIsOk(response);
		assertI18nHtmlTitlePresent(response, "views.users.edit.title");
		// Assert binding
		assertBindingExists("user");
		assertBindedModel("user", loggedUser);

	}

	@Test
	public void testForbidShowEditFormForNotLoggedUser() {
		Http.Response response = GET(Router.reverse("Users.edit"));
		assertErrorFlashed("user.not.authorized");
		assertRedirectedTo(response, "Users.login",
				new HashMap<String, Object>());
		assertNoBindingExists("user");
	}

	@Test
	public void testCannotEditEmail() {
		// Log a user
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user.email", "newemail@gmail.com");

		POST(Router.reverse("Users.save", params));
		assertErrorFlashed("controllers.users.save.fail");

		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals("active@gmail.com", savedUser.getEmail());

	}

	@Test
	public void testCannotChangeType() {
		// Log a user
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user.userType", UserType.ADMIN);
		POST(Router.reverse("Users.save", params));
		assertErrorFlashed("controllers.users.save.fail");
		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals("active@gmail.com", savedUser.getEmail());
		assertEquals(UserType.USER, savedUser.getUserType());

	}

	@Test
	public void testEditMyData() {
		// Log a user
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user.name", "new name for user");
		params.put("user.phone", "123456789");
		params.put("user.mobile", "987654321");

		Http.Response response = POST(Router.reverse("Users.save", params));
		assertNoErrorFlashed();
		assertNoWarningFlashed();
		assertSuccessFlashed("controllers.users.save.sucess");
		assertRedirectedTo(response, "Users.view",
				new HashMap<String, Object>());

		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals("new name for user", savedUser.getName());
		assertEquals("123456789", savedUser.getPhone());
		assertEquals("987654321", savedUser.getMobile());

	}

	@Test
	public void testForbidEditDataForNotLoggedUser() {
		// Log a user
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user.name", "new name for user");
		params.put("user.phone", "123456789");
		params.put("user.mobile", "987654321");
		Http.Response response = POST(Router.reverse("Users.save", params));
		assertErrorFlashed("user.not.authorized");
		assertRedirectedTo(response, "Users.login",
				new HashMap<String, Object>());
		assertNoBindingExists("user");

	}

	@Test
	public void testShowChangePassword() {
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		Http.Response response = GET(Router.reverse("Users.changePassword"));
		assertNoErrorFlashed();
		assertNoWarningFlashed();
		assertIsOk(response);
		assertI18nHtmlTitlePresent(response, "views.users.changePassword.title");
		// Assert binding
		assertBindingExists("user");
		assertBindedModel("user", loggedUser);
	}

	@Test
	public void testForbidShowChangePasswordForNotLoggedUser() {
		Http.Response response = GET(Router.reverse("Users.changePassword"));
		assertErrorFlashed("user.not.authorized");
		assertRedirectedTo(response, "Users.login",
				new HashMap<String, Object>());
		assertNoBindingExists("user");
	}

	@Test
	public void testDontChangePasswordForInvalidOriginalPassword() {
		// Log a user
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("originalPassword", "11211");
		params.put("newPassword", "12345");
		params.put("newPasswordConfirmation", "12345");

		POST(Router.reverse("Users.saveNewPassword", params));
		assertErrorFlashed("controllers.users.saveNewPassword.fail");

		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals(User.generatePasswordHash("11111", loggedUser.getEmail()),
				savedUser.getPasswordHash());
	}

	@Test
	public void testDontChangePasswordForInvalidNewPassword() {
		// Log a user
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("originalPassword", "11111");
		params.put("newPassword", "1234");
		params.put("newPasswordConfirmation", "1234");

		POST(Router.reverse("Users.saveNewPassword", params));
		assertErrorFlashed("controllers.users.saveNewPassword.fail");

		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals(User.generatePasswordHash("11111", loggedUser.getEmail()),
				savedUser.getPasswordHash());

	}

	@Test
	public void testDontChangePasswordForNullNewPassword() {
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("originalPassword", "11111");
		params.put("newPassword", null);
		params.put("newPasswordConfirmation", "54321");

		POST(Router.reverse("Users.saveNewPassword", params));
		assertErrorFlashed("controllers.users.saveNewPassword.fail");

		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals(User.generatePasswordHash("11111", loggedUser.getEmail()),
				savedUser.getPasswordHash());

	}

	@Test
	public void testDontChangePasswordForEmptyNewPassword() {
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("originalPassword", "11111");
		params.put("newPassword", "");
		params.put("newPasswordConfirmation", "");

		POST(Router.reverse("Users.saveNewPassword", params));
		assertErrorFlashed("controllers.users.saveNewPassword.fail");

		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals(User.generatePasswordHash("11111", loggedUser.getEmail()),
				savedUser.getPasswordHash());

	}

	@Test
	public void testDontChangePasswordForInvalidNewPasswordConfirmation() {

		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("originalPassword", "11111");
		params.put("newPassword", "12345");
		params.put("newPasswordConfirmation", "54321");

		POST(Router.reverse("Users.saveNewPassword", params));
		assertErrorFlashed("controllers.users.saveNewPassword.fail");

		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals(User.generatePasswordHash("11111", loggedUser.getEmail()),
				savedUser.getPasswordHash());

	}

	@Test
	public void testChangePassword() {
		// Log a user
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		// Create data to edit
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("originalPassword", "11111");
		params.put("newPassword", "12345");
		params.put("newPasswordConfirmation", "12345");

		Http.Response response = POST(Router.reverse("Users.saveNewPassword",
				params));
		assertNoErrorFlashed();
		assertNoWarningFlashed();
		assertSuccessFlashed("controllers.users.saveNewPassword.sucess");
		assertRedirectedTo(response, "Users.view",
				new HashMap<String, Object>());

		// get the user from the DB
		User savedUser = User.findById(loggedUser.id);
		assertEquals(User.generatePasswordHash("12345", loggedUser.getEmail()),
				savedUser.getPasswordHash());

	}

	@Test
	public void testForbidChangePasswordForNotLoggedUser() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("originalPassword", "11111");
		params.put("newPassword", "12345");
		params.put("newPasswordConfirmation", "12345");

		Http.Response response = POST(Router.reverse("Users.saveNewPassword",
				params));

		assertErrorFlashed("user.not.authorized");
		assertRedirectedTo(response, "Users.login",
				new HashMap<String, Object>());
		assertNoBindingExists("user");
	}

	@Test
	public void testShowMyData() {

		// Log a user
		authenticateUser("active@gmail.com", "11111", false);
		User loggedUser = User.find("byEmail", "active@gmail.com").first();
		Http.Response response = GET(Router.reverse("Users.view"));
		assertNoErrorFlashed();
		assertNoWarningFlashed();
		assertIsOk(response);
		assertI18nHtmlTitlePresent(response, "views.users.view.title");
		// Assert binding
		assertBindingExists("user");
		assertBindedModel("user", loggedUser);
	}

	@Test
	public void testForbidShowDataForNotLoggedUser() {
		Http.Response response = GET(Router.reverse("Users.view"));
		assertErrorFlashed("user.not.authorized");
		assertRedirectedTo(response, "Users.login",
				new HashMap<String, Object>());
		assertNoBindingExists("user");
	}

	@Test
	public void testShowRegisterForOfficeAdmin() {
		Response res = withController(Users.class)
				.withArgs("userType", UserType.BUSINESS_ADMIN)
				.withAction("blank").get();
		assertIsOk(res);
	}

	@Test
	public void testLogoutUserWithNoLogin() {

		Http.Response response = POST(Router.reverse("Users.logout"));
		assertWarningFlashed("logout.nologinsession");

	}

	@Test
	public void testLogoutUserWithRememberFunction() {
		authenticateUser("active@gmail.com", "11111", true);
		Http.Response response = POST(Router.reverse("Users.logout"));
		assertSuccessFlashed("logout.successfull");
		// get user

		User user = User.find("byEmail", "active@gmail.com").first();
		assertNull(user.getLoginInformation().getLoginToken());
		assertNull(Scope.Session.current().get(
				SessionValuesConstants.LOGIN_EMAIL));
		assertNull(Scope.Session.current().get(
				SessionValuesConstants.LOGIN_TOKEN));

		Http.Cookie cookieRM = response.cookies
				.get(SessionValuesConstants.REMEMBER_ME);

		Http.Cookie cookieLgTokenValue = response.cookies
				.get(SessionValuesConstants.REMEMBER_ME_TOKEN);
		assertNull(cookieLgTokenValue);
		assertNull(cookieRM);
	}

	@Test
	public void testAuthenticateInactiveUser() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", "bob@gmail.com");
		params.put("password", "11111");
		params.put("keepLogged", false);
		Http.Response response = POST(Router.reverse("Users.authenticate",
				params));
		assertErrorFlashed("login.innactive.user");

	}

	@Test
	public void testAuthenticateNotExistingUser() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", "bob@gmsssil.com");
		params.put("password", "11111");
		params.put("keepLogged", false);
		Http.Response response = POST(Router.reverse("Users.authenticate",
				params));
		assertErrorFlashed("login.user.notFound");
		assertStatus(401, response);

	}

	@Test
	public void testAuthenticateInvalidPassword() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", "active@gmail.com");
		params.put("password", "111ss11");
		params.put("keepLogged", false);
		Http.Response response = POST(Router.reverse("Users.authenticate",
				params));
		assertErrorFlashed("login.unsuccessful");
		assertStatus(401, response);
		User user = User.find("byEmail", "active@gmail.com").first();
		assertEquals(Integer.valueOf(1), user.getLoginInformation()
				.getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(1), user.getLoginInformation()
				.getUnsuccessfullLoginCountBeforeSuccessfulLogin());

	}

	@Test
	public void testAuthenticateUser() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", "active@gmail.com");
		params.put("password", "11111");
		params.put("keepLogged", false);
		Http.Response response = POST(Router.reverse("Users.authenticate",
				params));
		assertSuccessFlashed("login.successful");
		User user = User.find("byEmail", "active@gmail.com").first();
		assertEquals(Long.valueOf(1L), user.getLoginInformation()
				.getSuccessfulLoginCount());

		assertEquals(
				Scope.Session.current().get(SessionValuesConstants.LOGIN_EMAIL),
				"active@gmail.com");
		assertEquals(
				Scope.Session.current().get(SessionValuesConstants.LOGIN_TOKEN),
				user.getLoginInformation().getLoginToken());

	}

	@Test
	public void testRememberMeFunction() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", "active@gmail.com");
		params.put("password", "11111");
		params.put("keepLogged", true);
		Http.Response response = POST(Router.reverse("Users.authenticate",
				params));
		assertSuccessFlashed("login.successful");
		User user = User.find("byEmail", "active@gmail.com").first();
		assertEquals(Long.valueOf(1L), user.getLoginInformation()
				.getSuccessfulLoginCount());
		assertEquals(
				Scope.Session.current().get(SessionValuesConstants.LOGIN_EMAIL),
				"active@gmail.com");
		assertEquals(
				Scope.Session.current().get(SessionValuesConstants.LOGIN_TOKEN),
				user.getLoginInformation().getLoginToken());

		Http.Cookie cookieEmail = response.cookies
				.get(SessionValuesConstants.REMEMBER_ME);

		assertEquals(
				Crypto.sign(user.getEmail())
						+ SessionValuesConstants.COOKIE_SIGNED_VAL_SEPARATOR
						+ user.getEmail(), cookieEmail.value);

		Http.Cookie cookieLgToken = response.cookies
				.get(SessionValuesConstants.REMEMBER_ME_TOKEN);

		assertEquals(Crypto.sign(user.getLoginInformation().getLoginToken())
				+ SessionValuesConstants.COOKIE_SIGNED_VAL_SEPARATOR
				+ user.getLoginInformation().getLoginToken(),
				cookieLgToken.value);

	}

	@Test
	public void testRegisterUser() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user.name", "user name");
		params.put("user.email", "myemail@gmail.com");
		params.put("user.password", "MyPassword");
		params.put("userType", UserType.BUSINESS_ADMIN);
		params.put("emailConfirmation", "myemail@gmail.com");
		params.put("passwordConfirmation", "MyPassword");
		params.put("termsAgreement", true);
		Http.Response response = POST(Router.reverse("Users.create", params));

		assertNoErrorFlashed();
		assertSuccessFlashed("user.register.success");
		assertStatus(200, response);
		// Find the user in DB
		BusinessAdministrator oa = BusinessAdministrator.find("email=?",
				"myemail@gmail.com").first();
		assertNotNull(oa);
		assertFalse(oa.isActive());
		assertTrue(Mock.getLastMessageReceivedBy("myemail@gmail.com").contains(
				Messages.get("email.user.activation.subject", oa.getName())));
		assertTrue(Mock.getLastMessageReceivedBy("myemail@gmail.com").contains(
				oa.getActivationUUID()));
	}

	@Test
	public void testFailRegisterUserForNoPassword() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user.name", "user name");
		params.put("user.email", "myemail@gmail.com");

		params.put("userType", UserType.BUSINESS_ADMIN);
		params.put("emailConfirmation", "myemail@gmail.com");
		params.put("passwordConfirmation", "MyPassword");
		params.put("termsAgreement", true);
		POST(Router.reverse("Users.create", params));

		assertErrorFlashed("controllers.users.create.error");

	}

	@Test
	public void testActivateUser() {
		// get user bob
		User bob = User.find("email=?", "bob@gmail.com").first();
		assertNotNull(bob);
		assertFalse(bob.isActive());
		// Now make a put

		// Http.Response response =
		// PUT(Router.reverse("Users.activateUser").add("activationKey",
		// bob.getActivationUUID()),"","");

		GET(Router.reverse("Users.activateUser").add("activationKey",
				bob.getActivationUUID()));
		User bobActive = User.find("email=?", "bob@gmail.com").first();
		assertNotNull(bobActive);
		assertTrue(bobActive.isActive());
		assertSuccessFlashed("user.activated");
		assertNoErrorFlashed();
	}

	@Test
	public void testActivateActiveUser() {
		User activeUser = User.find("email=?", "active@gmail.com").first();
		// PUT(Router.reverse("Users.activateUser").add("activationKey",
		// activeUser.getActivationUUID()),"","");
		GET(Router.reverse("Users.activateUser").add("activationKey",
				activeUser.getActivationUUID()));

		assertWarningFlashed("user.already.activated");
	}

	@Test
	public void testActivateUserInvalidCode() {
		// get user bob
		User bob = User.find("email=?", "bob@gmail.com").first();
		assertNotNull(bob);
		assertFalse(bob.isActive());
		// Now make a put
		// Http.Response response =
		// PUT(Router.reverse("Users.activateUser").add("activationKey",
		// "fdsjfhdsjhfj"),"","");
		GET(Router.reverse("Users.activateUser").add("activationKey", "saod"));

		User bobActive = User.find("email=?", "bob@gmail.com").first();
		assertNotNull(bobActive);
		assertFalse(bobActive.isActive());
		assertErrorFlashed("user.invalid.activation.key");

	}

	@Test
	public void testShowRecoverPasswordForNotLoggedUser() {
		// Show the recover password to the user if it's not logged in
		Response response = GET(Router.reverse("Users.recoverPassword"));
		assertI18nHtmlTitlePresent(response,
				"views.users.recoverPassword.title");
		assertNoErrorFlashed();
	}

	@Test
	public void testDontShowRecoverPasswordLoggedUser() {
		authenticateUser("active@gmail.com", "11111", true);
		Response response = GET(Router.reverse("Users.recoverPassword"));
		assertWarningFlashed("empty.user_session.required.fail");
		assertRedirectedTo(response, "Application.index",
				new HashMap<String, Object>());
	}

	@Test
	public void testRecoverPassword() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("email", "active@gmail.com");

		ActionDefinition adef = Router.reverse("Users.resetPassword", params);
		// Response response = POST(adef, params, null);

		Response response = POST(adef);

		// Expect a mail was sent with a secret token that gives access to the
		// change password page for the user
		assertSuccessFlashed("controllers.users.resetPassword.success");
		assertRedirectedTo(response, "Application.index",
				new HashMap<String, Object>());
		// Get the user
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user.getResetPasswordSecret());
		// Assert mail sent with correct link to reset password screen

		assertTrue(Mock.getLastMessageReceivedBy("active@gmail.com").contains(
				Messages.get("controllers.users.resetPassword.mail.subject")));

		params.clear();
		params.put("resetPasswordSecret", user.getResetPasswordSecret());

		ActionDefinition ad = Router.reverse("Users.changeResetedPassword",
				params);
		ad.absolute();
		String resetPasswordLink = ad.url;
		assertTrue(Mock.getLastMessageReceivedBy("active@gmail.com").contains(
				resetPasswordLink));

	}

	@Test
	public void testFailRecoverPasswordForInexistentMail() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("email", "acsadasdasdtivse@gmail.com");

		POST(Router.reverse("Users.resetPassword", params));

		assertErrorFlashed("controllers.users.resetPassword.fail");

	}

	@Test
	public void testSaveResetedPassword() {

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("email", "active@gmail.com");

		Response response = POST(Router.reverse("Users.resetPassword", params));

		// Expect a mail was sent with a secret token that gives access to the
		// change password page for the user
		assertSuccessFlashed("controllers.users.resetPassword.success");
		assertRedirectedTo(response, "Application.index",
				new HashMap<String, Object>());
		// Get the user
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user.getResetPasswordSecret());

		params.clear();

		// need new password
		// need new password confirmation
		// need resetPasswordSecret
		params = new HashMap<String, Object>();
		params.put("newPassword", "12345");
		params.put("newPasswordConfirmation", "12345");
		params.put("resetPasswordSecret", user.getResetPasswordSecret());
		
		response = POST(Router.reverse("Users.saveNewPasswordAfterReset", params));
		// Redirect to login
		assertRedirectedTo(response, "Users.login",
				new HashMap<String, Object>());
		assertSuccessFlashed("controllers.users.saveNewPasswordAfterReset.success");
		// assert new password was saved
		user = User.find("byEmail", "active@gmail.com").first();
		assertNull(user.getResetPasswordSecret());
		assertEquals(User.generatePasswordHash("12345", user.getEmail()),
				user.getPasswordHash());
	}

	@Test
	public void testInvalidSecretInResetPassword() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("email", "active@gmail.com");

		Response response = POST(Router.reverse("Users.resetPassword", params));

		// Expect a mail was sent with a secret token that gives access to the
		// change password page for the user
		assertSuccessFlashed("controllers.users.resetPassword.success");
		assertRedirectedTo(response, "Application.index",
				new HashMap<String, Object>());
		// Get the user
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user.getResetPasswordSecret());

		params.clear();

		// need new password
		// need new password confirmation
		// need resetPasswordSecret
		params = new HashMap<String, Object>();
		params.put("newPassword", "12345");
		params.put("newPasswordConfirmation", "12345");
		params.put("resetPasswordSecret", user.getResetPasswordSecret()+"1");
		
		response = POST(Router.reverse("Users.saveNewPasswordAfterReset", params));
		assertRedirectedTo(response, "Application.index",new HashMap<String, Object>());
		assertErrorFlashed("controllers.users.saveNewPasswordAfterReset.fail");
		
	}
	
	@Test
	public void testInvalidNewPasswordInResetPassword() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("email", "active@gmail.com");

		Response response = POST(Router.reverse("Users.resetPassword", params));

		// Expect a mail was sent with a secret token that gives access to the
		// change password page for the user
		assertSuccessFlashed("controllers.users.resetPassword.success");
		assertRedirectedTo(response, "Application.index",
				new HashMap<String, Object>());
		// Get the user
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user.getResetPasswordSecret());

		params.clear();

		// need new password
		// need new password confirmation
		// need resetPasswordSecret
		params = new HashMap<String, Object>();
		params.put("newPassword", "1234");
		params.put("newPasswordConfirmation", "1234");
		params.put("resetPasswordSecret", user.getResetPasswordSecret());
		
		response = POST(Router.reverse("Users.saveNewPasswordAfterReset", params));
		assertErrorFlashed("controllers.users.saveNewPasswordAfterReset.fail");
		
	}

	@Test
	public void testInvalidNewPasswordConfirmationInResetPassword() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("email", "active@gmail.com");

		Response response = POST(Router.reverse("Users.resetPassword", params));

		// Expect a mail was sent with a secret token that gives access to the
		// change password page for the user
		assertSuccessFlashed("controllers.users.resetPassword.success");
		assertRedirectedTo(response, "Application.index",
				new HashMap<String, Object>());
		// Get the user
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user.getResetPasswordSecret());

		params.clear();

		// need new password
		// need new password confirmation
		// need resetPasswordSecret
		params = new HashMap<String, Object>();
		params.put("newPassword", "12345");
		params.put("newPasswordConfirmation", "12346");
		params.put("resetPasswordSecret", user.getResetPasswordSecret());
		
		response = POST(Router.reverse("Users.saveNewPasswordAfterReset", params));
		assertErrorFlashed("controllers.users.saveNewPasswordAfterReset.fail");
		
	}


}
