package models;

import java.util.ArrayList;
import java.util.List;

import models.factories.TestOfficeFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.data.validation.Error;
import play.data.validation.Validation;
import play.test.Fixtures;
import play.test.UnitTest;

public class OfficeTest extends ModelUnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
		// Fixtures.load("partners.yml");
	}

	@Test
	public void officeCanHaveMultipleAdmins() {
		OfficeAdministrator admin = new OfficeAdministrator();
		admin.setEmail("joao@email.com");
		admin.setName("Some fancy name");
		admin.setPassword("123456");
		assertTrue(admin.validateAndSave());
		OfficeAdministrator admin2 = new OfficeAdministrator();
		admin2.setEmail("joao2@email.com");
		admin2.setName("Some fancy name");
		admin2.setPassword("123456");

		assertTrue(admin2.validateAndSave());
		

		Office office = TestOfficeFactory.createOffice(null,null);
		Office office2 = TestOfficeFactory.createOffice(null,null);

		office2.addAdministrator(admin2);
		office2.addAdministrator(admin);
		office.addAdministrator(admin2);
		office.addAdministrator(admin);

		assertTrue(office.validateAndSave());
		assertFalse(office.isActive());
		assertNotNull(office.getCreatedAt());
		assertNotNull(office.getModifiedAt());
		
		assertTrue(office2.validateAndSave());

		admin2.addAdministeredOffice(office);
		admin.addAdministeredOffice(office);

		admin.save();
		admin2.save();

		// get office
		Office o1 = Office.findById(office.id);
		assertEquals(2, o1.getAdministrators().size());
		Office o2 = Office.findById(office2.id);
		assertEquals(2, o2.getAdministrators().size());
		assertEquals(2, o2.getAdminCount());
		assertEquals(2, o1.getAdminCount());
		
		
		

	}

	@Test
	public void addAdministrator() {
		OfficeAdministrator admin = new OfficeAdministrator();
		admin.setEmail("joao@email.com");
		admin.setName("Some fancy name");
		admin.setPassword("123456");
		boolean saved = admin.validateAndSave();

		assertEntitySaved();
		// assertTrue(admin.validateAndSave());

		Office office = TestOfficeFactory.createOffice(null,null);
		ArrayList<OfficeAdministrator> admins = new ArrayList<OfficeAdministrator>();
		admins.add(admin);
		office.setAdministrators(admins);

		// Save it
		assertTrue(office.validateAndSave());
		admin.getAdministeredOffices().add(office);
		Office savedOffice = Office.findById(office.id);
		assertNotNull(savedOffice);
		assertNotNull(savedOffice.getAdministrators());
		assertTrue(savedOffice.getAdministrators().size() == 1);
		for (OfficeAdministrator oa : savedOffice.getAdministrators()) {
			assertEquals("joao@email.com", oa.getEmail());
		}

		OfficeAdministrator oa = OfficeAdministrator.findById(admin.id);
		assertEquals(1, oa.getAdministeredOffices().size());

		for (Office o : oa.getAdministeredOffices()) {
			assertTrue(o.getName().equals(office.getName()));
		}

	}

	@Test
	public void invalidPhones() {

		Office partner = TestOfficeFactory.createOffice(null,null);

		partner.setPhone1("invalid1");
		// Save it
		assertFalse(partner.validateAndSave());

		partner.setPhone1("2345228228");

		// Save it
		assertFalse(partner.validateAndSave());

	}

	@Test
	public void validateDuplicatedName() {

		Office partner = TestOfficeFactory.createOffice(null,null);

		// Save it
		partner.validateAndSave();

		// retrieve it
		Office savedPartner = Office.find("name=?", partner.getName()).first();
		assertNotNull(savedPartner);

		Office partner2 = TestOfficeFactory.createOffice(partner.getName(),null);

		// Don't save it because the name is not unique
		assertFalse(partner2.validateAndSave());

	}

	@Test
	public void createAndRetrievePartner() {

		Office office = TestOfficeFactory.createOffice(null,null);
		// Save it

		assertTrue(office.validateAndSave());
		// Validation.current().valid(partner);
		// for (Error error : Validation.current().errors()) {
		// System.err.println(error.message());
		// }

		// retrieve it
		Office savedPartner = Office.find("name=?", office.getName()).first();
		assertNotNull(savedPartner);
		assertFalse(savedPartner.isActive());

	}

}
