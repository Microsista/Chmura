package com.example.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cameraapp.ui.login.LoginFragment;

import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static int index = 0;
    public final String directory = "" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    Bitmap imageBitmap;
    String fileContent;
    double latitude;
    double longitude;
    public static Bitmap bitmap;
//
//    private final LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(final Location location) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//        }
//    };
//
//    private LocationManager mLocationManager;
//
//
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }



    public String getLocale() {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        String myLocale = conf.locale.toString();
        return myLocale;
    }



    public void onLanguage(View view) {
        Fragment frag = null;
        frag = getSupportFragmentManager().findFragmentByTag("LoginFragment");
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(frag);
        fragmentTransaction.attach(frag);
        fragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request permissions and set strict mode
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GRANTED);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        Fragment firstFragment = new LoginFragment();

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.flFragment, new LoginFragment(), "loginTag");
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();


//        final Button button4 = findViewById(R.id.button4);
//
//        button4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new AppFragment()).commit();
//            }
//        });



//        // Get the Intent that started this activity
//        Intent intent = getIntent();
//        String action = intent.getAction();
//        String type = intent.getType();
//
//        if (Intent.ACTION_SEND.equals(action) && type != null) {
//            if (type.startsWith("text/")) {
//                TextView textView = findViewById(R.id.textView);
//                textView.setMovementMethod(new ScrollingMovementMethod());
//                handleSendText(intent); // Handle text being sent
//            } else if (type.startsWith("image/")) {
//                handleSendImage(intent); // Handle single image being sent
//            }
//        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
//            if (type.startsWith("image/")) {
//                handleSendMultipleImages(intent); // Handle multiple images being sent
//            }
//        } else {
//            // Handle other intents, such as being started from the home screen
//            String username = intent.getStringExtra(LoginFragment.EXTRA_MESSAGE1);
//
//            TextView textView = findViewById(R.id.textView2);
//            textView.setText("Signed in as " + username);
//        }
//
//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
//                    10, mLocationListener);
//            // mLocationManager.getCurrentLocation();
//
//            System.out.println("no fine or coarse location");
//
//            return;
//        }
//        //locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
    }
//
//    void handleSendText(Intent intent) {
//        //String sharedText = intent.getStringExtra(Intent.EXTRA_STREAM);
//        try {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                for (String key : bundle.keySet()) {
//                    fileContent = readTextFile((Uri)bundle.get(key));
////                    Log.d("ABC", bundle.get("android.intent.extra.STREAM").toString());
////                    fileContent = readTextFile((Uri) bundle.get("android.intent.extra.STREAM"));
//                    TextView textView = findViewById(R.id.textView);
//                    textView.setText(fileContent);
//
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                for (String key : bundle.keySet()) {
//                    System.out.println(key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
//                }
//            }
//        }
//    }
//
//    private String readTextFile(Uri uri) {
//        BufferedReader reader = null;
//        StringBuilder builder = new StringBuilder();
//        try {
//            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
//            String line = "";
//
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null){
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return builder.toString();
//    }
//
//    void handleSendImage(Intent intent) {
//        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//        if (imageUri != null) {
//            // Update UI to reflect image being shared
////            Bundle extras = intent.getExtras();
////            imageBitmap = (Bitmap) extras.get("data");
//            try {
//                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
//            imageView.setImageBitmap(imageBitmap);
//        }
//    }
//
//    void handleSendMultipleImages(Intent intent) {
//        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//        if (imageUris != null) {
//            // Update UI to reflect multiple images being shared
//        }
//    }
//
//    public void CameraButton(View view) {
//
//        index++;
//        String file = directory + index + ".jpg";
//        File newFile = new File(file);
//
//        try {
//            newFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Uri outputFileUri = Uri.fromFile(newFile);
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
//        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        try {
//            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
//        } catch (Exception e){
//
//        }
//    }
//
//    public String getStringImage(Bitmap bmp){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//        return encodedImage;
//    }
//
//    public void SendButton(View view) {
//    //Showing the progress dialog
//    final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
//    String url ="https://webhook.site/c62781ca-517b-437f-ac57-158097c11701";
//    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//            new Response.Listener<String>() {
//                @Override
//                public void onResponse(String s) {
//                    //Disimissing the progress dialog
//                    loading.dismiss();
//                    //Showing toast message of the response
//                    Toast.makeText(MainActivity.this, "SUCCESS" , Toast.LENGTH_LONG).show();
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    //Dismissing the progress dialog
//                    loading.dismiss();
//
//                    //Showing toast
//                    Toast.makeText(MainActivity.this, "NO IMAGE\n"+volleyError, Toast.LENGTH_LONG).show();
//                }
//            }){
//        @Override
//        protected Map<String, String> getParams() throws AuthFailureError {
//            //Converting Bitmap to String
//            String image = getStringImage(imageBitmap);
//            //Getting Image Name
//            String name = "image";//editTextName.getText().toString().trim();
//            //Creating parameters
//            Map<String,String> params = new Hashtable<String, String>();
//            params.put("szerokosc", String.valueOf(latitude));
//            params.put("wysokosc", String.valueOf(longitude));
//            params.put("empsno", "81");
//            params.put("storesno", "165");
//            params.put("lrSno", "1808");
//            params.put("recQty", "0");
//            params.put("recVol", "0");
//            params.put("recWgt", "0");
//            params.put("damageQty", "0");
//            params.put("looseQty", "0");
//            params.put("deliveryDate", "2016-09-24");
//            params.put("deliveryTime", "10:15");
//            params.put("uploadFile", image);
//            params.put("remarks", "mytestingrem");
//            params.put("receivedBy", "amankumar");
//            params.put("ipAddress", "12.65.65.32");
//
//            //returning parameters
//            return params;
//        }
//    };
//
//    //Creating a Request Queue
//    RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//    //Adding request to the queue
//    requestQueue.add(stringRequest);
//}
//
//    public void SendTextButton(View view) {
//        //Showing the progress dialog
//        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
//        String url ="https://webhook.site/c62781ca-517b-437f-ac57-158097c11701";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        //Disimissing the progress dialog
//                        loading.dismiss();
//                        //Showing toast message of the response
//                        Toast.makeText(MainActivity.this, "SUCCESS" , Toast.LENGTH_LONG).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Dismissing the progress dialog
//                        loading.dismiss();
//
//                        //Showing toast
//                        Toast.makeText(MainActivity.this, "NO TEXT\n"+volleyError, Toast.LENGTH_LONG).show();
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
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        //Adding request to the queue
//        requestQueue.add(stringRequest);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        System.out.println("hey");
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
//            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
//            imageView.setImageBitmap(imageBitmap);
//        }
//    }

//    public void onLogin(View view) {
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivityForResult(intent, LOGIN_RETURN_CODE);
//    }
public void onSubmit(View view) {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        Intent intent = new Intent(this, MainActivity.class);
//        TextView usernameTextView = (TextView)findViewById(R.id.username);
//        TextView passwordTextView = (TextView)findViewById(R.id.password);
//        String username = usernameTextView.getText().toString();
//        String password = passwordTextView.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE1, username);
//        intent.putExtra(EXTRA_MESSAGE2, password);
//        startActivity(intent);
//        startActivityForResult(intent, LOGIN_RETURN_CODE);
}
}

