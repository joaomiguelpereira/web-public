package validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;
import play.data.validation.EqualsCheck;

/**
 * This field must be unique.
 * Message key: validation.equals
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = UniqueCheck.class)
public @interface Unique {
	String message() default UniqueCheck.mes;

}
