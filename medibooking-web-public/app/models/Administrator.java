package models;

import javax.persistence.Entity;

import models.enums.UserType;

@Entity
public class Administrator extends User {
	{
		setUserType(UserType.ADMIN);
	}
}
