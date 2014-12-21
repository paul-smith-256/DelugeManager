package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import info.itline.delugemanager.net.DelugeDaemonInfo;
import info.itline.delugemanager.net.ServiceWrapper;
import info.itline.delugemanager.net.ServiceWrapper.ServiceStateListener;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class LoginFragment extends Fragment implements ServiceStateListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mServiceWrapper = new ServiceWrapper(getActivity(), this);
		mServiceWrapper.bind();
		if (haveConnectionInfo()) {
			mPrefDaemonInfo = makeDaemonInfoFromPrefs();
		}
	}
	
	@Override
	public void onDestroy() {
		mServiceWrapper.unbind();
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mInBackgroud = false;
	}
	
	@Override
	public void onPause() {
		mInBackgroud = true;
		super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.fragment_login, container, false);
		
		mViewSwitcher = (ViewSwitcher) result.findViewById(R.id.viewSwitcher);
		mHostname = (EditText) result.findViewById(R.id.hostname);
		mPort = (EditText) result.findViewById(R.id.port);
		mLogin = (EditText) result.findViewById(R.id.login);
		mPassword = (EditText) result.findViewById(R.id.password);
		mGo = (Button) result.findViewById(R.id.go);
		
		if (mIsFirstViewCreation) {
			mIsFirstViewCreation = false;
			if (mPrefDaemonInfo != null) {
				fillInputs(mPrefDaemonInfo);
			}
		}
		mPassword.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					initiateConnectionAndStartNextActivity(makeDaemonInfoFromInput());
					return true;
				}
				else {
					return false;
				}
			}
		});
		mGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initiateConnectionAndStartNextActivity(makeDaemonInfoFromInput());
			}
		});
		if (mConnectionInitiated || !mServiceWrapper.isBound()) {
			showWaitView();
		}
		return result;
	}
	
	@Override
	public void onBound() {
		if (mPrefDaemonInfo != null) {
			if (mNextActivityStarted) {
				initiateConnection(mPrefDaemonInfo);
			}
			else {
				initiateConnectionAndStartNextActivity(mPrefDaemonInfo);
			}
		}
		else {
			showInputView();
		}
	}
	
	@Override
	public void onConnected() {
		showInputView();
		if (!mNextActivityStarted) {
			mServiceWrapper.keepConnection();
			saveDaemonInfo(makeDaemonInfoFromInput());
			startActivity(new Intent(getActivity(), TorrentListActivity.class));
			// Toast.makeText(getActivity(), "Starting activity", Toast.LENGTH_SHORT).show();
			mNextActivityStarted = true;
		}
		mConnectionInitiated = false;
	}
	
	private void saveDaemonInfo(DelugeDaemonInfo info) {
		SharedPreferences pref = getActivity()
				.getPreferences(Context.MODE_PRIVATE);
		pref.edit()
				.putBoolean(PREF_HAVE_CONNECTION_PARAMETERS, true)
				.putString(PREF_HOSTNAME, info.getHost())
				.putInt(PREF_PORT, info.getPort())
				.putString(PREF_LOGIN, info.getLogin())
				.putString(PREF_PASSWORD, info.getPassword())
				.commit();
		mPrefDaemonInfo = info;
	}

	@Override
	public void onEvent(String type, List<Object> data) {
		
	}

	@Override
	public void onConnectionFailed(int reason, String description) {
		mConnectionInitiated = false;
		showInputView();
		if (!mInBackgroud) {
			Toast.makeText(getActivity(), description, 
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDisconnected() {
		// showWaitView();
	}

	@Override
	public void onTerminated() {
		showWaitView();
	}
	
	private DelugeDaemonInfo makeDaemonInfoFromInput() {
		String portString = mPort.getText().toString();
		int port = portString.length() != 0 ? Integer.parseInt(portString) : DEFAULT_PORT;
		return new DelugeDaemonInfo(
				mHostname.getText().toString(), 
				port,
				mLogin.getText().toString(), 
				mPassword.getText().toString());
	}
	
	private void fillInputs(DelugeDaemonInfo info) {
		mHostname.setText(info.getHost());
		mPort.setText(Integer.toString(info.getPort()));
		mLogin.setText(info.getLogin());
		mPassword.setText(info.getPassword());
	}
	
	private DelugeDaemonInfo makeDaemonInfoFromPrefs() {
		SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		return new DelugeDaemonInfo(
				prefs.getString(PREF_HOSTNAME, ""), 
				prefs.getInt(PREF_PORT, DEFAULT_PORT),
				prefs.getString(PREF_LOGIN, ""),
				prefs.getString(PREF_PASSWORD, ""));
	}
	
	private boolean haveConnectionInfo() {
		SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
		return prefs.getBoolean(PREF_HAVE_CONNECTION_PARAMETERS, false);
	}
	
	private void showInputView() {
		if (mViewSwitcher != null) {
			mViewSwitcher.setDisplayedChild(INPUT_VIEW);
		}
	}
	
	private void showWaitView() {
		if (mViewSwitcher != null) {
			mViewSwitcher.setDisplayedChild(WAIT_VIEW);
		}
	}
	
	private void initiateConnection(DelugeDaemonInfo info) {
		mConnectionInitiated = true;
		showWaitView();
		mServiceWrapper.connect(info);
	}
	
	private void initiateConnectionAndStartNextActivity(DelugeDaemonInfo info) {
		mNextActivityStarted = false;
		initiateConnection(info);
	}
	
	private ServiceWrapper mServiceWrapper;
	private ViewSwitcher mViewSwitcher;
	private EditText mHostname, mPort, mLogin, mPassword;
	private Button mGo;
	private DelugeDaemonInfo mPrefDaemonInfo;
	private boolean mIsFirstViewCreation = true;
	private boolean mConnectionInitiated;
	private boolean mInBackgroud = true;
	private boolean mNextActivityStarted;
	
	private static final int
	
		INPUT_VIEW = 0,
		WAIT_VIEW = 1;
	
	private static final int DEFAULT_PORT = 58846;
	
	private static final String 
	
		PREF_HAVE_CONNECTION_PARAMETERS 	= "hasConnectionParamenters",
		PREF_HOSTNAME						= "host",
		PREF_PORT							= "port",
		PREF_LOGIN							= "login",
		PREF_PASSWORD						= "password";
}
