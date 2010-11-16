package templating;



import play.i18n.Messages;
import play.templates.JavaExtensions;

public class Extensions extends JavaExtensions {
	public static String booleanAs(Boolean value, String trueValue, String falseValue) {
		return value?trueValue:falseValue;
	}
}
