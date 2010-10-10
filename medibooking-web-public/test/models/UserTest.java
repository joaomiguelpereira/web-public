package models;

import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class UserTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
		Fixtures.load("users.yml");
	}
	
	

	@Test
	public void createAndRetrieveUser() {
		
		final String email = "joaomiguel.pereira@gmail.com";
		final String fullName = "João Pereira";
		// Create new user
		new User(fullName, email, "mypassword").save();

		// Retrieve the user with eamil=email
		User user = User.find("email='" + email + "'").first();
		assertNotNull(user);
		assertEquals(fullName, user.fullName);
	}

	@Test
	public void tryConnectAsUser() {
		final String email = "joaomiguel.pereira@gmail.com";
		final String fullName = "João Pereira";
		final String password = "myPasswod";
		// Create new user
		new User(fullName, email, password).save();

		// test it can connect
		assertNotNull(User.connect(email, password));

		// test can't connect
		assertNull(User.connect(email, "badpassword"));
	}

}
