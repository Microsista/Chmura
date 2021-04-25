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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA,
                WRITE_EXTERNAL_STORAGE}, PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
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