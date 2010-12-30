package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import play.Logger;
import play.data.binding.Binder;
import play.data.binding.Unbinder;
import play.data.validation.Error;
import play.db.jpa.Model;
import play.i18n.Messages;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;
import play.mvc.Scope.Params;

public class JSONUtils {

	public static final String MESSAGE_ERROR = "error";
	public static final String MESSAGE_WARNING = "warning";
	public static final String MESSAGE_SUCCESS = "success";
	
	public static String successMessage(String i18nKey) {
		Map<String, String> jsonOut = new HashMap<String, String>();
		jsonOut.put(MESSAGE_SUCCESS, Messages.get(i18nKey));
		return new Gson().toJson(jsonOut);
	}
	
	
	public static String errorMessage(String i18nKey) {
		Map<String, String> jsonOut = new HashMap<String, String>();
		jsonOut.put(MESSAGE_ERROR, Messages.get(i18nKey));
		return new Gson().toJson(jsonOut);
	}
	
	public static String warningMessage(String i18nKey) {
		Map<String, String> jsonOut = new HashMap<String, String>();
		jsonOut.put(MESSAGE_WARNING, Messages.get(i18nKey));
		return new Gson().toJson(jsonOut);
	}
	
	public static <T extends Model> boolean mergeFromJson(T target,
			String varName, Params params) {
		T source = (T) new Gson().fromJson(params.get("body"),
				target.getClass());
		Logger.debug("Body: "+ params.get("body"));
		boolean result = false;
		if (source != null) {
			result = !result;
			Map<String, Object> tmpParams = new HashMap<String, Object>();
			Unbinder.unBind(tmpParams, source, varName);
			// Transform into params
			for (String key : tmpParams.keySet()) {
				Object value = tmpParams.get(key);
				if (value != null) {
					// is String, int, long?
					if (value instanceof String || value instanceof Integer
							|| value instanceof Long) {
						params.put(key, value.toString());
					}
					// TODO: Rest of the cases when needed
				}
			}
			// Now bind
			Binder.bind(target, varName, params.all());
		}
		return result;

	}


	public static String createRedirectionTo(String url, String i18nKey) {
		Map<String, String> jsonOut = new HashMap<String, String>();
		jsonOut.put(MESSAGE_ERROR, Messages.get(i18nKey));
		ActionDefinition ad = Router.reverse(url);
		ad.absolute();
		jsonOut.put("redirectTo",ad.url);
		
		return new Gson().toJson(jsonOut);
		
		
	}
	

}