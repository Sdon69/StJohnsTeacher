package com.theironfoundry8890.stjohnsteacher;

/*
 * BEFORE RUNNING:
 * ---------------
 * 1. If not already done, enable the Google Sheets API
 *    and check the quota for your project at
 *    https://console.developers.google.com/apis/api/sheets
 * 2. Install the Java client library on Maven or Gradle. Check installation
 *    instructions at https://github.com/google/google-api-java-client.
 *    On other build systems, you can add the jar files to your project from
 *    https://developers.google.com/resources/api-libraries/download/sheets/v4/java
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;


/**
 * Created by Sdon69 on 07-09-2017.
 */



public class Select extends AppCompatActivity {

    private TextView mOutputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectlayout);
        mOutputText = (TextView) findViewById(R.id.display);


    }


    public static void main(String args[]) throws IOException, GeneralSecurityException {
        // The ID of the spreadsheet to retrieve data from.
        String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"; // TODO: Update placeholder value.

        // The A1 notation of the values to retrieve.
        String range = "Class Data!A2:E"; // TODO: Update placeholder value.

        Sheets sheetsService = createSheetsService();
        Sheets.Spreadsheets.Values.Get request =
                sheetsService.spreadsheets().values().get(spreadsheetId, range);

        ValueRange response = request.execute();


        // TODO: Change code below to process the `response` object:
        System.out.println(response);





    }


    public static Sheets createSheetsService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // TODO: Change placeholder below to generate authentication credentials. See
        // https://developers.google.com/sheets/quickstart/java#step_3_set_up_the_sample
        //
        // Authorize using one of the following scopes:
        //   "https://www.googleapis.com/auth/drive"
        //   "https://www.googleapis.com/auth/drive.file"
        //   "https://www.googleapis.com/auth/drive.readonly"
        //   "https://www.googleapis.com/auth/spreadsheets"
        //   "https://www.googleapis.com/auth/spreadsheets.readonly"
        GoogleCredential credential = null;

        return new Sheets.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Google-SheetsSample/0.1")
                .build();
    }
}


