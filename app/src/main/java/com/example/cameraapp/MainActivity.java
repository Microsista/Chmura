package com.example.cameraapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    int LAUNCH_SECOND_ACTIVITY = 1;
    public static int index = 0;
    public final String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/myCamera/";
    Bitmap imageBitmap;
    String fileContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA,
                WRITE_EXTERNAL_STORAGE}, PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            System.out.println("debug");
//            if ("text/plain".equals(type)) {
            if (type.startsWith("text/")) {
                System.out.println("debug2");
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_STREAM);
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    fileContent = readTextFile((Uri)bundle.get(key));
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText(fileContent);
                    //System.out.println(key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
            //Uri uri = intent.getData();


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


//        if (sharedText != null) {
//            // Update UI to reflect text being shared
//            TextView textView = (TextView) findViewById(R.id.textView);
//            System.out.println(sharedText);
//            textView.setText("LOL");
//            textView.setText(sharedText);
//        }
    }

    private String readTextFile(Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
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

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
//            Bundle extras = intent.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            imageView.setImageBitmap(imageBitmap);
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

//    public void SendButton(View view) {
//        final TextView textView = (TextView) findViewById(R.id.textView);
//
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="https://webhook.site/0afd3378-3864-4d1e-a0bd-0adb9c7c42ad";
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        textView.setText("Response is: "+ response.toString());
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                textView.setText("That didn't work!");
//            }
//        }){
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                String your_string_json = "{\"name\": \"picture1.jpg\"}";
//                return your_string_json.getBytes();
//            }
//        };
//
//// Add the request to the RequestQueue.
//        queue.add(stringRequest);
//        //requestQueue.start()
//    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void SendButton(View view) {
    //Showing the progress dialog
    final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
    String url ="https://webhook.site/0afd3378-3864-4d1e-a0bd-0adb9c7c42ad";
    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    //Disimissing the progress dialog
                    loading.dismiss();
                    //Showing toast message of the response
                    Toast.makeText(MainActivity.this, s , Toast.LENGTH_LONG).show();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    //Dismissing the progress dialog
                    loading.dismiss();

                    //Showing toast
                    Toast.makeText(MainActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
                }
            }){
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            //Converting Bitmap to String
            String image = getStringImage(imageBitmap);
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
    RequestQueue requestQueue = Volley.newRequestQueue(this);

    //Adding request to the queue
    requestQueue.add(stringRequest);
}

    public void SendTextButton(View view) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        String url ="https://webhook.site/0afd3378-3864-4d1e-a0bd-0adb9c7c42ad";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(MainActivity.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(MainActivity.this, ""+volleyError, Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //String image = getStringImage(imageBitmap);
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("hey");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            imageView.setImageBitmap(imageBitmap);
        }
    }
}

