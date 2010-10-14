package models;

import models.enums.BusinessType;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class PartnerTest extends UnitTest {
	
	@Before
	public void setup() {
		//Fixtures.deleteAll();
		//Fixtures.load("partners.yml");
	}
	
	@Test
	public void createAndRetrievePartner() {
		final String partnerName = "Office 1";
		final BusinessType businessType = BusinessType.OFFICE;
		final String addressLineOne = "Address Line 1";
		final String addressLineTwo = "Address Line 2";
		final String addressPostalCode = "Postal Code";
		final String addressCity = "City";
		final String addressCountry = "Country";
		
		
		Partner partner = new Partner();
		partner.setName(partnerName);
		partner.setBusinessType(businessType);
		
		
		Address partnerAddress = new Address();
		partnerAddress.setAddressLineOne(addressLineOne);
		partnerAddress.setAddressLineTwo(addressLineTwo);
		partnerAddress.setPostalCode(addressPostalCode);
		partnerAddress.setCity(addressCity);
		partnerAddress.setCountry(addressCountry);
		partner.setAddress(partnerAddress);
		
		//Save it
		partner.save();
		
		//retrieve it
		
		Partner savedPartner = Partner.find("name=?", partnerName).first();
		assertNotNull(savedPartner);
		
		
	}


}
