package com.amplify.webservice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.model.AddSocialAccount;
import com.amplify.model.DeleteAccount;
import com.amplify.model.EditProfileResponse;
import com.amplify.model.FilterResponse;
import com.amplify.model.GetAllUsers.GetAllUsers;
import com.amplify.model.GetLikedMe.GetLikedMeResponse;
import com.amplify.model.InviteFriend;
import com.amplify.model.LocationResponse;
import com.amplify.model.LogOutResponse;
import com.amplify.model.LoginResponse;
import com.amplify.model.MyProfileRes.MyProfleDetailsResponse;
import com.amplify.model.SearchTag;
import com.amplify.model.TagResponse;
import com.amplify.model.UserFlag;
import com.amplify.model.UserLike;
import com.amplify.utils.Constant;
import com.amplify.utils.SnackBarUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by bitware on 28/2/18.
 */

public class APIRequestWithDefaultloader extends AppCompatActivity {

    private JSONObject mJsonObject;
    private String mUrl;
    private ResponseHandler responseHandler;
    private int API_NAME;
    private Context mContext;
    ProgressDialog progressDialog;
    BaseResponse baseResponse;

    public APIRequestWithDefaultloader(Context context, JSONObject jsonObject, String url, APIRequestWithDefaultloader.ResponseHandler responseHandler, int api, String methodName) {
        this.responseHandler = responseHandler;
        this.API_NAME = api;
        this.mUrl = url;
        this.mJsonObject = jsonObject;
        this.mContext = context;

        System.out.println("api NO >>> " + api);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (methodName.equals(Constant.GET)) {
            System.out.println("***method*****" + methodName);
            apiGetRequest();
        } else if (methodName.equals(Constant.POST)) {
            System.out.println("***method*****" + methodName);
            apiPostRequest();
        } else {
            apiDeleteRequest();
        }
    }

    private void apiPatchRequest() {
        System.out.println("***apiPatchRequest*****" + mUrl);
        String url = mUrl + "?" + "user_id=1" + "&access_token=t0hFa5FmllrmH2zmSybr" + "&device_id=dsdassad4234"
                + "&device_type=android" + "&instagram=gdg" + "&twitter=wsq" + "&snapchat=dsf" + "&registration_ip=11:22:33:444"
                + "&kik=crt";

        System.out.println("************" + url);
        String REQUEST_TAG = String.valueOf(API_NAME);

        StringRequest deleteRequest = new StringRequest(Request.Method.PATCH, mUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        AppSingleton.getInstance(mContext).addToRequestQueue(deleteRequest, REQUEST_TAG);
    }

    private void apiDeleteRequest() {
        System.out.println("*******patch*******");
        String REQUEST_TAG = String.valueOf(API_NAME);
        StringRequest stringRequest = null;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        try {
            String URL = "http://...";
            /*JSONObject jsonBody = new JSONObject();
            jsonBody.put("Title", "Android Volley Demo");
            jsonBody.put("Author", "BNK");
            final String requestBody = jsonBody.toString();*/
            final String requestBody = mJsonObject.toString();
            System.out.println("**************"+mUrl);

            stringRequest = new StringRequest(Request.Method.PATCH, mUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY**********res", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY error*******", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        System.out.println("**************"+responseString);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
        } catch (Exception e) {

        }
        requestQueue.add(stringRequest);
    }

    private void apiPostRequest() {
        System.out.println("***apiPostRequest*****");
        String REQUEST_TAG = String.valueOf(API_NAME);
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(mUrl, mJsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(" >>> API RESPONSE " + response);
                        setResponseToBody(response);
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("**********error********" + error.toString());
               // Toast.makeText(mContext,getResources().getString(R.string.network_error),Toast.LENGTH_SHORT).show();
            //    SnackBarUtils.showSnackBarPink(mContext,findViewById(android.R.id.content),getResources().getString(R.string.server_error));
                progressDialog.dismiss();
            }
        });

        jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(mContext).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

    private void apiGetRequest() {
        System.out.println("********get******");
        String REQUEST_TAG = String.valueOf(API_NAME);
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(mUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response is " + response);
                        setResponseToBody(response);
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("************"+error.toString());
               // Toast.makeText(mContext,getResources().getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                //SnackBarUtils.showSnackBarPink(mContext,findViewById(android.R.id.content),getResources().getString(R.string.server_error));

                progressDialog.dismiss();
            }
        });

        jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(mContext).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

    private void setResponseToBody(JSONObject response) {
        Gson gson = new Gson();
        System.out.println("*****API NAme******" + API_NAME);
        switch (API_NAME) {
            case Constant.API_CREATE_ACCOUNT:
                baseResponse = gson.fromJson(response.toString(), LoginResponse.class);
                break;
            case Constant.API_ADD_SOCIAL_ACCT:
                baseResponse = gson.fromJson(response.toString(), AddSocialAccount.class);
                break;
            case Constant.API_DELETE_ACCOUNT:
                baseResponse = gson.fromJson(response.toString(),DeleteAccount.class);
                break;

            case Constant.API_FILTER:
                baseResponse = gson.fromJson(response.toString(),FilterResponse.class);
                break;
            case Constant.API_LOGOUT:
                baseResponse = gson.fromJson(response.toString(), LogOutResponse.class);
                break;
            case Constant.API_EDIT_PROFILE:
                baseResponse = gson.fromJson(response.toString(), EditProfileResponse.class);
                break;

            case Constant.API_INVITE_FRIEND:
                baseResponse = gson.fromJson(response.toString(), InviteFriend.class);
                break;

            case Constant.API_GET_PROFILE:
                baseResponse = gson.fromJson(response.toString(), MyProfleDetailsResponse.class);
                break;

            case Constant.API_SEARCH_TAG:
                baseResponse = gson.fromJson(response.toString(),SearchTag.class);
                break;
            case Constant.API_ADD_TAG:
                baseResponse = gson.fromJson(response.toString(), TagResponse.class);
                break;

            case Constant.API_LOCATION:
                baseResponse = gson.fromJson(response.toString(), LocationResponse.class);
                break;
        }
        baseResponse.setApiName(API_NAME);
        responseHandler.onSuccess(baseResponse);
    }

    public interface ResponseHandler {
        public void onSuccess(BaseResponse response);
        public void onFailure(BaseResponse response);

    }

}

