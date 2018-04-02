package com.theironfoundry8890.stjohnsteacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class dStudentProfile extends Activity
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

    private boolean appFirstUse;


    private String firstName;
    private String lastName;
    private String id;
    private String phoneNo;
    private String email;
    private String pass;
    private String className;
    private String section;
    private String semester;

    private String dFName;
    private String dLName;
    private String dClass;
    private String dEmail;
    private String dSection;
    private String dId;
    private String dPassword;
    private String dPhone;
    private List dRow;

    private int a;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;




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

        loadUse();

        if(appFirstUse)
        {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            Toast.makeText(this, String.valueOf(appFirstUse) , Toast.LENGTH_SHORT).show();
//            setContentView(R.layout.wr);
//
//            swipe();
        }
        else {
            setContentView(R.layout.profile_lay);

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


        String FirstName = mPrefs.getString("FirstName", "default_value_if_variable_not_found");
        String LastName = mPrefs.getString("LastName", "default_value_if_variable_not_found");
        String PhoneNo = mPrefs.getString("Phone", "default_value_if_variable_not_found");
        String Email = mPrefs.getString("Email", "default_value_if_variable_not_found");
        String Id = mPrefs.getString("tag", "default_value_if_variable_not_found");
        String Pass = mPrefs.getString("pass", "default_value_if_variable_not_found");
        String Section = mPrefs.getString("Section", "default_value_if_variable_not_found");
        String Class = mPrefs.getString("Class", "default_value_if_variable_not_found");
        String Semester = mPrefs.getString("Semester", "default_value_if_variable_not_found");


        //Initializing using above variables


        firstName = FirstName;
        lastName = LastName;
        phoneNo = PhoneNo;
        sId = Id;
        email = Email;
        pass = Pass;
        className = Class;
        section = Section;
        semester = Semester;


        Log.v("details",firstName.concat(lastName + phoneNo + sId + email + pass + semester));


        IntializeData();


    }

    public void loadUse()
    {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        boolean firstUse = mPrefs.getBoolean("firstUse", true);

        appFirstUse = firstUse;
    }

    public void IntializeData() {
        TextView fName = (TextView) findViewById(R.id.pFirstName);
        TextView lName = (TextView) findViewById(R.id.pLastName);
        TextView Email = (TextView) findViewById(R.id.pEmail);
        TextView Phone = (TextView) findViewById(R.id.pPhoneNo);
        TextView TextView_id = (TextView) findViewById(R.id.pUser_id);
        TextView Class = (TextView) findViewById(R.id.pClass);
        TextView Section = (TextView) findViewById(R.id.pSection);
        TextView Semester = (TextView) findViewById(R.id.pSemester);


        fName.setText(firstName);
        lName.setText(lastName);
        Email.setText(email);
        Phone.setText(phoneNo);
        TextView_id.setText(sId);
        Class.setText(className);
        Section.setText(section);
        Semester.setText(semester);



    }

    public void onClickintro(View v) {

        Intent selectIntent = new Intent(dStudentProfile.this, s_EditProfileActivity.class);
        startActivity(selectIntent);


    }


    private void swipe() {

        TextView head2 = (TextView) findViewById(R.id.head2);
        TextView head1 = (TextView) findViewById(R.id.head);

        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);



        if(a==0){
            head1.setText("Instant Attendance");
            head2.setText("Get Attendance using \n the instructor guided code (IGC)");

            button1.setBackgroundResource(R.drawable.grey_button);
            button2.setBackgroundResource(R.drawable.blue_button);
            button3.setBackgroundResource(R.drawable.blue_button);


        }


        if(a==1) {
            head1.setText("Onetime Signup");
            head2.setText("Easy and Quick Signup \n with Unique ID");
            button1.setBackgroundResource(R.drawable.blue_button);
            button2.setBackgroundResource(R.drawable.grey_button);
            button3.setBackgroundResource(R.drawable.blue_button);


        }

        if(a==2) {
            head1.setText("Innovation");
            head2.setText("New Update every Month on \n  basis of Your Feedback");

            button1.setBackgroundResource(R.drawable.blue_button);
            button2.setBackgroundResource(R.drawable.blue_button);
            button3.setBackgroundResource(R.drawable.grey_button);
        }

        if(a==3) {
            Intent selectIntent = new Intent(dStudentProfile.this,signin.class);
            startActivity(selectIntent);
        }









    }




    public void onClickButton0(View v) {

        a=0;
        swipe();


    }

    public void onClickButton1(View v) {

        a=1;
        swipe();


    }

    public void onClickButton2(View v) {

        a=2;
        swipe();


    }

    private void colorCheck() {

        ImageView attendanceImageView = (ImageView) findViewById(R.id.attendance);
        ImageView announcementImageView = (ImageView) findViewById(R.id.announcement);
        ImageView notesImageView = (ImageView) findViewById(R.id.notes);
        ImageView eventsImageView = (ImageView) findViewById(R.id.events);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile);

        a = 4;




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

    public void onClickAttendance(View v) {
        Intent selectIntent = new Intent(dStudentProfile.this, t_Attendance.class);
        startActivity(selectIntent);


    }

    public void onClickAnnouncement(View v) {
        Intent selectIntent = new Intent(dStudentProfile.this, t_Announcement_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickNotes(View v) {
        Intent selectIntent = new Intent(dStudentProfile.this, t_notes_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickEvents(View v) {
        Intent selectIntent = new Intent(dStudentProfile.this, EventViewer.class);
        startActivity(selectIntent);


    }

    public void onClickProfile(View v) {
        Intent selectIntent = new Intent(dStudentProfile.this, t_Teacher_Profile.class);
        startActivity(selectIntent);


    }


}
