package com.example.eliad.traceyourkid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity implements AlertDialog.OnClickListener, CompoundButton.OnCheckedChangeListener {


    EditText email,password,kidName;
    ImageButton login;
    CheckBox ChildPhone;
    TextView sign;
    String parentEmail,parentPass, myChildName,kidToken;
    JSONObject parentLogin;
    AlertDialog ad,wrnPass,errUser;
    boolean doubleBackToExitPressedOnce = false;
    RegistrationIntentService regIg ;
    SharedPreferences settings ;
    public static final String mypreference = "gcm_token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        String response;

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        kidName = (EditText) findViewById(R.id.kidName);
        login  = (ImageButton) findViewById(R.id.login);
        ChildPhone = (CheckBox) findViewById(R.id.ChildPhone);
        ChildPhone.setOnCheckedChangeListener(this);
//        forgot = (TextView) findViewById(R.id.forgot);
        sign = (TextView) findViewById(R.id.signup);
        regIg = new RegistrationIntentService();
        settings = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

    }

    public void SignUp(View view) {
        ad = new AlertDialog.Builder(this)
        .setTitle("Your age?")
        .setMessage("Are you over 18?")
        .setPositiveButton("Yes",this)
        .setNegativeButton("No" , this)
        .create();
        ad.show();



    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == ad.BUTTON_POSITIVE) {
            Intent signUp = new Intent(this, AddParentUser.class);
            startActivity(signUp);
//        }if (which == emptyKidName.BUTTON_NEGATIVE){
//            return;
//        }

//        }if (which == whichUser.BUTTON_NEGATIVE){
//            Intent parentUser = new Intent(this,parentUser.class);
//            startActivity(parentUser);
        }

    }
    public void login(View view) {
        parentEmail = email.getText().toString();
        parentPass = password.getText().toString();
        myChildName = kidName.getText().toString();
        kidToken = settings.getString("token",regIg.tokenOrRegistrationID);


        parentLogin = new JSONObject();

        try {
            parentLogin.put("email", parentEmail);
            parentLogin.put("password", parentPass);
            parentLogin.put("kidname",myChildName);
            parentLogin.put("gcm_token",kidToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (parentLogin.length() > 0) {
            if (ChildPhone.isChecked()  && !myChildName.isEmpty()){
                new GetJsonDataToServer().execute(String.valueOf(parentLogin));
            }if (ChildPhone.isChecked() == false && myChildName.isEmpty()){
                new ParentLoginToServer().execute(String.valueOf(parentLogin));
            }else if (ChildPhone.isChecked() && myChildName.isEmpty()){
                final AlertDialog.Builder emptyKidName = new AlertDialog.Builder(this);
                emptyKidName.setTitle("You have forgotten something ");
                emptyKidName.setMessage("Please enter your child name!");
                emptyKidName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                emptyKidName.show();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (ChildPhone.isChecked()){
            kidName.setVisibility(View.VISIBLE);
        }else{
            kidName.setVisibility(View.GONE);
        }
    }

    public void maps(View view) {
        Intent maps = new Intent(this,KidMapLocation.class);
        startActivity(maps);
    }

    private class GetJsonDataToServer extends AsyncTask<String, String, String> {


            @Override
            protected String doInBackground(String... params) {
                String jsonResponse = null;
                String jsonData = params[0];
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL("http://10.0.2.2:3000/kidlogin");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                    writer.write(jsonData);
                    writer.close();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String inputLine = null;
                    while ((inputLine = reader.readLine()) != null)
                        buffer.append(inputLine + "\n");
                    if (buffer.length() == 0) {
                        return null;
                    }
                    jsonResponse = buffer.toString();
                    Log.d("probLog", jsonResponse);
                    if (jsonResponse.equals("info" + "kid Login Sucess")){
                        Toast.makeText(MainActivity.this,"login succes",Toast.LENGTH_SHORT).show();
                    }

                    return jsonResponse;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            e.printStackTrace();
                            Log.d("error", "error closing stream", e);
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {

            }

        }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            onDestroy();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to Exit ", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private class ParentLoginToServer extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            String jsonResponse = null;
            String jsonData = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://10.0.2.2:3000/login");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonData);
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder sb = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(
                        inputStream, "iso-8859-1"), 8);

                String inputLine = null;
                while ((inputLine = reader.readLine()) != null)
                    sb.append(inputLine + "\n");
                if (sb.length() == 0) {
                    return null;
                }
                inputStream.close();
                jsonResponse = sb.toString();
                Log.d("probLog", jsonResponse);
                if (jsonResponse.equals("response':\"Invalid Password")) {
                    wrnPass = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Who are you")
                            .setMessage("Are you the parent or the kid?")
                            .setPositiveButton("kid", (DialogInterface.OnClickListener) this)
                            .create();
                    wrnPass.show();
                }if (jsonResponse.toString().equals("info':\"kid Login Sucess")){
                    Toast.makeText(MainActivity.this, " " +jsonResponse,Toast.LENGTH_SHORT).show();
                }

                return jsonResponse;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                        Log.d("error", "error closing stream", e);
                    }
                }
            }

            return null;
        }
    }
}

