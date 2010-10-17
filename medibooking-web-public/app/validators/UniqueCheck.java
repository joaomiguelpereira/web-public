package validators;

import javax.persistence.Query;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import play.data.validation.Check;
import play.data.validation.Equals;
import play.db.jpa.JPA;
import play.db.jpa.Model;

public class UniqueCheck extends AbstractAnnotationCheck<Unique> {

	final static String mes = "validation.unique";

	@Override
	public boolean isSatisfied(Object validatedObject, Object value,
			OValContext context, Validator validator) throws OValException {

		boolean satisfied = true;
		// Check if the value of the field is null or empty and assume that the
		// validation is satisfied in this case
		
		if (value != null && value.toString().length() > 0) {
			StringBuffer queryStr = new StringBuffer();
			queryStr.append("select p from ");
			queryStr.append(validatedObject.getClass().getSimpleName());
			queryStr.append(" p where ");
			queryStr.append(((FieldContext) context).getField().getName());
			queryStr.append("=:value");
			Query query = JPA.em().createQuery(queryStr.toString());
			query.setParameter("value", value);
			
			if (query.getResultList().size() != 0) {
				satisfied = false;
			} 
		}
		
		return satisfied;

	}

}
