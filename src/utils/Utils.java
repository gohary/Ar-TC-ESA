package utils;

public class Utils {

	public static String implode(String[] array, String sep) {
		if (array.length == 0)
			return "";
		String str = array[0];
		for (int i = 1; i < array.length; i++) {
			str += sep + array[i];
		}
		return str;
	}
}
