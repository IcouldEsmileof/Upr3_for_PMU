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


public class MainActivity extends AppCompatActivity {

    // създава Dialog Object
    ProgressDialog prgDialog;

    TextView errorMsg;
    // Name Edit View Object
    EditText nameET;
    // Email Edit View Object
    EditText emailET;
    // Password Edit View Object
    EditText pwdET;

    EditText answer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // откриване на грешка Msg Text View control by ID
        errorMsg = findViewById(R.id.login_error);
        // Find Email Edit View control by ID
        emailET = findViewById(R.id.loginEmail);
        // Find Password Edit View control by ID
        pwdET = findViewById(R.id.loginPassword);
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        answer=findViewById(R.id.answer);
    }

    /**
     * Метода се задейства, когато Login бутона е натиснат * * @param view
     */
    public void loginUser(View view) {
        if("да".equals(answer.getText().toString().toLowerCase())||"не".equals(answer.getText().toString().toLowerCase())){
            Toast.makeText(getApplicationContext(), "That's all I needed to know.", Toast.LENGTH_LONG).show();
            navigatetoHomeActivity();
        }else {
            // взима стойността на Email ET control
            String email = emailET.getText().toString();
            // взима стойността на Password ET control
            String password = pwdET.getText().toString();
            // инстанция на Http Request обект за параметри
            RequestParams params = new RequestParams();
            // когато Name Edit View, Email Edit View и Password Edit View имат стойност различна от Null
            if (Utility.isNotNull(email) && Utility.isNotNull(password)) {
                // Когато въведеният Email е валиден
                if (Utility.validate(email)) {
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
                // При празно поле
            } else {
                Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void invokeWS(RequestParams params) {
        // Показване на Progress Dialog
        prgDialog.show();
        // Осъществяване на RESTful webservice извикване чрез използване на AsyncHttpClient обект
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d("\n Android say: ", params.toString());
        client.get("http://192.168.0.137:8080/useraccount/login/dologin", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Скриване на Progress Dialog
                prgDialog.hide();
                try {
                    // JSON обект
                    Log.d("\n Server say: ", new
                            String(responseBody));
                    JSONObject obj = new JSONObject(new String(responseBody));
                    // Когато JSON отговаря със булева стойност която е true
                    if (obj.getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                        // Navigate to Home screen
                        navigatetoHomeActivity();
                    }
                    // в Else секцията визуализира съобщение за грешка !
                    else {
                        errorMsg.setText(obj.getString("error_msg"));
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) { // TODO Auto-generated catch block
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
                } // Когато Http отговаря с код '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // Когато Http отговаря с код различен от 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
            // Когато отговорът се върне от REST сървъра Http връща код '200'
        });
    }

    /**
     * Метода се изпълнява, когати бъде натиснат бутона Register * * @param view
     */
    public void navigatetoRegisterActivity(View view) {
        Intent loginIntent = new Intent(getApplicationContext(), RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }


    public void navigatetoHomeActivity() {
        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
