package controllers;

import java.util.ArrayList;
import java.util.List;


import models.Partner;

import play.mvc.Controller;

public class Offices extends Application {

	/**
	 * List all clinics
	 */
	public static void index() {
		//retrieve all Partners (Just for testing purpose)
		List<Partner> offices = Partner.findAll();
		render(offices);
	}
	

}
