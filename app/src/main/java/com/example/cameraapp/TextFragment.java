package com.example.cameraapp;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cameraapp.ui.login.LoginViewModel;
import com.example.cameraapp.ui.login.LoginViewModelFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class TextFragment extends Fragment {
    View m_view;

    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        final Button sendTextButton = view.findViewById(R.id.bSendText);
        final TextView loginInfo = view.findViewById(R.id.textView2);
        final Button images2Button = view.findViewById(R.id.myButton);

        loginViewModel = new ViewModelProvider(requireActivity(), new LoginViewModelFactory())
                .get(LoginViewModel.class);

        loginInfo.setText(getString(R.string.login_name) + " " + loginViewModel.getDisplayName());

        images2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new ImageFragment()).commit();
            }
        });

        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Showing the progress dialog
                final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
                String url ="https://webhook.site/c62781ca-517b-437f-ac57-158097c11701";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                //Disimissing the progress dialog
                                loading.dismiss();
                                //Showing toast message of the response
                                Toast.makeText(getActivity(), "SUCCESS" , Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //Dismissing the progress dialog
                                loading.dismiss();

                                //Showing toast
                                Toast.makeText(getActivity(), "NO TEXT\n"+volleyError, Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //Converting Bitmap to String
                        String image = fileContent;
                        //Getting Image Name
                        String name = "image";//editTextName.getText().toString().trim();
                        //Creating parameters
                        Map<String,String> params = new Hashtable<String, String>();
                        params.put("empsno", "81");
                        params.put("storesno", "165");
                        params.put("lrSno", "1808");
                        params.put("recQty", "0");
                        params.put("recVol", "0");
                        params.put("recWgt", "0");
                        params.put("damageQty", "0");
                        params.put("looseQty", "0");
                        params.put("deliveryDate", "2016-09-24");
                        params.put("deliveryTime", "10:15");
                        params.put("uploadFile", image);
                        params.put("remarks", "mytestingrem");
                        params.put("receivedBy", "amankumar");
                        params.put("ipAddress", "12.65.65.32");

                        //returning parameters
                        return params;
                    }
                };

                //Creating a Request Queue
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                //Adding request to the queue
                requestQueue.add(stringRequest);
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
            if (type.startsWith("text/")) {
                TextView textView = view.findViewById(R.id.textView);
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
}