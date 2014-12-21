package info.itline.delugemanager.domain;

import java.io.Serializable;
import java.util.Map;

import static info.itline.delugemanager.domain.MapHelper.getOrDefault;

public class Peer implements Serializable {
	
	public Peer(Map<Object, Object> m) throws MessageParsingException {
		try {
			mDownSpeed 			= (Integer) 	getOrDefault(m, K_DOWN_SPEED, 0);
			mCountry			= (String) 		getOrDefault(m, K_COUNTRY, "");
			mSeed				= (Integer) 	getOrDefault(m, K_SEED, 0);
			mIp					= (String) 		getOrDefault(m, K_IP, "");
			mClient				= (String) 		getOrDefault(m, K_CLIENT, "");
			mProgress			= (Double) 		getOrDefault(m, K_PROGRESS, 0.0);
			mUpSpeed			= (Integer) 	getOrDefault(m, K_UP_SPEED, 0);
		}
		catch (ClassCastException e) {
			throw new MessageParsingException(e);
		}
	}

	public int getDownSpeed() {
		return mDownSpeed;
	}
	
	public String getCountry() {
		return mCountry;
	}
	
	public int getSeed() {
		return mSeed;
	}
	
	public String getIp() {
		return mIp;
	}
	
	public String getClient() {
		return mClient;
	}
	
	public double getProgress() {
		return mProgress;
	}
	
	public int getUpSpeed() {
		return mUpSpeed;
	}
	
	@Override
	public String toString() {
		return "Peer [mDownSpeed=" + mDownSpeed + ", mCountry=" + mCountry
				+ ", mSeed=" + mSeed + ", mIp=" + mIp + ", mClient=" + mClient
				+ ", mProgress=" + mProgress + ", mUpSpeed=" + mUpSpeed + "]";
	}

	private final int mDownSpeed;
	private final String mCountry;
	private final int mSeed;
	private final String mIp;
	private final String mClient;
	private final double mProgress;
	private final int mUpSpeed;
	
	private static final String
	
		K_DOWN_SPEED		= "down_speed",
		K_COUNTRY			= "country",
		K_SEED				= "seed",
		K_IP				= "ip",
		K_CLIENT			= "client",
		K_PROGRESS			= "progress",
		K_UP_SPEED			= "up_speed";
	
	private static final long serialVersionUID = 1;
}