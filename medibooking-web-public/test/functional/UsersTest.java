package functional;

import java.util.HashMap;
import java.util.Map;

import models.enums.UserType;

import org.junit.Test;

import controllers.Users;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.mvc.Router.Route;
import play.test.FunctionalTest;

public class UsersTest extends ApplicationFunctionalTest {
	
	
	@Test
	public void testShowRegisterForOfficeAdmin() {
		Response res = withController(Users.class).withArgs("userType",UserType.OFFICE_ADMIN).withAction("blank").get();
		assertIsOk(res);	
	}
	
	@Test
	public void testRegisterNewOfficeAdmin() {
		
	}

}

