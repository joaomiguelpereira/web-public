package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import annotations.authorization.RequiresUserSession;




import models.Office;
import models.OfficeAdministrator;

import models.User;
import models.enums.BusinessType;
import models.enums.UserType;

import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;



public class Offices extends BaseController {

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
	@RequiresUserSession(userTypes={UserType.OFFICE_ADMIN})
	public static void preRegister() {
		render();
	}
	@RequiresUserSession(userTypes={UserType.OFFICE_ADMIN})
	public static void view(Long id) {
		boolean allowed = false;
		//get the office
		Office office = Office.findById(id);
		if ( office == null ) {
			notFound("Office not found");
		}
		//check if current user is one of the admins
		for ( OfficeAdministrator oa : office.getAdministrators() ) {
			if ( oa.id.equals(currentUser.get().id)) {
				allowed = true;
				break;
			}
		}
		if ( !allowed ) {
			flashError("user.not.authorized");
			Offices.listUserOffices();
		} else {
			renderText("Hi ther "+id);
		}
		
	}

	@RequiresUserSession(userTypes={UserType.OFFICE_ADMIN, UserType.ADMIN})
	public static void listUserOffices() {
		//get current user
		//try to find the user
		OfficeAdministrator oa = getCurrentAdministrator();
		
		
		List<Office> offices = oa.getAdministeredOffices();

		
		render(offices);
		
		
		
	}
	
	@RequiresUserSession(userTypes={UserType.OFFICE_ADMIN})
	public static void savePreRegistration(@Valid Office office) {		
		//get the current user
		
		if ( currentUser.get() == null ) {
			notFound(Messages.get("user.not.found"));
		} 
		
		if (validation.hasErrors()) {
			flash.error(Messages.get("office.register.error"));
			render("@preRegister", office);
		}
		//Set the owne
		getCurrentAdministrator().addAdministeredOffice(office);
		office.addAdministrator(getCurrentAdministrator());
		office.save();
		
		currentUser.get().save();
		
		flash.success(Messages.get("office.register.success"));
		Offices.listUserOffices();
		
	}

	
	

}
