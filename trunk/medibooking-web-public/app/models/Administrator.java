package models;

import javax.persistence.Entity;

import models.enums.UserType;

@Entity
public class Administrator extends OfficeAdministrator {
	{
		setUserType(UserType.ADMIN);
	}
}
