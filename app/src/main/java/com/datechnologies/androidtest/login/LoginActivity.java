package com.datechnologies.androidtest.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.datechnologies.androidtest.MainActivity;
import com.datechnologies.androidtest.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A screen that displays a login prompt, allowing the user to login to the D & A Technologies Web Server.
 *
 */
public class LoginActivity extends AppCompatActivity {

    //==============================================================================================
    // Static Class Methods
    //==============================================================================================
    private String email;
    private String password;

    public static void start(Context context)
    {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    public void update(){
        EditText e = findViewById(R.id.emailField);
        EditText p = findViewById(R.id.passwordField);

        this.email = e.getText().toString();
        this.password = p.getText().toString();
    }

    public void login(){
        String httpURL = getResources().getString(R.string.loginURL);
        HttpURLConnection urlConnection = null;

        String code = null, message = null;
        long time = System.currentTimeMillis();

        try {
            String data = URLEncoder.encode("email", "UTF-8") + "=" +
                            URLEncoder.encode(this.email, "UTF-8") + "&" +
                            URLEncoder.encode("password", "UTF-8") + "=" +
                            URLEncoder.encode(this.password, "UTF-8");
            URL url = new URL(httpURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null){
                buffer.append(line);
            }

            JSONObject response = new JSONObject(buffer.toString());
            code = response.getString("code");
            message = response.getString("message");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

        if(code !=null && message != null){
            String finalMessage = message;
            String finalCode = code;
            long elapsedTime = System.currentTimeMillis() - time;
            runOnUiThread(()-> showDialog(finalCode, finalMessage, elapsedTime));
        }
    }

    public void showDialog(String code, String message, long elapsedTime){
        Log.e("TIME", Long.toString(elapsedTime));
        Intent intent = new Intent(this, MainActivity.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(code);
        builder.setMessage(message + "\n\nTotal Time: " + elapsedTime + "ms.");
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            startActivity(intent);
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        update();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // TODO: Make the UI look like it does in the mock-up. Allow for horizontal screen rotation.
        // TODO: Add a ripple effect when the buttons are clicked
        // TODO: Save screen state on screen rotation, inputted username and password should not disappear on screen rotation

        // TODO: Send 'email' and 'password' to http://dev.rapptrlabs.com/Tests/scripts/login.php
        // TODO: as FormUrlEncoded parameters.

        // TODO: When you receive a response from the login endpoint, display an AlertDialog.
        // TODO: The AlertDialog should display the 'code' and 'message' that was returned by the endpoint.
        // TODO: The AlertDialog should also display how long the API call took in milliseconds.
        // TODO: When a login is successful, tapping 'OK' on the AlertDialog should bring us back to the MainActivity

        // TODO: The only valid login credentials are:
        // TODO: email: info@rapptrlabs.com
        // TODO: password: Test123
        // TODO: so please use those to test the login.
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginClicked(View v){
        update();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(this::login);
    }
}
