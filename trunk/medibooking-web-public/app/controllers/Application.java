package controllers;

import play.*;
import play.i18n.Messages;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	
	protected static void flashError(String i18nKey) {
		flash.clear();
		flash.error(Messages.get(i18nKey));
	}
	

	protected static void flashSuccess(String i18nKey) {
		flash.clear();
		flash.success(Messages.get(i18nKey));
	}
	
	
	protected static void flashWarning(String i18nKey) {
		flash.clear();
		flash.put("warning",Messages.get(i18nKey));
	}
	
    public static void index() {
        
    	render();
        
    }

}