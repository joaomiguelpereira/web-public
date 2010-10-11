package controllers.partners;

import models.Partner;
import play.Logger;
import play.data.validation.Valid;
import play.mvc.Controller;

public class Partners extends Controller {

	public static void index() {
		render();
	}
	
	public static void preRegister() {
		render();
	}
	
	public static void savePreRegister(@Valid Partner partner) {
		if(validation.hasErrors()) {
            render("@preRegister", partner);
        }
		partner.save();
		Logger.debug("partner name: "+partner.getName());
	}
}
