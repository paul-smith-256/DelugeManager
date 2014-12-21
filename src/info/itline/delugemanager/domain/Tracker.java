package info.itline.delugemanager.domain;

import java.io.Serializable;
import java.util.Map;

import static info.itline.delugemanager.domain.MapHelper.getOrDefault;

public class Tracker implements Serializable {
	
	public Tracker(Map<Object, Object> m) throws MessageParsingException {
		try {
			mUpdating 			= (Boolean) 	getOrDefault(m, K_UPDATING, false);
			mTier 				= (Integer) 	getOrDefault(m, K_TIER, 0);
			mFailLimit			= (Integer) 	getOrDefault(m, K_FAIL_LIMIT, 0);
			mVerified			= (Boolean)		getOrDefault(m, K_VERIFIED, false);
			mCompleteSent		= (Boolean) 	getOrDefault(m, K_COMPLETE_SENT, false);
			mSource				= (Integer) 	getOrDefault(m, K_SOURCE, 0);
			mSendStats			= (Boolean) 	getOrDefault(m, K_SEND_STATS, false);
			mFails				= (Integer) 	getOrDefault(m, K_FAILS, 0);
			mUrl				= (String) 		getOrDefault(m, K_URL, "");
			mStartSent			= (Boolean)		getOrDefault(m, K_START_SENT, false);
		}
		catch (ClassCastException e) {
			throw new MessageParsingException(e);
		}
	}

	public boolean isUpdating() {
		return mUpdating;
	}

	public int getTier() {
		return mTier;
	}

	public int getFailLimit() {
		return mFailLimit;
	}

	public boolean isVerified() {
		return mVerified;
	}

	public boolean isCompleteSent() {
		return mCompleteSent;
	}

	public int getSource() {
		return mSource;
	}

	public boolean isSendStats() {
		return mSendStats;
	}

	public int getFails() {
		return mFails;
	}

	public String getUrl() {
		return mUrl;
	}

	public boolean isStartSent() {
		return mStartSent;
	}

	@Override
	public String toString() {
		return "Tracker [mUpdating=" + mUpdating + ", mTier=" + mTier
				+ ", mFailLimit=" + mFailLimit + ", mVerified=" + mVerified
				+ ", mCompleteSent=" + mCompleteSent + ", mSource=" + mSource
				+ ", mSendStats=" + mSendStats + ", mFails=" + mFails
				+ ", mUrl=" + mUrl + ", mStartSent=" + mStartSent + "]";
	}

	private final boolean mUpdating;
	private final int mTier;
	private final int mFailLimit;
	private final boolean mVerified;
	private final boolean mCompleteSent;
	private final int mSource;
	private final boolean mSendStats;
	private final int mFails;
	private final String mUrl;
	private final boolean mStartSent;
	
	private static final String
	
		K_UPDATING 			= "updating",
		K_TIER				= "tier",
		K_FAIL_LIMIT		= "fail_limit",
		K_VERIFIED			= "verified",
		K_COMPLETE_SENT		= "complete_sent",
		K_SOURCE			= "source",
		K_SEND_STATS		= "send_stats",
		K_FAILS				= "fails",
		K_URL				= "url",
		K_START_SENT		= "start_sent";
	
	private static final long serialVersionUID = 1;
}