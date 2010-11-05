package notifiers;

import models.User;
import play.mvc.Mailer;

public class UserMailer extends Mailer {
	
	public static void activateAccount(User user) {
		setSubject("Bem vindo ao mediBooking. Por favor active a sua conta");
		addRecipient(user.getEmail());
		setFrom("joaomiguel.pereira@gmail.com");
		send(user);
		
		
	}

}
