package controllers;

import constants.SessionValuesConstants;
import models.enums.UserType;


public class Application extends BaseController {

	
	
    public static void index() {
        //decide what is the front page for the current user
    	if (hasSession()) {
    		UserType uType = UserType.valueOf(session.get(SessionValuesConstants.USER_TYPE));
    		
    		switch (uType) {
			case OFFICE_ADMIN:
				Offices.listUserOffices();
				break;
			default:
				render();
			}
    	} else {
    		render();
    	}
    	
        
    }

}