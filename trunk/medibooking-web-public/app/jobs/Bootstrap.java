package jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jj.play.ns.com.jhlabs.image.CraterFilter;

import models.Address;
import models.Administrator;
import models.Office;
import models.OfficeAdministrator;
import models.User;
import models.enums.BusinessType;
import models.enums.UserType;
import models.factories.TestOfficeFactory;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Scope.Session;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job<String> {

	@Override
	public void doJob() throws Exception {

		// loadHSQLConsole();
		// load dummy users
		prepareTestData();
	}

	private void prepareTestData() {
		Fixtures.deleteAll();
		User user = new User();

		user.setName("Regular User");
		user.setActive(true);
		user.setEmail("user@gmail.com");
		user.setPassword("12345");
		user.save();

		OfficeAdministrator oAdmin = new OfficeAdministrator();
		oAdmin.setName("Office admin");
		oAdmin.setActive(true);
		oAdmin.setEmail("oadmin@gmail.com");
		oAdmin.setPassword("12345");
		oAdmin.save();

		OfficeAdministrator oAdmin2 = new OfficeAdministrator();
		oAdmin2.setName("Office admin2");
		oAdmin2.setActive(true);
		oAdmin2.setEmail("oadmin2@gmail.com");
		oAdmin2.setPassword("12345");
		oAdmin2.save();

		Administrator admin = new Administrator();
		admin.setName("Service Admin");
		admin.setActive(true);
		admin.setEmail("admin@gmail.com");
		admin.setPassword("12345");
		admin.save();

		// create at least two clinics for user oadmin

		
		List<Office> offices = new ArrayList<Office>();
		for (int i=0; i<10; i++) {
			Office office = createTestOffice("my Office nbr "+i);
			office.addAdministrator(oAdmin);
			offices.add(office);	
			oAdmin.addAdministeredOffice(office);
		}
		
		//createTestOffice(oAdmin, "Office nbr asd");
		
		
		oAdmin.save();

	}

	private Office createTestOffice(String officeName) {
		final BusinessType businessType = BusinessType.OFFICE;
		final String addressLineOne = "Address Line ";
		final String addressLineTwo = "Address Line 2";
		final String addressPostalCode = "Postal Code";
		final String addressCity = "City";
		final String addressCountry = "Country";
		final String phoneNumber1 = "123456789";
		final String phoneNumber2 = "987654321";

		Office office = new Office();

		office.setName(officeName);
		office.setBusinessType(businessType);
		office.setPhone1(phoneNumber1);
		office.setPhone2(phoneNumber2);

		Address officeAddress = new Address();
		officeAddress.setAddressLineOne(addressLineOne+officeName);
		officeAddress.setAddressLineTwo(addressLineTwo);
		officeAddress.setPostalCode(addressPostalCode);
		officeAddress.setCity(addressCity);
		officeAddress.setCountry(addressCountry);
		office.setAddress(officeAddress);
		return office;

	}
	
	private void loadHSQLConsole() {
		org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url",
				"jdbc:hsqldb:mem:playembed", "--noexit" });

	}
}
