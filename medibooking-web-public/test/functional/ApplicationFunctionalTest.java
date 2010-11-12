package functional;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import models.User;

import org.junit.Ignore;

import constants.SessionValuesConstants;
import controllers.Users;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Scope;
import play.mvc.Http.Response;
import play.mvc.Router.ActionDefinition;
import play.mvc.Scope.Flash;
import play.mvc.Router;
import play.test.FunctionalTest;

@Ignore
public class ApplicationFunctionalTest extends FunctionalTest {

	private Class controllerClass;
	private String methodName;
	private Map<String, Object> args = new HashMap<String, Object>();
	private Class[] argTypes;

	/**
	 * Call FunctionalTest.GET
	 * 
	 * @return
	 */
	protected Response get() {
		return GET(calculateRouteURL());
	}

	/**
	 * 
	 * @param url
	 *            Url to test
	 * @param expectedUrl
	 *            The expected URL
	 * @param expectedUrlparams
	 *            The params to construct the expected URL. Must have even
	 * 
	 */
	protected void assertRedirectedTo(String url, String expectedUrl,
			Map<String, Object> expectedUrlparams) {
		ActionDefinition ad = Router.reverse(expectedUrl, expectedUrlparams);
		ad.absolute();
		assertEquals(ad.url, url);
	}

	protected void authenticateUser(String email, String password,
			boolean keepLogged) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", email);
		params.put("password", password);
		params.put("keepLogged", keepLogged);
		Http.Response response = POST(Router.reverse("Users.authenticate",
				params));
		assertSuccessFlashed("login.successful");
		User user = User.find("byEmail", email).first();
		assertEquals(Long.valueOf(1L), user.getLoginInformation()
				.getSuccessfulLoginCount());
		assertEquals(
				Scope.Session.current().get(SessionValuesConstants.LOGIN_EMAIL),
				email);
		assertEquals(
				Scope.Session.current().get(SessionValuesConstants.LOGIN_TOKEN),
				user.getLoginInformation().getLoginToken());
		Flash.current().clear();

	}

	protected void assertSuccessFlashed(String i18nKey) {
		assertEquals(Messages.get(i18nKey), Flash.current().get("success"));
	}

	protected void assertErrorFlashed(String i18nKey) {
		assertEquals(Messages.get(i18nKey), Flash.current().get("error"));
	}

	protected void assertWarningFlashed(String i18nKey) {
		assertEquals(Messages.get(i18nKey), Flash.current().get("warning"));
	}

	protected void assertNoErrorFlashed() {
		assertNull(Flash.current().get("error"));

	}

	/**
	 * Calculate reverse route and return a URL as String
	 * 
	 * @return The URL reversed from controllerClass, actionName and args
	 */
	protected String calculateRouteURL() {
		return Router.reverse(this.controllerClass.getName() + "."
				+ this.methodName, this.args).url;
	}

	/**
	 * Reset all information
	 */
	protected void reset() {
		this.controllerClass = null;
		this.methodName = null;
		this.args.clear();
		this.argTypes = null;
	}

	/**
	 * Add the action (methodName)
	 * 
	 * @param actionName
	 * @return
	 */
	protected ApplicationFunctionalTest withAction(String actionName) {
		// try to see if the methos exists
		if (this.controllerClass == null) {
			throw new IllegalArgumentException(
					"Please use withController first to set the controller class");
		}
		try {
			if (this.args != null) {
				this.controllerClass.getMethod(actionName, this.argTypes);
			} else {
				this.controllerClass.getMethod(actionName);
			}

		} catch (SecurityException e) {
			throw e;
		} catch (NoSuchMethodException e) {
			StringBuffer argumentsError = new StringBuffer();
			if (this.argTypes == null) {
				argumentsError.append("no args");
			} else {
				argumentsError.append("args ");
				int i = 0;
				for (Class clazz : this.argTypes) {
					argumentsError.append("<");
					argumentsError.append(clazz);
					argumentsError.append(">");
					if (i < this.argTypes.length - 1) {
						argumentsError.append(",");
					}
					i++;

				}
			}
			throw new IllegalArgumentException("No such method: " + actionName
					+ " for class: " + this.controllerClass.getName()
					+ " with " + argumentsError.toString());
		}

		this.methodName = actionName;

		return this;
	}

	/**
	 * Add the params
	 * 
	 * @param args
	 * @return
	 */
	protected ApplicationFunctionalTest withArgs(Object... args) {
		// The args must be an even number
		if (args == null || args.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Please specify even number of arguments where argn is the key and argn+1 is the value");
		}
		argTypes = new Class[args.length / 2];
		for (int i = 0, j = 0; i < args.length - 1; i = i + 2, j++) {
			this.args.put(args[i].toString(), args[i + 1]);
			argTypes[j] = args[i + 1].getClass();
		}

		return this;
	}

	/**
	 * Set the controller to test
	 * 
	 * @param controllerClass
	 * @return
	 */
	protected ApplicationFunctionalTest withController(
			Class<Users> controllerClass) {
		this.controllerClass = controllerClass;
		return this;
	}
}
