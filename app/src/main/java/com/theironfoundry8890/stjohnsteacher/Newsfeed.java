package com.theironfoundry8890.stjohnsteacher;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.theironfoundry8890.stjohnsteacher.youtubeDataUploader.PlayActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.theironfoundry8890.stjohnsteacher.sheetsIdCollection.getAnnouncementSheetId;
import static com.theironfoundry8890.stjohnsteacher.sheetsIdCollection.getEventSheetId;
import static com.theironfoundry8890.stjohnsteacher.sheetsIdCollection.getMiscSheetId;
import static com.theironfoundry8890.stjohnsteacher.sheetsIdCollection.getNoteSheetId;
import static com.theironfoundry8890.stjohnsteacher.sheetsIdCollection.getUploadedVideoInfoSheetId;

public class Newsfeed extends Activity
        implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private Button mCallApiButton;
    ProgressDialog mProgress;


    private String sSemester;
    private String sDepartment;
    private String globalTransferArray[];
    private boolean timetableVisible = false;
    private boolean appFirstUse;
    private String globalDataArrayString;
  private int integerSemester = 2;

    private boolean isUpdateAvailable = false;
    private String updateNotes = "";
    private ListView  listViewGlobal;

    private boolean retrievingDataEnd = false;
    private String  updateUrl = "";

    private String newsfeedNotesTimestamp;
    private String newsfeedAnnouncementTimestamp;
    private String newsfeedEventTimestamp;

    private boolean isTimetableDrawerOpen = false;
    private boolean isVideoInfoViewerTimestampUpdated = false;
    private String videoInfoViewerTimestamp;
    private String videoInfoViewerTimestampHolder;

    private String timeCollection[];

    private String newsfeedNotesTimestampHolder;
    private String newsfeedAnnouncementTimestampHolder;
    private String newsfeedEventTimestampHolder;

    private boolean isNotesTimestampUpdated = false;
    private boolean isAnnouncementTimestampUpdated = false;
    private boolean isEventTimestampUpdated = false;




    private TextView mOutputText;
    private String sPassword;private String userId;
    private boolean idAvailcheck = true;
    private String mode = "timestampViewer" ;

    private int a = 4;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    private String fullName;


    private String title;
    private String description;
    private String publishDate;
    private String eventDate;
    private String publishId;
    private String departments;

    private String lastDateofRegistration;
    private String fees;
    final ArrayList<newsfeedPublic> words = new ArrayList<newsfeedPublic>();

    final ArrayList<newsfeedPublic> containers = new ArrayList<newsfeedPublic>();
    final ArrayList<newsfeedPublic> newsfeedAnnouncements = new ArrayList<newsfeedPublic>();
    final ArrayList<newsfeedPublic> newsfeedEvents = new ArrayList<newsfeedPublic>();
    final ArrayList<newsfeedPublic> newsfeedNotes = new ArrayList<newsfeedPublic>();
    final ArrayList<newsfeedPublic> toBeEliminated = new ArrayList<newsfeedPublic>();


    private String dept_filter = "All Departments";
    private String semester_filter = "All Semesters";
    private String currentVersion ;

    private String dId;

    private List dRow;

    private int dayIndex;
    private int dayIndexSecondSemester;
    private int dayIndexThirdSemester;
    private boolean end = true;

    private int tableNo;
    private String gSavedAnnSheetId;


    private String globalDepartment;


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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadUse();

        if(appFirstUse)
        {

            Intent selectIntent = new Intent(Newsfeed.this,t_SignUp.class);
            startActivity(selectIntent);
        }else {

            setContentView(R.layout.newsfeed);


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

            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                currentVersion = pInfo.versionName;


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }



            colorCheck();
            // Initialize credentials and service object.
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            loadData();
            loadDataArray();

            if(globalDepartment.equals("unknown")) {
                RelativeLayout whatDepartment = (RelativeLayout) findViewById(R.id.whatDepartment);
                whatDepartment.setVisibility(View.VISIBLE);
            }else

            {

                loadDataArray();
                if(!globalDataArrayString.equals("unknown"))
                {

                    sortDataByDate(globalDataArrayString);
                    EventList();
                }

                dept_filter = sDepartment;

                semester_filter = sSemester;

                Date now = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);


                dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                dayIndexSecondSemester = dayIndex;
                dayIndexThirdSemester = dayIndex;
                timetableDataConversion(dayIndex);
                timetableDataConversionSecondSemester(dayIndexSecondSemester);
                timetableDataConversionThirdSemester(dayIndexThirdSemester);
                generatePeriodNotification(dayIndex);

//                timer.schedule(doAsynchronousTask, 0    , 6000);


                getResultsFromApi();

            }
        }

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
                Newsfeed.this,
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
            String spreadsheetId = getAnnouncementSheetId();
            int a = 2;
            idAvailcheck = true;
            String range = "Stj Teacher Notes!".concat("A"+ a++ + ":J");
            end = false;



            ValueRange oRange = new ValueRange();



            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);


            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);

            if(mode.equals("timestampViewer"))
            {
                spreadsheetId = getMiscSheetId();
                range = "Timestamp!".concat("A"+ 2 + ":D");
            } else if(mode.equals("notesViewer")) {
                spreadsheetId = getNoteSheetId();
            }else if(mode.equals("eventViewer"))
            {
                spreadsheetId = getEventSheetId();
                range = "Events!".concat("A"+ 2 + ":S");
            }else if(mode.equals("videoInfoViewer"))
            {
                spreadsheetId = getUploadedVideoInfoSheetId();
                range = "videoInfo!".concat("A"+ 2 + ":I");
            }


            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            if (values != null) {


                results.add("");


                for (List row : values) {



                    if(mode.equals("timestampViewer"))
                    {
                        String modeRetrieved = String.valueOf(row.get(0));

                        if(modeRetrieved.equals("announcementViewer"))
                        {
                            String timeStamp = String.valueOf(row.get(1));
                           isAnnouncementTimestampUpdated =
                                   timestampCompare(timeStamp,newsfeedAnnouncementTimestamp);

                            newsfeedAnnouncementTimestampHolder = timeStamp;

                        }else if(modeRetrieved.equals("eventViewer"))
                        {
                            String timeStamp = String.valueOf(row.get(1));
                            isEventTimestampUpdated =
                                    timestampCompare(timeStamp,newsfeedEventTimestamp);
                            newsfeedEventTimestampHolder = timeStamp;

                        }else if(modeRetrieved.equals("notesViewer"))
                        {
                            String timeStamp = String.valueOf(row.get(1));
                            isNotesTimestampUpdated =
                                    timestampCompare(timeStamp,newsfeedNotesTimestamp);
                            newsfeedNotesTimestampHolder = timeStamp;
                        }else if(modeRetrieved.equals("videoInfoViewer")){
                        String timeStamp = String.valueOf(row.get(1));
                        isVideoInfoViewerTimestampUpdated =
                                timestampCompare(timeStamp, videoInfoViewerTimestamp);

                        videoInfoViewerTimestampHolder = timeStamp;
                    }else if(modeRetrieved.equals("versionViewerTeacher")){

                            String version = String.valueOf(row.get(1));

                            if(Float.parseFloat(version)>Float.parseFloat(currentVersion))
                            {
                                isUpdateAvailable = true;
                                updateNotes = String.valueOf(row.get(2));
                                updateUrl  = String.valueOf(row.get(3));


                            }

                        }


                    }


                    if(mode.equals("announcementViewer")) {



                        String timeStamp = String.valueOf(row.get(8));

                        if(Integer.parseInt(timeStamp)<=Integer.parseInt(newsfeedAnnouncementTimestamp))
                            continue;

                        dId = String.valueOf(row.get(0));

                        dRow = row;



                        if (dId.contains("BonBlank88")) {



                            end = true;

                            continue;
                        }


                        String cataegories = String.valueOf(row.get(2));

                        if (cataegories.contains(dept_filter)) {
                            if (cataegories.contains(semester_filter)) {

                                title = String.valueOf(row.get(0));

                                description = String.valueOf(row.get(1));
                                String publisherId = String.valueOf(row.get(3));//Departments
                                fullName = String.valueOf(row.get(4));
                                String uniqueId = String.valueOf(row.get(5));
                                String datePublished = String.valueOf(row.get(6));
                                String fileAttachment = String.valueOf(row.get(7));

                                description =  splitProtection(description);
                                title =   splitProtection(title);
                                cataegories =     splitProtection(cataegories);
                                publisherId =   splitProtection(publisherId);
                                fullName =   splitProtection(fullName);
                                uniqueId =   splitProtection(uniqueId);
                                datePublished = splitProtection(datePublished);
                                fileAttachment = splitProtection(fileAttachment);
                                timeStamp = splitProtection(timeStamp);




                                newsfeedAnnouncements.add(new newsfeedPublic(description, title, cataegories, publisherId, fullName, uniqueId, datePublished
                                        , fileAttachment,"ANNOUNCEMENTS",timeStamp));
                            }
                        }


                    } //End of announcementViewer Mode


                    if(mode.equals("notesViewer")) {



                        String timeStamp = String.valueOf(row.get(8));

                        if(Integer.parseInt(timeStamp)<=Integer.parseInt(newsfeedNotesTimestamp))
                            continue;



                        dId = String.valueOf(row.get(0));

                        if (dId.contains("BonBlank88"))
                        {


                            end = true;

                            continue;
                        }



                        String cataegories = String.valueOf(row.get(2));

                        if(cataegories.contains(dept_filter)) {
                            if(cataegories.contains(semester_filter)) {
                                title = String.valueOf(row.get(0));

                                description = String.valueOf(row.get(1));
                                String publisherId = String.valueOf(row.get(3));//Departments
                                fullName = String.valueOf(row.get(4));
                                String uniqueId = String.valueOf(row.get(5));
                                String datePublished = String.valueOf(row.get(6));
                                String fileAttachment = String.valueOf(row.get(7));

                                description =  splitProtection(description);
                                title =   splitProtection(title);
                                cataegories =     splitProtection(cataegories);
                                publisherId =   splitProtection(publisherId);
                                fullName =   splitProtection(fullName);
                                uniqueId =   splitProtection(uniqueId);
                                datePublished = splitProtection(datePublished);
                                fileAttachment = splitProtection(fileAttachment);
                                timeStamp = splitProtection(timeStamp);

                                newsfeedNotes.add(new newsfeedPublic(description, title, cataegories, publisherId, fullName, uniqueId,datePublished
                                        ,fileAttachment, "NOTES",timeStamp));
                            }
                        }

                    }  //End of notesViewer mode

                    if(mode.equals("videoInfoViewer")) {



                        String timeStamp = String.valueOf(row.get(8));

                        if (Integer.parseInt(timeStamp) <= Integer.parseInt(videoInfoViewerTimestamp))
                            continue;



                        dId = String.valueOf(row.get(0));

                        if (dId.contains("BonBlank88"))
                        {


                            end = true;

                            continue;
                        }



                        String cataegories = String.valueOf(row.get(2));

                        if(cataegories.contains(dept_filter)) {
                            if(cataegories.contains(semester_filter)) {
                                title = String.valueOf(row.get(0));

                                description = String.valueOf(row.get(1));
                                String publisherId = String.valueOf(row.get(3));//Departments
                                fullName = String.valueOf(row.get(4));
                                String uniqueId = String.valueOf(row.get(5));
                                String datePublished = String.valueOf(row.get(6));
                                String fileAttachment = String.valueOf(row.get(7));

                                description =  splitProtection(description);
                                title =   splitProtection(title);
                                cataegories =     splitProtection(cataegories);
                                publisherId =   splitProtection(publisherId);
                                fullName =   splitProtection(fullName);
                                uniqueId =   splitProtection(uniqueId);
                                datePublished = splitProtection(datePublished);
                                fileAttachment = splitProtection(fileAttachment);
                                timeStamp = splitProtection(timeStamp);

                                newsfeedNotes.add(new newsfeedPublic(description, title, cataegories, publisherId, fullName, uniqueId,datePublished
                                        ,fileAttachment, "NOTES",timeStamp));
                            }
                        }

                    }  //End of uploadedVideoInfoViewer mode

                    else if(mode.equals("eventViewer"))
                    {




                        String timeStamp = String.valueOf(row.get(11));

                        if(Integer.parseInt(timeStamp)<=Integer.parseInt(newsfeedEventTimestamp))
                            continue;




                        String str1 = String.valueOf(row.get(0));

                        if (str1.contains("BonBlank88"))
                        {

                            end = true;

                            continue;
                        }
          String status = String.valueOf(row.get(9));
    if(status.equals("A")) {




                                title = String.valueOf(row.get(0));

                                description = String.valueOf(row.get(1));

                                String fullName = String.valueOf(row.get(8));
                                publishDate = String.valueOf(row.get(2));
                                eventDate = String.valueOf(row.get(3));
                                lastDateofRegistration = String.valueOf(row.get(4));
                                fees = String.valueOf(row.get(5));
                                fees = " ₹".concat(fees);

                                description = splitProtection(description);
                                title =       splitProtection(title);
                                publishDate =         splitProtection(publishDate);
                                eventDate =         splitProtection(eventDate);
                                lastDateofRegistration =            splitProtection(lastDateofRegistration);
                                fees =            splitProtection(fees);
                                fullName =            splitProtection(fullName);
                                timeStamp =            splitProtection(timeStamp);




        newsfeedEvents.add(new newsfeedPublic(description, title, publishDate, eventDate,
                                        lastDateofRegistration, fees, fullName,"EVENTS",timeStamp));

                        }
                    } //End of eventViewer Mode
                }
            }

            return results;

        }




        @Override
        protected void onPreExecute() {

            showLoading();




        }



        @Override
        protected void onPostExecute(List<String> output) {

            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");

            } else {

                end = true;

                if(mode.equals("timestampViewer")) {

                    if(isAnnouncementTimestampUpdated) {
                        mode = "announcementViewer";
                        getResultsFromApi();
                    }else if(isNotesTimestampUpdated)
                    {
                        mode = "notesViewer";
                        getResultsFromApi();
                    }else if (isVideoInfoViewerTimestampUpdated) {
                        mode = "videoInfoViewer";
                        getResultsFromApi();
                    }else if(isEventTimestampUpdated)
                    {
                        mode = "eventViewer";
                        getResultsFromApi();
                    }else
                    {
                        retrievingDataEnd = true;
                        hideLoading();
                    }

                }else if(mode.equals("announcementViewer")) {

                    if(isNotesTimestampUpdated)
                    {
                        mode = "notesViewer";
                        getResultsFromApi();
                    }else if (isVideoInfoViewerTimestampUpdated) {
                        mode = "videoInfoViewer";
                        getResultsFromApi();
                    }else if(isEventTimestampUpdated)
                    {
                        mode = "eventViewer";
                        getResultsFromApi();
                    }else
                    {
                        retrievingDataEnd = true;
                        hideLoading();
                    }

                }
                else if(mode.equals("notesViewer")) {
                    if (isVideoInfoViewerTimestampUpdated) {
                        mode = "videoInfoViewer";
                        getResultsFromApi();
                      }
                    else if(isEventTimestampUpdated)
                    {
                        mode = "eventViewer";
                        getResultsFromApi();
                    }else
                    {
                        retrievingDataEnd = true;
                        hideLoading();
                    }
                } else if(mode.equals("videoInfoViewer")) {
                 if(isEventTimestampUpdated)
                    {
                        mode = "eventViewer";
                        getResultsFromApi();
                    }else
                    {
                        retrievingDataEnd = true;
                        hideLoading();
                    }
                }  else if(mode.equals("eventViewer"))
                {
                      retrievingDataEnd = true;
                    if(globalDataArrayString.equals("unknown"))
                        postEventViewerMode();

                }

                if(retrievingDataEnd) {
                    if(isUpdateAvailable)
                        onUpdateAvailable();
                    if (isAnnouncementTimestampUpdated || isNotesTimestampUpdated || isEventTimestampUpdated)
                        postEventViewerMode();
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
                            Newsfeed.REQUEST_AUTHORIZATION);
                } else {

                    end = true;

                    if(mode.equals("timestampViewer")) {

                        if(isAnnouncementTimestampUpdated) {
                            mode = "announcementViewer";
                            getResultsFromApi();
                        }else if(isNotesTimestampUpdated)
                        {
                            mode = "notesViewer";
                            getResultsFromApi();
                        }else if (isVideoInfoViewerTimestampUpdated) {
                            mode = "videoInfoViewer";
                            getResultsFromApi();
                        }else if(isEventTimestampUpdated)
                        {
                            mode = "eventViewer";
                            getResultsFromApi();
                        }else
                        {
                            retrievingDataEnd = true;
                            hideLoading();
                        }

                    }else if(mode.equals("announcementViewer")) {

                        if(isNotesTimestampUpdated)
                        {
                            mode = "notesViewer";
                            getResultsFromApi();
                        }else if (isVideoInfoViewerTimestampUpdated) {
                            mode = "videoInfoViewer";
                            getResultsFromApi();
                        }else if(isEventTimestampUpdated)
                        {
                            mode = "eventViewer";
                            getResultsFromApi();
                        }else
                        {
                            retrievingDataEnd = true;
                            hideLoading();
                        }

                    }
                    else if(mode.equals("notesViewer")) {
                        if (isVideoInfoViewerTimestampUpdated) {
                            mode = "videoInfoViewer";
                            getResultsFromApi();
                        }
                        else if(isEventTimestampUpdated)
                        {
                            mode = "eventViewer";
                            getResultsFromApi();
                        }else
                        {
                            retrievingDataEnd = true;
                            hideLoading();
                        }
                    } else if(mode.equals("videoInfoViewer")) {
                        if(isEventTimestampUpdated)
                        {
                            mode = "eventViewer";
                            getResultsFromApi();
                        }else
                        {
                            retrievingDataEnd = true;
                            hideLoading();
                        }
                    }  else if(mode.equals("eventViewer"))
                    {
                        retrievingDataEnd = true;
                        if(globalDataArrayString.equals("unknown"))
                            postEventViewerMode();

                    }

                    if(retrievingDataEnd) {
                        if(isUpdateAvailable)
                            onUpdateAvailable();
                        if (isAnnouncementTimestampUpdated || isNotesTimestampUpdated || isEventTimestampUpdated)
                            postEventViewerMode();
                    }

                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }



    public void onClick2(View v) {

        getResultsFromApi();


    }









    public void saveTimestamps()
    {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("newsfeedNotesTimestamp", newsfeedNotesTimestamp).apply();
        mEditor.putString("newsfeedAnnouncementTimestamp", newsfeedAnnouncementTimestamp).apply();
        mEditor.putString("newsfeedEventTimestamp", newsfeedEventTimestamp).apply();
        mEditor.putString("newsfeedVideoInfoViewerTimestamp", videoInfoViewerTimestamp).apply();
    }


    public void loadData(){
        SharedPreferences mPrefs = getSharedPreferences("label", 0);


        sDepartment =  mPrefs.getString("Department", "unknown");
        globalDepartment = sDepartment;

        newsfeedNotesTimestamp =  mPrefs.getString("newsfeedNotesTimestamp", "1521000000");
        newsfeedAnnouncementTimestamp = mPrefs.getString("newsfeedAnnouncementTimestamp", "1521000000");
        newsfeedEventTimestamp = mPrefs.getString("newsfeedEventTimestamp", "1521000000");
        videoInfoViewerTimestamp = mPrefs.getString("newsfeedVideoInfoViewerTimestamp", "1521000000");

       sSemester = "All Semesters";
       boolean firebaseSubscribed = mPrefs.getBoolean("firebaseSubscribed", false);

        if(!firebaseSubscribed)
        {
            if(!sDepartment.equals("unknown"))
            {
                FirebaseMessaging.getInstance().subscribeToTopic(sDepartment+"Teachers");
                FirebaseMessaging.getInstance().subscribeToTopic("globalTeachers");
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putBoolean("firebaseSubscribed", true).apply();
            }
        }



    }

    public void removeIndexFromArray(String array[],int index){
        array[index] = "remove";
        int i;
        for(i=0;i< array.length;i++)
        {
            if(array[i].contains("remove"))
            {

            }else
            {
                array[i] = array[i];
            }
        }

        globalTransferArray = array;

        return;




    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void timetableDataConversion(int localDayIndex)
    {

        if(dayIndex==7)
        {
            dayIndex = 1;
            localDayIndex = 1;
        }

        if(dayIndex==0)
        {
            dayIndex = 1;
            localDayIndex = 1;
        }
        String timetable = "<semester6643>1" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>2" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>3" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>4" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "RM<subjects5432>SM<subjects5432>FM<subjects5432>OR<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>RM<subjects5432>SM<subjects5432><days5548>" +
                "FM<subjects5432>PM<subjects5432>SM<subjects5432>RM<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>FM<subjects5432>PM<subjects5432><days5548>" +
                "SM<subjects5432>FM<subjects5432>PM<subjects5432>CB<subjects5432><days5548>" +
                "RM<subjects5432>OR<subjects5432>CB<subjects5432>PM<subjects5432>"+
                "<semester6643>5" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "RM<subjects5432>SM<subjects5432>FM<subjects5432>OR<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>RM<subjects5432>SM<subjects5432><days5548>" +
                "FM<subjects5432>PM<subjects5432>SM<subjects5432>RM<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>FM<subjects5432>PM<subjects5432><days5548>" +
                "SM<subjects5432>FM<subjects5432>PM<subjects5432>CB<subjects5432><days5548>" +
                "RM<subjects5432>OR<subjects5432>CB<subjects5432>PM<subjects5432>" +
                "<semester6643>6" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "MIS<subjects5432>SM & BP<subjects5432>F ECOM<subjects5432>AUDITING<subjects5432><days5548>" +
                "VAT & ST<subjects5432>MIS<subjects5432>SM & BP<subjects5432>F ECOM<subjects5432><days5548>" +
                "F ECOM<subjects5432>Int Trade<subjects5432>SM & BP<subjects5432>MIS<subjects5432><days5548>" +
                "Int Trade<subjects5432>VAT & ST<subjects5432>MIS<subjects5432>AUDITING<subjects5432><days5548>" +
                "Int Trade<subjects5432>AUDITING<subjects5432>VAT & ST<subjects5432>F ECOM<subjects5432><days5548>" +
                "AUDITING<subjects5432>Int Trade<subjects5432>VAT & ST<subjects5432>SM & BP<subjects5432>"  ;

        String semesterSplit[] = timetable.split("<semester6643>");

        String dataSplit[] =  semesterSplit[integerSemester].split("<type6280>");  // 1 = A section 2 = B section and so on....

        String timeSlots[] = dataSplit[1].split("<time2298>");


        TextView timeSlot0 = (TextView) findViewById(R.id.timeSlot0);
        timeSlot0.setText(timeSlots[0]);



        TextView timeSlot1 = (TextView) findViewById(R.id.timeSlot1);
        timeSlot1.setText(timeSlots[1]);

        TextView timeSlot2 = (TextView) findViewById(R.id.timeSlot2);
        timeSlot2.setText(timeSlots[2]);

        TextView timeSlot3 = (TextView) findViewById(R.id.timeSlot3);
        timeSlot3.setText(timeSlots[3]);



        String dayText = "default";

        if(localDayIndex == 1)
            dayText = "Monday";
        else if(localDayIndex == 2)
            dayText = "Tuesday";
        else if(localDayIndex == 3)
            dayText = "Wednesday";
        else if(localDayIndex == 4)
            dayText = "Thursday";
        else if(localDayIndex == 5)
            dayText = "Friday";
        else if(localDayIndex == 6)
            dayText = "Saturday";


        TextView dayOfTheWeek = (TextView) findViewById(R.id.dayOfTheWeek);
        dayOfTheWeek.setText(dayText);


        String daySlots [] = dataSplit[2].split("<days5548>");
        String subjectSlots[] = daySlots[localDayIndex - 1].split("<subjects5432>");
        TextView subjectSlot0 = (TextView) findViewById(R.id.subjectSlot0);
        subjectSlot0.setText(subjectSlots[0]);

        TextView subjectSlot1 = (TextView) findViewById(R.id.subjectSlot1);
        subjectSlot1.setText(subjectSlots[1]);

        TextView subjectSlot2 = (TextView) findViewById(R.id.subjectSlot2);
        subjectSlot2.setText(subjectSlots[2]);

        TextView subjectSlot3 = (TextView) findViewById(R.id.subjectSlot3);
        subjectSlot3.setText(subjectSlots[3]);









    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void timetableDataConversionSecondSemester(int localDayIndex)
    {

        if(dayIndexSecondSemester==7)
        {
            dayIndexSecondSemester = 1;
            localDayIndex = 1;
        }

        if(dayIndexSecondSemester==0)
        {
            dayIndexSecondSemester = 1;
            localDayIndex = 1;
        }
        String timetable = "<semester6643>1" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>2" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>3" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>4" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "RM<subjects5432>SM<subjects5432>FM<subjects5432>OR<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>RM<subjects5432>SM<subjects5432><days5548>" +
                "FM<subjects5432>PM<subjects5432>SM<subjects5432>RM<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>FM<subjects5432>PM<subjects5432><days5548>" +
                "SM<subjects5432>FM<subjects5432>PM<subjects5432>CB<subjects5432><days5548>" +
                "RM<subjects5432>OR<subjects5432>CB<subjects5432>PM<subjects5432>"+
                "<semester6643>5" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "RM<subjects5432>SM<subjects5432>FM<subjects5432>OR<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>RM<subjects5432>SM<subjects5432><days5548>" +
                "FM<subjects5432>PM<subjects5432>SM<subjects5432>RM<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>FM<subjects5432>PM<subjects5432><days5548>" +
                "SM<subjects5432>FM<subjects5432>PM<subjects5432>CB<subjects5432><days5548>" +
                "RM<subjects5432>OR<subjects5432>CB<subjects5432>PM<subjects5432>" +
                "<semester6643>6" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "MIS<subjects5432>SM & BP<subjects5432>F ECOM<subjects5432>AUDITING<subjects5432><days5548>" +
                "VAT & ST<subjects5432>MIS<subjects5432>SM & BP<subjects5432>F ECOM<subjects5432><days5548>" +
                "F ECOM<subjects5432>Int Trade<subjects5432>SM & BP<subjects5432>MIS<subjects5432><days5548>" +
                "Int Trade<subjects5432>VAT & ST<subjects5432>MIS<subjects5432>AUDITING<subjects5432><days5548>" +
                "Int Trade<subjects5432>AUDITING<subjects5432>VAT & ST<subjects5432>F ECOM<subjects5432><days5548>" +
                "AUDITING<subjects5432>Int Trade<subjects5432>VAT & ST<subjects5432>SM & BP<subjects5432>"  ;

        String semesterSplit[] = timetable.split("<semester6643>");

        String dataSplit[] =  semesterSplit[4].split("<type6280>");  // 1 = A section 2 = B section and so on....

        String timeSlots[] = dataSplit[1].split("<time2298>");


        TextView timeSlot0 = (TextView) findViewById(R.id.secondSemestertimeSlot0);
        timeSlot0.setText(timeSlots[0]);



        TextView timeSlot1 = (TextView) findViewById(R.id.secondSemestertimeSlot1);
        timeSlot1.setText(timeSlots[1]);

        TextView timeSlot2 = (TextView) findViewById(R.id.secondSemestertimeSlot2);
        timeSlot2.setText(timeSlots[2]);

        TextView timeSlot3 = (TextView) findViewById(R.id.secondSemestertimeSlot3);
        timeSlot3.setText(timeSlots[3]);



        String dayText = "default";

        if(localDayIndex == 1)
            dayText = "Monday";
        else if(localDayIndex == 2)
            dayText = "Tuesday";
        else if(localDayIndex == 3)
            dayText = "Wednesday";
        else if(localDayIndex == 4)
            dayText = "Thursday";
        else if(localDayIndex == 5)
            dayText = "Friday";
        else if(localDayIndex == 6)
            dayText = "Saturday";


        TextView dayOfTheWeek = (TextView) findViewById(R.id.secondSemesterdayOfTheWeek);
        dayOfTheWeek.setText(dayText);


        String daySlots [] = dataSplit[2].split("<days5548>");
        String subjectSlots[] = daySlots[localDayIndex - 1].split("<subjects5432>");
        TextView subjectSlot0 = (TextView) findViewById(R.id.secondSemestersubjectSlot0);
        subjectSlot0.setText(subjectSlots[0]);

        TextView subjectSlot1 = (TextView) findViewById(R.id.secondSemestersubjectSlot1);
        subjectSlot1.setText(subjectSlots[1]);

        TextView subjectSlot2 = (TextView) findViewById(R.id.secondSemestersubjectSlot2);
        subjectSlot2.setText(subjectSlots[2]);

        TextView subjectSlot3 = (TextView) findViewById(R.id.secondSemestersubjectSlot3);
        subjectSlot3.setText(subjectSlots[3]);









    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void timetableDataConversionThirdSemester(int localDayIndex)
    {

        if(dayIndexThirdSemester==7)
        {
            dayIndexThirdSemester = 1;
            localDayIndex = 1;
        }

        if(dayIndexThirdSemester==0)
        {
            dayIndexThirdSemester = 1;
            localDayIndex = 1;
        }
        String timetable = "<semester6643>1" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>2" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>3" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
                "<semester6643>4" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "RM<subjects5432>SM<subjects5432>FM<subjects5432>OR<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>RM<subjects5432>SM<subjects5432><days5548>" +
                "FM<subjects5432>PM<subjects5432>SM<subjects5432>RM<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>FM<subjects5432>PM<subjects5432><days5548>" +
                "SM<subjects5432>FM<subjects5432>PM<subjects5432>CB<subjects5432><days5548>" +
                "RM<subjects5432>OR<subjects5432>CB<subjects5432>PM<subjects5432>"+
                "<semester6643>5" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "RM<subjects5432>SM<subjects5432>FM<subjects5432>OR<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>RM<subjects5432>SM<subjects5432><days5548>" +
                "FM<subjects5432>PM<subjects5432>SM<subjects5432>RM<subjects5432><days5548>" +
                "CB<subjects5432>OR<subjects5432>FM<subjects5432>PM<subjects5432><days5548>" +
                "SM<subjects5432>FM<subjects5432>PM<subjects5432>CB<subjects5432><days5548>" +
                "RM<subjects5432>OR<subjects5432>CB<subjects5432>PM<subjects5432>" +
                "<semester6643>6" +
                "<type6280>" +
                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
                "MIS<subjects5432>SM & BP<subjects5432>F ECOM<subjects5432>AUDITING<subjects5432><days5548>" +
                "VAT & ST<subjects5432>MIS<subjects5432>SM & BP<subjects5432>F ECOM<subjects5432><days5548>" +
                "F ECOM<subjects5432>Int Trade<subjects5432>SM & BP<subjects5432>MIS<subjects5432><days5548>" +
                "Int Trade<subjects5432>VAT & ST<subjects5432>MIS<subjects5432>AUDITING<subjects5432><days5548>" +
                "Int Trade<subjects5432>AUDITING<subjects5432>VAT & ST<subjects5432>F ECOM<subjects5432><days5548>" +
                "AUDITING<subjects5432>Int Trade<subjects5432>VAT & ST<subjects5432>SM & BP<subjects5432>"  ;

        String semesterSplit[] = timetable.split("<semester6643>");

        String dataSplit[] =  semesterSplit[6].split("<type6280>");  // 1 = A section 2 = B section and so on....

        String timeSlots[] = dataSplit[1].split("<time2298>");


        TextView timeSlot0 = (TextView) findViewById(R.id.thirdSemestertimeSlot0);
        timeSlot0.setText(timeSlots[0]);



        TextView timeSlot1 = (TextView) findViewById(R.id.thirdSemestertimeSlot1);
        timeSlot1.setText(timeSlots[1]);

        TextView timeSlot2 = (TextView) findViewById(R.id.thirdSemestertimeSlot2);
        timeSlot2.setText(timeSlots[2]);

        TextView timeSlot3 = (TextView) findViewById(R.id.thirdSemestertimeSlot3);
        timeSlot3.setText(timeSlots[3]);



        String dayText = "default";

        if(localDayIndex == 1)
            dayText = "Monday";
        else if(localDayIndex == 2)
            dayText = "Tuesday";
        else if(localDayIndex == 3)
            dayText = "Wednesday";
        else if(localDayIndex == 4)
            dayText = "Thursday";
        else if(localDayIndex == 5)
            dayText = "Friday";
        else if(localDayIndex == 6)
            dayText = "Saturday";


        TextView dayOfTheWeek = (TextView) findViewById(R.id.thirdSemesterdayOfTheWeek);
        dayOfTheWeek.setText(dayText);


        String daySlots [] = dataSplit[2].split("<days5548>");
        String subjectSlots[] = daySlots[localDayIndex - 1].split("<subjects5432>");
        TextView subjectSlot0 = (TextView) findViewById(R.id.thirdSemestersubjectSlot0);
        subjectSlot0.setText(subjectSlots[0]);

        TextView subjectSlot1 = (TextView) findViewById(R.id.thirdSemestersubjectSlot1);
        subjectSlot1.setText(subjectSlots[1]);

        TextView subjectSlot2 = (TextView) findViewById(R.id.thirdSemestersubjectSlot2);
        subjectSlot2.setText(subjectSlots[2]);

        TextView subjectSlot3 = (TextView) findViewById(R.id.thirdSemestersubjectSlot3);
        subjectSlot3.setText(subjectSlots[3]);









    }
    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void generatePeriodNotification(int localDayIndex)
    {
//
//        if(dayIndex==7)
//        {
//            dayIndex = 1;
//            localDayIndex = 1;
//        }
//
//        if(dayIndex==0)
//        {
//            dayIndex = 1;
//            localDayIndex = 1;
//        }
//        String timetable = "<semester6643>1" +
//                "<type6280>" +
//                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
//                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
//                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
//                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
//                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
//                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
//                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
//                "<semester6643>2" +
//                "<type6280>" +
//                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
//                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
//                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
//                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
//                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
//                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
//                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
//                "<semester6643>3" +
//                "<type6280>" +
//                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
//                "CA<subjects5432>BS<subjects5432>ME<subjects5432>BC-II<subjects5432><days5548>" +
//                "MTP<subjects5432>CA<subjects5432>BS<subjects5432>ME<subjects5432><days5548>" +
//                "LRF<subjects5432>ME<subjects5432>CA<subjects5432>BS<subjects5432><days5548>" +
//                "BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432>CA<subjects5432><days5548>" +
//                "BS<subjects5432>BC-II<subjects5432>LRF<subjects5432>MTP<subjects5432><days5548>" +
//                "ME<subjects5432>BC-II<subjects5432>MTP<subjects5432>LRF<subjects5432>" +
//                "<semester6643>4" +
//                "<type6280>" +
//                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
//                "RM<subjects5432>SM<subjects5432>FM<subjects5432>OR<subjects5432><days5548>" +
//                "CB<subjects5432>OR<subjects5432>RM<subjects5432>SM<subjects5432><days5548>" +
//                "FM<subjects5432>PM<subjects5432>SM<subjects5432>RM<subjects5432><days5548>" +
//                "CB<subjects5432>OR<subjects5432>FM<subjects5432>PM<subjects5432><days5548>" +
//                "SM<subjects5432>FM<subjects5432>PM<subjects5432>CB<subjects5432><days5548>" +
//                "RM<subjects5432>OR<subjects5432>CB<subjects5432>PM<subjects5432>"+
//                "<semester6643>5" +
//                "<type6280>" +
//                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
//                "RM<subjects5432>SM<subjects5432>FM<subjects5432>OR<subjects5432><days5548>" +
//                "CB<subjects5432>OR<subjects5432>RM<subjects5432>SM<subjects5432><days5548>" +
//                "FM<subjects5432>PM<subjects5432>SM<subjects5432>RM<subjects5432><days5548>" +
//                "CB<subjects5432>OR<subjects5432>FM<subjects5432>PM<subjects5432><days5548>" +
//                "SM<subjects5432>FM<subjects5432>PM<subjects5432>CB<subjects5432><days5548>" +
//                "RM<subjects5432>OR<subjects5432>CB<subjects5432>PM<subjects5432>" +
//                "<semester6643>6" +
//                "<type6280>" +
//                "  10:10 -  10:55<time2298>  10:55 -  11:40<time2298>  11:40 -  12:25<time2298>  12:25 -  13:10<type6280>" +
//                "MIS<subjects5432>SM & BP<subjects5432>F ECOM<subjects5432>AUDITING<subjects5432><days5548>" +
//                "VAT & ST<subjects5432>MIS<subjects5432>SM & BP<subjects5432>F ECOM<subjects5432><days5548>" +
//                "F ECOM<subjects5432>Int Trade<subjects5432>SM & BP<subjects5432>MIS<subjects5432><days5548>" +
//                "Int Trade<subjects5432>VAT & ST<subjects5432>MIS<subjects5432>AUDITING<subjects5432><days5548>" +
//                "Int Trade<subjects5432>AUDITING<subjects5432>VAT & ST<subjects5432>F ECOM<subjects5432><days5548>" +
//                "AUDITING<subjects5432>Int Trade<subjects5432>VAT & ST<subjects5432>SM & BP<subjects5432>"  ;
//
//        String semesterSplit[] = timetable.split("<semester6643>");
//
//        String dataSplit[] =  semesterSplit[integerSemester].split("<type6280>");  // 1 = A section 2 = B section and so on....
//
//        String timeSlots[] = dataSplit[1].split("<time2298>");
//
//
//
//        int t;
//        String returnedString = "0";
//        boolean initial = false;
//        boolean limit = false;
//        boolean limit0 = false;
//        int subjectIndex = 0;
//        for(t = 0;t<timeSlots.length;t++)
//        {
//            if(t == 0)
//                initial = true;
//
//
//
//
//            returnedString = calculateTimeLeft(timeSlots[t],initial,limit);
//            initial = false;
//
//
//
//            if(!returnedString.equals("break"))
//            {
//                subjectIndex = t;
//                break;
//            }
//
//        }
//
//
//
//
//
//        String daySlots [] = dataSplit[2].split("<days5548>");
//        String subjectSlots[] = daySlots[localDayIndex - 1].split("<subjects5432>");
//
//        String constructionBody = "lets see...";
//        if(!returnedString.equals("break")) {
//            if(Integer.parseInt(returnedString)<=90) {
//
//
//                constructionBody = subjectSlots[subjectIndex] + " starts in " + returnedString + " minutes";
//                generateNotification(constructionBody);
//            }
//
//        }
//
//
//
//
//
//


    }

    public void EventList(){

        if(mode.equals("announcementViewer")) {



            newsfeedAdapter adapter = new newsfeedAdapter(this, words);


            ListView listView = (ListView) findViewById(R.id.list);


            listView.setAdapter(adapter);

            if (end) {

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        newsfeedPublic word = words.get(position);

                        //Get on clicked data
                        String exTitle = word.getMiwokTranslation();  //Title
                        String exDesc = word.getDefaultTranslation();  //Description
                        String exPublishDate = word.getPublishDate(); // Cataegories
                        String exEventDate = word.getEventDate();// Publisher Id
                        String exFullName = word.getLastDateofRegistration(); //full name
                        String exFees = word.getEntryFees(); // Unique Id
                        String exLastDateofRegistation = word.getfullName(); // Publish Date
                        String exFileAttachment = word.getFileAttachment();



                        //Save Data
                        SharedPreferences mPrefs = getSharedPreferences("label", 0);
                        SharedPreferences.Editor mEditor = mPrefs.edit();
                        mEditor.putString("title", exTitle).apply();
                        mEditor.putString("desc", exDesc).apply();
                        mEditor.putString("publishDate", exPublishDate).apply();
                        mEditor.putString("eventDate", exEventDate).apply();
                        mEditor.putString("lastDateOfRegistration", exLastDateofRegistation).apply();
                        mEditor.putString("fees", exFees).apply();
                        mEditor.putString("fullName", exFullName).apply();
                        mEditor.putString("fileAttachment", exFileAttachment).apply();


                        Intent selectIntent = new Intent(Newsfeed.this, t_Detailed_Announcement.class);
                        startActivity(selectIntent);


                    }


                });
            }
        }// End of announcementViewer mode for EventList

        if(mode.equals("notesViewer")) {



            newsfeedAdapter adapter = new newsfeedAdapter(this, words);


            ListView listView = (ListView) findViewById(R.id.list);


            listView.setAdapter(adapter);

            if(end) {

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        newsfeedPublic word = words.get(position);

                        //Get on clicked data
                        String exTitle = word.getMiwokTranslation();  //Title
                        String exDesc = word.getDefaultTranslation();  //Description
                        String exPublishDate = word.getPublishDate(); // Cataegories
                        String exEventDate = word.getEventDate();// Publisher Id
                        String exFullName = word.getLastDateofRegistration(); //full name
                        String exFees = word.getEntryFees(); // Unique Id
                        String exLastDateofRegistation = word.getfullName(); // Publish Date
                        String exFileAttachment = word.getFileAttachment();




                        //Save Data
                        SharedPreferences mPrefs = getSharedPreferences("label", 0);
                        SharedPreferences.Editor mEditor = mPrefs.edit();
                        mEditor.putString("title", exTitle).apply();
                        mEditor.putString("desc", exDesc).apply();
                        mEditor.putString("fullName", exFullName).apply();
                        mEditor.putString("fileAttachment", exFileAttachment).apply();





                        Intent selectIntent = new Intent(Newsfeed.this,t_Detailed_Notes.class);
                        startActivity(selectIntent);






                    }


                });
            }


        }else if(mode.equals("eventViewer")){


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
                        String exMode = word.getMode();
                        if(exMode.contains("EVENTS")) {
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
                            mEditor.putString("title", exTitle).apply();
                            mEditor.putString("desc", exDesc).apply();
                            mEditor.putString("publishDate", exPublishDate).apply();
                            mEditor.putString("eventDate", exEventDate).apply();
                            mEditor.putString("lastDateOfRegistration", exLastDateofRegistation).apply();
                            mEditor.putString("fees", exFees).apply();
                            mEditor.putString("fullName", exFullName).apply();


                            Intent selectIntent = new Intent(Newsfeed.this, DetailedEvent.class);
                            startActivity(selectIntent);


                        }else if(exMode.contains("ANNOUNCEMENTS"))
                        {
                            String exTitle = word.getMiwokTranslation();  //Title
                            String exDesc = word.getDefaultTranslation();  //Description
                            String exPublishDate = word.getPublishDate(); // Cataegories
                            String exEventDate = word.getEventDate();// Publisher Id
                            String exFullName = word.getLastDateofRegistration(); //full name
                            String exFees = word.getEntryFees(); // Unique Id
                            String exLastDateofRegistation = word.getfullName(); // Publish Date
                            String exFileAttachment = word.getFileAttachment();



                            //Save Data
                            SharedPreferences mPrefs = getSharedPreferences("label", 0);
                            SharedPreferences.Editor mEditor = mPrefs.edit();
                            mEditor.putString("title", exTitle).apply();
                            mEditor.putString("desc", exDesc).apply();
                            mEditor.putString("publishDate", exPublishDate).apply();
                            mEditor.putString("eventDate", exEventDate).apply();
                            mEditor.putString("lastDateOfRegistration", exLastDateofRegistation).apply();
                            mEditor.putString("fees", exFees).apply();
                            mEditor.putString("fullName", exFullName).apply();
                            mEditor.putString("fileAttachment", exFileAttachment).apply();


                            Intent selectIntent = new Intent(Newsfeed.this, t_Detailed_Announcement.class);
                            startActivity(selectIntent);

                        }else
                        {
                            String exTitle = word.getMiwokTranslation();  //Title
                            String exDesc = word.getDefaultTranslation();  //Description
                            String exPublishDate = word.getPublishDate(); // Cataegories
                            String exEventDate = word.getEventDate();// Publisher Id
                            String exFullName = word.getLastDateofRegistration(); //full name
                            String exFees = word.getEntryFees(); // Unique Id
                            String exLastDateofRegistation = word.getfullName(); // Publish Date
                            String exFileAttachment = word.getFileAttachment();




                            //Save Data
                            SharedPreferences mPrefs = getSharedPreferences("label", 0);
                            SharedPreferences.Editor mEditor = mPrefs.edit();
                            mEditor.putString("title", exTitle).apply();
                            mEditor.putString("desc", exDesc).apply();
                            mEditor.putString("fullName", exFullName).apply();
                            mEditor.putString("fileAttachment", exFileAttachment).apply();




                            if(exFileAttachment.length()>4 && exFileAttachment.length()<20) {
                                Intent selectIntent = new Intent(Newsfeed.this, PlayActivity.class);
                                startActivity(selectIntent);

                            }else
                            {
                                Intent selectIntent = new Intent(Newsfeed.this, t_Detailed_Notes.class);
                                startActivity(selectIntent);
                            }


                        }


                    }

                });
            }

        }else
        {



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
                        String exMode = word.getMode();
                        if(exMode.contains("EVENTS")) {
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
                            mEditor.putString("title", exTitle).apply();
                            mEditor.putString("desc", exDesc).apply();
                            mEditor.putString("publishDate", exPublishDate).apply();
                            mEditor.putString("eventDate", exEventDate).apply();
                            mEditor.putString("lastDateOfRegistration", exLastDateofRegistation).apply();
                            mEditor.putString("fees", exFees).apply();
                            mEditor.putString("fullName", exFullName).apply();


                            Intent selectIntent = new Intent(Newsfeed.this, DetailedEvent.class);
                            startActivity(selectIntent);


                        }else if(exMode.contains("ANNOUNCEMENTS"))
                        {
                            String exTitle = word.getMiwokTranslation();  //Title
                            String exDesc = word.getDefaultTranslation();  //Description
                            String exPublishDate = word.getPublishDate(); // Cataegories
                            String exEventDate = word.getEventDate();// Publisher Id
                            String exFullName = word.getLastDateofRegistration(); //full name
                            String exFees = word.getEntryFees(); // Unique Id
                            String exLastDateofRegistation = word.getfullName(); // Publish Date
                            String exFileAttachment = word.getFileAttachment();



                            //Save Data
                            SharedPreferences mPrefs = getSharedPreferences("label", 0);
                            SharedPreferences.Editor mEditor = mPrefs.edit();
                            mEditor.putString("title", exTitle).apply();
                            mEditor.putString("desc", exDesc).apply();
                            mEditor.putString("publishDate", exPublishDate).apply();
                            mEditor.putString("eventDate", exEventDate).apply();
                            mEditor.putString("lastDateOfRegistration", exLastDateofRegistation).apply();
                            mEditor.putString("fees", exFees).apply();
                            mEditor.putString("fullName", exFullName).apply();
                            mEditor.putString("fileAttachment", exFileAttachment).apply();


                            Intent selectIntent = new Intent(Newsfeed.this, t_Detailed_Announcement.class);
                            startActivity(selectIntent);

                        }else
                        {
                            String exTitle = word.getMiwokTranslation();  //Title
                            String exDesc = word.getDefaultTranslation();  //Description
                            String exPublishDate = word.getPublishDate(); // Cataegories
                            String exEventDate = word.getEventDate();// Publisher Id
                            String exFullName = word.getLastDateofRegistration(); //full name
                            String exFees = word.getEntryFees(); // Unique Id
                            String exLastDateofRegistation = word.getfullName(); // Publish Date
                            String exFileAttachment = word.getFileAttachment();




                            //Save Data
                            SharedPreferences mPrefs = getSharedPreferences("label", 0);
                            SharedPreferences.Editor mEditor = mPrefs.edit();
                            mEditor.putString("title", exTitle).apply();
                            mEditor.putString("desc", exDesc).apply();
                            mEditor.putString("fullName", exFullName).apply();
                            mEditor.putString("fileAttachment", exFileAttachment).apply();




                            if(exFileAttachment.length()>4 && exFileAttachment.length()<20) {
                                Intent selectIntent = new Intent(Newsfeed.this, PlayActivity.class);
                                startActivity(selectIntent);

                            }else
                            {
                                Intent selectIntent = new Intent(Newsfeed.this, t_Detailed_Notes.class);
                                startActivity(selectIntent);
                            }


                        }


                    }

                });
            }

        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickRightArrow(View v)
    {
        timetableDataConversion(++dayIndex);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickLeftArrow(View v)
    {
        timetableDataConversion(--dayIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickRightArrowSecondSemester(View v)
    {
        timetableDataConversionSecondSemester(++dayIndexSecondSemester);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickLeftArrowSecondSemester(View v)
    {
        timetableDataConversionSecondSemester(--dayIndexSecondSemester);
    }


    public void onClickTimetableDrawerHandle(View v)
    {
        LinearLayout secondSemesterTimetableLayout = (LinearLayout) findViewById(R.id.secondSemesterTimetableLayout);
        LinearLayout thirdSemesterTimetableLayout = (LinearLayout) findViewById(R.id.thirdSemesterTimetableLayout);
        TextView timetableDrawerHandle = (TextView) findViewById(R.id.timetableDrawerHandle);
        if(isTimetableDrawerOpen)
        {
            secondSemesterTimetableLayout.setVisibility(View.GONE);
            thirdSemesterTimetableLayout.setVisibility(View.GONE);
            timetableDrawerHandle.setText("▼");
            isTimetableDrawerOpen = false;
        }else
        {

            secondSemesterTimetableLayout.setVisibility(View.VISIBLE);
            thirdSemesterTimetableLayout.setVisibility(View.VISIBLE);
            timetableDrawerHandle.setText("▲");
            isTimetableDrawerOpen = true;
        }

    }
    public void onClickTimetable(View v)
    {

        LinearLayout firstSemeter = (LinearLayout) findViewById(R.id.firstSemesterTimetableLayout);
        LinearLayout secondSemeter = (LinearLayout) findViewById(R.id.secondSemesterTimetableLayout);
        LinearLayout thirdSemeter = (LinearLayout) findViewById(R.id.thirdSemesterTimetableLayout);
        TextView arrowLayout = (TextView) findViewById(R.id.timetableDrawerHandle);

        if(timetableVisible)
        {

            firstSemeter.setVisibility(View.GONE);
            secondSemeter.setVisibility(View.GONE);
            thirdSemeter.setVisibility(View.GONE);
            arrowLayout.setVisibility(View.GONE);
            timetableVisible =false;
        }else
        {

            firstSemeter.setVisibility(View.VISIBLE);
            secondSemeter.setVisibility(View.VISIBLE);
            thirdSemeter.setVisibility(View.VISIBLE);
            arrowLayout.setVisibility(View.VISIBLE);
            timetableVisible = true;
        }


    }




    public void onClickAttendance(View v)
    {
        Intent selectIntent = new Intent(Newsfeed.this,t_Attendance.class);
        startActivity(selectIntent);


    }


    public void onClickAnnouncement(View v)
    {
        Intent selectIntent = new Intent(Newsfeed.this,t_Announcement_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickNotes(View v)
    {
        Intent selectIntent = new Intent(Newsfeed.this,t_notes_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickEvents(View v)
    {
        Intent selectIntent = new Intent(Newsfeed.this,EventViewer.class);
        startActivity(selectIntent);


    }

    public void onClickProfile(View v)
    {

        Intent selectIntent = new Intent(Newsfeed.this,Newsfeed.class);
        startActivity(selectIntent);


    }
    public void onClickProfileButton(View v)
    {
        Intent selectIntent = new Intent(Newsfeed.this,t_Teacher_Profile.class);
        startActivity(selectIntent);


    }

    public void onClickPlus(View v)
    {
        Intent selectIntent = new Intent(Newsfeed.this,t_Announcement_Writer.class);
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



    public void loadUse()
    {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        boolean firstUse = mPrefs.getBoolean("firstUse", true);

        appFirstUse = firstUse;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickOk(View v) {
        Spinner deptSpinner = (Spinner) findViewById(R.id.spinner_department);
        String dept_filter = String.valueOf(deptSpinner.getSelectedItem());

        if(dept_filter.equals("Department"))
        {
            Toast.makeText(this, "Please choose your Department" , Toast.LENGTH_SHORT).show();
        }
        else{
            globalDepartment = dept_filter;
            saveDepartment();
            RelativeLayout whatDepartment = (RelativeLayout) findViewById(R.id.whatDepartment);
            whatDepartment.setVisibility(View.GONE);
            dept_filter = sDepartment;

            semester_filter = sSemester;

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);


            dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            timetableDataConversion(dayIndex);

            getResultsFromApi();

        }

    }
    public void saveDepartment(){


        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();

        mEditor.putString("Department", globalDepartment).apply();
        FirebaseMessaging.getInstance().subscribeToTopic("global");
        FirebaseMessaging.getInstance().subscribeToTopic(globalDepartment+"Teachers");
        mEditor.putBoolean("firebaseSubscribed", true).apply();


    }
    public void saveDataArray(String dataArray){


        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("dataArray", dataArray).apply();

    }

    public void loadDataArray(){


        SharedPreferences mPrefs = getSharedPreferences("label", 0);


        globalDataArrayString = mPrefs.getString("dataArray", "unknown");


    }
    public boolean timestampCompare(String timestampRetrieved, String timestampStored){


        return !timestampRetrieved.equals(timestampStored);

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

    public void postEventViewerMode()
    {

        String newsfeedAnnouncementsString;
        newsfeedAnnouncementsString = newsfeedAnnouncements.toString().replace("[","");
        newsfeedAnnouncementsString = newsfeedAnnouncementsString.replace("]","");
        String newsfeedNotesString;
        newsfeedNotesString = newsfeedNotes.toString().replace("[","");
        newsfeedNotesString = newsfeedNotesString.replace("]","");
        String newsfeedEventString = newsfeedEvents.toString().replace("[","");
         newsfeedEventString = newsfeedEventString.replace("]","");
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
        newsfeedAnnouncementTimestamp = newsfeedAnnouncementTimestampHolder;
        newsfeedEventTimestamp = newsfeedEventTimestampHolder;
        newsfeedNotesTimestamp = newsfeedNotesTimestampHolder;
        videoInfoViewerTimestamp = videoInfoViewerTimestampHolder;



        saveTimestamps();




        if(newsfeedNotesString.length()>5)
            dataRetrieved = dataRetrieved  + "," +  newsfeedNotesString;

        if(newsfeedEventString.length()>5)
            dataRetrieved = dataRetrieved  + "," +  newsfeedEventString;

        String concatenatedData =  storedData + dataRetrieved + "]";

        int maxLogSize = 1000;
        for(int i = 0; i <= concatenatedData.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > concatenatedData.length() ? concatenatedData.length() : end;
        }



        sortDataByDate(concatenatedData);
        mProgress.hide();
        EventList();

        hideLoading();

    }

    public  void showLoading()
    {
        RelativeLayout progressLayout  = (RelativeLayout) findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.VISIBLE);
    }
    public  void hideLoading()
    {
        RelativeLayout progressLayout  = (RelativeLayout) findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.GONE);
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void generateNotification(String body) {




        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (getApplicationContext()).setContentTitle("tiltle").setContentText(body).
                setContentTitle("Next Period").setSmallIcon(R.drawable.notification_icon).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);
    }

    public String calculateTimeLeft(String timeSlots, boolean intial, boolean limit)
    {
        int indexNo = 0;
        String timeDivide []  = timeSlots.split("-");
        String periodEnd = timeDivide[indexNo].trim();
        String periodEndTime[] = periodEnd.split(":");
        int periodEndTimeMinutes = Integer.parseInt(periodEndTime[1]);
        int periodEndTimeHours = Integer.parseInt(periodEndTime[0]);




        String currentDateTimeString = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String currentTime[] = currentDateTimeString.split(":");
        int currentTimeMinutes = Integer.parseInt(currentTime[1]);
        int currentTimeHours = Integer.parseInt(currentTime[0]);

        if(periodEndTimeHours < currentTimeHours){
            return "break";
        }
        if(periodEndTimeMinutes < currentTimeMinutes)  // 11:40 < 11:53
        {
            if(periodEndTimeHours <= currentTimeHours) //  11:40 <= 11:53
            {
                return "break";
            }
        }


        if(periodEndTimeHours > currentTimeHours)
        {
            int hoursLeft = periodEndTimeHours - currentTimeHours;
            int differenceInHours = periodEndTimeHours - currentTimeHours;
            int minutesLeft = periodEndTimeMinutes + 60 * hoursLeft - currentTimeMinutes;
            return String.valueOf(minutesLeft);
        }
        if(periodEndTimeHours == currentTimeHours)
        {

            int minutesLeft = periodEndTimeMinutes  - currentTimeMinutes;
            return String.valueOf(minutesLeft);
        }
        return "nothing";
    }



    final Handler handler = new Handler();
    Timer    timer = new Timer();
    TimerTask doAsynchronousTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @SuppressWarnings("unchecked")
                public void run() {
                    try {
                        Date now = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(now);
                        dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                        generatePeriodNotification(3);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                    }
                }
            });
        }
    };



    public void onUpdateAvailable()
    {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String today = df.format(c.getTime());

        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        String savedDate = mPrefs.getString("checkUploadDate", "default_value_if_variable_not_found");

        if(!savedDate.equals(today)) {

            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString("checkUploadDate", today).apply();

            LinearLayout updateAvailableLayout = (LinearLayout) findViewById(R.id.updateAvailableLayout);
            TextView updateNotesTextView = (TextView) findViewById(R.id.updateNotes);
            updateAvailableLayout.setVisibility(View.VISIBLE);
            updateNotesTextView.setText(updateNotes);

        }
    }

    public void onClickDownloadUpdate(View v) {
        LinearLayout updateAvailableLayout = (LinearLayout) findViewById(R.id.updateAvailableLayout);

        updateAvailableLayout.setVisibility(View.GONE);
        Uri webpage = Uri.parse(updateUrl.trim());
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        try {
            startActivity(webIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }




    }

    public  void onClickLater(View v)
    {
        LinearLayout updateAvailableLayout = (LinearLayout) findViewById(R.id.updateAvailableLayout);

        updateAvailableLayout.setVisibility(View.GONE);
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
    private void hideOnScroll()
    {
        LinearLayout timeTableOverallLayout = (LinearLayout) findViewById(R.id.timetableNewsfeedLayout);
        timeTableOverallLayout.setVisibility(View.GONE);
    }

    private void showOnScroll()
    {
        LinearLayout filterBar = (LinearLayout) findViewById(R.id.timetableNewsfeedLayout);
        filterBar.setVisibility(View.VISIBLE);

    }

}