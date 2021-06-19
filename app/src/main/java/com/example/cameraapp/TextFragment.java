package com.example.cameraapp;

import android.Manifest;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cameraapp.ui.login.LoginViewModel;
import com.example.cameraapp.ui.login.LoginViewModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.cert.CertificateException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

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
//import retrofit2.Callback;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.MediaType.*;
import okhttp3.RequestBody.*;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.android.volley.VolleyLog.TAG;
import static com.example.cameraapp.App.CHANNEL_1_ID;

public class TextFragment extends Fragment {
    View m_view;

    private NotificationManagerCompat notificationManager;

    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = NotificationManagerCompat.from(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text, container, false);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    String fileContent;
    double latitude;
    double longitude;
    Uri filePath;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    };

    private LocationManager mLocationManager;

    private LoginViewModel loginViewModel;

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

    void writeStreamToFile(InputStream input, File file) {
        try {
            try (OutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_view = view;

        final Button sendTextButton = view.findViewById(R.id.bSendText);
        final TextView loginInfo = view.findViewById(R.id.textView2);
        final Button images2Button = view.findViewById(R.id.myButton);
        final TextView textView = view.findViewById(R.id.textView);
        final EditText fileNameET = view.findViewById(R.id.fileName);

        Bundle bundle = this.getArguments();
        String username = bundle.getString("username");
        String password = bundle.getString("password");

        loginViewModel = new ViewModelProvider(requireActivity(), new LoginViewModelFactory())
                .get(LoginViewModel.class);

        loginInfo.setText(getString(R.string.login_name) + " " + loginViewModel.getDisplayName());

        images2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("username", loginViewModel.getDisplayName());
                bundle.putString("password", password);
                ImageFragment fragment = new ImageFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
            }
        });
        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
                String url = "https://192.168.1.13:8443/api/fileDrop";

                ///////////////////////////////////
                // Get token
                String tokenUrl = "https://192.168.1.13:8443/api/auth/signIn";
                Map<String, String> tokenParams = new HashMap<>();
                System.out.println("USERNAME: "+username+" PASSWORD: "+password);
                tokenParams.put("username", username);
                tokenParams.put("password", password);
                System.out.println("COKOLWIEK TOKENOWE");

                JsonObjectRequest tokenRequest = new JsonObjectRequest(Request.Method.POST,
                        tokenUrl, new JSONObject(tokenParams), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject outresponse) {
                        System.out.println("|"+ textView.getText().toString() + "|");
                        if(!textView.getText().toString().equals("No text") && !textView.getText().toString().equals("Brak tekstu")) {
                            String text = textView.getText().toString();
                            System.out.println(text);

                            System.out.println("MY PATH: " + filePath);


                            InputStream stream = null;
                            try {
                                stream = getContext().getContentResolver().openInputStream(filePath);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            File file = null;
                            try {
                                if (fileNameET.getText().toString().equals("")) {
                                    int leftLimit = 97; // letter 'a'
                                    int rightLimit = 122; // letter 'z'
                                    int targetStringLength = 10;
                                    Random random = new Random();
                                    StringBuilder buffer = new StringBuilder(targetStringLength);
                                    for (int i = 0; i < targetStringLength; i++) {
                                        int randomLimitedInt = leftLimit + (int)
                                                (random.nextFloat() * (rightLimit - leftLimit + 1));
                                        buffer.append((char) randomLimitedInt);
                                    }
                                    String generatedString = buffer.toString();
                                    System.out.println(generatedString);
                                    fileNameET.setText(generatedString);
                                }


                                String fp = getContext().getFilesDir().getPath().toString() + "/" + fileNameET.getText().toString() + ".txt";

                                file = new File(fp);
                                try (OutputStream output = new FileOutputStream(file)) {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = stream.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }

                                    output.flush();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } finally {
                                try {
                                    stream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (file.exists())
                                System.out.println("file: " + file);
                            else
                                System.out.println("FILE DOES NOT EXIST");


                            // create retrofit instance
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://192.168.1.13:8443/api/")
                                    .client(getUnsafeOkHttpClient().build())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            // create api instance
                            Api api = retrofit.create(Api.class);

                            String bearerToken = null;
                            try {
                                bearerToken = "Bearer " + outresponse.getString("token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // create call object
                            Call<ResponseBody> uploadFileCall = api.uploadFile(bearerToken,
                                    MultipartBody.Part.createFormData(
                                            "files",
                                            file.getName(),
                                            RequestBody.create(MediaType.parse(getContext().getContentResolver().getType(filePath)), file)),
                                    MultipartBody.Part.createFormData("dir", "")
                            );
                            // async call
                            uploadFileCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                    System.out.println(response.code());
                                    System.out.println(response.toString());
                                    System.out.println(response.body());
                                    System.out.println(response.errorBody());
                                    System.out.println(response.message());
                                    System.out.println(response.raw());
                                    System.out.println(response.headers());
                                    if (response.isSuccessful()) {
                                        System.out.println("TAKK");
                                        System.out.println(call);
                                        System.out.println(response);
                                        loading.dismiss();
                                        sendOnChannel1();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    // TODO
                                    System.out.println("ERROR?");
                                    System.out.println(call);
                                    System.out.println(t);
                                    loading.dismiss();
                                    sendErrorOnChannel1();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "There is no file to send!", Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        System.out.println("NIEDOBRE PLACKI TOKENOWE");
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                tokenRequest.setTag(TAG);
                Volley.newRequestQueue(getContext()).add(tokenRequest);
            }
        });
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GRANTED);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("text/")) {

                textView.setMovementMethod(new ScrollingMovementMethod());
                handleSendText(view, intent); // Handle text being sent
            }
        }

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                    10, mLocationListener);

            System.out.println("no fine or coarse location");

            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
    }

    void handleSendText(View view, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    filePath = (Uri)bundle.get(key);
                    System.out.println("bundle.get(key): " + bundle.get(key));
                    fileContent = readTextFile((Uri)bundle.get(key));

                    TextView textView = view.findViewById(R.id.textView);
                    textView.setText(fileContent);

                }
            }
        }
        catch (Exception e)
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    System.out.println(key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
        }
    }

    private String readTextFile(Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getActivity().getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("hey");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = (ImageView) m_view.findViewById(R.id.imageView2);
            imageView.setImageBitmap(imageBitmap);
        }
    }

    public void sendOnChannel1() {
        String title = "File upload complete";
        String message = "File successfully uploaded onto the server";

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_1)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendErrorOnChannel1() {
        String title = "File upload error";
        String message = "File not uploaded onto the server, it already exists or there was another error.";

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_1)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }
}