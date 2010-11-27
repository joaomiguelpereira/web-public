package notifiers;

import java.util.HashMap;
import java.util.Map;

import models.User;
import play.Play;
import play.i18n.Messages;
import play.mvc.Mailer;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;

public class UserMailer extends Mailer {
	
	public static void activateAccount(User user) {
		setSubject(Messages.get("email.user.activation.subject",user.getName()));
		addRecipient(user.getEmail());
		
		setFrom(Play.configuration.get("admin.email"));
		send(user);
		
		
		
	}

	public static void resetPassword(User user) {
		setSubject(Messages.get("controllers.users.resetPassword.mail.subject"));
		addRecipient(user.getEmail());
		setFrom(Play.configuration.get("admin.email"));
		send(user);
				
	}

}
