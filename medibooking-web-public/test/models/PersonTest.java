package models;

import org.junit.Test;

import play.test.UnitTest;

public class PersonTest extends UnitTest {

	@Test
	public void dontCreateDuplicatedEmail() {
		Person person = PersonFactory.createPerson();
		assertTrue(person.validateAndSave());
		assertNotNull(Person.find("name=?",person.getName()).first());
		
		Person person2 = PersonFactory.createPerson();
		person2.setEmail(person.getEmail());
		assertFalse(person2.validateAndSave());
	}
	
	@Test
	public void createAndRetrievePerson() {
		Person person = PersonFactory.createPerson();
		assertTrue(person.validateAndSave());
		assertNotNull(Person.find("name=?",person.getName()).first());
		
	}
}
