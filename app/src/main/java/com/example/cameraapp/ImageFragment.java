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
import com.example.cameraapp.ui.login.RegisterFragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {

    View m_view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    //    static public int LOGIN_RETURN_CODE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    int LAUNCH_SECOND_ACTIVITY = 1;
    public static int index = 0;
    //    public final String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/myCamera/";
    public final String directory = "" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    String fileContent;
    Location locationGPS;
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
        final Button sendTextButton = view.findViewById(R.id.bSendText);
        final TextView loginInfo = view.findViewById(R.id.textView2);
        ImageView imageView = m_view.findViewById(R.id.imageView2);


        loginViewModel = new ViewModelProvider(requireActivity(), new LoginViewModelFactory())
                .get(LoginViewModel.class);


        loginInfo.setText(getString(R.string.login_name) + " " + loginViewModel.getDisplayName());


        textsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new TextFragment()).commit();
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
                                Toast.makeText(getActivity(), "NO IMAGE\n"+volleyError, Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //Converting Bitmap to String
                        String image = getStringImage(MainActivity.bitmap);
                        //Getting Image Name
                        String name = "image";//editTextName.getText().toString().trim();
                        //Creating parameters
                        Map<String,String> params = new Hashtable<String, String>();
                        params.put("szerokosc", String.valueOf(latitude));
                        params.put("wysokosc", String.valueOf(longitude));
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

//        sendTextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Showing the progress dialog
//                final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
//                String url ="https://webhook.site/c62781ca-517b-437f-ac57-158097c11701";
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String s) {
//                                //Disimissing the progress dialog
//                                loading.dismiss();
//                                //Showing toast message of the response
//                                Toast.makeText(getActivity(), "SUCCESS" , Toast.LENGTH_LONG).show();
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError volleyError) {
//                                //Dismissing the progress dialog
//                                loading.dismiss();
//
//                                //Showing toast
//                                Toast.makeText(getActivity(), "NO TEXT\n"+volleyError, Toast.LENGTH_LONG).show();
//                            }
//                        }){
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        //Converting Bitmap to String
//                        //String image = getStringImage(imageBitmap);
//                        String image = fileContent;
//                        //Getting Image Name
//                        String name = "image";//editTextName.getText().toString().trim();
//                        //Creating parameters
//                        Map<String,String> params = new Hashtable<String, String>();
//                        params.put("empsno", "81");
//                        params.put("storesno", "165");
//                        params.put("lrSno", "1808");
//                        params.put("recQty", "0");
//                        params.put("recVol", "0");
//                        params.put("recWgt", "0");
//                        params.put("damageQty", "0");
//                        params.put("looseQty", "0");
//                        params.put("deliveryDate", "2016-09-24");
//                        params.put("deliveryTime", "10:15");
//                        params.put("uploadFile", image);
//                        params.put("remarks", "mytestingrem");
//                        params.put("receivedBy", "amankumar");
//                        params.put("ipAddress", "12.65.65.32");
//
//                        //returning parameters
//                        return params;
//                    }
//                };
//
//                //Creating a Request Queue
//                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//
//                //Adding request to the queue
//                requestQueue.add(stringRequest);
//            }
//        });











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
//                TextView textView = view.findViewById(R.id.textView);
//                textView.setMovementMethod(new ScrollingMovementMethod());
//                handleSendText(view, intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(view, intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
//            String username = intent.getStringExtra(LoginFragment.EXTRA_MESSAGE1);
//
//            TextView textView = view.findViewById(R.id.textView2);
//            textView.setText("Signed in as " + username);
        }

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                    10, mLocationListener);
            // mLocationManager.getCurrentLocation();

            System.out.println("no fine or coarse location");

            return;
        }
        //locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
    }

    void handleSendText(View view, Intent intent) {
        //String sharedText = intent.getStringExtra(Intent.EXTRA_STREAM);
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    fileContent = readTextFile((Uri)bundle.get(key));
//                    Log.d("ABC", bundle.get("android.intent.extra.STREAM").toString());
//                    fileContent = readTextFile((Uri) bundle.get("android.intent.extra.STREAM"));
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

    void handleSendImage(View view, Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
//            Bundle extras = intent.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
            try {
                MainActivity.bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);
            imageView.setImageBitmap(MainActivity.bitmap);
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    public void CameraButton(View view) {

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

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void SendButton(View view) {
        //Showing the progress dialog
//        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
//        String url ="https://webhook.site/c62781ca-517b-437f-ac57-158097c11701";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        //Disimissing the progress dialog
//                        loading.dismiss();
//                        //Showing toast message of the response
//                        Toast.makeText(getActivity(), "SUCCESS" , Toast.LENGTH_LONG).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Dismissing the progress dialog
//                        loading.dismiss();
//
//                        //Showing toast
//                        Toast.makeText(getActivity(), "NO IMAGE\n"+volleyError, Toast.LENGTH_LONG).show();
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                //Converting Bitmap to String
//                String image = getStringImage(imageBitmap);
//                //Getting Image Name
//                String name = "image";//editTextName.getText().toString().trim();
//                //Creating parameters
//                Map<String,String> params = new Hashtable<String, String>();
//                params.put("szerokosc", String.valueOf(latitude));
//                params.put("wysokosc", String.valueOf(longitude));
//                params.put("empsno", "81");
//                params.put("storesno", "165");
//                params.put("lrSno", "1808");
//                params.put("recQty", "0");
//                params.put("recVol", "0");
//                params.put("recWgt", "0");
//                params.put("damageQty", "0");
//                params.put("looseQty", "0");
//                params.put("deliveryDate", "2016-09-24");
//                params.put("deliveryTime", "10:15");
//                params.put("uploadFile", image);
//                params.put("remarks", "mytestingrem");
//                params.put("receivedBy", "amankumar");
//                params.put("ipAddress", "12.65.65.32");
//
//                //returning parameters
//                return params;
//            }
//        };
//
//        //Creating a Request Queue
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//
//        //Adding request to the queue
//        requestQueue.add(stringRequest);
    }

    public void SendTextButton(View view) {
//        //Showing the progress dialog
//        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
//        String url ="https://webhook.site/c62781ca-517b-437f-ac57-158097c11701";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        //Disimissing the progress dialog
//                        loading.dismiss();
//                        //Showing toast message of the response
//                        Toast.makeText(getActivity(), "SUCCESS" , Toast.LENGTH_LONG).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Dismissing the progress dialog
//                        loading.dismiss();
//
//                        //Showing toast
//                        Toast.makeText(getActivity(), "NO TEXT\n"+volleyError, Toast.LENGTH_LONG).show();
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                //Converting Bitmap to String
//                //String image = getStringImage(imageBitmap);
//                String image = fileContent;
//                //Getting Image Name
//                String name = "image";//editTextName.getText().toString().trim();
//                //Creating parameters
//                Map<String,String> params = new Hashtable<String, String>();
//                params.put("empsno", "81");
//                params.put("storesno", "165");
//                params.put("lrSno", "1808");
//                params.put("recQty", "0");
//                params.put("recVol", "0");
//                params.put("recWgt", "0");
//                params.put("damageQty", "0");
//                params.put("looseQty", "0");
//                params.put("deliveryDate", "2016-09-24");
//                params.put("deliveryTime", "10:15");
//                params.put("uploadFile", image);
//                params.put("remarks", "mytestingrem");
//                params.put("receivedBy", "amankumar");
//                params.put("ipAddress", "12.65.65.32");
//
//                //returning parameters
//                return params;
//            }
//        };
//
//        //Creating a Request Queue
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//
//        //Adding request to the queue
//        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("hey");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            MainActivity.bitmap = (Bitmap) extras.get("data");
            ImageView imageView = m_view.findViewById(R.id.imageView2);
            imageView.setImageBitmap(MainActivity.bitmap);
        }
    }

//    public void onLogin(View view) {
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivityForResult(intent, LOGIN_RETURN_CODE);
//    }



}