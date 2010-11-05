package models.factories;

import models.User;

import org.junit.Ignore;

import play.Logger;

@Ignore
public abstract class TestUserFactory {

	
	public static User createUser() {
		User person = new User();
		String prefix = String.valueOf(System.nanoTime());
		Logger.debug("Creating dummy user: "+prefix);
		person.setName(prefix+"Name");
		person.setEmail(prefix+"@mail.com");
		person.setMobile("123456789");
		person.setPassword("123456789");
		person.setPhone("987654321");
		return person;
		
	}
}
