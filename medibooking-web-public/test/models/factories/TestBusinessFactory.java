package models.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.Address;
import models.Business;
import models.BusinessAdministrator;

import org.junit.Ignore;

@Ignore
public abstract class TestBusinessFactory {
	
	
	/**
	 * Create a new test Office
	 * @param name The name. If null some random String will be used
	 * @param oAdmin The administrator of the office
	 * @return the newly created office. Not persisted
	 */
	public static Business createBusiness(String name, BusinessAdministrator oAdmin) {
		
		final String sufix = System.nanoTime()+"";
		String officeName = "Office "+sufix;
		
		if ( name == null ) {
			officeName = "Office "+sufix;
		} else {
			officeName = name;
		}
		
		final String addressLineOne = "Address Line "+sufix;
		final String addressLineTwo = "Address Line 2"+sufix;
		final String addressPostalCode = "Postal Code";
		final String addressCity = "City";
		final String addressCountry = "Country";
		final String phoneNumber1 = "123456789";
		final String phoneNumber2 = "987654321";
		
		
		
		
		Business office = new Business();
		
		office.setName(officeName);
	
		office.setPhone1(phoneNumber1);
		office.setPhone2(phoneNumber2);
		
	
		
		Address officeAddress = new Address();
		officeAddress.setAddressLineOne(addressLineOne);
		officeAddress.setAddressLineTwo(addressLineTwo);
		officeAddress.setPostalCode(addressPostalCode);
		officeAddress.setCity(addressCity);
		officeAddress.setCountry(addressCountry);
		office.setAddress(officeAddress);
		if ( oAdmin != null ) {
			office.addAdministrator(oAdmin);
		}
		
		return office;
		
	}

	
	
}
