package info.itline.delugemanager.domain;

import info.itline.delugemanager.R;
import info.itline.jrencode.Decoder;

import java.io.Serializable;
import java.util.Map;

import static info.itline.delugemanager.domain.MapHelper.getOrDefault;

public class FileInTorrent implements Serializable {
	
	public FileInTorrent(Map<Object, Object> m, double progress, int priority) throws MessageParsingException {
		try {
			mPath 		= (String) 		getOrDefault(m, K_PATH, "");
			mSize		= (Long) 		Decoder.asLong(getOrDefault(m, K_SIZE, 0));
			mOffset		= (Long)		Decoder.asLong(getOrDefault(m, K_OFFSET, 0));
			mIndex		= (Integer)		getOrDefault(m, K_INDEX, 0);
		}
		catch (ClassCastException e) {
			throw new MessageParsingException(e);
		}
		
		mProgress = progress;
		mPriority = priority;
	}

	public String getPath() {
		return mPath;
	}
	
	public long getSize() {
		return mSize;
	}
	
	public long getOffset() {
		return mOffset;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	public double getProgress() {
		return mProgress;
	}
	
	public int getPriority() {
		return mPriority;
	}
	
	public int getPriorityDescription() {
		if (mPriority <= 0) {
			return R.string.priorityDontDownload;
		}
		else if (mPriority >= PRIORITY_NORMAL && mPriority < PRIORITY_HIGH) {
			return R.string.priorityNormal;
		}
		else if (mPriority >= PRIORITY_HIGH && mPriority < PRIORITY_MAX) {
			return R.string.priorityHigh;
		}
		else {
			return R.string.priorityMaximum;
		}
	}
	
	private final String mPath;
	private final long mSize;
	private final long mOffset;
	private final int mIndex;
	private final double mProgress;
	private final int mPriority;
	
	private static final String
	
		K_PATH			= "path",
		K_SIZE			= "size",
		K_OFFSET		= "offset",
		K_INDEX			= "index";
	
	public static final int 
	
		PRIORITY_DONT_DOWNLOAD 		= 0,
		PRIORITY_NORMAL		= 1,
		PRIORITY_HIGH		= 5,
		PRIORITY_MAX		= 7;
	
	private static final long serialVersionUID = 1;
}