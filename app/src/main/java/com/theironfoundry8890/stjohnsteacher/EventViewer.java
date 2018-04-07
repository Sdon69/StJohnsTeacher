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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.theironfoundry8890.stjohnsteacher.R.id.pass;

public class EventViewer extends Activity
        implements EasyPermissions.PermissionCallbacks {
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
    private String sPassword;private String userId;
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
    final ArrayList<Word> words = new ArrayList<Word>();

    private String dFName;
    private String dLName;
    private String dClass;
    private String dEmail;
    private String dSection;
    private String dId;
    private String dPassword;
    private String dPhone;


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
            String spreadsheetId = "1SC0UPYthsoS5NKDuC5oJt-y29__f0gm0wkIkJoDduWw";
            int a = 2;
            idAvailcheck = true;
            String range = "Events!".concat("A"+ a++ + ":S");
            end = false;

            List<List<Object>> arrData = getData(title,description );

            ValueRange oRange = new ValueRange();
            oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
            oRange.setValues(arrData);


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







                    String str1 = String.valueOf(row.get(0));

                    if (str1.contains("BonBlank88"))
                    {

                        end = true;

                        continue;
                    }








                    status = String.valueOf(row.get(9));

                    Log.v("status",status);


                    if(status.equals("A")) {
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


                            Log.v("Departments", dept_filter);
                            words.add(new Word(description, title, publishDate, eventDate, lastDateofRegistration, fees, fullName));
                        }
                    }















                }

                Collections.reverse(words);


            }




            return results;



        }




        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
            Log.v("EventViewer" , "Worked");


        }



        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
                Log.v("EventViewer" , "damn");
            } else {
                output.add(0, " ");
                mOutputText.setText(TextUtils.join("\n", output));
                Log.v("EventViewer" , "Wofdad21");
                end = true;
                EventList();


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
                            EventViewer.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                    Log.v("EventViewer" , "Worked2");
                    end = true;
                    EventList();
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
            Log.v("EventViewer"   , "Wofdad");

        }
    }

    public void onClick2(View v) {

        mOutputText.setText("");
        getResultsFromApi();


    }

    public static List<List<Object>> getData (String id , String passString )  {

        List<Object> data1 = new ArrayList<Object>();
        data1.add(id);
        data1.add(pass);




        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add (data1);

        return data;
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
        Log.v(passString , idString);
        mOutputText.setText("");
        getResultsFromApi();

    }

    public void EventList(){




        Log.v("done","done");






        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // adapter knows how to create list items for each item in the list.
        EventsAdapter adapter = new EventsAdapter(this, words);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // activity_numbers.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);
        listViewGlobal = listView;

        listViewGlobal.setOnScrollListener(onScrollListener());

        if(end) {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Word word = words.get(position);

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

        words.clear();


        mOutputText.setText("");
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
        Intent selectIntent = new Intent(EventViewer.this,t_Teacher_Profile.class);
        startActivity(selectIntent);


    }


    private void swipe() {

        TextView head2 = (TextView) findViewById(R.id.head2);
        TextView head1 = (TextView) findViewById(R.id.head);

        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);



        if(a==0){

            Intent selectIntent = new Intent(EventViewer.this,t_Attendance.class);
            startActivity(selectIntent);

        }


        if(a==1) {

            Intent selectIntent = new Intent(EventViewer.this,t_Announcement_Viewer.class);
            startActivity(selectIntent);


        }

        if(a==2) {
            Intent selectIntent = new Intent(EventViewer.this,t_notes_Viewer.class);
            startActivity(selectIntent);
        }

        if(a==3) {
            Intent selectIntent = new Intent(EventViewer.this,EventViewer.class);
            startActivity(selectIntent);

        }

        if(a==4){
            Intent selectIntent = new Intent(EventViewer.this,t_Teacher_Profile.class);
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
            profileImageView.setImageResource(R.drawable.profile);

        }
        if (a == 1) {
            attendanceImageView.setImageResource(R.drawable.attendance);
            announcementImageView.setImageResource(R.drawable.announcements_grey);
            notesImageView.setImageResource(R.drawable.notes);
            eventsImageView.setImageResource(R.drawable.events);
            profileImageView.setImageResource(R.drawable.profile);
        }

        if (a == 2) {
            attendanceImageView.setImageResource(R.drawable.attendance);
            announcementImageView.setImageResource(R.drawable.announcements);
            notesImageView.setImageResource(R.drawable.notes_grey);
            eventsImageView.setImageResource(R.drawable.events);
            profileImageView.setImageResource(R.drawable.profile);
        }

        if (a == 3) {
            attendanceImageView.setImageResource(R.drawable.attendance);
            announcementImageView.setImageResource(R.drawable.announcements);
            notesImageView.setImageResource(R.drawable.notes);
            eventsImageView.setImageResource(R.drawable.events_grey);
            profileImageView.setImageResource(R.drawable.profile);
        }

        if (a == 4) {
            attendanceImageView.setImageResource(R.drawable.attendance);
            announcementImageView.setImageResource(R.drawable.announcements);
            notesImageView.setImageResource(R.drawable.notes);
            eventsImageView.setImageResource(R.drawable.events);
            profileImageView.setImageResource(R.drawable.profile_grey);

        }


    }

    private void deviceOffline()
    {
        LinearLayout activityLayout = (LinearLayout) findViewById(R.id.mLayout);
        activityLayout.setBackgroundResource(R.drawable.no_connection);
        RelativeLayout listviewer = (RelativeLayout) findViewById(R.id.listinflater);
        listviewer.setVisibility(View.INVISIBLE);
    }

    private void deviceOnline()
    {
        LinearLayout activityLayout = (LinearLayout) findViewById(R.id.mLayout);
        activityLayout.setBackgroundResource(0);
        RelativeLayout listviewer = (RelativeLayout) findViewById(R.id.listinflater);
        listviewer.setVisibility(View.VISIBLE);
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