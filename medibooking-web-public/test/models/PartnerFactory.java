package models;

import java.util.UUID;

import models.enums.BusinessType;

import org.junit.Ignore;

@Ignore
public class PartnerFactory {
	
	
	public Partner createPartner(String name) {
		
		final String sufix = System.nanoTime()+"";
		String partnerName = "Office "+sufix;
		
		if ( name == null ) {
			partnerName = "Office "+sufix;
		} else {
			partnerName = name;
		}
		
		final BusinessType businessType = BusinessType.OFFICE;
		final String addressLineOne = "Address Line "+sufix;
		final String addressLineTwo = "Address Line 2"+sufix;
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
		return partner;
		
	}
	
	
}
