package controllers;

import java.util.List;

import constants.SessionValuesConstants;
import models.Business;
import models.User;
import models.enums.UserType;


public class Application extends BaseController {

	
    public static void index() {
        //decide what is the front page for the current user
    	if (hasSession()) {
    		UserType uType = UserType.valueOf(session.get(SessionValuesConstants.USER_TYPE));
    		
    		switch (uType) {
			case BUSINESS_ADMIN:
				flash.keep();
				Businesses.list();
				break;
			default:
				render();
			}
    	} else {
    		render();
    	}
    	
        
    }

}