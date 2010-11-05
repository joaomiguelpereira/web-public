package models;

import org.junit.Ignore;

import play.Logger;
import play.data.validation.Error;
import play.test.UnitTest;

@Ignore
public class ModelUnitTest extends UnitTest {

	protected void assertEntitySaved() {
		//get the validation errors from last op
		int errorsSize = play.data.validation.Validation.errors().size();
		for (Error error: play.data.validation.Validation.errors()) {
			Logger.error("Error in : "+error.getKey()+ "-> "+error.message());
		}
		
		play.data.validation.Validation.current().clear();		
		assertEquals(0, errorsSize);
		
	}
}
