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
		
		return createBusiness(name, oAdmin, false);
		/*
		final String sufix = System.nanoTime()+"";
		String businessName = "Office "+sufix;
		
		if ( name == null ) {
			businessName = "Office "+sufix;
		} else {
			businessName = name;
		}
		
		final String addressLineOne = "Address Line "+sufix;
		final String addressLineTwo = "Address Line 2"+sufix;
		final String addressPostalCode = "Postal Code";
		final String addressCity = "City";
		final String addressCountry = "Country";
		
		
		
		
		
		Business business = new Business();
		
		business.setName(businessName);
	
		
	
		
		Address officeAddress = new Address();
		officeAddress.setAddressLineOne(addressLineOne);
		officeAddress.setAddressLineTwo(addressLineTwo);
		officeAddress.setPostalCode(addressPostalCode);
		officeAddress.setCity(addressCity);
		officeAddress.setCountry(addressCountry);
		business.setAddress(officeAddress);
		if ( oAdmin != null ) {
			business.addAdministrator(oAdmin);
		}
		
		return business;*/
		
	}



	/**
	 * Create a new test Office
	 * @param name The name. If null some random String will be used
	 * @param oAdmin The administrator of the office
	 * @return the newly created office. Not persisted
	 */
	public static Business createBusiness(String name, BusinessAdministrator oAdmin, boolean autoSave) {
		
		final String sufix = System.nanoTime()+"";
		String businessName = "Office "+sufix;
		
		if ( name == null ) {
			businessName = "Office "+sufix;
		} else {
			businessName = name;
		}
		
		final String addressLineOne = "Address Line "+sufix;
		final String addressLineTwo = "Address Line 2"+sufix;
		final String addressPostalCode = "Postal Code";
		final String addressCity = "City";
		final String addressCountry = "Country";
		
		
		
		
		
		Business business = new Business();
		
		business.setName(businessName);
	
		
	
		
		Address officeAddress = new Address();
		officeAddress.setAddressLineOne(addressLineOne);
		officeAddress.setAddressLineTwo(addressLineTwo);
		officeAddress.setPostalCode(addressPostalCode);
		officeAddress.setCity(addressCity);
		officeAddress.setCountry(addressCountry);
		business.setAddress(officeAddress);
		if ( oAdmin != null ) {
			business.addAdministrator(oAdmin);
			business.setSuperAdmin(oAdmin);
		}
		
		if ( autoSave ) {
			
			business.save();
			oAdmin.addAdministeredBusinesses(business);
			oAdmin.save();
		}
		return business;
		
	}

	
}
