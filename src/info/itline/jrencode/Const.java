package info.itline.jrencode;

public final class Const {
	
	private Const() {
	}
	
	public static final int
	
		CHR_LIST 	= 59,
		CHR_DICT 	= 60,
		CHR_INT 	= 61,
		CHR_INT1 	= 62,
		CHR_INT2 	= 63,
		CHR_INT4 	= 64,
		CHR_INT8 	= 65,
		CHR_FLOAT32 = 66,
		CHR_FLOAT64 = 44,
		CHR_TRUE 	= 67,
		CHR_FALSE 	= 68,
		CHR_NONE 	= 69,
		CHR_TERM 	= 127;
	
	public static final int
	
		INT_POS_FIXED_START = 0,
		INT_POS_FIXED_COUNT = 44,

		DICT_FIXED_START 	= 102,
		DICT_FIXED_COUNT 	= 25,
		
		INT_NEG_FIXED_START = 70,
		INT_NEG_FIXED_COUNT = 32,

		STR_FIXED_START 	= 128,
		STR_FIXED_COUNT 	= 64,

		LIST_FIXED_START 	= STR_FIXED_START + STR_FIXED_COUNT,
		LIST_FIXED_COUNT 	= 64;
	
	public static final int
	
		MAX_INT_LENGTH = 64;
	
	public static final byte
	
		STR_LEN_DATA_SEPARATOR = (byte) ':';
}
