package functional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.OfficeAdministrator;
import models.User;
import models.enums.UserType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import constants.CookieValuesConstants;
import controllers.Users;

import play.i18n.Messages;
import play.libs.Crypto;
import play.libs.Mail.Mock;
import play.mvc.Http;
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
		if ( Flash.current()!=null ) {
			Flash.current().clear();
		}
		
	}

	@Test
	public void testShowRegisterForOfficeAdmin() {
		Response res = withController(Users.class)
				.withArgs("userType", UserType.OFFICE_ADMIN)
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
		//get user
		
		User user = User.find("byEmail","active@gmail.com").first();
		assertNull(user.getLoginInformation().getLoginToken());
		assertNull(Scope.Session.current().get(CookieValuesConstants.LOGIN_EMAIL));
		assertNull(Scope.Session.current().get(CookieValuesConstants.LOGIN_TOKEN));
		Http.Cookie cookieValue = response.cookies.get(CookieValuesConstants.REMEMBER_ME);
		assertNull(cookieValue);
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
		assertStatus(401, response);
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
		Http.Response response = POST(Router.reverse("Users.authenticate", params));
		assertSuccessFlashed("login.successful");
		User user = User.find("byEmail", "active@gmail.com").first();
		assertEquals(Long.valueOf(1L), user.getLoginInformation()
				.getSuccessfulLoginCount());
		
		assertEquals(Scope.Session.current().get(CookieValuesConstants.LOGIN_EMAIL), "active@gmail.com");
		assertEquals(Scope.Session.current().get(CookieValuesConstants.LOGIN_TOKEN), user.getLoginInformation().getLoginToken());
		
	}

	@Test
	public void testRememberMeFunction() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", "active@gmail.com");
		params.put("password", "11111");
		params.put("keepLogged", true);
		Http.Response response = POST(Router.reverse("Users.authenticate", params));
		assertSuccessFlashed("login.successful");
		User user = User.find("byEmail", "active@gmail.com").first();
		assertEquals(Long.valueOf(1L), user.getLoginInformation()
				.getSuccessfulLoginCount());
		assertEquals(Scope.Session.current().get(CookieValuesConstants.LOGIN_EMAIL), "active@gmail.com");
		assertEquals(Scope.Session.current().get(CookieValuesConstants.LOGIN_TOKEN), user.getLoginInformation().getLoginToken());
		Http.Cookie cookieValue = response.cookies.get(CookieValuesConstants.REMEMBER_ME);
		assertEquals(Crypto.sign(user.getEmail()) + "-" + user.getEmail(),cookieValue.value);
		
	}

	@Test
	public void testRegisterUser() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user.name", "user name");
		params.put("user.email", "myemail@gmail.com");
		params.put("user.password", "MyPassword");
		params.put("userType", UserType.OFFICE_ADMIN);
		params.put("emailConfirmation", "myemail@gmail.com");
		params.put("passwordConfirmation", "MyPassword");
		params.put("termsAgreement", true);
		Http.Response response = POST(Router.reverse("Users.save", params));
		assertNoErrorFlashed();
		assertSuccessFlashed("user.register.success");
		assertStatus(200, response);
		// Find the user in DB
		OfficeAdministrator oa = OfficeAdministrator.find("email=?",
				"myemail@gmail.com").first();
		assertNotNull(oa);
		assertFalse(oa.isActive());
		assertTrue(Mock.getLastMessageReceivedBy("myemail@gmail.com").contains(
				Messages.get("email.user.activation.subject", oa.getName())));
		assertTrue(Mock.getLastMessageReceivedBy("myemail@gmail.com").contains(
				oa.getActivationUUID()));
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

}
