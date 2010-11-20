package functional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Office;
import models.OfficeAdministrator;
import models.User;

import models.factories.TestOfficeFactory;

import org.junit.Before;
import org.junit.Test;

import play.Logger;

import play.mvc.Http;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.test.Fixtures;

public class OfficesTest extends ApplicationFunctionalTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
		Fixtures.load("users.yml");
	}

	/**
	 * Test that a user can only see it's offices
	 */
	@Test
	public void checkOfficeOwnership() {
		// Get an office admin from Fixtures
		OfficeAdministrator oAdmin = OfficeAdministrator.find("byEmail",
				"oadmin_teste@gmail.com").first();

		assertNotNull(oAdmin);
		// Create one office
		final Office officeOne = TestOfficeFactory.createOffice("Office One",
				oAdmin);
		// Assign this office to the Admin
		oAdmin.addAdministeredOffice(officeOne);
		// Save the office
		assertTrue(officeOne.validateAndSave());
		oAdmin.save();
		// Save the admin new relationship
		// assertTrue(oAdmin.validateAndSave());
		//showValidationErrors();
		//Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);
		// Now try to get the page for Office viewing
		Response response = GET(Router.reverse("Offices.view",
				new HashMap<String, Object>() {
					{
						put("id", officeOne.id);
					}
				}));
		// Assert no error was flashed
		assertNoErrorFlashed();
		assertNoWarningFlashed();
		
		//Now create a new office
		final Office officeTwo = TestOfficeFactory.createOffice("Office Two",
				oAdmin);
		//Assigne it to oAdmin
		oAdmin.addAdministeredOffice(officeTwo);
		assertTrue(officeTwo.validateAndSave());
		oAdmin.save();
		
		response = GET(Router.reverse("Offices.view",
				new HashMap<String, Object>() {
					{
						put("id", officeTwo.id);
					}
				}));
		// Assert no error was flashed
		assertNoErrorFlashed();
		assertNoWarningFlashed();
		
		//Now add a new admin to the scene
		OfficeAdministrator oAdminTwo = OfficeAdministrator.find("byEmail", "oadmin_teste_two@gmail.com").first();
		assertNotNull(oAdminTwo);
		logoutCurrentUser();
		//uthenticate new office admin
		authenticateUser("oadmin_teste_two@gmail.com", "12345", false);
		//Try to get the office from oAdmin
		response = GET(Router.reverse("Offices.view",
				new HashMap<String, Object>() {
					{
						put("id", officeTwo.id);
					}
				}));
		// Assert no error was flashed
		assertErrorFlashed("user.not.authorized");
		
		
		
		
	}

	@Test
	public void addOfficeForOA() {
		OfficeAdministrator oAdmin = new OfficeAdministrator();
		oAdmin.setName("Some Office Admin");
		oAdmin.setEmail("someemail@gmail.com");
		oAdmin.setActive(true);
		oAdmin.setPassword("11111");
		assertTrue(oAdmin.validateAndSave());

		authenticateUser("someemail@gmail.com", "11111", false);
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("office.name", "Clinic Name");
		params.put("office.address.addressLineOne", "Address line 1");
		params.put("office.address.postalCode", "Postal Code");
		params.put("office.address.city", "City");
		params.put("office.email", "email@email.com");
		params.put("office.phone1", "123456789");

		Http.Response response = POST(Router.reverse(
				"Offices.create", params));

		assertNoErrorFlashed();
		assertSuccessFlashed("office.save.success");
		// get the user
		final OfficeAdministrator oa = User.find("byEmail",
				"someemail@gmail.com").first();
		assertNotNull(oa);
		assertEquals(1, oa.getAdministeredOffices().size());

		assertRedirectedTo(response, "Offices.list",
				new HashMap<String, Object>());

		// get the saved office
		Office office = Office.find("byEmail", "email@email.com").first();
		assertNotNull(office);
		assertEquals(1, office.getAdministrators().size());
		assertEquals(oa.id, office.getAdministrators().get(0).id);

	}

}
