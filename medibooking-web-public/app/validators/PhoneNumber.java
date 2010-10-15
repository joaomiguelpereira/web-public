package validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * This field must be a list of Phone Numbers. Currently use
 * Message key: validation.equals
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = PhoneNumberCheck.class)
public @interface PhoneNumber {
	String message() default PhoneNumberCheck.mes;
	

}
