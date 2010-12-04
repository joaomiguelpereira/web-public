package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import models.enums.UserType;

@Entity
public class BusinessAdministrator extends User{

	/**
	 * Instance initialization block. Run after all super constructors and
	 * before this constructor
	 */
	{
		administeredBusinesses = new ArrayList<Business>();
		this.setUserType(UserType.BUSINESS_ADMIN);
	}
	

	public BusinessAdministrator() {
		
	}
	public BusinessAdministrator(User user) {
		this.setEmail(user.getEmail());
		this.setMobile(user.getEmail());
		this.setName(user.getName());
		this.setPassword(user.getPassword());
		this.setPhone(user.getPhone());
	}
	@ManyToMany(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private List<Business> administeredBusinesses;
	

	/**
	 * Get the administered offices for this user
	 * @return A set of administered offices
	 */
	public List<Business> getAdministeredBusinesses() {
		return administeredBusinesses;
	}
	
	
	public void addAdministeredBusinesses(Business business) {
		this.administeredBusinesses.add(business);
		
	}
	public boolean removeAdministeredBusiness(Business business) {
		return this.administeredBusinesses.remove(business);
		
	}
	
}
