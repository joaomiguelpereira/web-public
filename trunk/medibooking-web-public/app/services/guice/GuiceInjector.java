package services.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import play.modules.guice.GuiceSupport;


public class GuiceInjector extends GuiceSupport {

	@Override
	protected Injector configure() {
		
		return Guice.createInjector(new MedibookingModule());
	}
	

}


