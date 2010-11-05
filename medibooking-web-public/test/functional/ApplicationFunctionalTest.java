package functional;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

import controllers.Users;
import play.mvc.Controller;
import play.mvc.Http.Response;
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
	 * @return
	 */
	protected Response get() {
		return GET(calculateRouteURL());
	}

	/**
	 * Calculate reverse route and return a URL as String
	 * @return The URL reversed from controllerClass, actionName and args
	 */
	protected String calculateRouteURL() {
		return Router.reverse(this.controllerClass.getName()+"."+this.methodName, this.args).url;
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
			if ( this.argTypes == null ) {
				argumentsError.append("no args");
			} else {
				argumentsError.append("args ");
				int i=0;
				for (Class clazz: this.argTypes) {
					argumentsError.append("<");
					argumentsError.append(clazz);
					argumentsError.append(">");
					if (i<this.argTypes.length-1 ) {
						argumentsError.append(",");
					}
					i++;
					
				}
			}
			throw new IllegalArgumentException("No such method: " + actionName
					+ " for class: " + this.controllerClass.getName()+ " with "+argumentsError.toString());
		}

		this.methodName = actionName;

		return this;
	}

	/**
	 * Add the params
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
	 * @param controllerClass
	 * @return
	 */
	protected ApplicationFunctionalTest withController(Class<Users> controllerClass) {
		this.controllerClass = controllerClass;
		return this;
	}
}
