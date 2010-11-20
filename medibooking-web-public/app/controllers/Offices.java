package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import annotations.authorization.RequiresUserSession;

import models.Office;
import models.OfficeAdministrator;

import models.User;
import models.enums.UserType;

import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Error;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import play.mvc.Controller;

import play.mvc.With;

public class Offices extends BaseController {

	// Start Actions
	/**
	 * List all clinics
	 */
	@RequiresUserSession(userTypes = UserType.ADMIN)
	public static void index() {
		// retrieve all Partners (Just for testing purpose)
		List<Office> offices = Office.findAll();

		render(offices);
	}

	/**
	 * Show
	 */
	@RequiresUserSession(userTypes = { UserType.OFFICE_ADMIN })
	public static void blank() {
		render();
	}

	/**
	 * Render the edit form
	 * 
	 * @param id
	 */
	public static void edit(Long id) {
		Office office = Office.findById(id);
		if (office == null) {
			notFound("Office not found");
		}

		checkOfficeOwnership(office);
		render(office);
	}

	@RequiresUserSession(userTypes = { UserType.OFFICE_ADMIN })
	public static void save(Long id) {
		Office office = Office.findById(id);
		notFoundIfNull(office);
		checkOfficeOwnership(office);
		Binder.bind(office, "office", params.all());

		if (office.validateAndSave()) {
			flashSuccess("office.save.successfull");
			flash.keep();
			Offices.view(id);
		} else {
			flashError("office.register.error");
			render("@edit", id, office);
		}

	}

	@RequiresUserSession(userTypes = { UserType.OFFICE_ADMIN })
	public static void view(Long id) {

		Office office = Office.findById(id);
		if (office == null) {
			notFound("Office not found");
		}

		checkOfficeOwnership(office);
		render(office);

	}

	@RequiresUserSession(userTypes = { UserType.OFFICE_ADMIN, UserType.ADMIN })
	public static void list() {
		// get current user
		// try to find the user
		OfficeAdministrator oa = getCurrentAdministrator();

		List<Office> offices = oa.getAdministeredOffices();

		render(offices);

	}

	@RequiresUserSession(userTypes = { UserType.OFFICE_ADMIN })
	public static void create(@Valid Office office) {
		// get the current user

		if (currentUser.get() == null) {
			notFound(Messages.get("user.not.found"));
		}

		if (validation.hasErrors()) {
			flash.error(Messages.get("office.register.error"));
			logValidationErrors();
			render("@blank", office);
		}
		// Set the owne
		getCurrentAdministrator().addAdministeredOffice(office);
		office.addAdministrator(getCurrentAdministrator());
		office.save();

		currentUser.get().save();

		flash.success(Messages.get("office.save.success"));
		Offices.list();

	}

	// ///////End actions

	/**
	 * Check if the office requested is administered by the current user
	 * 
	 * @param office
	 */
	private static void checkOfficeOwnership(Office office) {
		boolean allowed = false;
		if (currentUser.get() != null) {

			// check if current user is one of the admins
			for (OfficeAdministrator oa : office.getAdministrators()) {
				if (oa.id.equals(currentUser.get().id)) {
					allowed = true;
					break;
				}
			}
		}
		if (!allowed) {
			flashError("user.not.authorized");
			Offices.list();
		}
	}

}
