package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model{

	
	public String fullName;
	public String email;
	public String password;
	public boolean isAdmin;
	
	/*
	 * Constructor
	 */
	public User(String fullName, String email, String password) {
		super();
		this.fullName = fullName;
		this.email = email;
		this.password = password;
	}
	
	/**
	 * TODO: Make the documentation
	 * @param email
	 * @param password
	 * @return
	 */
	public static User connect(String email, String password) {
		return find("byEmailAndPassword",email, password).first();
	}
	
	
}
