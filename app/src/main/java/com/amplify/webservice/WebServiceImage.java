package com.amplify.webservice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class WebServiceImage {

	public static final String baseUrl = "";
	private String method;
	private MultipartEntity param;
	private int requestcode;
	CallbackImage resultCallback;
	Context context;

	public void getService(Context context, String url, MultipartEntity params, int requestCode) {

		method = url;
		this.param = params;
		this.requestcode = requestCode;
		this.context =  context;
		BackgroundTask background = new BackgroundTask(context);
		background.execute();
	}

	public void getService(Context context, String url, MultipartEntity params) {


		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		try {
			params.writeTo(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String content = bytes.toString();
		System.out.println("Url in get Service ---"+url+"\n params==="+content);
		this.context =  context;
		method = url;
		this.param = params;
		this.requestcode = 0;
		BackgroundTask background = new BackgroundTask(context);
		background.execute();
	}

	public WebServiceImage(CallbackImage callback0) {
		super();
		this.resultCallback = callback0;
	}

	private String parsehtml(String json_) {

		try {
			if (json_.contains("<pre") || json_.contains("<pre>")
					|| json_.contains("</pre>")) {
				json_ = json_.substring(json_.indexOf("<pre>"),
						json_.indexOf("</pre>") + 6);
				json_ = json_.replace("</div>", "");
				json_ = json_.replace("</pre>", "");
			}
		} catch (StringIndexOutOfBoundsException e) {
			// TODO: handle exception
			resultCallback.onErrorImage(requestcode, "Unexpected error. Please contact app administrator.");
		}
		return json_;
	}

	public void retry() {
		if (TextUtils.isEmpty(method)) {
			resultCallback.onErrorImage(requestcode, "Method empty");
		} else {
			BackgroundTask background = new BackgroundTask(context);
			background.execute();
		}
	}

	class BackgroundTask extends AsyncTask<String, String, String> {

		private ProgressDialog process;
		Context context;
		Dialog dialog;

		public BackgroundTask(Context context) {
			super();
			this.context = context;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if(process == null)
				process = new ProgressDialog(context);


			process.setMessage("Please wait...");

			process.setCancelable(false);
			process.show();

			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			try {

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(method);
				httpPost.setEntity(param);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream is_ = httpEntity.getContent();
				if (httpResponse != null) {

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is_, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is_.close();
					System.out.println("Result==========="+sb.toString());
					return sb.toString();
				}
			} catch(ConnectTimeoutException c){

				process.dismiss();
				resultCallback.onErrorImage(requestcode, "Network connection error, Please try again");
			}catch(UnknownHostException u){

				process.dismiss();
				resultCallback.onErrorImage(requestcode, "Network connection error, Please try again");
			}catch (Exception e) {
				e.printStackTrace();

				process.dismiss();
				resultCallback.onErrorImage(requestcode, "Network connection error, Please try again");
			}
			return null;
		}

		@Override
		protected void onPostExecute(String resultstr) {
			// TODO Auto-generated method stub
			process.dismiss();
			if (resultstr == null) {
				resultCallback.onErrorImage(requestcode, "Network connection error, Please try again");
				return;
			}else{
				try {
					JSONObject JSONObject = new JSONObject(parsehtml(resultstr));
					resultCallback.onSuccessImage(requestcode, JSONObject);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					resultCallback.onErrorImage(requestcode, "JSON Parsing Error");
					e.printStackTrace();
				}
			}
			super.onPostExecute(resultstr);
		}
	}

	public static interface CallbackImage {

		public void onSuccessImage(int reqestcode, JSONObject rootjson);

		public void onErrorImage(int reqestcode, String error);

	}

}
