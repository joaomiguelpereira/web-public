package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import annotations.authorization.RequiresUserSession;

import models.Business;
import models.BusinessAdministrator;

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

import play.mvc.With;

public class Businesses extends BaseController {

	// Start Actions
	/**
	 * List all clinics
	 */
	@RequiresUserSession(userTypes = UserType.ADMIN)
	public static void index() {
		// retrieve all Partners (Just for testing purpose)
		List<Business> businesses= Business.findAll();

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

		checkBusinessOwnership(business);
		render(business);
	}

	@RequiresUserSession(userTypes = { UserType.BUSINESS_ADMIN })
	public static void save(Long id) {
		Business business = Business.findById(id);
		notFoundIfNull(business);
		checkBusinessOwnership(business);
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

		checkBusinessOwnership(business);
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
	public static void create(@Valid Business business) {
		// get the current user

		if (currentUser.get() == null) {
			notFound(Messages.get("user.not.found"));
		}

		if (validation.hasErrors()) {
			flash.error(Messages.get("business.save.success"));
			logValidationErrors();
			render("@blank", business);
		}
		// Set the owner
		getCurrentAdministrator().addAdministeredBusinesses(business);
		business.addAdministrator(getCurrentAdministrator());
		business.save();

		currentUser.get().save();

		flash.success(Messages.get("business.save.success"));
		Businesses.list();

	}

	// ///////End actions

	/**
	 * Check if the business requested is administered by the current user
	 * 
	 * @param business
	 */
	private static void checkBusinessOwnership(Business business) {
		boolean allowed = false;
		if (currentUser.get() != null) {

			// check if current user is one of the admins
			for (BusinessAdministrator oa : business.getAdministrators()) {
				if (oa.id.equals(currentUser.get().id)) {
					allowed = true;
					break;
				}
			}
		}
		if (!allowed) {
			flashError("user.not.authorized");
			Businesses.list();
		}
	}

}
