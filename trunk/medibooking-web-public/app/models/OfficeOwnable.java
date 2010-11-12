package models;

import java.util.Set;

public interface OfficeOwnable {
	
	public void addOffice(Office office);

	public Set<Office> getOffices();

}
