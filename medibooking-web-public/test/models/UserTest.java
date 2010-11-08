package models;

import models.enums.UserType;
import models.factories.TestUserFactory;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends ModelUnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
		Fixtures.load("users.yml");
	}

	@Test
	public void invalidateUserLoginToken() {
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user);
		assertTrue(user.isActive());
		String token = user.authenticate("11111", "127.0.0.1");
		assertNotNull(token);
		
		assertEquals(Integer.valueOf(0), user.getLoginInformation().getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(0), user.getLoginInformation().getUnsuccessfullLoginCountBeforeSuccessfulLogin());
		assertEquals(Long.valueOf(1l), user.getLoginInformation().getSuccessfulLoginCount());
		assertEquals("127.0.0.1", user.getLoginInformation().getLastLoginClientIP());
		
		user.invalidateLoginToken();
		assertNull(user.getLoginInformation().getLoginToken());
	}
	@Test
	public void dontAuthenticateInnactiveUser() {
		User innactiveUser = User.find("byEmail", "bob@gmail.com").first();
		assertNotNull(innactiveUser);
		assertFalse(innactiveUser.isActive());
		String token = innactiveUser.authenticate("11111", "127.0.0.1");
		
		assertNull(token);
		
		assertEquals(Integer.valueOf(1), innactiveUser.getLoginInformation().getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(1), innactiveUser.getLoginInformation().getUnsuccessfullLoginCountBeforeSuccessfulLogin());
	}
	
	@Test
	public void dontAuthenticateInvalidPassword() {
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user);
		assertTrue(user.isActive());
		String token = user.authenticate("1232", "127.0.0.1");
		assertNull(token);
		assertEquals(Integer.valueOf(1), user.getLoginInformation().getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(1), user.getLoginInformation().getUnsuccessfullLoginCountBeforeSuccessfulLogin());
	}
	
	@Test
	public void authenticateUser() {
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user);
		assertTrue(user.isActive());
		String token = user.authenticate("11111", "127.0.0.1");
		assertNotNull(token);
		
		assertEquals(Integer.valueOf(0), user.getLoginInformation().getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(0), user.getLoginInformation().getUnsuccessfullLoginCountBeforeSuccessfulLogin());
		assertEquals(Long.valueOf(1l), user.getLoginInformation().getSuccessfulLoginCount());
		assertEquals("127.0.0.1", user.getLoginInformation().getLastLoginClientIP());
	}
	
	@Test
	public void authenticateUserResetCounter() {
		User user = User.find("byEmail", "active@gmail.com").first();
		assertNotNull(user);
		assertTrue(user.isActive());
		String token = user.authenticate("111asd1", "127.0.0.1");
		assertNull(token);
		assertEquals(Integer.valueOf(1), user.getLoginInformation().getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(1), user.getLoginInformation().getUnsuccessfullLoginCountBeforeSuccessfulLogin());
		assertEquals(Long.valueOf(0l), user.getLoginInformation().getSuccessfulLoginCount());
		assertEquals("127.0.0.1", user.getLoginInformation().getLastLoginClientIP());
		token = user.authenticate("11111", "127.0.0.1");
		assertNotNull(token);
		assertEquals(Integer.valueOf(1), user.getLoginInformation().getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(0), user.getLoginInformation().getUnsuccessfullLoginCountBeforeSuccessfulLogin());
		assertEquals(Long.valueOf(1l), user.getLoginInformation().getSuccessfulLoginCount());
		
	}
	@Test
	public void dontAuthenticateInnactiveUserTestCounters() {
		User innactiveUser = User.find("byEmail", "bob@gmail.com").first();
		assertNotNull(innactiveUser);
		assertFalse(innactiveUser.isActive());
		String token = innactiveUser.authenticate("11111", "127.0.0.1");
		assertNull(token);
		
		assertEquals(Integer.valueOf(1), innactiveUser.getLoginInformation().getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(1), innactiveUser.getLoginInformation().getUnsuccessfullLoginCountBeforeSuccessfulLogin());
		innactiveUser = User.find("byEmail", "bob@gmail.com").first();
		token = innactiveUser.authenticate("11111", "127.0.0.1");
		assertNull(token);
		assertEquals(Integer.valueOf(2), innactiveUser.getLoginInformation().getUnsuccessfullLoginCount());
		assertEquals(Integer.valueOf(2), innactiveUser.getLoginInformation().getUnsuccessfullLoginCountBeforeSuccessfulLogin());
	}
	
	
	
	@Test
	public void dontCreateDuplicatedEmail() {
		User user = TestUserFactory.createUser();
		assertTrue(user.validateAndSave());
		assertNotNull(User.find("name=?", user.getName()).first());

		User person2 = TestUserFactory.createUser();
		person2.setEmail(user.getEmail());
		assertFalse(person2.validateAndSave());
	}

	@Test
	public void testDifferentUUID() {
		User user = TestUserFactory.createUser();
		assertTrue(user.validateAndSave());

		User user2 = TestUserFactory.createUser();
		assertTrue(user2.validateAndSave());

		assertNotSame(user.getActivationUUID(), user2.getActivationUUID());

	}

	@Test
	public void createAndRetrievePerson() {
		User user = TestUserFactory.createUser();
		assertTrue(user.validateAndSave());	

		assertNotNull(user.getPasswordHash());
		assertFalse(user.isActive());
		assertEquals(UserType.USER, user.getUserType());
		assertNotNull(user.getActivationUUID());
		assertNotNull(User.find("name=?", user.getName()).first());
	}
}
