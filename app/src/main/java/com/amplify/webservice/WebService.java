package com.amplify.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

public class WebService {
    public static final String baseUrl = "";

    private String method;
    private List<NameValuePair> param;
    private int requestcode;
    Callback resultCallback;
    Context context;

    public void getService(Context context, String url, List<NameValuePair> params, int requestCode) {

        method = url;
        this.param = params;
        this.requestcode = requestCode;
        this.context = context;
        BackgroundTask background = new BackgroundTask(context);
        background.execute();
    }

    public void getService(Context context, String url, List<NameValuePair> params) {

        this.context = context;
        System.out.println("Url in get Service ---" + url + "\n params===" + params);
        method = url;
        this.param = params;
        this.requestcode = 0;
        BackgroundTask background = new BackgroundTask(context);
        background.execute();

    }

    public WebService(Callback result) {
        super();
        this.resultCallback = result;
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
            resultCallback.onError(requestcode, "Unexpected error. Please contact app administrator.");
        }
        return json_;
    }

    public void retry() {
        if (TextUtils.isEmpty(method)) {
            resultCallback.onError(requestcode, "Method empty");
        } else {
            BackgroundTask background = new BackgroundTask(context);
            background.execute();
        }
    }

    class BackgroundTask extends AsyncTask<String, String, String> {

        Context context;

        public BackgroundTask(Context context) {
            super();
            this.context = context;
        }

        private ProgressDialog process;
        //Dialog dialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            try {
                if (process == null) {
                    process = new ProgressDialog(context);
                    process.setMessage("Please wait...");
                    process.setCancelable(false);
                    process.show();
                }
                super.onPreExecute();
            } catch (Exception e) {

            }
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(method);
                httpPost.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is_ = httpEntity.getContent();

                if (httpResponse != null) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is_, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is_.close();

                    System.out.println("+++++++++SB+++++++++" + sb.toString());
                    return sb.toString();
                }
            } catch (ConnectTimeoutException c) {

                process.dismiss();
                resultCallback.onError(requestcode, "Network connection error, Please try again");
            } catch (UnknownHostException u) {

                process.dismiss();
                resultCallback.onError(requestcode, "Network connection error, Please try again");
            } catch (Exception e) {
                e.printStackTrace();
                process.dismiss();
                resultCallback.onError(requestcode, "Network connection error, Please try again");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String resultstr) {
            // TODO Auto-generated method stub
            System.out.println("TAXIAPP ON POST EXECUTE RESULT"+resultstr);

            try {

                if (process != null && process.isShowing()) {
                    process.dismiss();
                    process = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (resultstr == null) {
                resultCallback.onError(requestcode, "Network connection error, Please try again");

            } else {
                try {

                    JSONObject JSONObject = new JSONObject(resultstr);
                    resultCallback.onSuccess(requestcode, JSONObject);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    resultCallback.onError(requestcode, "ERROR");
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultCallback.onError(requestcode, "Unexpected error. Please contact app administrator.");
                    e.printStackTrace();
                }
            }
            super.onPostExecute(resultstr);
        }

    }

    public static interface Callback {

        public void onSuccess(int reqestcode, JSONObject rootjson);

        public void onError(int reqestcode, String error);

    }

}
