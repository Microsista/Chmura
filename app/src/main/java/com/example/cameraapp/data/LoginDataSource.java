package com.example.cameraapp.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cameraapp.Api;
import com.example.cameraapp.Api2;
import com.example.cameraapp.LocalDateAdapter;
//import com.example.cameraapp.LocalDateSerializer;
import com.example.cameraapp.R;
import com.example.cameraapp.data.model.LoggedInUser;
import com.example.cameraapp.ui.login.Example;
import com.example.cameraapp.ui.login.LoginFragment;
import com.example.cameraapp.ui.login.VolleyCallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.volley.VolleyLog.TAG;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
// User authentication
public class LoginDataSource {
    String token;
//    static String m_cookie = "";
    public Result<LoggedInUser> login(String username, String password, Context context, final VolleyCallBack callBack) {

        try {
            String url = "https://192.168.1.13:8443/api/auth/signIn";

            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            System.out.println("COKOLWIEK");

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    try {
                        Log.d(TAG, response.getString("token"));
                        token = response.getString("token");
                        System.out.println("inside: " + token);

                        callBack.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("PLACKI");
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    System.out.println("NIEDOBRE PLACKI");
                    callBack.onFailure();
                }
            }) {
                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
            postRequest.setTag(TAG);

            Volley.newRequestQueue(context).add(postRequest);

            System.out.println("just before: " + username);

            LoggedInUser fakeUser = new LoggedInUser(username, username, token);


            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    OkHttpClient.Builder getUnsafeOkHttpClient()
    {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();



    public Result<LoggedInUser> register(String username, String password, String email, String dob, Context context, LocalDate date, VolleyCallBack volleyCallBack, FragmentManager manager) {

//        try {
//            // create retrofit instance
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("https://192.168.1.13:8443/api/auth/")
//                    .client(getUnsafeOkHttpClient().build())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            // create api instance
//            Api2 api = retrofit.create(Api2.class);
//
//            // request body
//            Example example = new Example();
//            example.setEmail(email);
//            example.setUsername(username);
//            example.setPassword(password);
//            example.setDob(date);
//
//            //GsonBuilder gsonBuilder = new GsonBuilder();
//            //gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
//
//            //Gson gson = gsonBuilder.setPrettyPrinting().create();
//
//            // Convert to JSON
//            System.out.println(gson.toJson(example));
//
//
//            // create call object
//            Call<ResponseBody> uploadFileCall = api.register(
//                    gson.toJson(example).replace("/\n/g", "\\\\n").replace("/\r/g", "\\\\r").replace("/\t/g", "\\\\t")
//            );
//
//            // async call
//            uploadFileCall.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                    if (response.isSuccessful()) {
//                        System.out.println("TAKK");
//                        System.out.println(call);
//                        System.out.println(response);
//                        Toast.makeText(context, "zarejestrowany", Toast.LENGTH_LONG).show();
//                        manager.beginTransaction().replace(R.id.flFragment, new LoginFragment()).commit();
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    // TODO
//                    System.out.println("ERROR?");
//                    System.out.println(call);
//                    System.out.println(t);
//                    Toast.makeText(context, "incorrect credentials", Toast.LENGTH_LONG).show();
//
//                }
//            });
//
//            LoggedInUser fakeUser = new LoggedInUser(username, password, "");
//
//
//            return new Result.Success<>(fakeUser);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result.Error(new IOException("Error registering in", e));
//        }


        try {
            String url = "https://192.168.1.13:8443/api/auth/signUp";

            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            params.put("email", email);
            params.put("dob", date.toString());
            System.out.println("COKOLWIEK");

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                    url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    try {
                        Log.d(TAG, response.getString("token"));
                        token = response.getString("token");
                        System.out.println("inside: " + token);

                        //callBack.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("PLACKI");
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    System.out.println("NIEDOBRE PLACKI");
                    //callBack.onFailure();
                }
            }) {
                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
            postRequest.setTag(TAG);

            Volley.newRequestQueue(context).add(postRequest);

            System.out.println("just before: " + username);

            LoggedInUser fakeUser = new LoggedInUser(username, username, token);


            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}