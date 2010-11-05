package models;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import models.enums.UserType;

@Entity
public class OfficeAdministrator extends User {

	/**
	 * Instance initialization block. Run after all super constructors and
	 * before this constructor
	 */
	{
		administeredOffices = new TreeSet<Office>();
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
	@ManyToMany
	private Set<Office> administeredOffices;

	/**
	 * Set the Administered offices for this User
	 * @param administeredOffices A set of administered offices
	 */
	public void setAdministeredOffices(Set<Office> administeredOffices) {
		this.administeredOffices = administeredOffices;
	}

	/**
	 * Get the administered offices for this user
	 * @return A set of administered offices
	 */
	public Set<Office> getAdministeredOffices() {
		return administeredOffices;
	}

}
