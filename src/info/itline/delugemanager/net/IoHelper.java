package info.itline.delugemanager.net;

import static info.itline.jrencode.Constructor.list;
import info.itline.jrencode.Decoder;
import info.itline.jrencode.Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.SSLSocketFactory;

final class IoHelper {
	
	private IoHelper() {
	}
	
	static Socket createSocket(String host, int port) throws IOException {
		SSLSocketFactory f = SimpleSSLSocketFactory.getInstance();
		Socket s = f.createSocket();
		s.setSoTimeout(SOCKET_TIMEOUT);
		s.setReuseAddress(true);
		if (RUNNING_ON_EMULATOR) {
			s.bind(new InetSocketAddress(LOCAL_PORT));
		}
		s.connect(new InetSocketAddress(host, port), 10000);
		return s;
	}
	
	static void closeQuetly(Socket s) {
		try {
			if (s != null) {
				s.close();
			}
		}
		catch (IOException e) {
			
		}
	}
	
	static void serializeRequest(Request r, OutputStream o) throws IOException {
		List<Object> lo = list(list(r.getRequestId(), r.getMethodName(), r.getArgs(), r.getOpts()));
		DeflaterOutputStream dos = new DeflaterOutputStream(o);
		sEncoder.encode(lo, dos);
		dos.finish();
	}
	
	static Object deserializeResponse(InputStream o) throws IOException {
		return sDecoder.decode(new InflaterInputStream(o));
	}
	
	@SuppressWarnings("unchecked")
	static RpcResponse parseResponse(Object o) throws IllegalArgumentException {
		try {
			List<Object> r = (List<Object>) o;
			int messageType = (Integer) r.get(0);
			switch (messageType) {
			case RPC_RESPONSE:
				if (r.get(2) instanceof List) {
					return new RpcResult((Integer) r.get(1), (List<Object>) r.get(2));
				}
				else {
					return new RpcResult((Integer) r.get(1), list(r.get(2)));
				}
			case RPC_ERROR:
				List<Object> args = (List<Object>) r.get(2);
				return new RpcError((Integer) r.get(1), (String) args.get(0), (String) args.get(1));
			case RPC_EVENT:
				return new RpcEvent((String) r.get(1), (List<Object>) r.get(2));
			default:
				throw new IllegalArgumentException();
			}
		}
		catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException(e);
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private static final Encoder sEncoder = new Encoder();
	private static final Decoder sDecoder = new Decoder();
	
	private static final int
	
		RPC_RESPONSE			= 1,
		RPC_ERROR				= 2,
		RPC_EVENT				= 3;
	
	private static final boolean RUNNING_ON_EMULATOR = false;
	private static final int LOCAL_PORT = 35000;
	private static final int SOCKET_TIMEOUT = 5000;
}
