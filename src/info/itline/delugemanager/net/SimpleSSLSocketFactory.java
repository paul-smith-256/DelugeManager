package info.itline.delugemanager.net;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

public class SimpleSSLSocketFactory {
	
	public static SSLSocketFactory getInstance() {
		if (mFactory == null) {
			try {
				TrustManager[] tm = new TrustManager[] {new SimpleTrustManager()};
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(new KeyManager[0], tm, new SecureRandom());
				mFactory = (SSLSocketFactory) context.getSocketFactory();
			} 
			catch (KeyManagementException e) {
				Log.i(LOG_TAG, "KeyManagerException", e);
			} 
			catch (NoSuchAlgorithmException e) {
				Log.i(LOG_TAG, "NoSuchAlgorithException", e);
			}
		}
		return mFactory;
	}
	
	private static class SimpleTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		
	}
		
	private static SSLSocketFactory mFactory;
	
	private static final String LOG_TAG = SimpleSSLSocketFactory.class.getSimpleName();
}
