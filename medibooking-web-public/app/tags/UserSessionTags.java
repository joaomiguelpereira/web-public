package tags;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.enums.UserType;

import constants.SessionValuesConstants;

import play.Logger;
import play.mvc.Scope.Session;
import play.templates.FastTags;
import play.templates.JavaExtensions;
import play.templates.GroovyTemplate.ExecutableTemplate;

@FastTags.Namespace("session")
public class UserSessionTags extends FastTags {

	/**
	 * Constant to avoid the magic values
	 */
	private static String USER_TYPES_KEY = "userTypes";

	/**
	 * Render the current user email, if any
	 * @param args
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 */
	public static void _userEmail(Map<?, ?> args, Closure body,
			PrintWriter out, ExecutableTemplate template, int fromLine) {
		String userEmail = Session.current().get(SessionValuesConstants.LOGIN_EMAIL);
		if ( userEmail!=null ) {
			out.println(userEmail);	
		}
	}
	
	/**
	 * Tag require user
	 * 
	 * @param args
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 */
	public static void _requireUser(Map<?, ?> args, Closure body,
			PrintWriter out, ExecutableTemplate template, int fromLine) {

		boolean render = false;
		if (Session.current().contains(SessionValuesConstants.LOGIN_EMAIL)
				&& Session.current()
						.contains(SessionValuesConstants.LOGIN_TOKEN)
				&& Session.current().contains(SessionValuesConstants.USER_TYPE)) {

			UserType currentUserType = UserType.valueOf(Session.current().get(
					SessionValuesConstants.USER_TYPE));
			Object userTypes = args.get(USER_TYPES_KEY);

			if (userTypes != null) {

				if (userTypes instanceof List<?>) {
					if (((List<?>) userTypes).contains(currentUserType)) {
						render = true;
					}
				} else if (userTypes instanceof UserType) {
					if (userTypes.equals(currentUserType)) {
						render = true;
					}
				} else {
					throw new RuntimeException("Argument "+USER_TYPES_KEY+ " should be either a array [] or an instance of UserType");
				}
			} else {
				render = true;
			}
			if (render) {
				out.println(JavaExtensions.toString(body));
			}

		}

	}

	/**
	 * 
	 * @param args
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 */
	public static void _requireNoUser(Map<?, ?> args, Closure body,
			PrintWriter out, ExecutableTemplate template, int fromLine) {

		if (!Session.current().contains(SessionValuesConstants.LOGIN_EMAIL)
				|| !Session.current().contains(
						SessionValuesConstants.LOGIN_TOKEN)
				|| !Session.current().contains(SessionValuesConstants.USER_TYPE)) {
			out.println(JavaExtensions.toString(body));

		}

	}

}
