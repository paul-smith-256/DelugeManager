package info.itline.delugemanager.ui;

public final class UnitHelper {
	
	public UnitHelper() {
	}
	
	public static String getSizeWithUnits(long v) {
		double value;
		String unit;
		
		if (v < KiB) {
			unit = "B";
			value = v;
		}
		else if (v < MiB) {
			unit = "KiB";
			value = v / KiB;
		}
		else if (v < GiB) {
			unit = "MiB";
			value = v / MiB;
		}
		else if (v < TiB) {
			unit = "GiB";
			value = v / GiB;
		}
		else {
			unit = "TiB";
			value = v / TiB;
		}
		
		return String.format("%.2f %s", value, unit);
	}
	
	public static String getSpeedWithUnits(long v) {
		return getSizeWithUnits(v) + "/s";
	}
	
	public static String getTimeWithUnits(long v) {
		if (v < MINUTE) {
			return v + " s";
		}
		else if (v < HOUR) {
			return String.format("%dm %ds", v / MINUTE, v % MINUTE);
		}
		else if (v < DAY) {
			return String.format("%dh %dm", v / HOUR, (v % HOUR) / MINUTE);
		}
		else {
			return "> 1d";
		}
	}
	
	private static final double 
	
		KiB = 1024.0,
		MiB = 1024.0 * KiB,
		GiB = 1024.0 * MiB,
		TiB = 1024.0 * GiB;
	
	private static final int
	
		MINUTE		= 60,
		HOUR		= 60 * MINUTE,
		DAY			= 24 * HOUR;
}
