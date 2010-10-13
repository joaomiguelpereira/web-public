package controllers.partners;

import models.Partner;
import models.enums.BusinessType;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.I18N;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

public class Partners extends Controller {

	public static void index() {
		render();
	}

	@Before(only = { "preRegister", "savePreRegister" })
	private static void setRederArgs() {
		// Pass the literals of the enum to the template
		renderArgs.put("clinicBusinessTypeLiteral",
				BusinessType.CLINIC.name());
		renderArgs.put("officeBusinessTypeLiteral",
				BusinessType.OFFICE.toString());

	}

	public static void preRegister() {
		render();
	}

	public static void savePreRegister(@Valid Partner partner) {		
		Logger.debug("business enum name: " + partner.getBusinessType());
		Logger.debug("partner name: " + partner.getName());
		if (validation.hasErrors()) {
			Logger.debug("ERRORS");
			//params.flash(); // add http parameters to the flash scope
			//validation.keep();
			//preRegister();
			flash.error(Messages.get("partner.register.error"));
			render("@preRegister", partner);
		}

		partner.save();
		
	}
}
