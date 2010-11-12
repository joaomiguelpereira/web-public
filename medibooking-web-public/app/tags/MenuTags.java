package tags;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import constants.SessionValuesConstants;

import models.enums.UserType;

import play.Logger;
import play.mvc.Scope.Session;
import play.templates.FastTags;
import play.templates.JavaExtensions;
import play.templates.Template;
import play.templates.TemplateLoader;
import play.templates.GroovyTemplate.ExecutableTemplate;
import utils.StringUtils;

@FastTags.Namespace("menus")
public class MenuTags extends BaseFastTags {

	public static void _mainMenu(Map<?, ?> args, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {

		StringBuilder templateName = new StringBuilder("Menus/");

		// check if a user is logged in

		if (hasSession()) {
			UserType uType = UserType.valueOf(Session.current().get(
					SessionValuesConstants.USER_TYPE));
			
			templateName.append(StringUtils.camelize(uType.toString(), false));
			templateName.append("MainMenu.html");
			
			
		} else {
			templateName.append("defaultMainMenu.html");
			
		}
		Logger.debug("Template Name is:" +templateName.toString());
		// Get user type
		Template menuTemplate = TemplateLoader.load(templateName.toString());
		
		menuTemplate.compile();
		String templateStr = menuTemplate.render(new HashMap<String, Object>());
		out.println(JavaExtensions.raw(templateStr));
		
	}
}
