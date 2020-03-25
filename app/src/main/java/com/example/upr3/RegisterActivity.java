package com.example.upr3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    ProgressDialog prgDialog;
    TextView errorMsg;
    EditText nameET;
    EditText emailET;
    EditText pwdET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        errorMsg = findViewById(R.id.register_error);
        nameET = findViewById(R.id.registerName);
        emailET = findViewById(R.id.registerEmail);
        pwdET = findViewById(R.id.registerPassword);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

    }

    /**
     * Метод който се задейства когато Register бутоноа е натиснат ! * * @param view
     */
    public void registerUser(View view) {
        // взима стойността на NAme ET control
        String name = nameET.getText().toString();
        // взима стойността на Email ET control
        String email = emailET.getText().toString();
        // взима стойността на Password ET control
        String password = pwdET.getText().toString();
        // инстанция на Http Request обект за параметри
        RequestParams params = new RequestParams();
        // когато Name Edit View, Email Edit View и Password Edit View имат стойност различна от Null
        if (Utility.isNotNull(name) && Utility.isNotNull(email) && Utility.isNotNull(password)) {
            // Когато въведеният Email е валиден
            if (Utility.validate(email)) {
                // Поставяне на Http параметър name от полето на формата
                params.put("name", name);
                // Поставяне на Http параметър username от полето на формата
                params.put("username", email);
                // Поставяне на Http параметър password от полето на формата
                params.put("password", password);
                // Извикване на RESTful Web Service със Http параметри
                invokeWS(params);
            }
            // При невалиден Email
            else {
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }
        }
        // При празно поле
        else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }
    }

    public void invokeWS(RequestParams params) {
        // Показване на Progress Dialog
        prgDialog.show();
        // Осъществяване на RESTful webservice извикване чрез използване на AsyncHttpClient обект
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d("\n Android say: ", params.toString());
        client.get("http://192.168.0.137:8080/useraccount/register/doregister", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Скриване на Progress Dialog
                prgDialog.hide();
                try {
                    Log.d("\n Server say: ", new String(responseBody));
                    // JSON обект
                    JSONObject obj = new JSONObject(new String(responseBody));
                    // Когато JSON отговаря със булева стойност която е true
                    if (obj.getBoolean("status")) {
                        // Задава стойности по Default за Edit View controls
                        setDefaultValues();
                        // Показва сполучливо регистрирано съобщение чрез Toast
                        Toast.makeText(getApplicationContext(), "You are successfully registered!", Toast.LENGTH_LONG).show();
                    }
                    // в Else секцията визуализира съобщение за грешка !
                    else {
                        errorMsg.setText(obj.getString("error_msg"));
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (
                        JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // скриване на Progress Dialog
                prgDialog.hide();
                // Когато Http отговаря с код '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // Когато Http отговаря с код '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // Когато Http отговаря с код различен от 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    /**
     * метод навигиращ от Register Activity към MainActivity ( Login Activity )
     */
    public void navigatetoLoginActivity(View view) {
        Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
        // за изчистване на историята на Activity
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    /**
     * стойности подразбиране на Edit View controls
     */
    public void setDefaultValues() {
        nameET.setText("");
        emailET.setText("");
        pwdET.setText("");
    }
}
