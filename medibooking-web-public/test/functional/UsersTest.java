package functional;

import java.util.HashMap;
import java.util.Map;

import models.OfficeAdministrator;
import models.User;
import models.enums.UserType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import controllers.Users;

import play.i18n.Messages;
import play.libs.Mail.Mock;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.mvc.Router.Route;
import play.mvc.Scope.Flash;
import play.test.Fixtures;
import play.test.FunctionalTest;

public class UsersTest extends ApplicationFunctionalTest {

	
	@Before
	public void setup() {
		Fixtures.deleteAll();
		Fixtures.load("users.yml");
	}
	@Test
	public void testShowRegisterForOfficeAdmin() {
		Response res = withController(Users.class)
				.withArgs("userType", UserType.OFFICE_ADMIN)
				.withAction("blank").get();
		assertIsOk(res);
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
		Http.Response response = POST(Router.reverse("Users.save",params));
		assertNoErrorFlashed();
		assertSuccessFlashed("user.register.success");
		assertStatus(200, response);
		//Find the user in DB
		OfficeAdministrator oa = OfficeAdministrator.find("email=?","myemail@gmail.com").first();
		assertNotNull(oa);
		assertFalse(oa.isActive());
		assertTrue(Mock.getLastMessageReceivedBy("myemail@gmail.com").contains(Messages.get("email.user.activation.subject",oa.getName())));
		assertTrue(Mock.getLastMessageReceivedBy("myemail@gmail.com").contains(oa.getActivationUUID()));
		//Assert it contains the correct link for activation
		
		
	}
	
	@Test
	public void testActivateUser() {
		//get user bob
		User bob = User.find("email=?","bob@gmail.com").first();
		assertNotNull(bob);
		assertFalse(bob.isActive());
		//Now make a put
		
		//Http.Response response = PUT(Router.reverse("Users.activateUser").add("activationKey", bob.getActivationUUID()),"","");
		
		GET(Router.reverse("Users.activateUser").add("activationKey", bob.getActivationUUID()));
		User bobActive = User.find("email=?","bob@gmail.com").first();
		assertNotNull(bobActive);
		assertTrue(bobActive.isActive());
		assertSuccessFlashed("user.activated");
		assertNoErrorFlashed();
	}
	
	@Test
	public void testActivateActiveUser() {
		User activeUser = User.find("email=?", "active@gmail.com").first();
		//PUT(Router.reverse("Users.activateUser").add("activationKey", activeUser.getActivationUUID()),"","");
		GET(Router.reverse("Users.activateUser").add("activationKey", activeUser.getActivationUUID()));
		
		assertWarningFlashed("user.already.activated");
	}
	@Test
	public void testActivateUserInvalidCode() {
		//get user bob
		User bob = User.find("email=?","bob@gmail.com").first();
		assertNotNull(bob);
		assertFalse(bob.isActive());
		//Now make a put
		//Http.Response response = PUT(Router.reverse("Users.activateUser").add("activationKey", "fdsjfhdsjhfj"),"","");
		GET(Router.reverse("Users.activateUser").add("activationKey", "saod"));
		
		User bobActive = User.find("email=?","bob@gmail.com").first();
		assertNotNull(bobActive);
		assertFalse(bobActive.isActive());
		assertErrorFlash("user.invalid.activation.key");
		
	}
	

}
