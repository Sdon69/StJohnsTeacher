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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class t_Attendance extends Activity
        implements  AdapterView.OnItemSelectedListener , EasyPermissions.PermissionCallbacks{
    GoogleAccountCredential mCredential;
    private Button mCallApiButton;
    ProgressDialog mProgress;
    private String sFName;
    private String sLName;
    private String sClass;
    private String sEmail;
    private String sSection;
    private String sId = "hello";
    private String sPhone;
    private TextView mOutputText;
    private String sPassword;
    private boolean idAvailcheck = true;
    private String buttonMode = "remove";

    private String listmode = "add";

    private String doneRange;



    private String randomOtp;

    private Spinner subjectSpinner;

    public final static int PERM_REQUEST_CODE_DRAW_OVERLAYS = 1234;

    private int ai = 1;
    private int totalStudents ;
    private String ifPresent = "true";

    private String mode = "subjectfeed";

    private String attendanceClass;
    private String attendanceSection;
    private String attendancefullName;

    private String SpinnerClass;
    private String SpinnerSection;

    private String subject_filter;
    private String semester_filter;
    private String class_filter;
    private String section_filter;

    private int a = 0;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    private ArrayList ids = new ArrayList();
    private ArrayList addedStudents = new ArrayList<students>();
    private ArrayList subjects = new ArrayList();

    private ArrayList className = new ArrayList();

    private ArrayList sectionName = new ArrayList();

    private  Student_add_Adapter arrayAdapter;
    private AttendanceAdapter adapter;





    private String title;
    private String description;
    private String publishDate;
    private String eventDate;
    private String lastDateofRegistration;
    private String fees;
    final ArrayList<Word> words = new ArrayList<Word>();
    final ArrayList<students> studentsAdd = new ArrayList<students>();

    private String dFName;
    private String dLName;
    private String dClass;
    private String dEmail;
    private String dSection;
    private String dId;
    private String dPassword;
    private String dPhone;
    private List dRow;

    private String cataegories;

    private String dept_filter ;

    private boolean end = true;

    private boolean spinnerSelect = false;


    private String attendanceDate;
    private String attendanceDay;
    private String attendanceMonth;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS_READONLY};

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subjects.add("Subject");
        className.add("Class");


        setContentView(R.layout.t_att_department_chooser);

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




        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        loadData();




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
            String accountName = "suyashdoneria@gmail.com";
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
        Log.v("t_Attendance" , "Success");
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
                t_Attendance.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner dept_spin = (Spinner) findViewById(R.id.spinner_class);
        class_filter = String.valueOf(dept_spin.getSelectedItem());
        Toast.makeText(getApplicationContext(), class_filter,
                Toast.LENGTH_SHORT).show();

        if (!class_filter.equals("Class")) {
            mode = "sectionfeed";

            getResultsFromApi();
        }

    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

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
            String spreadsheetId = "1qtzLH2JKOURnvIrc3Q-mlCfQLHtfNbUmZnAiRGJWHBM";
            int a = 1;
            int otpIncrementer = 1;
            idAvailcheck = true;
            String range = "Class Data!".concat("A"+ a++ + ":S");
            end = true;
            int attDays = 2;



            List<List<Object>> arrData = getData("Hello");

            ValueRange oRange = new ValueRange();
            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
            oRange.setValues(arrData);


            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);


            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);


            if(mode=="subjectfeed") {
                spreadsheetId = "1nzKRlq7cQrI_XiJGxJdNax5oB91bR_SypiazWO2JTuU"; range = "Subjects!".concat("A" + 1 + ":S");
            }
            if(mode=="classfeed") {
                spreadsheetId = "1nzKRlq7cQrI_XiJGxJdNax5oB91bR_SypiazWO2JTuU"; range = "Class Name!".concat("A" + a++ + ":S");
            }
            if(mode=="sectionfeed") {
                spreadsheetId = "1nzKRlq7cQrI_XiJGxJdNax5oB91bR_SypiazWO2JTuU"; range = "Sections!".concat("A" + 1 + ":S");
                sectionName.add("Section");
            }
            if(mode=="otp")
            {
                spreadsheetId = "1nzKRlq7cQrI_XiJGxJdNax5oB91bR_SypiazWO2JTuU";  range =  "Sections!".concat("A" + 1 + ":S");
                Log.v("mode", mode);
            }
            if(mode=="doneotp")
            {
                range = doneRange;

            }

            if(mode=="incrementDays")
            {
                Log.v("hellomoto","hellomoto");
                spreadsheetId = "1jXGefIFb2w8_0FMtrqSB0iTrnn_PQMt9Zp70WIJ1F7U"; range = "Attendance Total Days!".concat("A" + attDays + ":S");
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



                    if(mode=="incrementDays")
                    {
                        String className = String.valueOf(row.get(0));

                        Log.v("className",className);
                        if(className.equals("BonBlank88")) {
                            Log.v("affected",className);

                            arrData = getDataForTotalDays(class_filter,section_filter,subject_filter,semester_filter,attendanceDate,"1");
                            range = "Attendance Total Days!".concat("A" + attDays + ":J");
                            oRange.setValues(arrData);
                            oRange.setRange(range);
                            BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();
                            range = "Attendance Total Days!".concat("A" + ++attDays + ":J");
                            arrData = getDataAsZero("BonBlank88");
                            oRange.setValues(arrData);
                            oRange.setRange(range);
                            oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();


                        }

                        else if(!className.equals("BonBlank88")) {
                            if(className.equals(class_filter)) {
                                String section = String.valueOf(row.get(1));
                                if(section.equals(section_filter)) {
                                    String subject = String.valueOf(row.get(2));
                                    if(subject.equals(subject_filter)) {
                                        String semester = String.valueOf(row.get(3));
                                        if(semester.equals(semester_filter)) {
                                            String lastDate = String.valueOf(row.get(4));
                                            if(!lastDate.equals(attendanceDate)) {

                                                int currentDays = Integer.parseInt(String.valueOf(row.get(5)));


                                                range = "Attendance Total Days!".concat("E" + attDays + ":J");
                                                arrData = getDataToIncrementDaysDate(attendanceDate,String.valueOf(++currentDays));
                                                oRange.setValues(arrData);
                                                oRange.setRange(range);
                                                BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();
                                                break;
                                            }
                                            else {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            range = "Attendance Total Days!".concat("A" + attDays++ + ":J");

                        }




                    }





                    SpinnerClass = "BBA";
                    SpinnerSection = "B";
                    String dept = String.valueOf(row.get(1));
                    String fcell = String.valueOf(row.get(0));

                    boolean doneotp = true;
                    if(mode.equals("doneotp"))
                    {
                        Random rand = new Random();
                        int otpNumber = rand.nextInt(999) + 100;
                        randomOtp = (String.valueOf(otpNumber));
                        arrData = getData(randomOtp);



                        oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
                        oRange.setValues(arrData);
                        BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();
                        break;
                    }
                    if(mode.equals("otp"))
                    {
                        Log.v(class_filter , dept);
                        Log.v(section_filter , fcell);
                        range =  "Sections!".concat("C" + otpIncrementer++ + ":S");

                        if(fcell.equals(section_filter) && dept.equals(class_filter))
                        {

                            doneRange = range;

                            Calendar c = Calendar.getInstance();

                            SimpleDateFormat df = new SimpleDateFormat("dd");

                            String formattedDate = df.format(c.getTime());

                            SimpleDateFormat month = new SimpleDateFormat("dd-MMM-yyyy");

                            String formattedDateMonth = df.format(c.getTime());

                            df = new SimpleDateFormat("MMM");
                            formattedDate = df.format(c.getTime());

                            String formattedYear = df.format(c.getTime());

                            df = new SimpleDateFormat("yyyy");
                            formattedYear = df.format(c.getTime());


                            attendanceDay = formattedDateMonth; //Ignore name
                            attendanceMonth = formattedDate;
                            attendanceDate = attendanceDay.concat(attendanceMonth + formattedYear);

                            Random rand = new Random();
                            int otpNumber = rand.nextInt(999) + 100;
                            randomOtp = (String.valueOf(otpNumber));
                            arrData = getDataForOtp(randomOtp, attendanceDate.concat(subject_filter));


                            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
                            oRange.setValues(arrData);
                            BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();
                            Log.v("Execute", "Batch0");
                        }
                    }




                    if(mode=="sectionfeed") {
                        // if (fcell.equals("Subject"))
                        //{
                        //  mode = "subjectfeed";
                        //}
                        Spinner classSpinner = (Spinner) findViewById(R.id.spinner_class);
                        class_filter = String.valueOf(classSpinner.getSelectedItem());
                        if(class_filter.equals(dept)) {
                            Log.v("ClassName", dept);
                            sectionName.add(String.valueOf(row.get(0)));
                            Log.v("Section Name", String.valueOf(sectionName));
                        }



                    }



                    if(mode=="subjectfeed") {
                        Log.v("Department", dept_filter);
                        if (dept.equals(dept_filter)) {

                            subjects.add(String.valueOf(row.get(0)));
                            Log.v("Ss", String.valueOf(subjects));


                        }
                    }

                    if(mode=="classfeed")
                    {
                        if (dept.equals(dept_filter)) {

                            className.add(String.valueOf(row.get(0)));
                            Log.v("Ss", String.valueOf(subjects));


                        }
                    }

                   /* if(mode=="otp")
                    {
                        Log.v("otp", "worked");

                        Log.v(fcell , section_filter);

                        Log.v("otp2", "worked2");

                        Log.v(dept , class_filter);
                        if(fcell == section_filter && dept == class_filter)
                        {
                            arrData = getData("8890");
                            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
                            range =  "Sections!".concat("C" + otpIncrementer + ":S");
                            oRange.setValues(arrData);
                            BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();
                            Log.v("Execute", "Batch0");
                        }
                    }*/


                    if(mode=="attendancetick")
                    {

                        attendanceClass = String.valueOf(row.get(4));
                        attendanceSection = String.valueOf(row.get(5));
                        String attendanceSemester = String.valueOf(row.get(9));
                        Log.v("Semester",attendanceSemester);

                        if (attendanceSemester.equals("0"))
                        {
                            break;
                        }
                        if(attendanceClass.equals(class_filter)) {
                            if(attendanceSemester.equals(semester_filter))
                            {

                                if(attendanceSection.equals(section_filter)) {
                                    String attendanceFirstName = String.valueOf(row.get(2));
                                    String attendanceLastName = String.valueOf(row.get(3));
                                    attendancefullName = attendanceFirstName.concat(" " + attendanceLastName);
                                    dId = String.valueOf(row.get(0));

                                    Log.v(attendanceClass, attendanceSection);


                                    Log.v("Full Name", attendancefullName);



                                    words.add(new Word(attendancefullName,dId));

                                    Log.v("tick2", String.valueOf(words));
                                }
                            }
                        }

                    }




                    if(mode=="attendancefeed")
                    {


                        Log.v("array", String.valueOf(attendanceDate));




                        Log.v("id3s", String.valueOf(ids));


                        dRow = row;

                        String attCheckid = String.valueOf(row.get(0));

                        String serverExistingDate =  String.valueOf(row.get(8));



                        if(!serverExistingDate.contains(attendanceDate)) {
                            if (ids.contains(attCheckid)) {
                                Log.v("Execute", "Batch-1");
                                if (serverExistingDate != null) {
                                    arrData = getData(serverExistingDate.concat(attendanceDate + subject_filter));
                                } else {
                                    arrData = getData(attendanceDate);
                                }
                                Log.v("Execute", "Batch-1");


                                oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
                                oRange.setValues(arrData);
                                BatchUpdateValuesResponse oResp1 = mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();
                                Log.v("Execute", "Batch0");
                            }
                        }

                        range = "Class Data!".concat("I" + a++ + ":S"); //Sets from which column to prints , in this case I

                    }

                }

            }




            return results;



        }





        @Override
        protected void onPreExecute() {

            mProgress.show();
            Log.v("t_Attendance" , "Worked");


        }



        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
                Log.v("t_Attendance" , "damn");
            } else {
                output.add(0, " ");
                mOutputText.setText(TextUtils.join("\n", output));
                Log.v("t_Attendance" , "Wofdad21");
                if(mode=="subjectfeed") {
                    subjectList();
                }
                if(mode=="attendancetick") {
                    EventList();
                    totalStudents = words.size();
                    TextView totalTextView = (TextView) findViewById(R.id.sTotal);
                    totalTextView.setText(String.valueOf(words.size()));
                }
                if(mode=="classfeed")
                {
                    classList();
                }
                if(mode=="sectionfeed")
                {

                    sectionList();
                }
                if(mode=="otp")
                {

                    TextView otpTextView = (TextView) findViewById(R.id.otp);

                    otpTextView.setText(randomOtp);
                }
                if(mode=="doneotp")
                {
                    successfulRecord();
                    Intent selectIntent = new Intent(t_Attendance.this,t_Attendance.class);
                    startActivity(selectIntent);

                }
                if(mode=="attendancefeed")
                {

                    successfulRecord();
                    Intent selectIntent = new Intent(t_Attendance.this,t_Attendance.class);
                    startActivity(selectIntent);
                }
                if(mode=="incrementDays")
                {
                    mode = "attendancefeed";


                    getResultsFromApi();
                }




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
                            t_Attendance.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                    Log.v("t_Attendance" , "Worked2");

                    if(mode=="sectionfeed")
                    {
                        sectionList();
                    }

                    if(mode=="attendancefeed")
                    {

                        successfulRecord();
                        Intent selectIntent = new Intent(t_Attendance.this,t_Attendance.class);
                        startActivity(selectIntent);
                    }

                    if(mode=="attendancetick") {
                        EventList();
                        totalStudents = words.size();
                        TextView totalTextView = (TextView) findViewById(R.id.sTotal);
                        totalTextView.setText(String.valueOf(words.size()));
                    }
                    if(mode=="incrementDays")
                    {
                        mode = "attendancefeed";


                        getResultsFromApi();
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
            Log.v("t_Attendance"   , "Wofdad");

        }
    }

    public void onClick2(View v) {




        getResultsFromApi();


    }

    public void onClickDone(View v)
    {

        mode = "doneotp";



        getResultsFromApi();

    }

    public static List<List<Object>> getData (String datespresent)  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(datespresent);





        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add (data1);

        return data;
    }

    public static List<List<Object>> getDataForOtp (String otp , String subject )  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(otp);
        data1.add(subject);





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

    public static List<List<Object>> getDataToIncrementDaysDate (String lastDate , String totalDays)  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(lastDate);
        data1.add(totalDays);




        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add (data1);

        return data;
    }

    public static List<List<Object>> getDataForTotalDays (String className,String section , String subject ,String semester ,String currentDate,String days)  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(className);
        data1.add(section);
        data1.add(subject);
        data1.add(semester);
        data1.add(currentDate);
        data1.add(days);




        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add (data1);

        return data;
    }


    public void subjectList()
    {


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, subjects);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner_subjects);



        sItems.setAdapter(adapter);

        mode = "classfeed";

        getResultsFromApi();

    }

    public void classList()
    {


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, className);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner_class);



        sItems.setAdapter(adapter);
    }

    public void successfulRecord()
    {
        Toast.makeText(this, "Record Successfully added", Toast.LENGTH_SHORT).show();
    }

    public void sectionList()
    {


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, sectionName);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner_section);



        sItems.setAdapter(adapter);
        mode = "Submit";


    }








    public void displayStudentInfo()
    {
        TextView fName = (TextView) findViewById(R.id.pFirstName);
        TextView lName = (TextView) findViewById(R.id.pLastName);
        TextView tclass = (TextView) findViewById(R.id.pClass);
        TextView section = (TextView) findViewById(R.id.pSection);
        TextView Email = (TextView) findViewById(R.id.pEmail);
        TextView Phone = (TextView) findViewById(R.id.pPhoneNo);
        TextView id = (TextView) findViewById(R.id.pUser_id);


        fName.setText(dFName);
        lName.setText(dLName);
        tclass.setText(dClass);
        section.setText(dSection);
        Email.setText(dEmail);
        Phone.setText(dPhone);
        id.setText(dId);

    }


    public void saveData(){


        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("tag", sId).commit();
        Log.v("Saved data" , sId);
    }

    public void loadData(){
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        String idString = mPrefs.getString("tag", "default_value_if_variable_not_found");
        String passString = mPrefs.getString("tag", "default_value_if_variable_not_found");
        sId = idString;
        sPassword = passString;



    }

    public void EventList(){










        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // adapter knows how to create list items for each item in the list.
        adapter = new AttendanceAdapter(this, words);


        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // activity_numbers.xml layout file.
        final ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);



        end = true;

        if(end) {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {






                    Log.v("listmode",listmode);



                    if(listmode=="add") {
                        //Get on clicked data
                        Word word = words.get(position);
                        String Studentid = word.getEventDate();


                        if (!ids.contains(Studentid)) { // So that existing data cant be added into the array // after Change 22/10/2017 23:41 this conditional statement quite useless
                            // as it just removes the clicked list item
                            ids.add(Studentid);


                            words.remove(position);  // Change 22/10/2017 23:41
                            studentsAdd.add(new students(word.getDefaultTranslation(),word.getEventDate()));


                            adapter.notifyDataSetChanged();
                            adapter.notifyDataSetInvalidated();

                            TextView presentTextView = (TextView) findViewById(R.id.sPresent);

                            presentTextView.setText(String.valueOf(ids.size()));


                            Log.v("word size", String.valueOf(words.size()));


                        }

                        if (words.size() == 0) {
                            Log.v("end", "end");
                        }



                    }

                    if(listmode=="remove")
                    {

                        students st = studentsAdd.get(position);
                        studentsAdd.remove(position);  // Change 22/10/2017 23:41
                        words.add(new Word(st.getDefaultTranslation(),st.getEventDate()));
                        ids.remove(st.getEventDate());
                        arrayAdapter.notifyDataSetChanged();
                        arrayAdapter.notifyDataSetInvalidated();
                    }



                }


            });
        }
    }

    public void onClickFilter(View v) {

        mode = "attendancetick";
        subjectSpinner = (Spinner) findViewById(R.id.spinner_subjects);
        subject_filter = String.valueOf(subjectSpinner.getSelectedItem());
        Spinner semSpinner = (Spinner) findViewById(R.id.spinner_semesters);
        String semester = String.valueOf(semSpinner.getSelectedItem());
        Spinner classSpinner = (Spinner) findViewById(R.id.spinner_class);
        class_filter = String.valueOf(classSpinner.getSelectedItem());
        Spinner sectionSpinner = (Spinner) findViewById(R.id.spinner_section);
        section_filter = String.valueOf(sectionSpinner.getSelectedItem());

        if (!semester.equals("Semester")) {
            if (!class_filter.equals("Class")) {

                if (!section_filter.equals("Section")) {
                    if (!subject_filter.equals("Subject")) {


                        if (semester.equals("First Semester")) {
                            semester_filter = "1";
                        } else if (semester.equals("Second Semester")) {
                            semester_filter = "2";
                        } else if (semester.equals("Third Semester")) {
                            semester_filter = "3";
                        } else if (semester.equals("Fourth Semester")) {
                            semester_filter = "4";
                        } else if (semester.equals("Fifth Semester")) {
                            semester_filter = "5";
                        } else if (semester.equals("Sixth and Above Semesters")) {
                            semester_filter = "6";
                        } 

                        setContentView(R.layout.atendance_retrieve_auto);





                        getResultsFromApi();

                    }else{
                        Toast.makeText(this, "Please Choose a Subject", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    Toast.makeText(this, "Please Choose a Section", Toast.LENGTH_SHORT).show();

                }

            }else{
                Toast.makeText(this, "Please Choose a Class", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Please Choose a Semester", Toast.LENGTH_SHORT).show();
        }


    }



    public void onClickSubmit(View v){

        System.out.println("Contents of al: " + ids);

        mode = "incrementDays";

     //   mode = "attendancefeed";

        Log.v("mode",mode);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd");

        String formattedDate = df.format(c.getTime());

        SimpleDateFormat month = new SimpleDateFormat("dd-MMM-yyyy");

        String formattedDateMonth = df.format(c.getTime());

        df = new SimpleDateFormat("MMM");
        formattedDate = df.format(c.getTime());

        String formattedYear = df.format(c.getTime());

        df = new SimpleDateFormat("yyyy");
        formattedYear = df.format(c.getTime());


        attendanceDay = formattedDateMonth; //Ignore name
        attendanceMonth = formattedDate;
        attendanceDate = attendanceDay.concat(attendanceMonth + formattedYear);





        Log.v("mode", attendanceDay.concat(attendanceMonth));



        Log.v("id2s", String.valueOf(ids));

        TextView absentTextView = (TextView) findViewById(R.id.sAbsent);
        int present = ids.size();
        Log.v("present" , String.valueOf(present));
        Log.v("total" , String.valueOf(totalStudents));


        absentTextView.setText(String.valueOf(totalStudents - present));




        getResultsFromApi();
    }

    public void onClickOtp(View v)
    {
        subjectSpinner = (Spinner) findViewById(R.id.spinner_subjects);
        subject_filter = String.valueOf(subjectSpinner.getSelectedItem());
        Spinner semSpinner = (Spinner) findViewById(R.id.spinner_semesters);
        String semester = String.valueOf(semSpinner.getSelectedItem());
        Spinner classSpinner = (Spinner) findViewById(R.id.spinner_class);
        class_filter = String.valueOf(classSpinner.getSelectedItem());
        Spinner sectionSpinner = (Spinner) findViewById(R.id.spinner_section);
        section_filter = String.valueOf(sectionSpinner.getSelectedItem());
        if (!semester.equals("Semester")) {
            if (!class_filter.equals("Class")) {

                if (!section_filter.equals("Section")) {
                    if (!subject_filter.equals("Subject")) {



                        section_filter = String.valueOf(sectionSpinner.getSelectedItem());
                        subjectSpinner = (Spinner) findViewById(R.id.spinner_subjects);
                        subject_filter = String.valueOf(subjectSpinner.getSelectedItem());
                        mode = "otp";
                        setContentView(R.layout.t_otp_displayer);



                        getResultsFromApi();



                    }else{
                        Toast.makeText(this, "Please Choose a Subject", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    Toast.makeText(this, "Please Choose a Section", Toast.LENGTH_SHORT).show();

                }

            }else{
                Toast.makeText(this, "Please Choose a Class", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Please Choose a Semester", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickPlus(View v)
    {
        Intent selectIntent = new Intent(t_Attendance.this,t_EventWriter.class);
        startActivity(selectIntent);


    }


    public void onClickAttendance(View v)
    {
        Intent selectIntent = new Intent(t_Attendance.this,t_Attendance.class);
        startActivity(selectIntent);


    }

    public void onClickAnnouncement(View v)
    {
        Intent selectIntent = new Intent(t_Attendance.this,t_Announcement_Viewer.class);
        startActivity(selectIntent);
    }

    public void onClickNotes(View v)
    {
        Intent selectIntent = new Intent(t_Attendance.this,t_notes_Viewer.class);
        startActivity(selectIntent);
    }

    public void onClickEvents(View v)
    {
        Intent selectIntent = new Intent(t_Attendance.this,EventViewer.class);
        startActivity(selectIntent);
    }

    public void onClickProfile(View v)
    {
        Intent selectIntent = new Intent(t_Attendance.this,Newsfeed.class);
        startActivity(selectIntent);


    }

    public void onClickRefreshSubjects (View v)
    {
        Spinner dept_Spinner = (Spinner) findViewById(R.id.spinner_department);
        dept_filter = String.valueOf(dept_Spinner.getSelectedItem());

        if (!dept_filter.equals("Department"))
        {
            setContentView(R.layout.t_att_sem_class_chooser);
            mode = "subjectfeed";

            Spinner spin = (Spinner) findViewById(R.id.spinner_class);
            spin.setOnItemSelectedListener(this);

            getResultsFromApi();
        }
        else
        {
            Toast.makeText(this, "Please Choose a Department", Toast.LENGTH_SHORT).show();
        }

    }


    private void swipe() {

        TextView head2 = (TextView) findViewById(R.id.head2);
        TextView head1 = (TextView) findViewById(R.id.head);

        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);



        if(a==0){

            Intent selectIntent = new Intent(t_Attendance.this,t_Attendance.class);
            startActivity(selectIntent);

        }


        if(a==1) {

            Intent selectIntent = new Intent(t_Attendance.this,t_Announcement_Viewer.class);
            startActivity(selectIntent);


        }

        if(a==2) {
            Intent selectIntent = new Intent(t_Attendance.this,t_notes_Viewer.class);
            startActivity(selectIntent);
        }

        if(a==3) {
            Intent selectIntent = new Intent(t_Attendance.this,t_Attendance.class);
            startActivity(selectIntent);

        }

        if(a==4){
            Intent selectIntent = new Intent(t_Attendance.this,Newsfeed.class);
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

    public void onClickRemove(View v)
    {
        if(buttonMode=="remove")
        {


            listmode = "remove";
            buttonMode = "add";
            Button removeToggle = (Button) findViewById(R.id.remove);

            removeToggle.setText("Add Students");

            //   ids.addAll(words) ;

            ListView lv = (ListView) findViewById(R.id.list);

            // Instanciating an array list (you don't need to do this,
            // you already have yours).
            List<String> your_array_list = new ArrayList<String>();
            your_array_list.add("foo");
            your_array_list.add("bar");

            // This is the array adapter, it takes the context of the activity as a
            // first parameter, the type of list view as a second parameter and your
            // array as a third parameter.
            arrayAdapter = new Student_add_Adapter(this, studentsAdd);


            lv.setAdapter(arrayAdapter);

            arrayAdapter.notifyDataSetChanged();
            arrayAdapter.notifyDataSetInvalidated();

            // EventList();
        }

        else if(buttonMode=="add")
        {
            listmode = "add";
            buttonMode = "remove";

            Button removeToggle = (Button) findViewById(R.id.remove);

            removeToggle.setText("Remove Students");
            EventList();
        }

    }

}