package info.itline.delugemanager.domain;

import static info.itline.delugemanager.domain.MapHelper.getOrDefault;
import static info.itline.jrencode.Constructor.list;
import static info.itline.jrencode.Constructor.map;
import info.itline.jrencode.Decoder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import android.util.Log;

public class Torrent implements Serializable {
	
	@SuppressWarnings("unchecked")
	public static List<Torrent> parseMessage(List<Object> m) 
			throws MessageParsingException {
		try {
			Map<String, Map<Object, Object>> torrents = 
					(Map<String, Map<Object, Object>>) m.get(0);
			ArrayList<Torrent> result = new ArrayList<Torrent>();
			for (Entry<String, Map<Object, Object>> t: torrents.entrySet()) {
				result.add(new Torrent(t.getValue()));
			}
			return result;
		}
		catch (ClassCastException e) {
			throw new MessageParsingException(e);
		}
		catch (IndexOutOfBoundsException e) {
			throw new MessageParsingException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Torrent(Map<Object, Object> m) throws MessageParsingException {
		try {
			List<Map<Object, Object>> t0 = (List<Map<Object, Object>>)
					getOrDefault(m, K_PEERS, sEmptyList);
			for (Map<Object, Object> o: t0) {
				mPeers.add(new Peer(o));
			}
			
			List<Map<Object, Object>> t1 = (List<Map<Object, Object>>) 
					getOrDefault(m, K_TRACKERS, sEmptyList);
			for (Map<Object, Object> o: t1) {
				mTrackers.add(new Tracker(o));
			}
			
			List<Integer> t3 = (List<Integer>)
					getOrDefault(m, K_FILE_PRIORITIES, sEmptyList);
			for (Integer i: t3) {
				mFilePriorities.add(i);
			}
			
			List<Double> t4 = (List<Double>)
					getOrDefault(m, K_FILE_PROGRESS, sEmptyList);
			for (Double d: t4) {
				mFileProgress.add(d);
			}
			
			List<Map<Object, Object>> t2 = (List<Map<Object, Object>>)
					getOrDefault(m, K_FILES, sEmptyList);
			Iterator<Integer> filePriority = mFilePriorities.iterator();
			Iterator<Double> fileProgress = mFileProgress.iterator();
			for (Map<Object, Object> o: t2) {
				mFiles.add(new FileInTorrent(o, fileProgress.next(), filePriority.next()));
			}
			
					
		    mMoveCompletedPath 			= (String) 		getOrDefault(m, K_MOVE_COMPLETED_PATH, "");
			mPaused						= (Boolean) 	getOrDefault(m, K_PAUSED, false);
			mCompact					= (Boolean)		getOrDefault(m, K_COMPACT, false);
			mUploadPayloadRate			= (Integer)		getOrDefault(m, K_UPLOAD_PAYLOAD_RATE, 0);
			mPrioritizeFirstLast		= (Boolean)		getOrDefault(m, K_PRIORITIZE_FIRST_LAST, false);
			mEta						= (Integer)		getOrDefault(m, K_ETA, 0);
			mNumPeers					= (Integer)		getOrDefault(m, K_NUM_PEERS, 0);
			mTrackerStatus				= (String)		getOrDefault(m, K_TRACKER_STATUS, "");
			mState						= (String)		getOrDefault(m, K_STATE, "");
			mPieceLength				= (Integer)		getOrDefault(m, K_PIECE_LENGTH, 0);
			mMoveOnCompletedPath		= (String)		getOrDefault(m, K_MOVE_ON_COMPLETED_PATH, "");
			mMoveCompleted				= (Boolean)		getOrDefault(m, K_MOVE_COMPLETED, false);
			mSeedsPeersRatio			= (Double)		getOrDefault(m, K_SEEDS_PEERS_RATIO, 0.0);
			mMaxUploadSpeed				= (Double)		Decoder.asDouble(getOrDefault(m, K_MAX_UPLOAD_SPEED, -1.0));
			mNumPieces					= (Integer)		getOrDefault(m, K_NUM_PIECES, 0);
			mMaxDownloadSpeed			= (Double)		Decoder.asDouble(getOrDefault(m, K_MAX_DOWNLOAD_SPEED, -1.0));
			mActiveTime					= (Integer)		getOrDefault(m, K_ACTIVE_TIME, 0);
			mName						= (String)		getOrDefault(m, K_NAME, "");
			mNumFiles					= (Integer)		getOrDefault(m, K_NUM_FILES, -1);
			mTimeAdded					= (Double)		getOrDefault(m, K_TIME_ADDED, 0.0);
			mHash						= (String)		getOrDefault(m, K_HASH, "");
			mNextAnnounce				= (Integer)		getOrDefault(m, K_NEXT_ANNOUNCE, 0);
			mPrivate					= (Boolean)		getOrDefault(m, K_PRIVATE, false);
			mSeedingTime				= (Integer)		getOrDefault(m, K_SEEDING_TIME, 0);
			mSeedRank					= (Integer)		getOrDefault(m, K_SEED_RANK, 0);
			mAllTimeDownload			= (Integer)		getOrDefault(m, K_ALL_TIME_DOWNLOAD, 0);
			mTrackerHost				= (String)		getOrDefault(m, K_TRACKER_HOST, "");
			mDownloadPayloadRate		= (Integer)		getOrDefault(m, K_DOWNLOAD_PAYLOAD_RATE, 0);
			mSavePath					= (String)		getOrDefault(m, K_SAVE_PATH, "");
			mNumSeeds					= (Integer)		getOrDefault(m, K_NUM_SEEDS, 0);
			mMaxUploadSlots				= (Integer)		getOrDefault(m, K_MAX_UPLOAD_SLOTS, 0);
			mTracker					= (String)		getOrDefault(m, K_TRACKER, "");
			mMoveOnCompleted			= (Boolean)		getOrDefault(m, K_MOVE_ON_COMPLETED, false);
			mStopAtRatio				= (Boolean)		getOrDefault(m, K_STOP_AT_RATIO, false);
			mTotalPayloadUpload			= (Integer)		getOrDefault(m, K_TOTAL_PAYLOAD_UPLOAD, 0);
			mRemoveAtRatio				= (Boolean)		getOrDefault(m, K_REMOVE_AT_RATIO, false);
			mMaxConnections				= (Integer)		getOrDefault(m, K_MAX_CONNECTIONS, 0);
			mTotalWanted				= (Integer)		getOrDefault(m, K_TOTAL_WANTED, 0);
			mStopRatio					= (Double)		getOrDefault(m, K_STOP_RATIO, 1.0);
			mIsAutoManaged				= (Boolean)		getOrDefault(m, K_IS_AUTO_MANAGED, false);
			mTotalPayloadDownload		= (Integer)		getOrDefault(m, K_TOTAL_PAYLOAD_DOWNLOAD, 0);
			mTotalSeeds					= (Integer)		getOrDefault(m, K_TOTAL_SEEDS, 0);
			mMessage					= (String)		getOrDefault(m, K_MESSAGE, "");
			mIsFinished					= (Boolean)		getOrDefault(m, K_IS_FINISHED, false);
			mTotalPeers					= (Integer)		getOrDefault(m, K_TOTAL_PEERS, 0);
			mProgress					= (Double)		getOrDefault(m, K_RPOGRESS, 0.0);
			mComment					= (String)		getOrDefault(m, K_COMMENT, "");
			mIsSeed						= (Boolean)		getOrDefault(m, K_IS_SEED, false);
			mQueue						= (Integer)		getOrDefault(m, K_QUEUE, 0);
			mRatio						= (Double)		getOrDefault(m, K_RATIO, 0.0);
			mDistributedCopies			= (Double)		getOrDefault(m, K_DISTRIBUTED_COPIES, 0.0);
			
			mTotalSize					= (Long)		Decoder.asLong(getOrDefault(m, K_TOTAL_SIZE, 0));
			mTotalDone					= (Long)		Decoder.asLong(getOrDefault(m, K_TOTAL_DONE, 0));
			mTotalUploaded				= (Long)		Decoder.asLong(getOrDefault(m, K_TOTAL_UPLOADED, 0));
			
		}
//		catch (ClassCastException e) {
//			throw new MessageParsingException(e);
//		}
		catch (NoSuchElementException e) {
			throw new MessageParsingException(e);
		}
	}
	
	private static final Map<Object, Object> sEmptyMap = map();
	private static final List<Object> sEmptyList = list();
	
	public List<Peer> getPeers() {
		return mPeers;
	}
	
	public List<Tracker> getTrackers() {
		return mTrackers;
	}
	
	public String getMoveCompletedPath() {
		return mMoveCompletedPath;
	}
	
	public boolean isPaused() {
		return mPaused;
	}
	
	public boolean isCompact() {
		return mCompact;
	}
	
	public int getUploadPayloadRate() {
		return mUploadPayloadRate;
	}
	
	public List<Integer> getFilePriorities() {
		return Collections.unmodifiableList(mFilePriorities);
	}
	
	public boolean isPrioritizeFirstLast() {
		return mPrioritizeFirstLast;
	}
	
	public int getEta() {
		return mEta;
	}
	
	public int getNumPeers() {
		return mNumPeers;
	}
	
	public String getTrackerStatus() {
		return mTrackerStatus;
	}
	
	public String getState() {
		return mState;
	}
	
	public int getPieceLength() {
		return mPieceLength;
	}
	
	public String getMoveOnCompletedPath() {
		return mMoveOnCompletedPath;
	}
	
	public boolean isMoveCompleted() {
		return mMoveCompleted;
	}
	
	public double getSeedsPeersRatio() {
		return mSeedsPeersRatio;
	}
	
	public double getMaxUploadSpeed() {
		return mMaxUploadSpeed;
	}
	
	public int getNumPieces() {
		return mNumPieces;
	}
	
	public double getMaxDownloadSpeed() {
		return mMaxDownloadSpeed;
	}
	
	public int getActiveTime() {
		return mActiveTime;
	}
	
	public String getName() {
		return mName;
	}
	
	public List<FileInTorrent> getFiles() {
		return Collections.unmodifiableList(mFiles);
	}
	
	public int getNumFiles() {
		return mNumFiles;
	}
	
	public double getTimeAdded() {
		return mTimeAdded;
	}
	
	public String getHash() {
		return mHash;
	}
	
	public int getNextAnnounce() {
		return mNextAnnounce;
	}
	
	public boolean isPrivate() {
		return mPrivate;
	}
	
	public int getSeedingTime() {
		return mSeedingTime;
	}
	
	public int getSeedRank() {
		return mSeedRank;
	}
	
	public int getAllTimeDownload() {
		return mAllTimeDownload;
	}
	
	public String getTackerHost() {
		return mTrackerHost;
	}
	
	public int getDownloadPayloadRate() {
		return mDownloadPayloadRate;
	}
	
	public String getSavePath() {
		return mSavePath;
	}
	
	public List<Double> getFileProgress() {
		return mFileProgress;
	}
	
	public int getNumSeeds() {
		return mNumSeeds;
	}
	
	public int getMaxUploadSlots() {
		return mMaxUploadSlots;
	}
	
	public String getTracker() {
		return mTracker;
	}
	
	public boolean isMoveOnCompleted() {
		return mMoveOnCompleted;
	}
	
	public boolean isStopAtRatio() {
		return mStopAtRatio;
	}
	
	public int getTotalPayloadUpload() {
		return mTotalPayloadUpload;
	}
	
	public boolean isRemoveAtRatio() {
		return mRemoveAtRatio;
	}
	
	public int getMaxConnections() {
		return mMaxConnections;
	}
	
	public int getTotalWanted() {
		return mTotalWanted;
	}
	
	public double getStopRatio() {
		return mStopRatio;
	}
	
	public boolean isAutoManaged() {
		return mIsAutoManaged;
	}
	
	public int getTotalPayloadDownload() {
		return mTotalPayloadDownload;
	}
	
	public long getTotalSize() {
		return mTotalSize;
	}
	
	public int getTotalSeeds() {
		return mTotalSeeds;
	}
	
	public String getMessage() {
		return mMessage;
	}
	
	public boolean isFinished() {
		return mIsFinished;
	}
	
	public long getTotalDone() {
		return mTotalDone;
	}
	
	public int getTotalPeers() {
		return mTotalPeers;
	}
	
	public long getTotalUploaded() {
		return mTotalUploaded;
	}
	
	public double getProgress() {
		return mProgress;
	}
	
	public String getComment() {
		return mComment;
	}
	
	public boolean isSeed() {
		return mIsSeed;
	}
	
	public int getQueue() {
		return mQueue;
	}
	
	public double getRatio() {
		return mRatio;
	}
	
	public double getDistributedCopies() {
		return mDistributedCopies;
	}
	
	@Override
	public String toString() {
		return "Torrent [mPeers=" + mPeers + ", mTrackers=" + mTrackers
				+ ", mMoveCompletedPath=" + mMoveCompletedPath + ", mPaused="
				+ mPaused + ", mCompact=" + mCompact + ", mUploadPayloadRate="
				+ mUploadPayloadRate + ", mFilePriorities=" + mFilePriorities
				+ ", mPrioritizeFirstLast=" + mPrioritizeFirstLast + ", mEta="
				+ mEta + ", mNumPeers=" + mNumPeers + ", mTrackerStatus="
				+ mTrackerStatus + ", mState=" + mState + ", mPieceLength="
				+ mPieceLength + ", mMoveOnCompletedPath="
				+ mMoveOnCompletedPath + ", mMoveCompleted=" + mMoveCompleted
				+ ", mSeedsPeersRatio=" + mSeedsPeersRatio
				+ ", mMaxUploadSpeed=" + mMaxUploadSpeed + ", mNumPieces="
				+ mNumPieces + ", mMaxDownloadSpeed=" + mMaxDownloadSpeed
				+ ", mActiveTime=" + mActiveTime + ", mName=" + mName
				+ ", mFiles=" + mFiles + ", mNumFiles=" + mNumFiles
				+ ", mTimeAdded=" + mTimeAdded + ", mHash=" + mHash
				+ ", mNextAnnounce=" + mNextAnnounce + ", mPrivate=" + mPrivate
				+ ", mSeedingTime=" + mSeedingTime + ", mSeedRank=" + mSeedRank
				+ ", mAllTimeDownload=" + mAllTimeDownload + ", mTrackerHost="
				+ mTrackerHost + ", mDownloadPayloadRate="
				+ mDownloadPayloadRate + ", mSavePath=" + mSavePath
				+ ", mFileProgress=" + mFileProgress + ", mNumSeeds="
				+ mNumSeeds + ", mMaxUploadSlots=" + mMaxUploadSlots
				+ ", mTracker=" + mTracker + ", mMoveOnCompleted="
				+ mMoveOnCompleted + ", mStopAtRatio=" + mStopAtRatio
				+ ", mTotalPayloadUpload=" + mTotalPayloadUpload
				+ ", mRemoveAtRatio=" + mRemoveAtRatio + ", mMaxConnections="
				+ mMaxConnections + ", mTotalWanted=" + mTotalWanted
				+ ", mStopRatio=" + mStopRatio + ", mIsAutoManaged="
				+ mIsAutoManaged + ", mTotalPayloadDownload="
				+ mTotalPayloadDownload + ", mTotalSize=" + mTotalSize
				+ ", mTotalSeeds=" + mTotalSeeds + ", mMessage=" + mMessage
				+ ", mIsFinished=" + mIsFinished + ", mTotalDone=" + mTotalDone
				+ ", mTotalPeers=" + mTotalPeers + ", mTotalUploaded="
				+ mTotalUploaded + ", mProgress=" + mProgress + ", mComment="
				+ mComment + ", mIsSeed=" + mIsSeed + ", mQueue=" + mQueue
				+ ", mRatio=" + mRatio + ", mDistributedCopies="
				+ mDistributedCopies + "]";
	}

	private final List<Peer> mPeers = new ArrayList<Peer>();
	private final List<Tracker> mTrackers = new ArrayList<Tracker>();
	private final String mMoveCompletedPath;
	private final boolean mPaused;
	private final boolean mCompact;
	private final int mUploadPayloadRate;
	private final List<Integer> mFilePriorities = new ArrayList<Integer>();
	private final boolean mPrioritizeFirstLast;
	private final int mEta;
	private final int mNumPeers;
	private final String mTrackerStatus;
	private final String mState;
	private final int mPieceLength;
	private final String mMoveOnCompletedPath;
	private final boolean mMoveCompleted;
	private final double mSeedsPeersRatio;
	private final double mMaxUploadSpeed;
	private final int mNumPieces;
	private final double mMaxDownloadSpeed;
	private final int mActiveTime;
	private final String mName;
	private final List<FileInTorrent> mFiles = new ArrayList<FileInTorrent>();
	private final int mNumFiles;
	private final double mTimeAdded;
	private final String mHash;
	private final int mNextAnnounce;
	private final boolean mPrivate;
	private final int mSeedingTime;
	private final int mSeedRank;
	private final int mAllTimeDownload;
	private final String mTrackerHost;
	private final int mDownloadPayloadRate;
	private final String mSavePath;
	private final List<Double> mFileProgress = new ArrayList<Double>();
	private final int mNumSeeds;
	private final int mMaxUploadSlots;
	private final String mTracker;
	private final boolean mMoveOnCompleted;
	private final boolean mStopAtRatio;
	private final int mTotalPayloadUpload;
	private final boolean mRemoveAtRatio;
	private final int mMaxConnections;
	private final int mTotalWanted;
	private final double mStopRatio;
	private final boolean mIsAutoManaged;
	private final int mTotalPayloadDownload;
	private final long mTotalSize;
	private final int mTotalSeeds;
	private final String mMessage;
	private final boolean mIsFinished;
	private final long mTotalDone;
	private final int mTotalPeers;
	private final long mTotalUploaded;
	private final double mProgress;
	private final String mComment;
	private final boolean mIsSeed;
	private final int mQueue;
	private final double mRatio;
	private final double mDistributedCopies;
	
	private static final String
	
		K_PEERS							= "peers",
		K_TRACKERS						= "trackers",
		K_MOVE_COMPLETED_PATH			= "move_completed_path",
		K_PAUSED						= "paused",
		K_COMPACT						= "compact",
		K_UPLOAD_PAYLOAD_RATE			= "upload_payload_rate",
		K_FILE_PRIORITIES				= "file_priorities",
		K_PRIORITIZE_FIRST_LAST			= "prioritize_first_last",
		K_ETA							= "eta",
		K_NUM_PEERS						= "num_peers",
		K_TRACKER_STATUS				= "tracker_status",
		K_STATE							= "state",
		K_PIECE_LENGTH					= "piece_length",
		K_MOVE_ON_COMPLETED_PATH		= "move_on_completed_path",
		K_MOVE_COMPLETED			 	= "move_completed",
		K_SEEDS_PEERS_RATIO				= "seeds_peers_ratio",
		K_MAX_UPLOAD_SPEED				= "max_upload_speed",
		K_NUM_PIECES					= "num_pieces",
		K_MAX_DOWNLOAD_SPEED			= "max_download_speed",
		K_ACTIVE_TIME					= "active_time",
		K_NAME							= "name",
		K_FILES							= "files",
		K_NUM_FILES						= "num_files",
		K_TIME_ADDED					= "time_added",
		K_HASH							= "hash",
		K_NEXT_ANNOUNCE					= "next_announce",
		K_PRIVATE						= "private",
		K_SEEDING_TIME					= "seeding_time",
		K_SEED_RANK						= "seed_rank",
		K_ALL_TIME_DOWNLOAD				= "all_time_download",
		K_TRACKER_HOST					= "tracker_host",
		K_DOWNLOAD_PAYLOAD_RATE			= "download_payload_rate",
		K_SAVE_PATH						= "save_path",
		K_FILE_PROGRESS					= "file_progress",
		K_NUM_SEEDS						= "num_seeds",
		K_MAX_UPLOAD_SLOTS				= "max_upload_slots",
		K_TRACKER						= "tracker",
		K_MOVE_ON_COMPLETED				= "move_on_completed",
		K_STOP_AT_RATIO					= "stop_at_ratio",
		K_TOTAL_PAYLOAD_UPLOAD			= "total_payload_upload",
		K_REMOVE_AT_RATIO				= "remove_at_ratio",
		K_MAX_CONNECTIONS				= "max_connections",
		K_TOTAL_WANTED					= "total_wanted",
		K_STOP_RATIO					= "stop_ratio",
		K_IS_AUTO_MANAGED				= "is_auto_managed",
		K_TOTAL_PAYLOAD_DOWNLOAD		= "total_payload_download",
		K_TOTAL_SIZE					= "total_size",
		K_TOTAL_SEEDS 					= "total_seeds",
		K_MESSAGE						= "message",
		K_IS_FINISHED					= "is_finished",
		K_TOTAL_DONE					= "total_done",
		K_TOTAL_PEERS					= "total_peers",
		K_TOTAL_UPLOADED				= "total_uploaded",
		K_RPOGRESS						= "progress",
		K_COMMENT						= "comment",
		K_IS_SEED						= "is_seed",
		K_QUEUE							= "queue",
		K_RATIO							= "ratio",
		K_DISTRIBUTED_COPIES			= "distributed_copies";
	
	public static final String
	
	    QUEUED						= "Queued",
	    CHECKING					= "Checking",
	    DOWNLOADING_METADATA		= "Downloading Metadata",
	    DOWNLOADING					= "Downloading",
	    FINISHED					= "Finished",
	    SEEDING						= "Seeding",
	    ALOCATING					= "Allocating",
	    CHEKING_RESUME_DATA			= "Checking Resume Data";

		
	private static final long serialVersionUID = 1;	
}