package info.itline.jrencode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Constructor {
	
	private Constructor() {
	}
	
	public static List<Object> list(Object... o) {
		return Arrays.asList(o);
	}
	
	public static Map<Object, Object> map(Object[]... o) {
		HashMap<Object, Object> result = new HashMap<Object, Object>();
		for (Object[] e: o) {
			result.put(e[0], e[1]);
		}
		return result;
	}
}
