package tags;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import play.i18n.Messages;
import play.templates.BaseTemplate;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;

@FastTags.Namespace("messages")
public class _setI18nPageTitle extends BaseFastTags {

	private static final String KEY = "key";

	/**
	 * Use this tag to set the page title passing only the i18n key
	 * 
	 * @param args
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 */
	public static void _setI18nTitle(Map<?, ?> args, Closure body,
			PrintWriter out, ExecutableTemplate template, int fromLine) {
		// Format setI18nTitle key:'i18n.title'
		// find in args the key "key"

		throwIllegalArgumentExceptionIfNotTrue(args.keySet().contains(KEY),
				"Use this tag with argument 'key'");
		
		String titleMessage = Messages.get((String)args.get(KEY));
		
		BaseTemplate.layoutData.get().put("title", titleMessage);
		
		

	}

}
