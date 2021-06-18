package com.example.cameraapp;

import android.Manifest;
import android.app.Notification;
import android.app.ProgressDialog;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cameraapp.data.model.LoggedInUser;
import com.example.cameraapp.ui.login.LoginViewModel;
import com.example.cameraapp.ui.login.LoginViewModelFactory;
import com.example.cameraapp.ui.login.RegisterFragment;
import com.example.cameraapp.ui.login.VolleyCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.android.volley.VolleyLog.TAG;
import static com.example.cameraapp.App.CHANNEL_1_ID;

public class ImageFragment extends Fragment {

    private NotificationManagerCompat notificationManager;

    View m_view;
    Bitmap m_bitmap;

    String token;

    public ImageFragment() {
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
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static int index = 0;
    public final String directory = "" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    double latitude;
    double longitude;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    };

    private LocationManager mLocationManager;

    private LoginViewModel loginViewModel;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_view = view;

        final Button cameraButton = view.findViewById(R.id.button);
        final Button textsButton = view.findViewById(R.id.bTexts);
        final Button sendImageButton = view.findViewById(R.id.bSendImage);
        final TextView loginInfo = view.findViewById(R.id.textView2);
        final EditText usernameEditText = view.findViewById(R.id.username);
        final EditText passwordEditText = view.findViewById(R.id.password);
        final EditText fileNameET = view.findViewById(R.id.fileName);

        Bundle bundle = this.getArguments();
        String username = bundle.getString("username");
        String password = bundle.getString("password");


        loginViewModel = new ViewModelProvider(requireActivity(), new LoginViewModelFactory())
                .get(LoginViewModel.class);

        loginInfo.setText(getString(R.string.login_name) + " " + loginViewModel.getDisplayName());

        textsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("username", loginViewModel.getDisplayName());
                bundle.putString("password", password);
                TextFragment fragment = new TextFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new TextFragment()).commit();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                String file = directory + index + ".jpg";
                File newFile = new File(file);

                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri outputFileUri = Uri.fromFile(newFile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                try {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                } catch (Exception e){

                }
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
               String url = "https://192.168.1.13:8443/api/fileDrop/image";

                ///////////////////////////////////
                // Get token
                String tokenUrl = "https://192.168.1.13:8443/api/auth/signIn";
                Map<String, String> tokenParams = new HashMap<>();
                tokenParams.put("username", username);
                tokenParams.put("password", password);
                System.out.println("COKOLWIEK TOKENOWE");

                JsonObjectRequest tokenRequest = new JsonObjectRequest(Request.Method.POST,
                        tokenUrl, new JSONObject(tokenParams), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject outresponse) {
                        //Converting Bitmap to String
                        String image = getStringImage(m_bitmap);
                        System.out.println(image);
                        //Getting Image Name
                        String name = "image";//editTextName.getText().toString().trim();
                        final JSONObject params = new JSONObject();
                        try {
                            params.put("geoWidth", String.valueOf(latitude));
                            params.put("geoHeight", String.valueOf(longitude));
                            params.put("image", image);
                            params.put("path", fileNameET.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        StringRequest postRequest = new StringRequest(Request.Method.POST,
                                url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                                Log.d(TAG, response.toString());
                                System.out.println("PLACKI");
                                if (response.startsWith("success"))
                                    sendOnChannel1();
                                else
                                    sendErrorOnChannel1();
                                loading.dismiss();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("ERROR:" + error);
                                VolleyLog.d(TAG, "Error: " + error.getMessage());
                                System.out.println("NIEDOBRE PLACKI");
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }
                        }) {
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return params.toString().getBytes();
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json";
                            }
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                String bearerToken = null;
                                try {
                                    bearerToken = "Bearer " + outresponse.getString("token");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ;
                                System.out.println("MY BEARER TOKEN: " + bearerToken);
                                headers.put("Authorization", bearerToken);
                                return headers;
                            }
                        };
                        postRequest.setTag(TAG);
                        Volley.newRequestQueue(getContext()).add(postRequest);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        System.out.println("NIEDOBRE PLACKI TOKENOWE");
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
                tokenRequest.setTag(TAG);
                Volley.newRequestQueue(getContext()).add(tokenRequest);
            }
        });

        // Request permissions and set strict mode
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GRANTED);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Get the Intent that started this activity
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
           if (type.startsWith("image/")) {
                handleSendImage(view, intent); // Handle single image being sent
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

    void handleSendImage(View view, Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try {
                m_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);
            imageView.setImageBitmap(m_bitmap);
        }
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                .replace("_", "")
                .replace("-", "");
        return encodedImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("hey");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            m_bitmap = (Bitmap) extras.get("data");
            ImageView imageView = m_view.findViewById(R.id.imageView2);
            imageView.setImageBitmap(m_bitmap);
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