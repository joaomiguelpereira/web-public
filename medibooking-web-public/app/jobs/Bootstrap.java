package jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jj.play.ns.com.jhlabs.image.CraterFilter;

import models.Address;
import models.Administrator;
import models.Business;
import models.BusinessAdministrator;
import models.User;
import models.enums.UserType;
import models.factories.TestBusinessFactory;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Scope.Session;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job<String> {

	@Override
	public void doJob() throws Exception {

		//loadHSQLConsole();
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

		BusinessAdministrator oAdmin = new BusinessAdministrator();
		oAdmin.setName("Office admin");
		oAdmin.setActive(true);
		oAdmin.setEmail("oadmin@gmail.com");
		oAdmin.setPassword("12345");
		oAdmin.save();

		BusinessAdministrator oAdmin2 = new BusinessAdministrator();
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

		
		List<Business> businesses = new ArrayList<Business>();
		Long totalStartTime = System.nanoTime();
		for (int i=0; i<5; i++) {
			Long startTime = System.nanoTime();
			
			Business office = createTestBusiness("my Office nbr "+i);
			office.addAdministrator(oAdmin);
			businesses.add(office);	
			oAdmin.addAdministeredBusinesses(office);
			Logger.debug("Created record in: "+ (System.nanoTime()-startTime));
		}
		
		Logger.debug("Created record in: "+ (System.nanoTime()-totalStartTime));
		//createTestOffice(oAdmin, "Office nbr asd");
		
		
		oAdmin.save();
		
		businesses = new ArrayList<Business>();
		for (int i=0; i<1; i++) {
			Business business = createTestBusiness("my Office nbr "+i);
			business.addAdministrator(oAdmin2);
			business.addAdministrator(oAdmin);
			//offices.add(office);	
			oAdmin2.addAdministeredBusinesses(business);
			oAdmin.addAdministeredBusinesses(business);
		}
		
		//createTestOffice(oAdmin, "Office nbr asd");
		
		oAdmin.save();
		oAdmin2.save();

	}

	private Business createTestBusiness(String businessName) {
		
		final String addressLineOne = "Address Line ";
		final String addressLineTwo = "Address Line 2";
		final String addressPostalCode = "Postal Code";
		final String addressCity = "City";
		final String addressCountry = "Country";
		final String phoneNumber1 = "123456789";
		final String phoneNumber2 = "987654321";

		Business business = new Business();

		business.setName(businessName);
		
		business.setPhone1(phoneNumber1);
		business.setPhone2(phoneNumber2);

		Address officeAddress = new Address();
		officeAddress.setAddressLineOne(addressLineOne+businessName);
		officeAddress.setAddressLineTwo(addressLineTwo);
		officeAddress.setPostalCode(addressPostalCode);
		officeAddress.setCity(addressCity);
		officeAddress.setCountry(addressCountry);
		business.setAddress(officeAddress);
		return business;

	}
	
	private void loadHSQLConsole() {
		org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url",
				"jdbc:hsqldb:mem:playembed", "--noexit" });

	}
}
