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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

public class t_Announcement_Writer extends Activity
        implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    private String sId;
    private TextView mOutputText;
    private String sPassword;private String userId;
    private boolean idAvailcheck = true;
    private boolean workbookEnd = false;

    private String fullName;

    private int a = 1;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    //Event Details
    private String eventTitle;
    private String eventDescription;
    private String publishDate;
    private String confirmPass;
    private String savedPass;
    private String savedId;

    //Subjects
    private boolean art = false;
    private boolean commerce = false;
    private boolean management = false;
    private boolean science = false;
    private boolean education= false;
    private boolean otherSubjects = false;

    //Semesters
    private boolean semesterOne = false;
    private boolean semesterTwo = false;
    private boolean semesterThree = false;
    private boolean semesterFour = false;
    private boolean semesterFive = false;
    private boolean semesterSix = false;

    private String subCataegories = "All Departments All Semesters";

    private String sArt;
    private String sEducation;
    private String sCommerce;
    private String sOtherSubjects ;
    private String sManagement ;
    private String sScience ;

    private int tableNo;
    private String gSavedAnnSheetId;

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

        setContentView(R.layout.t_announcement_writer);

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

        loadData();

        colorCheck();


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
            mOutputText.setText("No network connection available.");
            Toast.makeText(getApplicationContext(), "No Network Connection",
                    Toast.LENGTH_SHORT).show();
        } else {

            new MakeRequestTask(mCredential).execute();

        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherSubjectswise an account
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
     * @return true if the device has a network connection, false otherSubjectswise.
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
     *     date on this device; false otherSubjectswise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
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
                t_Announcement_Writer.this,
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
            String spreadsheetId = sheetsIdCollection.getAnnouncementSheetId();
            int a = 2;
            idAvailcheck = true;
            String range =  "Stj Teacher Notes!".concat("A"+ a++ + ":S");

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


                results.add("");


                for (List row : values) {




                    String Str1 = String.valueOf(row.get(0));

                    if (Str1.contains("BonBlank88"))
                    {


                        break;
                    }

                    if (Str1.equals(sId))
                    {

                        idAvailcheck = false;
                    }


                    range =  "Stj Teacher Notes!".concat("A"+ a++ + ":S");

                }

            }
            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
            if(idAvailcheck) {           // Check if id is not taken
                long timestamp = System.currentTimeMillis() / 1000;
                //Getting System date
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = df.format(c.getTime());
                arrData = getData(eventTitle , eventDescription ,subCataegories, savedId , fullName ,
                        String.valueOf(--a),formattedDate,"None", String.valueOf(timestamp));

                SharedPreferences mPrefs = getSharedPreferences("label", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("savedCheckBoxesAnnouncements", subCataegories).apply();


                oRange.setValues(arrData);
                BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();

                range = "Timestamp!B2:B";
                oRange.setRange(range);
                spreadsheetId =  sheetsIdCollection.getMiscSheetId();
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
            mOutputText.setText("");
            mProgress.show();



        }



        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");

            } else {
                output.add(0, " ");
                mOutputText.setText(TextUtils.join("\n", output) );

                successfulRecord();


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
                            t_Announcement_Writer.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());

                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

    public void onClick2(View v) {

        mOutputText.setText("");
        getResultsFromApi();


    }


    public void onClickAttachFiles(View v) {

        Uri webpage = Uri.parse("https://stormy-bayou-35005.herokuapp.com/androidAnnouncementWriter");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(webIntent);


    }


    public static List<List<Object>> getData (String pTitle , String pDesc,
                                              String pCataegories , String pId
            ,String fullN,String uniqueId,String pDate,String fileUrl,String timestamp)  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(pTitle);
        data1.add(pDesc);
        data1.add(pCataegories);
        data1.add(pId);
        data1.add(fullN);
        data1.add(uniqueId);
        data1.add(pDate);
        data1.add(fileUrl);
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


        CheckBox lGArt = (CheckBox) findViewById(R.id.gArt);
        CheckBox lGCommerce = (CheckBox) findViewById(R.id.gCommerce);
        CheckBox lGManagement = (CheckBox) findViewById(R.id.gManagement);
        CheckBox lGScience = (CheckBox) findViewById(R.id.gScience);
        CheckBox lGEducation = (CheckBox) findViewById(R.id.gEducation);
        CheckBox lGOther = (CheckBox) findViewById(R.id.gOther);

        CheckBox lSemster1 = (CheckBox) findViewById(R.id.gSemester1);
        CheckBox lSemster2 = (CheckBox) findViewById(R.id.gSemester2);
        CheckBox lSemster3 = (CheckBox) findViewById(R.id.gSemester3);
        CheckBox lSemster4 = (CheckBox) findViewById(R.id.gSemester4);
        CheckBox lSemster5 = (CheckBox) findViewById(R.id.gSemester5);
        CheckBox lSemster6 = (CheckBox) findViewById(R.id.gSemester6);










        //Event Details
        eventTitle = String.valueOf(lEventTitle.getText());
        eventDescription = String.valueOf(lEventDesc.getText());







        //Getting System date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());


        publishDate = formattedDate;

        //Checkboxes for Genre
        art = lGArt.isChecked();
        education= lGEducation.isChecked();
        commerce = lGCommerce.isChecked();
        otherSubjects = lGOther.isChecked();
        management = lGManagement.isChecked();
        science = lGScience.isChecked();

        //Checkboxes for Semesters
        semesterOne = lSemster1.isChecked();
        semesterTwo= lSemster2.isChecked();
        semesterThree = lSemster3.isChecked();
        semesterFour = lSemster4.isChecked();
        semesterFive = lSemster5.isChecked();
        semesterSix = lSemster6.isChecked();

        //Converting above booleans to String so they can be used in public static list parameters
        sArt = String.valueOf(art);
        sEducation = String.valueOf(education);
        sCommerce = String.valueOf(commerce);
        sOtherSubjects = String.valueOf(otherSubjects);
        sManagement = String.valueOf(management);
        sScience = String.valueOf(science);








        if (eventTitle.length() >= 1) {
            if (eventDescription.length() >= 1) {
                if (eventDescription.length() <= 49000) {
                    if (true) {
                        if (true) {
                            if(art || commerce || management ||  science || education|| otherSubjects ) {
                                if(semesterOne || semesterTwo || semesterThree || semesterFour || semesterFive || semesterSix) {


                                    if (art) {
                                        subCataegories = subCataegories.concat("Art");
                                    }

                                    if (commerce) {
                                        subCataegories = subCataegories.concat("Commerce");
                                    }

                                    if (education) {
                                        subCataegories = subCataegories.concat("Education");
                                    }

                                    if (management) {
                                        subCataegories = subCataegories.concat("Management");
                                    }

                                    if (science) {
                                        subCataegories = subCataegories.concat("Science");
                                    }

                                    if (otherSubjects) {
                                        subCataegories = subCataegories.concat("Other");
                                    }

                                    if (semesterOne) {
                                        subCataegories = subCataegories.concat("First Semester");
                                    }

                                    if (semesterTwo) {
                                        subCataegories = subCataegories.concat("Second Semester");
                                    }

                                    if (semesterThree) {
                                        subCataegories = subCataegories.concat("Third Semester");
                                    }

                                    if (semesterFour) {
                                        subCataegories = subCataegories.concat("Fourth Semester");
                                    }

                                    if (semesterFive) {
                                        subCataegories = subCataegories.concat("Fifth Semester");
                                    }

                                    if (semesterSix) {
                                        subCataegories = subCataegories.concat("Sixth and Above Semesters");
                                    }

                                    mOutputText.setText("");
                                    getResultsFromApi();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Atleast Choose One Semester",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(), "Atleast Choose One Department",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }

                        else {

                            Toast.makeText(getApplicationContext(), "Password dont match",
                                    Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Minimum size of Password is 8 characters",
                                Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Notes field character limit is 49000",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Notes field is blank",
                        Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(getApplicationContext(), "Concept field is blank",
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

        String savedCheckboxes =  mPrefs.getString("savedCheckBoxesAnnouncements", "default_value_if_variable_not_found");

        loadCheckBoxes(savedCheckboxes);
        fullName = FirstName.concat(" " + LastName);


        savedPass = passString;
        savedId = idString;

    }
    
    




    public void onClickAttendance(View v)
    {
        Intent selectIntent = new Intent(t_Announcement_Writer.this,t_Attendance.class);
        startActivity(selectIntent);


    }

    public void onClickAnnouncement(View v)
    {
        Intent selectIntent = new Intent(t_Announcement_Writer.this,t_Announcement_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickNotes(View v)
    {
        Intent selectIntent = new Intent(t_Announcement_Writer.this,t_notes_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickEvents(View v)
    {
        Intent selectIntent = new Intent(t_Announcement_Writer.this,EventViewer.class);
        startActivity(selectIntent);


    }

    public void onClickProfile(View v)
    {
        Intent selectIntent = new Intent(t_Announcement_Writer.this,Newsfeed.class);
        startActivity(selectIntent);


    }


    private void swipe() {

        TextView head2 = (TextView) findViewById(R.id.head2);
        TextView head1 = (TextView) findViewById(R.id.head);

        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);



        if(a==0){

            Intent selectIntent = new Intent(t_Announcement_Writer.this,t_Attendance.class);
            startActivity(selectIntent);

        }


        if(a==1) {

            Intent selectIntent = new Intent(t_Announcement_Writer.this,t_Announcement_Viewer.class);
            startActivity(selectIntent);


        }

        if(a==2) {
            Intent selectIntent = new Intent(t_Announcement_Writer.this,t_notes_Viewer.class);
            startActivity(selectIntent);
        }

        if(a==3) {
            Intent selectIntent = new Intent(t_Announcement_Writer.this,EventViewer.class);
            startActivity(selectIntent);

        }

        if(a==4){
            Intent selectIntent = new Intent(t_Announcement_Writer.this,Newsfeed.class);
            startActivity(selectIntent);

        }









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






        send_firebase_notification.sendGcmMessage(eventTitle,eventDescription,subCataegories,"announcement");



        Intent selectIntent = new Intent(t_Announcement_Writer.this,t_Announcement_Viewer.class);
        startActivity(selectIntent);
    }


    public void  loadCheckBoxes(String saveKey)
    {

        String savedCheckBoxes = saveKey;
        if(savedCheckBoxes != null)
        {
            subCataegories = savedCheckBoxes;
            String[] departmentCollection = new String[6];
            String outputTopic =  "";
            int arrayIncrementer = 0;
            departmentCollection[arrayIncrementer] = "Art";
            departmentCollection[++arrayIncrementer] = "Commerce";
            departmentCollection[++arrayIncrementer] = "Management";
            departmentCollection[++arrayIncrementer] = "Education";
            departmentCollection[++arrayIncrementer] = "Science";
            departmentCollection[++arrayIncrementer] = "Other";

            String[] semesterCollection = new String[6];
            arrayIncrementer = 0;
            semesterCollection[arrayIncrementer] = "First Semester";
            semesterCollection[++arrayIncrementer] = "Second Semester";
            semesterCollection[++arrayIncrementer] = "Third Semester";
            semesterCollection[++arrayIncrementer] = "Fourth Semester";
            semesterCollection[++arrayIncrementer] = "Fifth Semester";
            semesterCollection[++arrayIncrementer] = "Sixth and Above Semesters";

            CheckBox lGArt = (CheckBox) findViewById(R.id.gArt);
            CheckBox lGCommerce = (CheckBox) findViewById(R.id.gCommerce);
            CheckBox lGManagement = (CheckBox) findViewById(R.id.gManagement);
            CheckBox lGScience = (CheckBox) findViewById(R.id.gScience);
            CheckBox lGEducation = (CheckBox) findViewById(R.id.gEducation);
            CheckBox lGOther = (CheckBox) findViewById(R.id.gOther);

            CheckBox lSemster1 = (CheckBox) findViewById(R.id.gSemester1);
            CheckBox lSemster2 = (CheckBox) findViewById(R.id.gSemester2);
            CheckBox lSemster3 = (CheckBox) findViewById(R.id.gSemester3);
            CheckBox lSemster4 = (CheckBox) findViewById(R.id.gSemester4);
            CheckBox lSemster5 = (CheckBox) findViewById(R.id.gSemester5);
            CheckBox lSemster6 = (CheckBox) findViewById(R.id.gSemester6);


            if (subCataegories.contains("Art")) {
            lGArt.setChecked(true);
            }  if (subCataegories.contains("Commerce")) {
            lGCommerce.setChecked(true);
        }  if (subCataegories.contains("Management")) {
            lGManagement.setChecked(true);
        }  if (subCataegories.contains("Education")) {
            lGEducation.setChecked(true);
        }  if (subCataegories.contains("Science")) {
            lGScience.setChecked(true);
        }  if (subCataegories.contains("Other")) {
            lGOther.setChecked(true);
        }



            if (subCataegories.contains("First Semester")) {
                lSemster1.setChecked(true);
            }  if (subCataegories.contains("Second Semester")) {
            lSemster2.setChecked(true);
        }  if (subCataegories.contains("Third Semester")) {
            lSemster3.setChecked(true);
        }  if (subCataegories.contains("Fourth Semester")) {
            lSemster4.setChecked(true);
        }  if (subCataegories.contains("Fifth Semester")) {
            lSemster5.setChecked(true);
        }  if (subCataegories.contains("Sixth and Above Semesters")) {
            lSemster6.setChecked(true);
        }


        }
    }


}
  
  