package com.example.cameraapp.ui.login.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class SSL {
//    public SSLContext context = null;
//    public Context m_activityContext = null;
//
//    public SSL(Context context) {
//        m_activityContext = context;
//    }
//
//    public void doBasicAuth(View view) {
//        new Connection().execute();
//    }
//
//    private class Connection extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            connect();
//            //check()
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//        }
//    }
//
//    private void connect() {
//        // Create certificate factory
//        CertificateFactory cf = null;
//        try {
//            cf = CertificateFactory.getInstance("X.509");
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return false;
//                }
//            });
//            // Certificate pinning, make sure the certificate is filled and present in backend.
//            InputStream caInput = m_activityContext.getAssets().open("load-der.crt");
//            Certificate ca = null;
//            try {
//                ca = cf.generateCertificate(caInput);
//            } catch (CertificateException e) {
//                e.printStackTrace();
//            } finally {
//                caInput.close();
//            }
//
//            // Create a KeyStore containing our trusted CAs
//            String keyStoreType = KeyStore.getDefaultType();
//            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//            keyStore.load(null, null);
//            keyStore.setCertificateEntry("ca", ca);
//
//            // Create a TrustManager that trusts the CAs in out KeyStore
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);
//
//            // Create an SSLContext that uses out TrustManager
//            context = SSLContext.getInstance("TLS");
//            context.init(null, tmf.getTrustManagers(), null);
//
//        } catch(IOException e) {
//            e.printStackTrace();
//        }catch(KeyManagementException e) {
//            e.printStackTrace();
//        }catch(KeyStoreException e) {
//            e.printStackTrace();
//        }catch(CertificateException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        URL url = null;
//        try {
//            url = new URL("https://192.168.1.13:8080/api/signIn");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        HttpsURLConnection urlConnection = null;
//        try {
//            urlConnection = (HttpsURLConnection)url.openConnection();
//            // Change to beared authorization?
//            final String basicAuth = "Basic " + Base64.encodeToString("user:pass".getBytes(), Base64.NO_WRAP); // this is just a template, you can provide your own user and password.
//            urlConnection.setRequestProperty("Authorization", basicAuth);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        urlConnection.setSSLSocketFactory(context.getSocketFactory());
//        try {
//            System.out.println(urlConnection.getResponseMessage());
//            System.out.println(urlConnection.getResponseCode());
//            if (urlConnection.getResponseCode() == 200) {
//                InputStream in = urlConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                StringBuilder out = new StringBuilder();
//                while ((line = reader.readLine()) != null) {
//                    out.append(line);
//                }
//                System.out.println(out.toString());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
