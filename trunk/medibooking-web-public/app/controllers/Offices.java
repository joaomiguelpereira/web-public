package controllers;

import java.util.ArrayList;
import java.util.List;

import annotations.authorization.RequiresUserSession;




import models.Office;
import models.OfficeAdministrator;
import models.OfficeOwnable;
import models.User;
import models.enums.BusinessType;
import models.enums.UserType;

import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;


public class Offices extends Application {

	/**
	 * List all clinics
	 */
	@RequiresUserSession(userTypes=UserType.ADMIN)
	public static void index() {
		//retrieve all Partners (Just for testing purpose)
		List<Office> offices = Office.findAll();
		render(offices);
	}
	
	@SuppressWarnings("unused")
	@Before(only = { "preRegister", "savePreRegistration" })
	private static void setRenderArgs() {
		// Pass the literals of the enum to the template
		renderArgs.put("clinicBusinessTypeLiteral",
				BusinessType.CLINIC.name());
		renderArgs.put("officeBusinessTypeLiteral",
				BusinessType.OFFICE.toString());

	}

	/**
	 * Index action
	 */
	@RequiresUserSession(userTypes={UserType.OFFICE_ADMIN, UserType.ADMIN})
	public static void preRegister() {
		render();
	}

	@RequiresUserSession(userTypes={UserType.OFFICE_ADMIN, UserType.ADMIN})
	public static void listUserOffices(Long userId) {
		//try to find the user
	}
	@RequiresUserSession(userTypes={UserType.OFFICE_ADMIN, UserType.ADMIN})
	public static void savePreRegistration(@Valid Office office) {		
		//get the current user
		
		if ( currentUser.get() == null ) {
			notFound(Messages.get("user.not.found"));
		} 
		
		if (validation.hasErrors()) {
			flash.error(Messages.get("office.register.error"));
			render("@preRegister", office);
		}
		getCurrentOfficeOwner().addOffice(office);
		office.save();
		currentUser.get().save();
		
		flash.success(Messages.get("office.register.success"));
		Offices.listUserOffices(currentUser.get().id);
		
	}

	
	

}
