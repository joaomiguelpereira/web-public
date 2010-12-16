package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import annotations.authorization.RequiresUserSession;

import models.Business;
import models.BusinessAdministrator;
import models.Email;

import models.User;
import models.enums.UserType;

import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Error;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.Params;

import play.mvc.With;
import sun.awt.X11.MWMConstants;

public class Businesses extends BaseController {

	// Start Actions
	/**
	 * List all clinics
	 */
	@RequiresUserSession(userTypes = UserType.ADMIN)
	public static void index() {
		// retrieve all Partners (Just for testing purpose)
		List<Business> businesses = Business.findAll();

		render(businesses);
	}

	/**
	 * Show
	 */
	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN })
	public static void blank() {
		render();
	}

	/**
	 * Render the edit form
	 * 
	 * @param id
	 */
	public static void edit(Long id) {
		Business business = Business.findById(id);
		if (business == null) {
			notFound("Business not found");
		}

		checkBusinessAdministrativeRights(business, false);
		render(business);
	}

	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN })
	public static void activate(Long id) {
		Business business = Business.findById(id);
		Map<String, Object> jsonOutMap = new HashMap<String, Object>();
		String jsonOut;
		if (business == null) {
			jsonOutMap.put("error",
					Messages.get("controllers.businesses.not.found"));
			jsonOut = new Gson().toJson(jsonOutMap);
			render(jsonOut);
		} else {
			// has complete info
			if ((business.getShortIntroduction() == null)
					|| (business.getShortIntroduction().isEmpty())
					|| business.getPhones() == null
					|| business.getPhones().isEmpty()
					|| business.getEmails() == null
					|| business.getEmails().isEmpty()) {
				jsonOutMap.put("error",
						Messages.get("controllers.businesses.activate.fail.incomplete.info"));
				jsonOut = new Gson().toJson(jsonOutMap);
				renderJSON(jsonOut);

			} else {
				business.setActive(true);
				business.save();
				jsonOutMap.put("active", Boolean.TRUE.toString());
				jsonOutMap.put("flash", new HashMap<String, String>(){{put("sucess",Messages.get("controllers.businesses.activate.fail.incomplete.info"));}});
				renderJSON(new Gson().toJson(jsonOutMap));
			}
		}
	}

	/**
	 * Delete a business
	 * 
	 * @param id
	 */
	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN })
	public static void delete(Long id) {
		Business business = Business.findById(id);
		if (business == null) {
			notFound("Business not found");
		}
		checkBusinessAdministrativeRights(business, true);
		// remove the business from the admin list
		boolean removed = getCurrentAdministrator().removeAdministeredBusiness(
				business);
		if (removed) {
			// save ba new state
			getCurrentAdministrator().save();
			// now, just delete the business
			business.delete();
			flashSuccess("controllers.businesses.delete.success");
			flash.keep();
			Businesses.list();
		} else {
			flashError("controllers.businesses.delete.fail");
			flash.keep();
			Businesses.view(business.id);

		}
	}

	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN })
	public static void saveAsync(Long id) {
		
		//Check if the business exists
		Business business = Business.findById(id);
		if ( business == null ) {
			jsonError("model.business.not.found");
			//generate JSON error -> Not Found
			//render error
		}
		//Business business = new Gson().fromJson(params.get("body"), Business.class);
		Logger.debug(business.toString());
	}
	
	private static void jsonError(String i18nKey) {
		Map<String, String> jsonOut = new HashMap<String, String>();
		jsonOut.put("error", Messages.get(i18nKey));
		renderJSON(new Gson().toJson(jsonOut));
		
	}

	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN })
	public static void save(Long id) {
		Business business = Business.findById(id);
		notFoundIfNull(business);
		checkBusinessAdministrativeRights(business, false);
		Binder.bind(business, "business", params.all());

		if (business.validateAndSave()) {
			flashSuccess("business.save.success");
			flash.keep();
			Businesses.view(id);
		} else {
			flashError("business.save.success");
			render("@edit", id, business);
		}

	}
	

	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN })
	public static void view(Long id) {

		Business business = Business.findById(id);
		if (business == null) {
			notFound("Business not found");
		}

		checkBusinessAdministrativeRights(business, false);
		render(business);

	}

	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN, UserType.ADMIN })
	public static void list() {
		// get current user
		// try to find the user
		BusinessAdministrator oa = getCurrentAdministrator();

		List<Business> businesses = oa.getAdministeredBusinesses();

		render(businesses);

	}

	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN })
	public static void create(@Valid Business business, List<Email> emails) {
		// get the current user
		if (currentUser.get() == null) {
			notFound(Messages.get("user.not.found"));
		}

		if (validation.hasErrors()) {
			flash.error(Messages.get("business.save.fail"));
			logValidationErrors();
			render("@blank", business);
		}
		// Set the owner
		getCurrentAdministrator().addAdministeredBusinesses(business);
		// May have any number of admins
		business.addAdministrator(getCurrentAdministrator());
		// Has only one superAdmin
		business.setSuperAdmin(getCurrentAdministrator());

		business.save();

		currentUser.get().save();

		flash.success(Messages.get("business.save.success"));
		Businesses.view(business.id);

	}

	// ///////End actions

	/**
	 * Check if the business requested is administered by the current user
	 * 
	 * @param business
	 */
	private static void checkBusinessAdministrativeRights(Business business,
			boolean shouldBeSuperAdmin) {

		boolean allowed = false;
		String errorKey = "user.not.authorized";

		if (currentUser.get() != null) {
			if (shouldBeSuperAdmin) {
				errorKey = "user.not.business.owner";
				if (business.getSuperAdmin().id.equals(currentUser.get().id)) {
					allowed = true;
				}
			} else {
				// check if current user is one of the admins
				for (BusinessAdministrator oa : business.getAdministrators()) {
					if (oa.id.equals(currentUser.get().id)) {
						allowed = true;
						break;
					}
				}

			}
		}
		if (!allowed) {
			flashError(errorKey);
			Businesses.list();
		}
	}

}
