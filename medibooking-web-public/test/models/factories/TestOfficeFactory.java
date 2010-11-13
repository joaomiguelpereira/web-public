package models.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.Address;
import models.Office;
import models.OfficeAdministrator;
import models.enums.BusinessType;

import org.junit.Ignore;

@Ignore
public abstract class TestOfficeFactory {
	
	
	public static Office createOffice(String name) {
		
		final String sufix = System.nanoTime()+"";
		String officeName = "Office "+sufix;
		
		if ( name == null ) {
			officeName = "Office "+sufix;
		} else {
			officeName = name;
		}
		
		final BusinessType businessType = BusinessType.OFFICE;
		final String addressLineOne = "Address Line "+sufix;
		final String addressLineTwo = "Address Line 2"+sufix;
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
		officeAddress.setAddressLineOne(addressLineOne);
		officeAddress.setAddressLineTwo(addressLineTwo);
		officeAddress.setPostalCode(addressPostalCode);
		officeAddress.setCity(addressCity);
		officeAddress.setCountry(addressCountry);
		office.setAddress(officeAddress);
		return office;
		
	}

	
	
}
