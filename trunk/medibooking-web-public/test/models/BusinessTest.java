package models;

import java.util.ArrayList;
import java.util.List;

import models.factories.TestBusinessFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.data.validation.Error;
import play.data.validation.Validation;
import play.test.Fixtures;
import play.test.UnitTest;

public class BusinessTest extends ModelUnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
		// Load users
		Fixtures.load("users.yml");
	}

	@Test
	public void officeCanHaveMultipleAdmins() {
		BusinessAdministrator admin = new BusinessAdministrator();
		admin.setEmail("joao@email.com");
		admin.setName("Some fancy name");
		admin.setPassword("123456");
		assertTrue(admin.validateAndSave());
		BusinessAdministrator admin2 = new BusinessAdministrator();
		admin2.setEmail("joao2@email.com");
		admin2.setName("Some fancy name");
		admin2.setPassword("123456");

		assertTrue(admin2.validateAndSave());

		Business office = TestBusinessFactory.createBusiness(null, null);
		Business office2 = TestBusinessFactory.createBusiness(null, null);

		office2.addAdministrator(admin2);
		office2.addAdministrator(admin);
		office.addAdministrator(admin2);
		office.addAdministrator(admin);

		assertTrue(office.validateAndSave());
		assertFalse(office.isActive());
		assertNotNull(office.getCreatedAt());
		assertNotNull(office.getModifiedAt());

		assertTrue(office2.validateAndSave());

		admin2.addAdministeredBusinesses(office);
		admin.addAdministeredBusinesses(office);

		admin.save();
		admin2.save();

		// get office
		Business o1 = Business.findById(office.id);
		assertEquals(2, o1.getAdministrators().size());
		Business o2 = Business.findById(office2.id);
		assertEquals(2, o2.getAdministrators().size());
		assertEquals(2, o2.getAdminCount());
		assertEquals(2, o1.getAdminCount());

	}

	@Test
	public void addAdministrator() {
		BusinessAdministrator admin = new BusinessAdministrator();
		admin.setEmail("jonas@email.com");
		admin.setName("Some fancy name");
		admin.setPassword("123456");
		boolean saved = admin.validateAndSave();

		// assertEntitySaved();
		assertTrue(saved);

		Business office = TestBusinessFactory.createBusiness(null, null);
		ArrayList<BusinessAdministrator> admins = new ArrayList<BusinessAdministrator>();
		admins.add(admin);
		office.setAdministrators(admins);

		// Save it
		assertTrue(office.validateAndSave());
		admin.getAdministeredBusinesses().add(office);
		Business savedOffice = Business.findById(office.id);
		assertNotNull(savedOffice);
		assertNotNull(savedOffice.getAdministrators());
		assertTrue(savedOffice.getAdministrators().size() == 1);
		for (BusinessAdministrator oa : savedOffice.getAdministrators()) {
			assertEquals("jonas@email.com", oa.getEmail());
		}

		BusinessAdministrator oa = BusinessAdministrator.findById(admin.id);
		assertEquals(1, oa.getAdministeredBusinesses().size());

		for (Business o : oa.getAdministeredBusinesses()) {
			assertTrue(o.getName().equals(office.getName()));
		}

	}


	@Test
	public void validateDuplicatedName() {

		Business partner = TestBusinessFactory.createBusiness(null, null);

		// Save it
		partner.validateAndSave();

		// retrieve it
		Business savedPartner = Business.find("name=?", partner.getName())
				.first();
		assertNotNull(savedPartner);

		Business partner2 = TestBusinessFactory.createBusiness(
				partner.getName(), null);

		// Don't save it because the name is not unique
		assertFalse(partner2.validateAndSave());

	}

	@Test
	public void createAndRetrievePartner() {

		Business office = TestBusinessFactory.createBusiness(null, null);
		// Save it

		assertTrue(office.validateAndSave());
		// Validation.current().valid(partner);
		// for (Error error : Validation.current().errors()) {
		// System.err.println(error.message());
		// }

		// retrieve it
		Business savedPartner = Business.find("name=?", office.getName())
				.first();
		assertNotNull(savedPartner);
		assertFalse(savedPartner.isActive());

	}

}
