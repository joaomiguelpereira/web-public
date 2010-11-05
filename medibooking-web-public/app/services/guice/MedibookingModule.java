package services.guice;

import services.DefaultUserService;
import services.UserService;

import com.google.inject.AbstractModule;

/**
 * The Guice module for the application
 * @author jpereira
 *
 */
public class MedibookingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserService.class).to(DefaultUserService.class);
		
	}
	
}