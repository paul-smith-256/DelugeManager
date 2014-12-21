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
import static info.itline.jrencode.Const.MAX_INT_LENGTH;
import static info.itline.jrencode.Const.STR_FIXED_COUNT;
import static info.itline.jrencode.Const.STR_FIXED_START;
import static info.itline.jrencode.Const.STR_LEN_DATA_SEPARATOR;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class Encoder {
	
	private void encodeNull(DataOutputStream o) throws IOException {
		o.writeByte(CHR_NONE);
	}
	
	private void encodePrimitive(boolean b, DataOutputStream o) throws IOException {
		o.writeByte(b ? CHR_TRUE : CHR_FALSE);
	}
	
	private void encodeDecimal(long v, DataOutputStream o) throws IOException {
		if (0 <= v && v < INT_POS_FIXED_COUNT) {
			o.writeByte((byte) (INT_POS_FIXED_START + v));
		}
		else if (-INT_NEG_FIXED_COUNT <= v && v < 0) {
			o.writeByte((byte) (INT_NEG_FIXED_START - 1 - v));
		}
		else if (Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE) {
			o.writeByte(CHR_INT1);
			o.writeByte((byte) v);
		}
		else if (Short.MIN_VALUE <= v && v < Short.MAX_VALUE) {
			o.writeByte(CHR_INT2);
			o.writeShort((short) v);
		}
		else if (Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE) {
			o.writeByte(CHR_INT4);
			o.writeInt((int) v);
		}
		else {
			o.writeByte(CHR_INT8);
			o.writeLong(v);
		}
	}
	
	private void encodePrimitive(byte b, DataOutputStream o) throws IOException {
		encodeDecimal(b, o);
	}
	
	private void encodePrimitive(short s, DataOutputStream o) throws IOException {
		encodeDecimal(s, o);
	}
	
	private void encodePrimitive(int i, DataOutputStream o) throws IOException {
		encodeDecimal(i, o);
	}
	
	private void encodePrimitive(long l, DataOutputStream o) throws IOException {
		encodeDecimal(l, o);
	}
	
	private void encodePrimitive(BigInteger i, DataOutputStream o) throws IOException {
		byte[] b = i.toString().getBytes("US-ASCII");
		if (b.length > MAX_INT_LENGTH) {
			throw new IllegalArgumentException("BigInteger value is too large");
		}
		o.writeByte(CHR_INT);
		o.write(b);
		o.write(CHR_TERM);
	}
	
	private void encodePrimitive(float f, DataOutputStream o) throws IOException {
		o.writeByte(CHR_FLOAT32);
		o.writeFloat(f);
	}
	
	private void encodePrimitive(double d, DataOutputStream o) throws IOException {
		o.writeByte(CHR_FLOAT64);
		o.writeDouble(d);
	}
	
	private void encodePrimitive(String s, DataOutputStream o) throws IOException {
		byte[] b = s.getBytes(mUseUtf ? "UTF-8" : "US-ASCII");
		if (b.length < STR_FIXED_COUNT) {
			o.writeByte((byte) (STR_FIXED_START + b.length));
			o.write(b);
		}
		else {
			o.write(Integer.toString(s.length()).getBytes("US-ASCII"));
			o.writeByte(STR_LEN_DATA_SEPARATOR);
			o.write(b);
		}
	}
	
	private void encodePrimitive(List<Object> t, DataOutputStream o) throws IOException {
		int size = t.size();
		if (size < LIST_FIXED_COUNT) {
			o.writeByte(LIST_FIXED_START + size); 
			for (Object j: t) {
				encodeObject(j, o);
			}
		}
		else {
			o.writeByte(CHR_LIST);
			for (Object j: t) {
				encodeObject(j, o);
			}
			o.writeByte(CHR_TERM);
		}
	}
	
	private void encodePrimitive(Map<Object, Object> m, DataOutputStream o) throws IOException {
		int size = m.size();
		if (size < DICT_FIXED_COUNT) {
			o.writeByte(DICT_FIXED_START + size);
			for (Map.Entry<Object, Object> e: m.entrySet()) {
				encodeObject(e.getKey(), o);
				encodeObject(e.getValue(), o);
			}
		}
		else {
			o.writeByte(CHR_DICT);
			for (Map.Entry<Object, Object> e: m.entrySet()) {
				encodeObject(e.getKey(), o);
				encodeObject(e.getValue(), o);
			}
			o.writeByte(CHR_TERM);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void encodeObject(Object j, DataOutputStream o) throws IOException {
		if (j == null) {
			encodeNull(o);
		}
		else if (j instanceof Boolean) {
			encodePrimitive((Boolean) j, o);
		}
		else if (j instanceof Byte) {
			encodePrimitive((Byte) j, o);
		}
		else if (j instanceof Short) {
			encodePrimitive((Short) j, o);
		}
		else if (j instanceof Integer) {
			encodePrimitive((Integer) j, o);
		}
		else if (j instanceof Long) {
			encodePrimitive((Long) j, o);
		}
		else if (j instanceof BigInteger) {
			encodePrimitive((BigInteger) j, o);
		}
		else if (j instanceof Float) {
			encodePrimitive((Float) j, o);
		}
		else if (j instanceof Double) {
			encodePrimitive((Double) j, o);
		}
		else if (j instanceof String) {
			encodePrimitive((String) j, o);
		}
		else if (j instanceof Map) {
			encodePrimitive((Map<Object, Object>) j, o);
		}
		else if (j instanceof List) {
			encodePrimitive((List<Object>) j, o);
		}
		else {
			throw new IllegalArgumentException("Object is not serializable: " + o.getClass());
		}
	}
	
	public void encode(Object j, OutputStream os) throws IOException {
		DataOutputStream o = new DataOutputStream(os);
		encodeObject(j, o);
		o.flush();
	}
	
	public boolean isUseUtf() {
		return mUseUtf;
	}

	public void setUseUtf(boolean useUtf) {
		mUseUtf = useUtf;
	}
	
	private boolean mUseUtf = true;
}
