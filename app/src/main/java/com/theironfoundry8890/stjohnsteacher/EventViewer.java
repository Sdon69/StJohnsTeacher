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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class EventViewer extends Activity
        implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private Button mCallApiButton;
    ProgressDialog mProgress;
    private String sId = "hello";
    private TextView mOutputText;
    private String userId;
    private boolean idAvailcheck = true;
    private ListView listViewGlobal;

    private int a = 3;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;


    private String title;
    private String description;
    private String publishDate;
    private String eventDate;
    private String lastDateofRegistration;
    private String fees;



    private String mode = "timestampViewer" ;
    private boolean isViewerTimestampUpdated = false;
    private String globalDataArrayString;
    private boolean retrievingDataEnd = false;
    private String viewerTimestamp;
    private String viewerTimestampHolder;
    final ArrayList<newsfeedPublic> words = new ArrayList<newsfeedPublic>();
    final ArrayList<newsfeedPublic> viewerArrayList = new ArrayList<newsfeedPublic>();


    private String cataegories;

    private String status;

    private String dept_filter = "All Events";

    private boolean end = true;




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

        setContentView(R.layout.eventviewer);

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
        Spinner deptSpinner = (Spinner) findViewById(R.id.spinner_dept);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genre_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(adapter);


        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        loadData();
        loadDataArray();


            loadDataForList();
        if(!globalDataArrayString.equals("unknown"))
        {

            sortDataByDate(globalDataArrayString);
            EventList();
        }

        getResultsFromApi();


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
            SharedPreferences mPrefs = getSharedPreferences("label", 0);userId = mPrefs.getString("UserId", "default_value_if_variable_not_found");Log.v("accountName", String.valueOf(userId));String accountName = userId;
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
        Log.v("EventViewer" , "Success");
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
                EventViewer.this,
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
            end = false;


            ValueRange oRange = new ValueRange();
            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW



            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);


            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);



            if(mode.equals("timestampViewer"))
            {
                spreadsheetId= sheetsIdCollection.getMiscSheetId(); //1nzKRlq7cQrI_XiJGxJdNax5oB91bR_SypiazWO2JTuU
                range = "Timestamp!".concat("A"+ 2 + ":B");
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





                    if(mode.equals("timestampViewer")) {
                        String modeRetrieved = String.valueOf(row.get(0));
                        Log.v("modeRetrieved",modeRetrieved);
                        if (modeRetrieved.equals("eventViewer")) {
                            String timeStamp = String.valueOf(row.get(1));
                            isViewerTimestampUpdated =
                                    timestampCompare(timeStamp, viewerTimestamp);

                            viewerTimestampHolder = timeStamp;
                        }
                    }else if(mode.equals("viewer")) {

                        String timeStamp = String.valueOf(row.get(11));
                        Log.v("timeStamp",timeStamp);

                        if (Integer.parseInt(timeStamp) <= Integer.parseInt(viewerTimestamp))
                            continue;


                        String str1 = String.valueOf(row.get(0));

                        if (str1.contains("BonBlank88")) {

                            end = true;

                            continue;
                        }


                        status = String.valueOf(row.get(9));

                        Log.v("status", status);


                        if (status.equals("A")) {
                            cataegories = String.valueOf(row.get(6));


                            if (cataegories.contains(dept_filter)) {

                                title = String.valueOf(row.get(0));
                                description = String.valueOf(row.get(1));
                                Log.v("descr", description);
                                String fullName = String.valueOf(row.get(8));
                                publishDate = String.valueOf(row.get(2));
                                eventDate = String.valueOf(row.get(3));
                                lastDateofRegistration = String.valueOf(row.get(4));
                                fees = String.valueOf(row.get(5));
                                fees = " ₹".concat(fees);


                                description = splitProtection(description);
                                title = splitProtection(title);
                                cataegories = splitProtection(cataegories);
                                publishDate = splitProtection(publishDate);
                                eventDate = splitProtection(eventDate);
                                lastDateofRegistration = splitProtection(lastDateofRegistration);
                                fees = splitProtection(fees);
                                fullName = splitProtection(fullName);
                                timeStamp = splitProtection(timeStamp);


                                Log.v("Departments", dept_filter);
                                viewerArrayList.add(new newsfeedPublic(description, title, publishDate, eventDate,
                                        lastDateofRegistration, fees, fullName, "EVENTS", timeStamp));
                            }
                        }


                    }












                }



            }




            return results;



        }




        @Override
        protected void onPreExecute() {

            ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.loadingCircle);
            loadingCircle.setVisibility(View.VISIBLE);
        }



        @Override
        protected void onPostExecute(List<String> output) {
       
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
                Log.v("EventViewer" , "damn");
            } else {


                end = true;

                if(mode.equals("timestampViewer")) {


                    if (isViewerTimestampUpdated) {
                        mode = "viewer";
                        getResultsFromApi();
                    }else
                    {
                        EventList();
                        ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.loadingCircle);
                        loadingCircle.setVisibility(View.GONE);
                    }
                }else if(mode.equals("viewer")) {
                    retrievingDataEnd = true;
                    if(globalDataArrayString.equals("unknown"))
                        postViewerMode();
                }

                if(retrievingDataEnd) {
                    if (isViewerTimestampUpdated)
                        postViewerMode();
                }
                EventList();

            }
        }

        @Override
        protected void onCancelled() {
         

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            EventViewer.REQUEST_AUTHORIZATION);
                } else {


                    end = true;

                    if(mode.equals("timestampViewer")) {


                        if (isViewerTimestampUpdated) {
                            mode = "viewer";
                            getResultsFromApi();
                        }else
                        {
                            EventList();
                            ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.loadingCircle);
                            loadingCircle.setVisibility(View.GONE);
                        }
                    }else if(mode.equals("viewer")) {
                        retrievingDataEnd = true;
                        if(globalDataArrayString.equals("unknown"))
                            postViewerMode();
                    }

                    if(retrievingDataEnd) {
                        if (isViewerTimestampUpdated)
                            postViewerMode();
                    }
                    EventList();

                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }


    public void onClick2(View v) {


        getResultsFromApi();


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

        sId = idString;



    }

    public void EventList(){

        newsfeedAdapter adapter = new newsfeedAdapter(this, words);


        ListView listView = (ListView) findViewById(R.id.list);


        listView.setAdapter(adapter);
        listViewGlobal = listView;

        listViewGlobal.setOnScrollListener(onScrollListener());

        if(end) {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    newsfeedPublic word = words.get(position);

                    //Get on clicked data
                    String exTitle = word.getMiwokTranslation();
                    String exDesc = word.getDefaultTranslation();
                    String exPublishDate = word.getPublishDate();
                    String exEventDate = word.getEventDate();
                    String exLastDateofRegistation = word.getLastDateofRegistration();
                    String exFees = word.getEntryFees();
                    String exFullName = word.getfullName();




                    //Save Data
                    SharedPreferences mPrefs = getSharedPreferences("label", 0);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putString("title", exTitle).commit();
                    mEditor.putString("desc", exDesc).commit();
                    mEditor.putString("publishDate", exPublishDate).commit();
                    mEditor.putString("eventDate", exEventDate).commit();
                    mEditor.putString("lastDateOfRegistration", exLastDateofRegistation).commit();
                    mEditor.putString("fees", exFees).commit();
                    mEditor.putString("fullName", exFullName).commit();
                    Log.v("done","done");



                    Intent selectIntent = new Intent(EventViewer.this,DetailedEvent.class);
                    startActivity(selectIntent);






                }


            });
        }
    }

    public void onChangeDept(View v){
        Spinner deptSpinner = (Spinner) findViewById(R.id.spinner_dept);
        dept_filter = String.valueOf(deptSpinner.getSelectedItem());



        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("savedDepartmentEvent", dept_filter).commit();



        words.clear();
        viewerArrayList.clear();
        mEditor.putString("savedEventTimestamp", "1521000000").commit();
        mEditor.putString("savedIndividualEventDataArray", "unknown").commit();
        viewerTimestamp = "1521000000";
        globalDataArrayString = "unknown";
        viewerTimestampHolder = "1521000000";
        mode = "timestampViewer";
        isViewerTimestampUpdated = false;
        retrievingDataEnd = false;
        EventList();



        getResultsFromApi();







    }

    public void onClickPlus(View v)
    {
        Intent selectIntent = new Intent(EventViewer.this,t_EventWriter.class);
        startActivity(selectIntent);
    }



    public void onClickAttendance(View v)
    {
        Intent selectIntent = new Intent(EventViewer.this,t_Attendance.class);
        startActivity(selectIntent);


    }

    public void onClickAnnouncement(View v)
    {
        Intent selectIntent = new Intent(EventViewer.this,t_Announcement_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickNotes(View v)
    {
        Intent selectIntent = new Intent(EventViewer.this,t_notes_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickEvents(View v)
    {
        Intent selectIntent = new Intent(EventViewer.this,EventViewer.class);
        startActivity(selectIntent);


    }

    public void onClickProfile(View v)
    {
        Intent selectIntent = new Intent(this,Newsfeed.class);
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



    private void hideOnScroll()
    {
        LinearLayout filterBar = (LinearLayout) findViewById(R.id.filterBar);
        filterBar.setVisibility(View.GONE);
    }

    private void showOnScroll()
    {
        LinearLayout filterBar = (LinearLayout) findViewById(R.id.filterBar);
        filterBar.setVisibility(View.VISIBLE);

    }



    //Saved listView functions


    public boolean timestampCompare(String timestampRetrieved, String timestampStored){


        return !timestampRetrieved.equals(timestampStored);

    }

    public void loadDataArray(){


        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        globalDataArrayString = mPrefs.getString("savedIndividualEventDataArray", "unknown");


    }


    public void postViewerMode()
    {

        String newsfeedAnnouncementsString;
        newsfeedAnnouncementsString = viewerArrayList.toString().replace("[","");
        newsfeedAnnouncementsString = newsfeedAnnouncementsString.replace("]","");
        String storedData;
        String dataRetrieved ="";
        if(globalDataArrayString.equals("unknown")) {

            storedData = "[";

            if(newsfeedAnnouncementsString.length()>5)
                dataRetrieved = dataRetrieved +  newsfeedAnnouncementsString;


        }else
        {
            if(newsfeedAnnouncementsString.length()>5)
                dataRetrieved = dataRetrieved + "," +  newsfeedAnnouncementsString;
            storedData =  globalDataArrayString.replace("]","");
        }
        viewerTimestamp = viewerTimestampHolder;




        saveTimestamps();




        String concatenatedData =  storedData + dataRetrieved + "]";

        int maxLogSize = 1000;
        for(int i = 0; i <= concatenatedData.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > concatenatedData.length() ? concatenatedData.length() : end;
        }



        sortDataByDate(concatenatedData);
//        mProgress.hide();
        EventList();
        ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.loadingCircle);
        loadingCircle.setVisibility(View.GONE);

//        hideLoading();

    }
    public void sortDataByDate(String dateRetrieved)
    {
        saveDataArray(dateRetrieved);


        String trimmedString = dateRetrieved.replace("[","");
        trimmedString = trimmedString.replace("]","");

        trimmedString = trimmedString.replace(" NOTES","NOTES");
        trimmedString = trimmedString.replace(" ANNOUNCEMENTS","ANNOUNCEMENTS");
        trimmedString = trimmedString.replace(" EVENTS","EVENTS");



        String dateArray[] = trimmedString.split(",");
        String elementArray[];
        String elementMode;



        int i;

        int latestIndex = 0;

        String transferArray[] = {"hello","hello"};



        words.clear();
        for(i=0;i<dateArray.length;i++) {

            String latestDate;

            elementArray = dateArray[i].split("%");
            elementMode = elementArray[0];



            if (elementMode.contains("ANNOUNCEMENTS") || elementMode.contains("NOTES")) {
                latestDate = elementArray[9];

            } else if (elementMode.contains("EVENTS")) {
                latestDate = elementArray[8];
            } else {
                break;
            }
//


            int k = i;

            for (k = k; k < dateArray.length; k++) {
                latestDate = latestDate.trim();
                int latestTimestamp = Integer.parseInt(latestDate);


                elementArray = dateArray[k].split("%");
                elementMode = elementArray[0];
                String challengeDate = "o";
                if (elementMode.equals("ANNOUNCEMENTS") || elementMode.equals("NOTES")) {
                    challengeDate = elementArray[9];

                } else if (elementMode.equals("EVENTS")) {
                    challengeDate = elementArray[8];
                } else {
                    break;
                }


                challengeDate = challengeDate.trim();
                int challengeTimestamp = Integer.parseInt(challengeDate);



                boolean lastestDayIsPast = false;

                if(challengeTimestamp >= latestTimestamp )
                    lastestDayIsPast = true;

//
                if (lastestDayIsPast) {

                    latestDate = challengeDate;
                    latestIndex = k;

                }




            }



            transferArray[0] = dateArray[i];
            dateArray[i] = dateArray[latestIndex];

            dateArray[latestIndex] = transferArray[0];


            if(i==dateArray.length) {

                elementArray = dateArray[dateArray.length - 2].split("%");

            }
            else
            {
                elementArray = dateArray[i].split("%");
            }




            elementMode = elementArray[0];
            String element0 = splitProtectionDeactivated(elementArray[0]);
            String element1 = splitProtectionDeactivated(elementArray[1]);
            String element2 = splitProtectionDeactivated(elementArray[2]);
            String element3 = splitProtectionDeactivated(elementArray[3]);
            String element4 = splitProtectionDeactivated(elementArray[4]);
            String element5 = splitProtectionDeactivated(elementArray[5]);
            String element6 = splitProtectionDeactivated(elementArray[6]);
            String element7 = splitProtectionDeactivated(elementArray[7]);
            String element8 = splitProtectionDeactivated(elementArray[8]);



            if (elementArray[0].equals("NOTES") || elementArray[0].equals("ANNOUNCEMENTS")) {

                String element9 = splitProtectionDeactivated(elementArray[9]);

                words.add(new newsfeedPublic(element1, element2, element3, element4,
                        element5, element6, element7
                        , element8, element0,element9));
            } else {

                words.add(new newsfeedPublic(element1, element2, element3, element4,
                        element5, element6, element7
                        , element0,element8));
            }


        }






    }
    public void saveTimestamps()
    {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("savedEventTimestamp", viewerTimestamp).commit();

    }


    public void loadDataForList(){
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        dept_filter =  mPrefs.getString("savedDepartmentEvent", "All Events");


        viewerTimestamp = mPrefs.getString("savedEventTimestamp", "1521000000");


        Spinner semesterSpinner = (Spinner) findViewById(R.id.spinner_semesters);
        Spinner departmentSpinner = (Spinner) findViewById(R.id.spinner_dept);

        departmentSpinner.setSelection(((ArrayAdapter)departmentSpinner.getAdapter()).getPosition(dept_filter));
    }

    public void saveDataArray(String dataArray){


        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();

        mEditor.putString("savedIndividualEventDataArray", dataArray).commit();


    }




    public String  splitProtection(String original)
    {
        original = original.replace(",","<comma5582>");
        original = original.replace("%","<percent6643>");
        original = original.replace("NOTES","<notes6513>");
        original = original.replace("ANNOUNCEMENTS","<announcements9235>");
        original = original.replace("EVENTS","<events3321>");
        return original;
    }

    public String  splitProtectionDeactivated(String original)
    {
        original = original.replace("<comma5582>",",");
        original = original.replace("<percent6643>","%");
        original = original.replace("<notes6513>","NOTES");
        original = original.replace("<announcements9235>","ANNOUNCEMENTS");
        original = original.replace("<events3321>","EVENTS");
        return original;
    }


    public AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = listViewGlobal.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top: visible header and footer
                        showOnScroll();
                        Log.i("scrollLocation", "top reached");

                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = listViewGlobal.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the bottom: visible header and footer
                        Log.i("scrollLocation", "bottom reached!");

                    }
                } else if (totalItemCount - visibleItemCount > firstVisibleItem){
                    // on scrolling
                    hideOnScroll();
                    Log.i("scrollLocation", "on scroll");
                }
            }
        };
    }
}