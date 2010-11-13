package functional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.data.validation.Validation;
import play.mvc.Http;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;
import play.mvc.results.Error;
import play.test.Fixtures;

import models.Address;
import models.Office;
import models.OfficeAdministrator;
import models.User;
import models.enums.BusinessType;

public class OfficesTest extends ApplicationFunctionalTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
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
		params.put("office.businessType", BusinessType.CLINIC);
		params.put("office.name", "Clinic Name");
		params.put("office.address.addressLineOne", "Address line 1");
		params.put("office.address.postalCode", "Postal Code");
		params.put("office.address.city", "City");
		params.put("office.email", "email@email.com");
		params.put("office.phone1", "123456789");

		Http.Response response = POST(Router.reverse(
				"Offices.savePreRegistration", params));

		assertNoErrorFlashed();
		assertSuccessFlashed("office.register.success");
		// get the user
		final OfficeAdministrator oa = User.find("byEmail", "someemail@gmail.com")
				.first();
		assertNotNull(oa);
		assertEquals(1, oa.getAdministeredOffices().size());
		
		assertRedirectedTo(response.getHeader("Location"),
				"Offices.listUserOffices",
				new HashMap<String, Object>());

		//get the saved office
		Office office = Office.find("byEmail", "email@email.com").first();
		assertNotNull(office);
		assertEquals(1, office.getAdministrators().size());
		assertEquals(oa.id, office.getAdministrators().get(0).id);
		
		

	}

}
