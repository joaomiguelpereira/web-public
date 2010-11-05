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
		// Fixtures.load("partners.yml");
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
