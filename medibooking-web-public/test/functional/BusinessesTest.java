package functional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Business;
import models.BusinessAdministrator;
import models.Email;
import models.Phone;
import models.User;

import models.factories.TestBusinessFactory;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import play.Logger;

import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.mvc.Router.Route;
import play.test.Fixtures;

public class BusinessesTest extends ApplicationFunctionalTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
		Fixtures.load("users.yml");
	}
	@Test
	public void testDeletePhone() {
		//Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		Business business = TestBusinessFactory.createBusiness("Test Business2", ba, true);
		
		//add one phone to it
		List<Phone> phones = new ArrayList<Phone>();
		Phone phone = new Phone();
		phone.setDescription("Desc");
		phone.setName("Principal");
		phone.setPhone("12345678");
		phones.add(phone);
		business.setPhones(phones);
	
		//Add short intro
		business.setShortIntroduction("Some short introductions here");
		//Save business
		business.save();
		assertNotNull(phone.id);
		
		//Now create request to remove a phone number. 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", business.id);
		params.put("phoneId", phone.id);
		
		Response response =  DELETE(Router.reverse("Businesses.removePhone", params).url);
		
		
		//Now check response
		assertJSONSuccess(response, "business.phone.removed.success");
			
		Business bu = Business.find("byName", "Test Business2").first();
		assertEquals(0,bu.getPhones().size());
		
	
	}

	@Test
	public void testFailDeletePhoneNotExisting() {
		//Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		Business business = TestBusinessFactory.createBusiness("Test Business2", ba, true);
		business.save();
				
		//Now create request to remove a phone number. 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", business.id);
		params.put("phoneId", 12121212);
		
		Response response =  DELETE(Router.reverse("Businesses.removePhone", params).url);
		
		
		//Now check response
		assertJSONError(response, "business.phone.removed.fail.phone.not.found");
			
	
	}
	
	@Test
	public void testFailDeletePhoneNotExistingBusiness() {
		//Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		Business business = TestBusinessFactory.createBusiness("Test Business2", ba, true);
		
		//add one phone to it
		List<Phone> phones = new ArrayList<Phone>();
		Phone phone = new Phone();
		phone.setDescription("Desc");
		phone.setName("Principal");
		phone.setPhone("12345678");
		phones.add(phone);
		business.setPhones(phones);
	
		//Add short intro
		business.setShortIntroduction("Some short introductions here");
		//Save business
		business.save();
		assertNotNull(phone.id);
		
		//Now create request to remove a phone number. 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 12322);
		params.put("phoneId", phone.id);
		
		Response response =  DELETE(Router.reverse("Businesses.removePhone", params).url);
		
		
		//Now check response
		assertJSONError(response, "business.phone.removed.fail.business.not.found");
			
	
	}



	@Test
	public void testFailDeletePhoneInvalidParentBusiness() {
		//Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		
		Business business = TestBusinessFactory.createBusiness("Test Business2", ba, true);
		Business business0 = TestBusinessFactory.createBusiness("Test Business3", ba, true);
		
		//add one phone to it
		List<Phone> phones = new ArrayList<Phone>();
		Phone phone = new Phone();
		phone.setDescription("Desc");
		phone.setName("Principal");
		phone.setPhone("12345678");
		phones.add(phone);
		business.setPhones(phones);
	
		//Add short intro
		business.setShortIntroduction("Some short introductions here");
		//Save business
		business.save();
		business0.save();
		
		assertNotNull(phone.id);
		
		//Now create request to remove a phone number. 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", business0.id);
		params.put("phoneId", phone.id);
		
		Response response =  DELETE(Router.reverse("Businesses.removePhone", params).url);
		
		
		//Now check response
		assertJSONError(response, "business.phone.removed.fail.phone.invalid.parent");
			
	
	}

	@Test
	public void testActivateBusiness() {

		//Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		Business business = TestBusinessFactory.createBusiness("Test Business2", ba, true);
		
		List<Phone> phones = new ArrayList<Phone>();
		Phone phone = new Phone();
		phone.setDescription("Desc");
		phone.setName("Principal");
		phone.setPhone("12345678");
		phones.add(phone);
		business.setPhones(phones);
		
		List<Email> emails = new ArrayList<Email>();
		Email email = new Email();
		email.setEmail("jonas@gmail.com");
		email.setDescription("Description");
		email.setName("principal");
		emails.add(email);
		business.setEmails(emails);
		
		business.setShortIntroduction("Some short introductions here");
		business.save();
		
		//Now create request to activate
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", business.id);
		Response response =  POST(Router.reverse("Businesses.activate", params));
		
		Map<String, String> expectedMap = new HashMap<String, String>();
		expectedMap.put("active", Boolean.TRUE.toString());
		assertJSONResponse(response, new Gson().toJson(expectedMap));
		
		Business bu = Business.find("byName", "Test Business2").first();
		assertTrue(bu.isActive());

	}
	
	
	

	@Test
	public void testDontActivateBusinessWithNoCompleteInformation() {
		//Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);
		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		final Long businessId = TestBusinessFactory.createBusiness("Test Business", ba, true).id;
		//Now create request to activate
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", businessId);
		Response response =  POST(Router.reverse("Businesses.activate", params));
		
		assertJSONError(response, "controllers.businesses.activate.fail.incomplete.info");
		
	}

	
	@Test
	public void testTestFactoryStuff() {
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		Business business = TestBusinessFactory.createBusiness("Test Business", ba, true);
		//Assert Factory stuff
		//Get the BA
		BusinessAdministrator ba2 = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		//Assert it has one linic
		assertEquals(1, ba2.getAdministeredBusinesses().size());
		//Get the business
		Business business2 = ba2.getAdministeredBusinesses().get(0);
		assertNotNull(business2);
		//Assert owner
		assertEquals(1,business2.getAdministrators().size());
		
		//End Factory assertion stuff
		
	}
	
	
	@Test
	public void deleteBusiness() {
		
		//Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);
		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		final Long businessId = TestBusinessFactory.createBusiness("Test Business", ba, true).id;
		
		
		//Now create request to delete
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", businessId);
		Response response =  DELETE(Router.reverse("Businesses.delete", params).url);
		//Now, what should happen?
		//Ba should have no business at all
		//Get the BA
		BusinessAdministrator ba2 = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertEquals(0, ba.getAdministeredBusinesses().size());
		//The Business should not exists any more
		Business business = Business.findById(businessId);
		assertNull(business);
		//Should flash success message
		assertSuccessFlashed("controllers.businesses.delete.success");
		//Should redirect to Business list
		assertRedirectedTo(response, "Businesses.list", new HashMap<String, Object>());
	}



	@Test
	public void deleteBusinessForNotLoggedIn() {
		//Authenticate user
		//authenticateUser("oadmin_teste@gmail.com", "12345", false);
		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		final Long businessId = TestBusinessFactory.createBusiness("Test Business", ba, true).id;
		
		
		//Now create request to delete
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", businessId);
		
		Response response =  DELETE(Router.reverse("Businesses.delete", params).url);
		assertErrorFlashed("user.not.authorized");
		assertRedirectedTo(response, "Users.login", new HashMap<String, Object>());
	}

	@Test
	public void deleteBusinessForNotSuperOwner() {
		//Authenticate user
		authenticateUser("oadmin_teste_two@gmail.com", "12345", false);
		
		//Get the Admin
		BusinessAdministrator ba = BusinessAdministrator.find("byEmail", "oadmin_teste@gmail.com").first();
		assertNotNull(ba);
		//Create a business
		final Long businessId = TestBusinessFactory.createBusiness("Test Business", ba, true).id;
		
		
		//Now create request to delete
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", businessId);
		
		Response response =  DELETE(Router.reverse("Businesses.delete", params).url);
		assertErrorFlashed("user.not.business.owner");
		assertRedirectedTo(response, "Businesses.list", new HashMap<String, Object>());
	}
	
	/**
	 * Test that a user can only see it's offices
	 */
	@Test
	public void testCheckBusinessOwnership() {
		// Get an office admin from Fixtures
		BusinessAdministrator oAdmin = BusinessAdministrator.find("byEmail",
				"oadmin_teste@gmail.com").first();

		assertNotNull(oAdmin);
		// Create one office
		final Business businessOne = TestBusinessFactory.createBusiness(
				"Office One", oAdmin);
		// Assign this office to the Admin
		businessOne.setSuperAdmin(oAdmin);
		oAdmin.addAdministeredBusinesses(businessOne);
		
		// Save the office
		assertTrue(businessOne.validateAndSave());
		oAdmin.save();
		assertNotNull(businessOne.id);
		// Save the admin new relationship
		// assertTrue(oAdmin.validateAndSave());
		// showValidationErrors();
		// Authenticate user
		authenticateUser("oadmin_teste@gmail.com", "12345", false);
		// Now try to get the page for Office viewing
		Response response = GET(Router.reverse("Businesses.view",
				new HashMap<String, Object>() {
					{
						put("id", businessOne.id);
					}
				}));
		// Assert no error was flashed
		assertNoErrorFlashed();
		assertNoWarningFlashed();

		// Now create a new office
		final Business businessTwo = TestBusinessFactory.createBusiness(
				"Office Two", oAdmin);
		// Assigne it to oAdmin
		oAdmin.addAdministeredBusinesses(businessTwo);
		assertTrue(businessTwo.validateAndSave());
		oAdmin.save();

		response = GET(Router.reverse("Businesses.view",
				new HashMap<String, Object>() {
					{
						put("id", businessTwo.id);
					}
				}));
		// Assert no error was flashed
		assertNoErrorFlashed();
		assertNoWarningFlashed();

		// Now add a new admin to the scene
		BusinessAdministrator oAdminTwo = BusinessAdministrator.find("byEmail",
				"oadmin_teste_two@gmail.com").first();
		assertNotNull(oAdminTwo);
		logoutCurrentUser();
		// uthenticate new office admin
		authenticateUser("oadmin_teste_two@gmail.com", "12345", false);
		// Try to get the office from oAdmin
		response = GET(Router.reverse("Businesses.view",
				new HashMap<String, Object>() {
					{
						put("id", businessTwo.id);
					}
				}));
		// Assert no error was flashed
		assertErrorFlashed("user.not.authorized");

	}

	@Test
	public void testCreateBusiness() {
		BusinessAdministrator bAdmin = new BusinessAdministrator();
		bAdmin.setName("Some Office Admin");
		bAdmin.setEmail("someemail@gmail.com");
		bAdmin.setActive(true);
		bAdmin.setPassword("11111");
		assertTrue(bAdmin.validateAndSave());

		authenticateUser("someemail@gmail.com", "11111", false);
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("business.name", "Clinic Name");
		params.put("business.address.addressLineOne", "Address line 1");
		params.put("business.address.postalCode", "Postal Code");
		params.put("business.address.city", "City");
		
		Http.Response response = POST(Router.reverse("Businesses.create",
				params));

		assertNoErrorFlashed();
		assertSuccessFlashed("business.create.success");
		// get the user
		final BusinessAdministrator oa = User.find("byEmail",
				"someemail@gmail.com").first();
		assertNotNull(oa);
		assertEquals(1, oa.getAdministeredBusinesses().size());

		assertRedirectedTo(response, "Businesses.view",
				new HashMap<String, Object>(){{put("id",oa.getAdministeredBusinesses().get(0).id);}});

		// get the saved office
		Business business = Business.find("byName", "Clinic Name").first();
		assertNotNull(business);
		assertEquals(1, business.getAdministrators().size());
		assertEquals(oa.id, business.getAdministrators().get(0).id);

	}

}
