package com.amplify.utils;



import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkStatus {

	public static String checkConnection(Context context){

		ConnectivityManager connectivitymanager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if(connectivitymanager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()==NetworkInfo.State.CONNECTED || connectivitymanager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()==NetworkInfo.State.CONNECTED){

			return "true";

		}else{
			return "false";
		}
	}

	public static boolean isConnectingToInternet(Context _context)
	{
		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
		}
		return false;
	}


}



