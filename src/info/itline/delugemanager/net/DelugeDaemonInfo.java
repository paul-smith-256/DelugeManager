package info.itline.delugemanager.net;

public class DelugeDaemonInfo {
	
	public DelugeDaemonInfo(String host, int port, String login, String password) {
		super();
		mHost = host;
		mPort = port;
		mLogin = login;
		mPassword = password;
	}

	public String getHost() {
		return mHost;
	}
	
	public int getPort() {
		return mPort;
	}
	
	public String getLogin() {
		return mLogin;
	}
	
	public String getPassword() {
		return mPassword;
	}
	
	private String mHost;
	private int mPort;
	private String mLogin;
	private String mPassword;
}
