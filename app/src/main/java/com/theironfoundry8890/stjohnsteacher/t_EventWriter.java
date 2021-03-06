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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class t_EventWriter extends Activity
        implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;

    ProgressDialog mProgress;
    private TextView mOutputText;

    private String userId;
    private boolean idAvailcheck = true;

    long timestamp = System.currentTimeMillis() / 1000;




    //Event Details
    private String eventTitle;
    private String eventDescription;
    private String publishDate;
    private String eventDate;
    private String confirmPass;
    private String savedPass;
    private String savedId;

    private String eventDay;
    private String eventMonth;
    private String eventYear;

    private String fullName;

    private int a = 3;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;


    private String lastDateOfRegistration;
    private String lastDayOfRegistration;
    private String lastMonthOfRegistration;
    private String lastYearOfRegistration;

    private String entryfees;


    //Genres
    private boolean technology = false;
    private boolean socialGathering = false;
    private boolean debate = false;
    private boolean convention = false;
    private boolean socialAwareness = false;
    private boolean other = false;
    private boolean sports = true;

    private String sTechnology;
    private String sSocialAwareness;
    private String sSocialGathering;
    private String sOther ;
    private String sDebate ;
    private String sConvention ;

    private String subCataegories = "All Events";



    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS};

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_writer_layout);

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

        colorCheck();

        loadData();

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
            SharedPreferences mPrefs = getSharedPreferences("label", 0);userId = mPrefs.getString("UserId", "default_value_if_variable_not_found");String accountName = userId;
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
        Log.v("t_EventWriter" , "Success");
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
                t_EventWriter.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
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
            String spreadsheetId = sheetsIdCollection.getEventSheetId();
            int a = 2;
            idAvailcheck = true;
            String range = "Events!".concat("A"+ a++ + ":S");

            List<List<Object>> arrData;

            ValueRange oRange = new ValueRange();
            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW



            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);


            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);





            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                Log.v("MainActivity", "Wofdad");

                results.add("");


                for (List row : values) {




                    String Str1 = String.valueOf(row.get(0));

                    if (Str1.contains("BonBlank88"))
                    {


                        break;
                    }



                    range = "Events!".concat("A"+ a++ + ":S");
                    Log.v("for", range);






                }

            }
            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
            if(idAvailcheck) {           // Check if id is not taken
                long timestamp = System.currentTimeMillis() / 1000;

                arrData = getData(eventTitle , eventDescription ,
                        publishDate,eventDate,lastDateOfRegistration,
                        entryfees, savedId , subCataegories , fullName ,"A", String.valueOf(--a), String.valueOf(timestamp));
                oRange.setValues(arrData);
                BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();

                range = "Timestamp!B4:B";
                oRange.setRange(range);
                spreadsheetId = sheetsIdCollection.getMiscSheetId();
                arrData = getDataForTimeStamp(String.valueOf(timestamp));
                oRange.setValues(arrData);
                 oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();



            }
            else {



            }



            return results;



        }




        @Override
        protected void onPreExecute() {

            mProgress.show();
            Log.v("t_EventWriter" , "Worked");


        }



        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
                Log.v("t_EventWriter" , "damn");
            } else {
                output.add(0, " ");
                mOutputText.setText(TextUtils.join("\n", output));
                Log.v("t_EventWriter" , "Wofdad21");

                successfulRecord();
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
                            t_EventWriter.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                    Log.v("t_EventWriter" , "Worked2");
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
            Log.v("t_EventWriter" , "Wofdad");

        }
    }

    public void onClick2(View v) {


        getResultsFromApi();


    }

    public static List<List<Object>> getData (String pTitle , String pDesc, String pPublishDate , String pEventDate ,
                                              String pEndDateReg ,String pFees , String pId ,String cataegories , String fullN ,
                                              String status,String uniqueId ,String timestamp)  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(pTitle);
        data1.add(pDesc);
        data1.add (pPublishDate);
        data1.add (pEventDate);
        data1.add (pEndDateReg);
        data1.add(pFees);
        data1.add(cataegories);
        data1.add(pId);
        data1.add(fullN);
        data1.add(status);
        data1.add(uniqueId);
        data1.add(timestamp);





        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add (data1);

        return data;
    }


    public static List<List<Object>> getDataForTimeStamp (String timeStamp)  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(timeStamp);







        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add (data1);

        return data;
    }

    public void submitInfo(View view) {



        //Retriving data from layout
        EditText lEventTitle =  (EditText) findViewById(R.id.eventTitle);
        EditText lEventDesc =  (EditText) findViewById(R.id.eventDesc);
        DatePicker lLastdateofReg =  (DatePicker) findViewById(R.id.lastDateOfRegistration);
        DatePicker lDateEvent =  (DatePicker) findViewById(R.id.date_of_event);
        EditText lentryFees =  (EditText) findViewById(R.id.entryFees);
        CheckBox lGTechnology = (CheckBox) findViewById(R.id.gArt);
        CheckBox lGSocialGathering = (CheckBox) findViewById(R.id.gCommerce);
        CheckBox lGDebate = (CheckBox) findViewById(R.id.gManagement);
        CheckBox lGConvention = (CheckBox) findViewById(R.id.gScience);
        CheckBox lGSocialAwareness = (CheckBox) findViewById(R.id.gEducation);
        CheckBox lGSport = (CheckBox) findViewById(R.id.gSports);
        CheckBox lGOther = (CheckBox) findViewById(R.id.gOther);








        //Event Details
        eventTitle = String.valueOf(lEventTitle.getText());
        eventDescription = String.valueOf(lEventDesc.getText());
        entryfees = String.valueOf(lentryFees.getText());

        //Event Date of Registration Cancatenation
        eventDay = String.valueOf(lDateEvent.getDayOfMonth());
        eventMonth = String.valueOf(lDateEvent.getMonth() + 1);
        eventYear = String.valueOf(lDateEvent.getYear());
        eventDate = eventDay.concat("/" + eventMonth + "/" + eventYear);

        //Last Date of Registration Cancatenation
        lastDayOfRegistration = String.valueOf(lLastdateofReg.getDayOfMonth());
        lastMonthOfRegistration = String.valueOf(lLastdateofReg.getMonth() + 1);
        lastYearOfRegistration = String.valueOf(lLastdateofReg.getYear());
        lastDateOfRegistration = lastDayOfRegistration.concat("/" + lastMonthOfRegistration + "/" + lastYearOfRegistration);




        //Getting System date
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        Log.v("Date" , formattedDate);



        SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
        String formattedYear = yearformat.format(c.getTime());
        int formattedYearInt = Integer.parseInt(formattedYear);
        int eventYearInteger = Integer.parseInt(eventYear);
        int lastYearOfRegistrationInteger = Integer.parseInt(lastYearOfRegistration);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        String formattedMonth = monthFormat.format(c.getTime());
        int formattedMonthInt = Integer.parseInt(formattedMonth);
        int eventMonthInteger = Integer.parseInt(eventMonth);
        int lastMonthOfRegistrationInteger = Integer.parseInt(lastMonthOfRegistration);

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        String formattedDay = dayFormat.format(c.getTime());
        int formattedDayInt = Integer.parseInt(formattedDay);
        int eventDayInteger = Integer.parseInt(eventDay);
        int lastDayOfRegistrationInteger = Integer.parseInt(lastDayOfRegistration);


        boolean isDateOfEventPast = false;
        boolean isLastDateofRegistrationPast = false;



        if(eventYearInteger<formattedYearInt)
        {
            isDateOfEventPast = true;
        }
        if (eventMonthInteger < formattedMonthInt) {
            if(eventYearInteger <= formattedYearInt)
            {
                isDateOfEventPast = true;
            }
        }


        if (eventDayInteger < formattedDayInt) {
            if (eventMonthInteger <= formattedMonthInt) {
                if(eventYearInteger <= formattedYearInt){
                    isDateOfEventPast = true;

                }
            }
        }

        if(lastYearOfRegistrationInteger < formattedYearInt)
        {
            isLastDateofRegistrationPast = true;
        }

        if(lastMonthOfRegistrationInteger < formattedMonthInt)
        {
            if(lastYearOfRegistrationInteger <= formattedYearInt)
            {
                isLastDateofRegistrationPast = true;
            }
        }


        if (lastDayOfRegistrationInteger < formattedDayInt)
        {
            if(lastMonthOfRegistrationInteger <= formattedMonthInt)
            {
                if(lastYearOfRegistrationInteger <= formattedYearInt)
                {
                    isLastDateofRegistrationPast = true;
                }
            }
        }



        publishDate = formattedDate;

        //Checkboxes
        technology = lGTechnology.isChecked();
        socialAwareness = lGSocialAwareness.isChecked();
        socialGathering = lGSocialGathering.isChecked();
        other = lGOther.isChecked();
        debate = lGDebate.isChecked();
        convention = lGConvention.isChecked();
        sports =lGSport.isChecked();

        //Converting above booleans to String so they can be used in public static list parameters
        //To be deleted
        sTechnology = String.valueOf(technology);
        sSocialAwareness = String.valueOf(socialAwareness);
        sSocialGathering = String.valueOf(socialGathering);
        sOther = String.valueOf(other);
        sDebate = String.valueOf(debate);
        sConvention = String.valueOf(convention);






        if (eventTitle.length() >= 1) {
            if (eventDescription.length() >= 1) {
                if (entryfees.length() >= 1) {
                    if (!isDateOfEventPast) {
                        if (!isLastDateofRegistrationPast) {
                            if(technology || socialGathering || debate ||  convention || socialAwareness  || sports || other ) {

                                //Concatenating a string to check which cataegory is present
                                if(technology)
                                {
                                    subCataegories = subCataegories.concat("Technology");
                                }

                                if(socialAwareness)
                                {
                                    subCataegories = subCataegories.concat("Social Awareness");
                                }

                                if(socialGathering)
                                {
                                    subCataegories = subCataegories.concat("Social Gathering");
                                }

                                if(debate){
                                    subCataegories = subCataegories.concat("Debate");
                                }

                                if(convention){
                                    subCataegories = subCataegories.concat("Convention");
                                }

                                if(sports)
                                {
                                    subCataegories = subCataegories.concat("Sports");
                                }


                                if(other){
                                    subCataegories = subCataegories.concat("Other");
                                }



                                getResultsFromApi();

                            }else {
                                Toast.makeText(getApplicationContext(), "Atleast Choose One Genre",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }

                        else {

                            Toast.makeText(getApplicationContext(), "Last Date of Registration Can not Be in Past",
                                    Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Date of Event Can not Be in Past",
                                Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Enter entry fees",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Enter Last Name",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else
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

    public void loadData(){
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        String idString = mPrefs.getString("ttag", "default_value_if_variable_not_found");
        String passString = mPrefs.getString("tpass", "default_value_if_variable_not_found");
        String FirstName =  mPrefs.getString("tFirstName", "default_value_if_variable_not_found");
        String LastName =  mPrefs.getString("tLastName", "default_value_if_variable_not_found");

        fullName = FirstName.concat(" " + LastName);

        savedPass = passString;
        savedId = idString;
        Log.v(passString , idString);
    }



    private void swipe() {

        TextView head2 = (TextView) findViewById(R.id.head2);
        TextView head1 = (TextView) findViewById(R.id.head);

        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);



        if(a==0){

            Intent selectIntent = new Intent(t_EventWriter.this,t_Attendance.class);
            startActivity(selectIntent);

        }


        if(a==1) {

            Intent selectIntent = new Intent(t_EventWriter.this,t_Announcement_Viewer.class);
            startActivity(selectIntent);


        }

        if(a==2) {
            Intent selectIntent = new Intent(t_EventWriter.this,t_notes_Viewer.class);
            startActivity(selectIntent);
        }

        if(a==3) {
            Intent selectIntent = new Intent(t_EventWriter.this,EventViewer.class);
            startActivity(selectIntent);

        }

        if(a==4){
            Intent selectIntent = new Intent(t_EventWriter.this,Newsfeed.class);
            startActivity(selectIntent);

        }











    }

    public void onClickAttendance(View v)
    {
        Intent selectIntent = new Intent(t_EventWriter.this,t_Attendance.class);
        startActivity(selectIntent);


    }

    public void onClickAnnouncement(View v)
    {
        Intent selectIntent = new Intent(t_EventWriter.this,t_Announcement_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickNotes(View v)
    {
        Intent selectIntent = new Intent(t_EventWriter.this,t_notes_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickEvents(View v)
    {
        Intent selectIntent = new Intent(t_EventWriter.this,EventViewer.class);
        startActivity(selectIntent);


    }

    public void onClickProfile(View v)
    {
        Intent selectIntent = new Intent(t_EventWriter.this,Newsfeed.class);
        startActivity(selectIntent);


    }





    private void colorCheck() {

        ImageView attendanceImageView = (ImageView) findViewById(R.id.attendance);
        ImageView announcementImageView = (ImageView) findViewById(R.id.announcement);
        ImageView notesImageView = (ImageView) findViewById(R.id.notes);
        ImageView eventsImageView = (ImageView) findViewById(R.id.events);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile);


        if (a == 0) {

            attendanceImageView.setImageResource(R.drawable.attendance_grey);
            announcementImageView.setImageResource(R.drawable.announcements);
            notesImageView.setImageResource(R.drawable.notes);
            eventsImageView.setImageResource(R.drawable.events);
            profileImageView.setImageResource(R.drawable.newsfeed);

        }
        if (a == 1) {
            attendanceImageView.setImageResource(R.drawable.attendance);
            announcementImageView.setImageResource(R.drawable.announcements_grey);
            notesImageView.setImageResource(R.drawable.notes);
            eventsImageView.setImageResource(R.drawable.events);
            profileImageView.setImageResource(R.drawable.newsfeed);
        }

        if (a == 2) {
            attendanceImageView.setImageResource(R.drawable.attendance);
            announcementImageView.setImageResource(R.drawable.announcements);
            notesImageView.setImageResource(R.drawable.notes_grey);
            eventsImageView.setImageResource(R.drawable.events);
            profileImageView.setImageResource(R.drawable.newsfeed);
        }

        if (a == 3) {
            attendanceImageView.setImageResource(R.drawable.attendance);
            announcementImageView.setImageResource(R.drawable.announcements);
            notesImageView.setImageResource(R.drawable.notes);
            eventsImageView.setImageResource(R.drawable.events_grey);
            profileImageView.setImageResource(R.drawable.newsfeed);
        }

        if (a == 4) {
            attendanceImageView.setImageResource(R.drawable.attendance);
            announcementImageView.setImageResource(R.drawable.announcements);
            notesImageView.setImageResource(R.drawable.notes);
            eventsImageView.setImageResource(R.drawable.events);
            profileImageView.setImageResource(R.drawable.newsfeed_grey);
        }


    }


    public void successfulRecord()
    {
        Toast.makeText(this, "Record Successfully added", Toast.LENGTH_SHORT).show();
        Log.v("pre-Send","pre-Send");
        send_firebase_notification.sendGcmMessage(eventTitle,eventDescription,subCataegories,"event");
        Intent selectIntent = new Intent(t_EventWriter.this,EventViewer.class);
        startActivity(selectIntent);
    }

    public void setSheetIds()
    {
        String mode = "test";

        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();

        if(mode.equals("test")) {
            mEditor.putString("eventSheetId", "1tFhDy9sR9dlJ0jwNbqbcq3TnFpViMHJOi2xeOv_Wqqw").apply();
            mEditor.putString("notesSheetId", "1pAZtRVUuQFuGoUiWjiZRwXbrfju3ZcJgR0Lq6mBmmW0").apply();
            mEditor.putString("announcementSheetId", "1P0iFk6F9AHddLOM4N_8NbMVVByz671rbzDikJIbcsS0").apply();
            mEditor.putString("miscSheetId", "10PpNnvF4j5GNlbGrP4vPoPV8pQhix_9JP5kK9zlQDmY").apply();
            mEditor.putString("uploadedVideoInfoSheetId", "12C3ceqz_Fr7GmXpLxt-n4iMhbr86yluGqT4fno_CW-8").apply();
        }else if(mode.equals("release"))
        {
            mEditor.putString("eventSheetId", "1SC0UPYthsoS5NKDuC5oJt-y29__f0gm0wkIkJoDduWw").apply();
            mEditor.putString("notesSheetId", "1UDDtel5vAFBqVnaPZIZl20SwZEz_7fxGXYQOuKLvSmQ").apply();
            mEditor.putString("announcementSheetId", "116OBhXliG69OB5bKRAEwpmlOz21LCCWStniSuIR6wPI").apply();
            mEditor.putString("miscSheetId", "1nzKRlq7cQrI_XiJGxJdNax5oB91bR_SypiazWO2JTuU  ").apply();
            mEditor.putString("uploadedVideoInfoSheetId", "12C3ceqz_Fr7GmXpLxt-n4iMhbr86yluGqT4fno_CW-8").apply();
        }



    }

    public String  getEventSheetId()
    {
        setSheetIds();
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        return mPrefs.getString("eventSheetId", "default_value_if_variable_not_found");
    }

    public String  getAnnouncementSheetId()
    {
        setSheetIds();
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        return mPrefs.getString("announcementSheetId", "default_value_if_variable_not_found");
    }

    public String  getNoteSheetId()
    {
        setSheetIds();
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        return mPrefs.getString("notesSheetId", "default_value_if_variable_not_found");

    }

    public String  getMiscSheetId()
    {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        return mPrefs.getString("miscSheetId", "default_value_if_variable_not_found");

    }

    public String getUploadedVideoInfoSheetId()
    {
        setSheetIds();
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        return mPrefs.getString("uploadedVideoInfoSheetId", "default_value_if_variable_not_found");
    }


}