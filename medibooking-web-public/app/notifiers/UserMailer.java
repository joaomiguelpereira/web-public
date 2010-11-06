package notifiers;

import java.util.HashMap;
import java.util.Map;

import models.User;
import play.i18n.Messages;
import play.mvc.Mailer;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;

public class UserMailer extends Mailer {
	
	public static void activateAccount(User user) {
		setSubject(Messages.get("email.user.activation.subject",user.getName()));
		addRecipient(user.getEmail());
		setFrom("joaomiguel.pereira@gmail.com");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activationKey", user.getActivationUUID());
		
		
		ActionDefinition actionDef = Router.reverse("Users.activateUser",params);
		System.err.println("---->"+Router.reverse("Users.activateUser",params).url);
		System.err.println("---->"+Router.reverse("Users.activateUser").add("activationKey",user.getActivationUUID()).url);
		
		actionDef.absolute();
		String activationLink = actionDef.url;
		send(user,activationLink);
		
		
		
	}

}
