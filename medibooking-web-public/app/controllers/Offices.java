package controllers;

import java.util.ArrayList;
import java.util.List;




import models.Office;
import models.enums.BusinessType;

import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;

public class Offices extends Application {

	/**
	 * List all clinics
	 */
	public static void index() {
		//retrieve all Partners (Just for testing purpose)
		List<Office> offices = Office.findAll();
		render(offices);
	}
	
	@SuppressWarnings("unused")
	@Before(only = { "preRegister", "savePreRegister" })
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
	public static void preRegister() {
		render();
	}

	public static void savePreRegistration(@Valid Office partner) {		
		if (validation.hasErrors()) {
			flash.error(Messages.get("partner.register.error"));
			render("@preRegister", partner);
		}
		partner.save();
		flash.success(Messages.get("partner.register.success"));
		Offices.index();
		
	}
	

}
