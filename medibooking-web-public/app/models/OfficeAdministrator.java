package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import models.enums.UserType;

@Entity
public class OfficeAdministrator extends User{

	/**
	 * Instance initialization block. Run after all super constructors and
	 * before this constructor
	 */
	{
		administeredOffices = new ArrayList<Office>();
		this.setUserType(UserType.OFFICE_ADMIN);
	}
	

	public OfficeAdministrator() {
		
	}
	public OfficeAdministrator(User user) {
		this.setEmail(user.getEmail());
		this.setMobile(user.getEmail());
		this.setName(user.getName());
		this.setPassword(user.getPassword());
		this.setPhone(user.getPhone());
	}
	@ManyToMany(cascade=CascadeType.PERSIST)
	private List<Office> administeredOffices;


	/**
	 * Get the administered offices for this user
	 * @return A set of administered offices
	 */
	public List<Office> getAdministeredOffices() {
		return administeredOffices;
	}
	
	
	public void addAdministeredOffice(Office office) {
		this.administeredOffices.add(office);
		
	}
	
}
