package tags;

import constants.SessionValuesConstants;
import play.mvc.Scope.Session;
import play.templates.FastTags;

public class BaseFastTags extends FastTags {
	
	protected static boolean hasSession() {

		return (Session.current().contains(SessionValuesConstants.LOGIN_EMAIL)
				&& Session.current().contains(
						SessionValuesConstants.LOGIN_TOKEN) && Session
				.current().contains(SessionValuesConstants.USER_TYPE));
	}
}
