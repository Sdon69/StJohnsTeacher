package com.theironfoundry8890.stjohnsteacher;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class details extends Activity
        implements AdapterView.OnItemSelectedListener , EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private Button mCallApiButton;
    ProgressDialog mProgress;
    private String sFName;
    private String sLName;
    private String sClass;
    private String sEmail;
    private String sSection;
    private String sDepartment;
    private String sSemester;
    private String sId;
    private String sPhone;
    private TextView mOutputText;
    private String sPassword;private String userId;
    private boolean idAvailcheck = true;
    private String mode = "department";
    private boolean dmode = true;

    private ArrayList className = new ArrayList();
    private ArrayList sectionName = new ArrayList();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        className.add("Class");
        sectionName.add("Section");
        setContentView(R.layout.detaillay);

        LinearLayout activityLayout = (LinearLayout) findViewById(R.id.mLayout);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);


        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText(
                " ");
        activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading ...");
        mProgress.setCanceledOnTouchOutside(false);

        setContentView(activityLayout);

        Spinner spin = (Spinner) findViewById(R.id.spinner_department);
        spin.setOnItemSelectedListener(this);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

    }



    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {

            Toast.makeText(getApplicationContext(), "No Network Connection",
                    Toast.LENGTH_SHORT).show();
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {

                        Log.v("Account",accountName);
                        userId = accountName;
                        //save User Email Id
                        SharedPreferences mPrefs = getSharedPreferences("label", 0);SharedPreferences.Editor mEditor = mPrefs.edit();mEditor.putString("UserId", userId).commit();

                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        Log.v("details" , "Success");
        return connectionStatusCode == ConnectionResult.SUCCESS;

    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                details.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (mode == "department") {
            Spinner dept_spin = (Spinner) findViewById(R.id.spinner_department);
            sDepartment = String.valueOf(dept_spin.getSelectedItem());

            if (!sDepartment.equals("Department")) {
                mode = "classfeed";
                dept_spin.setEnabled(false);
                dept_spin.setClickable(false);
                Button reselectDept = (Button) findViewById(R.id.reselect_dept);
                reselectDept.setClickable(true);
                reselectDept.setEnabled(true);
                reselectDept.setVisibility(view.VISIBLE);
                className.clear();
                className.add("Class");

                Spinner spin = (Spinner) findViewById(R.id.spinner_class);
                spin.setOnItemSelectedListener(this);
                mOutputText.setText("");
                getResultsFromApi();
            }


        }


        else if(mode=="classfeed")
        {


            Spinner dept_spin = (Spinner) findViewById(R.id.spinner_class);

            sClass = String.valueOf(dept_spin.getSelectedItem());


            if (!sClass.equals("Class")) {
                mode = "sectionfeed";
                sectionName.clear();
                sectionName.add("Section");
                mOutputText.setText("");
                getResultsFromApi();
            }
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            String spreadsheetId = "1_sidOscZwfzcs8VaHk4UT5NVUEXGvq9VdDxKDh_xZFU";
            int a = 2;
            idAvailcheck = true;
            String range = "Class Data!".concat("A"+ a++ + ":J");

            List<List<Object>> arrData = getData(sId , sPassword ,sFName,sLName,sClass,sSection,sEmail,sPhone,"d",sSemester);

            ValueRange oRange = new ValueRange();
            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
            oRange.setValues(arrData);


            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);


            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);


            if(mode=="classfeed") {
                range = "Class Name!".concat("A" + a++ + ":S");
            }
            if(mode=="sectionfeed") {

                range = "Sections!".concat("A" + 1 + ":S");

            }


            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                Log.v("MainActivity", "Wofdad");

                results.add("");


                for (List row : values) {


                    if(mode=="classfeed")
                    {

                        String dept = String.valueOf(row.get(1));

                        if (dept.equals(sDepartment)) {

                            className.add(String.valueOf(row.get(0)));



                        }
                    }

                    if(mode=="sectionfeed") {
                        String dept = String.valueOf(row.get(1));

                        Spinner classSpinner = (Spinner) findViewById(R.id.spinner_class);
                        sClass = String.valueOf(classSpinner.getSelectedItem());
                        if (sClass.equals(dept)) {

                            sectionName.add(String.valueOf(row.get(0)));


                        }
                    }


                    if(mode=="Submit") {


                        String Str1 = String.valueOf(row.get(0));

                        if (Str1.equals("0")) {
                            Log.v("if", Str1);
                            Log.v("if", range);

                            break;
                        }

                        if (Str1.equals(sId)) {
                            Log.v(sId, Str1);
                            idAvailcheck = false;
                        }


                        range = "Class Data!".concat("A" + a++ + ":J");
                        Log.v("for", range);


                    }



                }

            }
            if(mode=="Submit") {
                oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
                if (idAvailcheck) {           // Check if id is not taken
                    BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();
                    range = "Class Data!".concat("A" + a++ + ":J");
                    arrData = getDataAsZero("0");
                    oRange.setValues(arrData);
                    oRange.setRange(range);
                    oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();
                } else {
                    Log.v("Not available", sId);


                }
            }



            return results;



        }




        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
            Log.v("details" , "Worked");


        }



        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
                Log.v("details" , "damn");
            } else {
                output.add(0, " ");
                mOutputText.setText(TextUtils.join("\n", output));
                Log.v("details" , "Wofdad21");

                if(mode=="classfeed")
                {
                    classList();
                }

                if(mode=="sectionfeed")
                {

                    sectionList();
                }
                displayAvailability();

            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            details.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                    Log.v("details" , "Worked2");

                    if(mode=="sectionfeed")
                    {

                        sectionList();
                    }
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

    private void Go(List<String> output) {
        mProgress.hide();
        if (output == null || output.size() == 0) {
            mOutputText.setText("No results returned.");
        } else {
            output.add(0, "Data retrieved using the Google Sheets API:");
            mOutputText.setText(TextUtils.join("\n", output));
            Log.v("details" , "Wofdad");

        }
    }

    public void onClick2(View v) {

        mOutputText.setText("");
        getResultsFromApi();


    }

    public static List<List<Object>> getData (String id , String pass, String firstName ,
                                              String lastName , String sClasse ,String Section, String
                                                      emailAddress , String phNO , String attendanceDateStart , String semester  )  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(id);
        data1.add(pass);
        data1.add (firstName);
        data1.add (lastName);
        data1.add (sClasse);
        data1.add(Section);
        data1.add (emailAddress);
        data1.add (phNO);
        data1.add(attendanceDateStart);
        data1.add(semester);



        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add (data1);

        return data;
    }

    public static List<List<Object>> getDataAsZero (String id)  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(id);




        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add (data1);

        return data;
    }

    public void submitInfo(View view) {

        EditText fName = (EditText) findViewById(R.id.Student_FName);
        EditText lName = (EditText) findViewById(R.id.Student_LName);
        Spinner spinnerClass = (Spinner) findViewById(R.id.spinner_class);
        Spinner spinnerSection = (Spinner) findViewById(R.id.spinner_section);
        Spinner spinnerDepartment = (Spinner) findViewById(R.id.spinner_department);
        Spinner spinnerSemester = (Spinner) findViewById(R.id.spinner_semesters);
        EditText Email = (EditText) findViewById(R.id.email_address);
        EditText Phone = (EditText) findViewById(R.id.Phone_no);
        EditText id = (EditText) findViewById(R.id.User_id);
        EditText pass = (EditText) findViewById(R.id.pass);
        EditText cPass = (EditText) findViewById(R.id.cpass);

      


        sPhone = String.valueOf(Phone.getText());
        sFName = String.valueOf(fName.getText());
        sLName = String.valueOf(lName.getText());
        sClass = String.valueOf(spinnerClass.getSelectedItem());
        sEmail = String.valueOf(Email.getText());
        sSection = String.valueOf(spinnerSection.getSelectedItem());
        sDepartment = String.valueOf(spinnerDepartment.getSelectedItem());
        sSemester = String.valueOf(spinnerSemester.getSelectedItem());
        sId = String.valueOf(id.getText());
        sPassword = String.valueOf(pass.getText());
        String sCPassword = String.valueOf(cPass.getText());

        if (sSemester.equals("First Semester")) {
            sSemester = "1";
        } else if (sSemester.equals("Second Semester")) {
            sSemester = "2";
        } else if (sSemester.equals("Third Semester")) {
            sSemester = "3";
        } else if (sSemester.equals("Fourth Semester")) {
            sSemester = "4";
        } else if (sSemester.equals("Fifth Semester")) {
            sSemester = "5";
        } else if (sSemester.equals("Sixth and Above Semesters")) {
            sSemester = "6";
        }
        
        if (sFName.length() >= 1) {
            if (sLName.length() >= 1) {
                if (sId.length() >= 6) {
                    if (sPassword.length() >= 8) {
                        if (sPassword.equals(sCPassword)) {
                            if (!sSemester.equals("Semester")){
                                if (!sDepartment.equals("Department")){
                                    if (!sClass.equals("Class")){
                                        if (!sSection.equals("Section")){
                                     if (sEmail.contains("@")) {
                                         if (sPhone.length() >= 10) {

                                    mode = "Submit";

                                    mOutputText.setText("");
                                    getResultsFromApi();


                                } else {

                                    Toast.makeText(getApplicationContext(), "Minimum size of Phone No is 10 characters",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                Toast.makeText(getApplicationContext(), "Invalid email",
                                        Toast.LENGTH_SHORT).show();
                            }
                                        } else {

                                            Toast.makeText(getApplicationContext(), "Choose a Section",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                            } else {

                                Toast.makeText(getApplicationContext(), "Choose a Class",
                                        Toast.LENGTH_SHORT).show();

                            }

                            } else {

                                Toast.makeText(getApplicationContext(), "Choose a Department",
                                        Toast.LENGTH_SHORT).show();

                            }

                        } else {

                            Toast.makeText(getApplicationContext(), "Choose a Semester",
                                    Toast.LENGTH_SHORT).show();

                        }

                    } else {

                        Toast.makeText(getApplicationContext(), "Password dont match",
                                Toast.LENGTH_SHORT).show();

                    }

                    } else {
                        Toast.makeText(getApplicationContext(), "Minimum size of Password is 8 characters",
                                Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Minimum size of id is 6 characters",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Enter Last Name",
                        Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(getApplicationContext(), "Enter First Name",
                    Toast.LENGTH_SHORT).show();
        }


    }

    public void displayAvailability() {


        if (!idAvailcheck) {
            Toast.makeText(getApplicationContext(), " Id already taken",
                    Toast.LENGTH_SHORT).show();
        }


    }

    public void classList()
    {


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, className);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner_class);



        sItems.setAdapter(adapter);
    }

    public void sectionList()
    {


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, sectionName);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner_section);



        sItems.setAdapter(adapter);
        mode = "classfeed";


    }

    public void onClickReselectDepartment(View v)
    {
        Spinner dept_spin = (Spinner) findViewById(R.id.spinner_department);
        dept_spin.setEnabled(true);
        dept_spin.setClickable(true);
        Button reselectDept = (Button) findViewById(R.id.reselect_dept);
        reselectDept.setClickable(false);
        reselectDept.setEnabled(false);
        reselectDept.setVisibility(View.INVISIBLE);
        mode = "department";
        Spinner spin = (Spinner) findViewById(R.id.spinner_department);
        spin.setOnItemSelectedListener(this);
    }

    public void onClickSignin (View v) {

        Intent selectIntent = new Intent(details.this,signin.class);
        startActivity(selectIntent);


    }

}