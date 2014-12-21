package info.itline.delugemanager.net;

public final class Methods {

	private Methods() {
	}
	
	public static final String
	
		LOGIN					= "daemon.login",
		GET_TORRENT_LIST		= "core.get_torrents_status",
		GET_TORRENT_STATUS		= "core.get_torrent_status",
		PAUSE_TORRENT			= "core.pause_torrent",
		RESUME_TORRENT			= "core.resume_torrent",
		QUEUE_UP				= "core.queue_up",
		QUEUE_DOWN				= "core.queue_down",
		REMOVE_TORRENT			= "core.remove_torrent",
		GET_SESSION_STATUS 		= "core.get_session_status",
		SET_FILE_PRIORITIES		= "core.set_torrent_file_priorities";
	
	public static final String
	
		PAYLOAD_UPLOAD_RATE				= "payload_upload_rate",
		PAYLOAD_DOWNLOAD_RATE			= "payload_download_rate",
		TOTAL_PAYLOAD_DOWNLOAD			= "total_payload_download",
		TOTAL_PAYLOAD_UPLOAD			= "total_payload_upload";
}
