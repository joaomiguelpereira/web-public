package validators;

import java.util.regex.Pattern;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * Check for phone numbers. NOTE: Currently it does nothing besides validation
 * Portuguese phone numbers without the country code
 * 
 * @author jpereira
 * 
 */
public class PhoneNumberCheck extends AbstractAnnotationCheck<PhoneNumber> {

	final static String mes = "validation.phone";

	@Override
	public boolean isSatisfied(Object validatedObject, Object value,
			OValContext context, Validator validator) throws OValException {
		// Currently it does nothing, except validation the phone number for
		// Portugal without country code
		boolean satisfied = true;
		// Note the length== 9 (Portugal phone number format without country
		// code are 9 digits)
		
		if (value != null && value.toString().length() > 0) {
			// Check if is a number
			try {
				Integer.valueOf(value.toString());
				
				if ( value.toString().length()<9) {
					satisfied = false;
				}				
			} catch (Exception e) {
				//do nothing, just flag that this is not a number
				satisfied = false;
			}

		}
		return satisfied;
	}

}
