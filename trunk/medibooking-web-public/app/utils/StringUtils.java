package utils;

import java.util.regex.Matcher;

public class StringUtils {

	/**
	 * Given an object, this convenience method converts it to CamelCase
	 * Ex: CAMEL_CASE is tranformed in CamelCase
	 * 
	 * @return
	 */
	public static String camelize(Object arg, boolean firstLetterCapitalized) {
		StringBuffer sb = new StringBuffer();
		String[] str = arg.toString().split("_");
		int count = 0;
		for (String temp : str) {
			
			if (count==0 && !firstLetterCapitalized) {
				sb.append(Character.toLowerCase(temp.charAt(0)));
			} else {
				sb.append(Character.toUpperCase(temp.charAt(0)));
			}
			sb.append(temp.substring(1).toLowerCase());
			count++;
		}
		return sb.toString();

	}
}
