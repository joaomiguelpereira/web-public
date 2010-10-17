package models;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class OfficeAdministrator extends Person {
	
	{
		administeredOffices = new TreeSet<Office>();
	}
	
	
	@ManyToMany
	private Set<Office> administeredOffices;

	public void setAdministeredOffices(Set<Office> administeredOffices) {
		this.administeredOffices = administeredOffices;
	}

	public Set<Office> getAdministeredOffices() {
		return administeredOffices;
	}


	
}
