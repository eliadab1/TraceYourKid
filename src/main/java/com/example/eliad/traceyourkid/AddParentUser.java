package com.example.eliad.traceyourkid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


//Class add new parent user to server


public class AddParentUser extends AppCompatActivity implements AlertDialog.OnClickListener {

    Button register, cancel;
    EditText email, userName, password, repeatPassword, age, fullName;
    String userEmail, userNameInApp, userPassword, userAge, userFullName, repeatPass,parentToken;
    JSONObject userProfile;
    RegistrationIntentService registration;
    AlertDialog whichUser;
    SharedPreferences settings ;
    boolean doubleBackToExitPressedOnce = false;
    public static final String mypreference = "gcm_token";
//    boolean emptyField = false;
//    EditText[] fields;
//    List<EditText> textList;

    String parentUserstr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_parent_user);

        email = (EditText) findViewById(R.id.email);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        repeatPassword = (EditText) findViewById(R.id.repeatpassword);
        age = (EditText) findViewById(R.id.age);
        fullName = (EditText) findViewById(R.id.fullname);
        register = (Button) findViewById(R.id.registerparent);
        cancel = (Button) findViewById(R.id.cancel);
//        fields = new EditText[]{userName, password, repeatPassword, email, age, fullName};
//        textList = new ArrayList<>(Arrays.asList(fields));
        registration = new RegistrationIntentService();
        settings = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

    }

    public void RegisterParent(View view) {
        //Take all info and checking if everything is complete
        userEmail = email.getText().toString();
        userNameInApp = userName.getText().toString();
        userPassword = password.getText().toString();
        userAge = age.getText().toString();
        userFullName = fullName.getText().toString();
        repeatPass = repeatPassword.getText().toString();
        parentToken = settings.getString("token",registration.tokenOrRegistrationID);
        if (!userEmail.isEmpty() && !userNameInApp.isEmpty() &&
                !userPassword.isEmpty() && !userAge.isEmpty() &&
                !userFullName.isEmpty() && !repeatPass.isEmpty()) {
            Log.d("iterator", "not null");
            if (!userPassword.equals(repeatPass)) {
                final AlertDialog.Builder wrongPass = new AlertDialog.Builder(this);
                wrongPass.setTitle("Error creating account");
                wrongPass.setMessage("Passwords do not match");
                wrongPass.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                wrongPass.show();
            } else {
                userProfile = new JSONObject();

                try {
                    userProfile.put("fullname", userFullName);
                    userProfile.put("password", userPassword);
                    userProfile.put("username", userNameInApp);
                    userProfile.put("email", userEmail);
                    userProfile.put("age", userAge);
                    userProfile.put("gcm_token",parentToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (userProfile.length() > 0) {
                    new SendJsonDataToServer().execute(String.valueOf(userProfile));
                    Log.d("send succes", userProfile.toString());


                }
            }
        } else {
            Log.d("iterator", "something equals null");
            //not all info complete
            final AlertDialog.Builder nullEditText = new AlertDialog.Builder(this);
            nullEditText.setTitle("Error creating account");
            nullEditText.setMessage("Please fill out all the information!");
            nullEditText.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            nullEditText.show();
        }


    }



    @Override
    public void onBackPressed() {
        //back to main activity
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to return to login ", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    public void cancelSingUp(View view) {
        Intent login = new Intent(this, MainActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
//    if (which == whichUser.BUTTON_POSITIVE){
//            Intent kidUser = new Intent(this,kidUser.class);
//            startActivity(kidUser);
//        }if (which == whichUser.BUTTON_NEGATIVE){
//            Intent parentUser = new Intent(this,parentUser.class);
//            startActivity(parentUser);
//    }
    }


    private class SendJsonDataToServer extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            String jsonResponse = null;
            String jsonData = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://10.0.2.2:3000/register");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
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

}



