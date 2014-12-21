package info.itline.jrencode;

import static info.itline.jrencode.Const.CHR_DICT;
import static info.itline.jrencode.Const.CHR_FALSE;
import static info.itline.jrencode.Const.CHR_FLOAT32;
import static info.itline.jrencode.Const.CHR_FLOAT64;
import static info.itline.jrencode.Const.CHR_INT;
import static info.itline.jrencode.Const.CHR_INT1;
import static info.itline.jrencode.Const.CHR_INT2;
import static info.itline.jrencode.Const.CHR_INT4;
import static info.itline.jrencode.Const.CHR_INT8;
import static info.itline.jrencode.Const.CHR_LIST;
import static info.itline.jrencode.Const.CHR_NONE;
import static info.itline.jrencode.Const.CHR_TERM;
import static info.itline.jrencode.Const.CHR_TRUE;
import static info.itline.jrencode.Const.DICT_FIXED_COUNT;
import static info.itline.jrencode.Const.DICT_FIXED_START;
import static info.itline.jrencode.Const.INT_NEG_FIXED_COUNT;
import static info.itline.jrencode.Const.INT_NEG_FIXED_START;
import static info.itline.jrencode.Const.INT_POS_FIXED_COUNT;
import static info.itline.jrencode.Const.INT_POS_FIXED_START;
import static info.itline.jrencode.Const.LIST_FIXED_COUNT;
import static info.itline.jrencode.Const.LIST_FIXED_START;
import static info.itline.jrencode.Const.STR_FIXED_COUNT;
import static info.itline.jrencode.Const.STR_FIXED_START;
import static info.itline.jrencode.Const.STR_LEN_DATA_SEPARATOR;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decoder {
	
	public static Long asLong(Object o) {
		if (o instanceof Integer) {
			return (long) ((Integer) o);
		}
		else {
			return (Long) o;
		}
	}
	
	public static Double asDouble(Object o) {
		if (o instanceof Integer) {
			return (double) ((Integer) o);
		}
		else if (o instanceof Long) {
			return (double) ((Long) o);
		}
		else {
			return (Double) o;
		}
	}
	
	public Object decode(InputStream is) throws IOException {
		DataInputStream input = new DataInputStream(is);
		return decode(input.readByte() & 0xFF, input);
	}
	
	private Object decode(int t, DataInputStream input) throws IOException {
		if (t == CHR_NONE) {
			return null;
		}
		else if (t == CHR_FALSE) {
			return false;
		}
		else if (t == CHR_TRUE) {
			return true;
		}
		else if (t == CHR_INT1) {
			return (int) input.readByte();
		}
		else if (t == CHR_INT2) {
			return (int) input.readShort();
		}
		else if (t == CHR_INT4) {
			return input.readInt();
		}
		else if (t == CHR_INT8) {
			return input.readLong();
		}
		else if (t == CHR_FLOAT32) {
			return (double) input.readFloat();
		}
		else if (t == CHR_FLOAT64) {
			return input.readDouble();
		}
		else if (t == CHR_INT) {
			byte[] b = readWhile((byte) CHR_TERM, input);
			return new BigInteger(new String(b, "US-ASCII"));
		}
		else if ((int) '0' <= t && t <= (int) '9') {
			byte[] tmp = readWhile(STR_LEN_DATA_SEPARATOR, input);
			byte[] lenBytes = new byte[tmp.length + 1];
			lenBytes[0] = (byte) t;
			System.arraycopy(tmp, 0, lenBytes, 1, tmp.length);
			int len;
			try {
				len = Integer.parseInt(new String(lenBytes, "US-ASCII"));
			}
			catch (NumberFormatException e) {
				throw new IOException(e.getMessage());
			}
			byte[] data = new byte[len];
			input.readFully(data);
//			int bytesRead = input.read(data);
//			if (bytesRead != len) {
//				throw new EOFException();
//			}
			return new String(data, mUseUtf ? "UTF-8" : "US-ASCII");
		}
		else if (t == CHR_LIST) {
			List<Object> result = new ArrayList<Object>();
			int t1 = input.readByte() & 0xFF;
			while (t1 != CHR_TERM) {
				result.add(decode(t1, input));
				t1 = input.readByte() & 0xFF;
			}
			return result;
		}
		else if (t == CHR_DICT) {
			Map<Object, Object> result = new HashMap<Object, Object>();
			int t1 = input.readByte() & 0xFF;
			while (t1 != CHR_TERM) {
				Object key = decode(t1, input);
				t1 = input.readByte() & 0xFF;
				Object value = decode(t1, input);
				t1 = input.readByte() & 0xFF;
				result.put(key, value);
			}
			return result;
		}
		else if (INT_POS_FIXED_START <= t && t < INT_POS_FIXED_START + INT_POS_FIXED_COUNT) {
			return t - INT_POS_FIXED_START;
		}
		else if (INT_NEG_FIXED_START <= t && t < INT_NEG_FIXED_START + INT_NEG_FIXED_COUNT) {
			return INT_NEG_FIXED_START - t - 1;
		}
		else if (STR_FIXED_START <= t && t < STR_FIXED_START + STR_FIXED_COUNT) {
			int len = t - STR_FIXED_START;
			byte[] b = new byte[len];
			input.readFully(b);
//			int bytesRead = input.read(b);
//			System.err.println(len + "->" + bytesRead);
//			if (bytesRead != len) {
//				throw new EOFException();
//			}
			return new String(b, mUseUtf ? "UTF-8" : "US-ASCII");
		}
		else if (LIST_FIXED_START <= t && t < LIST_FIXED_START + LIST_FIXED_COUNT) {
			List<Object> result = new ArrayList<Object>();
			for (int i = 0; i < t - LIST_FIXED_START; i++) {
				int t1 = input.readByte() & 0xFF;
				result.add(decode(t1, input));
			}
			return result;
		}
		else if (DICT_FIXED_START <= t && t < DICT_FIXED_START + DICT_FIXED_COUNT) {
			Map<Object, Object> result = new HashMap<Object, Object>();
			for (int i = 0; i < t - DICT_FIXED_START; i++) {
				int t1 = input.readByte() & 0xFF;
				Object key = decode(t1, input);
				t1 = input.readByte() & 0xFF;
				Object value = decode(t1, input);
				result.put(key, value);
			}
			return result;
		}
		else {
			throw new IOException("Unknown data type " + t);
		}
	}
	
	private byte[] readWhile(byte c, DataInputStream input) throws IOException {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		byte b;
		while ((b = input.readByte()) != c) {
			s.write(b);
		}
		return s.toByteArray();
	}
	
	public boolean isUseUtf() {
		return mUseUtf;
	}

	public void setUseUtf(boolean useUtf) {
		mUseUtf = useUtf;
	}

	private boolean mUseUtf = true;
}
