package com.example.cameraapp.data;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cameraapp.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
// User authentication
public class LoginDataSource {
    static String m_cookie = "";
    public Result<LoggedInUser> login(String username, String password, Context context) {

        try {
            String url = "http://192.168.1.13:8080/api/auth/signIn";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println("RESPONSE: " + response);
                                JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                                String site = jsonResponse.getString("site"),
                                        network = jsonResponse.getString("network");
                                System.out.println("Site: "+site+"\nNetwork: "+network);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<>();
                    // the POST parameters:
                    params.put("username", username);
                    params.put("password", password);
                    System.out.println(params);
                    return params;
                }

                private static final String SET_COOKIE_KEY = "Set-Cookie";
                private static final String COOKIE_KEY = "Cookie";

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    System.out.println("HEYEYEYEYEYEYE");
                    String parsed;
                    try {

                        Map<String, String> headers=response.headers;

                        if(headers!=null&&headers.containsKey(SET_COOKIE_KEY)){
                            String cookie=headers.get(SET_COOKIE_KEY);

                            if(!TextUtils.isEmpty(cookie)){
                                // TODO: Save the cookie locally, such as Sharepreference
                                System.out.println("headers:"+cookie);
                                m_cookie = cookie;
                                System.out.println("m_cookie: " + m_cookie);
                            }
                        }

                        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        parsed = new String(response.data);
                    }
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String credentials = "token";
                    //String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    String auth = "" + credentials;//Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept","application/json");
                    System.out.println(auth);
//                    if(!LoginActivity.getCookie(context).equals("")){
//                        String cookie = LoginActivity.getCookie(context);
//                        System.out.println("Cookie to load from preferences: " + cookie);
//                        headers.put("Cookie", cookie);
//                    }

                    //headers.put("Cookie", UserCredentialsPersistence.restoreCookie(context).toString());
                    headers.put("Cookie", auth);
                    System.out.println(headers);
                    return headers;
                }
            };

            Volley.newRequestQueue(context).add(postRequest);


            // Connect to server
//            URL url = new URL("http://localhost:8080/api/auth/signIn");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//            conn.setRequestProperty("Accept","application/json");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//
//            JSONObject jsonParam = new JSONObject();
//            jsonParam.put("username", username);
//            jsonParam.put("password", password);
////            jsonParam.put("timestamp", 1488873360);
////            jsonParam.put("uname", message.getUser());
////            jsonParam.put("message", message.getMessage());
////            jsonParam.put("latitude", 0D);
////            jsonParam.put("longitude", 0D);
//
//            System.out.println("" + conn.getResponseCode());
////            Log.i("JSON", jsonParam.toString());
//////            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//////            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
//////            os.writeBytes(jsonParam.toString());
//////
//////            os.flush();
//////            os.close();
////
////            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
////            Log.i("MSG" , conn.getResponseMessage());
//
//            conn.disconnect();

            LoggedInUser fakeUser = new LoggedInUser(username, username);


            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<LoggedInUser> register(String username, String password, Context context) {

        try {
            String url = "http://192.168.1.13:8080/api/auth/signIn";

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println("RESPONSE: " + response);
                                JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                                String site = jsonResponse.getString("site"),
                                        network = jsonResponse.getString("network");
                                System.out.println("Site: "+site+"\nNetwork: "+network);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<>();
                    // the POST parameters:
                    params.put("username", username);
                    params.put("password", password);
                    System.out.println(params);
                    return params;
                }

                private static final String SET_COOKIE_KEY = "Set-Cookie";
                private static final String COOKIE_KEY = "Cookie";

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    //return super.parseNetworkResponse(response);
                    System.out.println("HEYEYEYEYEYEYE");
                    String parsed;
                    try {

                        Map<String, String> headers=response.headers;

                        if(headers!=null&&headers.containsKey(SET_COOKIE_KEY)){
                            String cookie=headers.get(SET_COOKIE_KEY);

                            if(!TextUtils.isEmpty(cookie)){
                                // TODO: Save the cookie locally, such as Sharepreference
                                System.out.println("headers:"+cookie);
                                m_cookie = cookie;
                                System.out.println("m_cookie: " + m_cookie);
                            }
                        }

                        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        parsed = new String(response.data);
                    }
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));

                }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String credentials = "token";
                    //String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    String auth = "" + credentials;//Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept","application/json");
                    System.out.println(auth);
//                    if(!LoginActivity.getCookie(context).equals("")){
//                        String cookie = LoginActivity.getCookie(context);
//                        System.out.println("Cookie to load from preferences: " + cookie);
//                        headers.put("Cookie", cookie);
//                    }

                    //headers.put("Cookie", UserCredentialsPersistence.restoreCookie(context).toString());
                    headers.put("Cookie", auth);
                    System.out.println(headers);
                    return headers;
                }


            };
            Volley.newRequestQueue(context).add(postRequest);


            // Connect to server
//            URL url = new URL("http://localhost:8080/api/auth/signIn");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//            conn.setRequestProperty("Accept","application/json");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//
//            JSONObject jsonParam = new JSONObject();
//            jsonParam.put("username", username);
//            jsonParam.put("password", password);
////            jsonParam.put("timestamp", 1488873360);
////            jsonParam.put("uname", message.getUser());
////            jsonParam.put("message", message.getMessage());
////            jsonParam.put("latitude", 0D);
////            jsonParam.put("longitude", 0D);
//
//            System.out.println("" + conn.getResponseCode());
////            Log.i("JSON", jsonParam.toString());
//////            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//////            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
//////            os.writeBytes(jsonParam.toString());
//////
//////            os.flush();
//////            os.close();
////
////            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
////            Log.i("MSG" , conn.getResponseMessage());
//
//            conn.disconnect();

            LoggedInUser fakeUser = new LoggedInUser(username, username);


            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}