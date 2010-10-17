package models;

import org.junit.Ignore;

@Ignore
public abstract class PersonFactory {

	
	public static Person createPerson() {
		Person person = new Person();
		String prefix = String.valueOf(System.nanoTime());
		person.setName(prefix+"Name");
		person.setEmail(prefix+"@mail.com");
		person.setMobile("123456789");
		person.setPassword("123456789");
		person.setPhone("987654321");
		return person;
		
	}
}
