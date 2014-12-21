package info.itline.delugemanager.domain;

import java.util.Map;

final class MapHelper {
	
	private MapHelper() {
	}
	
	public static Object getOrDefault(Map<Object, Object> m, Object key, Object defaultValue) {
		Object o = m.get(key);
		if (o != null) {
			return o;
		}
		else {
			return defaultValue;
		}
	}
}