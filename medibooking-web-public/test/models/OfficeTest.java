package models;

import java.util.List;
import java.util.TreeSet;

import models.enums.BusinessType;
import models.factories.TestOfficeFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.data.validation.Error;
import play.data.validation.Validation;
import play.test.Fixtures;
import play.test.UnitTest;

public class OfficeTest extends ModelUnitTest{

	@Before
	public void setup() {
		Fixtures.deleteAll();
		// Fixtures.load("partners.yml");
	}

	
	@Test
	public void addAdministrator() {
		OfficeAdministrator admin = new OfficeAdministrator();
		admin.setEmail("joao@email.com");
		admin.setName("Some fancy name");
		admin.setPassword("123456");
		boolean saved = admin.validateAndSave();
		
		
		assertEntitySaved();
		//assertTrue(admin.validateAndSave());
		
		Office office = TestOfficeFactory.createOffice(null);
		TreeSet<OfficeAdministrator> admins = new TreeSet<OfficeAdministrator>();
		admins.add(admin);
		office.setAdministrators(admins);
		
		// Save it
		assertTrue(office.validateAndSave());
		admin.getAdministeredOffices().add(office);
		Office savedOffice = Office.findById(office.id);
		assertNotNull(savedOffice);
		assertNotNull(savedOffice.getAdministrators());
		assertTrue(savedOffice.getAdministrators().size()==1);
		for(OfficeAdministrator oa: savedOffice.getAdministrators() ) {
			assertEquals("joao@email.com", oa.getEmail());
		}

		OfficeAdministrator oa = OfficeAdministrator.findById(admin.id);
		assertEquals(1, oa.getAdministeredOffices().size());
		
		for (Office o : oa.getAdministeredOffices() ) {
			assertTrue(o.getName().equals(office.getName()));
		}
		
	}

	@Test
	public void invalidPhones() {

		Office partner = TestOfficeFactory.createOffice(null);

		partner.setPhone1("invalid1");
		// Save it
		assertFalse(partner.validateAndSave());

		partner.setPhone1("2345228228");

		// Save it
		assertFalse(partner.validateAndSave());

	}

	@Test
	public void validateDuplicatedName() {

		Office partner = TestOfficeFactory.createOffice(null);

		// Save it
		partner.validateAndSave();

		// retrieve it
		Office savedPartner = Office.find("name=?", partner.getName()).first();
		assertNotNull(savedPartner);

		Office partner2 = TestOfficeFactory.createOffice(partner.getName());

		// Don't save it because the name is not unique
		assertFalse(partner2.validateAndSave());

	}

	@Test
	public void createAndRetrievePartner() {

		Office partner = TestOfficeFactory.createOffice(null);
		// Save it
		
		assertTrue(partner.validateAndSave());
		//Validation.current().valid(partner);
		//for (Error error : Validation.current().errors()) {
		//	System.err.println(error.message());
		//}

		// retrieve it
		Office savedPartner = Office.find("name=?", partner.getName()).first();
		assertNotNull(savedPartner);
		assertFalse(savedPartner.isActive());

	}

}
