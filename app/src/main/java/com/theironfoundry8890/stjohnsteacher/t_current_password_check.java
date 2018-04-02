package com.theironfoundry8890.stjohnsteacher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class t_current_password_check extends AppCompatActivity {




    private String sPassword;private String userId;
    private String loadedPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_password_check);
        loadData();



    }

    public void loadData(){

        //Loading Data via ShredPreferences
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        String Pass = mPrefs.getString("tpass", "default_value_if_variable_not_found");

        loadedPassword = Pass;

    }

    public void SubmitData(View v)
    {
        EditText passEditText = (EditText) findViewById(R.id.pCurrentPassword);

        sPassword = String.valueOf(passEditText.getText());

        if(sPassword.equals(loadedPassword))
        {
            Intent selectIntent = new Intent(t_current_password_check.this,t_ChangePassword.class);
            startActivity(selectIntent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
        }


    }




}
