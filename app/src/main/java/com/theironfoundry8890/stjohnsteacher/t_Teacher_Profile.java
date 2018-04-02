package com.theironfoundry8890.stjohnsteacher;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class t_Teacher_Profile extends AppCompatActivity implements  EasyPermissions.PermissionCallbacks{
    private String firstName;
    private String lastName;
    private String id;
    private String phoneNo;
    private String email;
    private String pass;

    private boolean appFirstUse;
    private boolean appReset;

    private int a = 4;
    private int intro = 0;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUse();
        Log.v("appreset", String.valueOf(appReset));
        EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account (via Contacts).",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS);


        if(appFirstUse)
        {

//            Toast.makeText(this, String.valueOf("Swipe Left") , Toast.LENGTH_SHORT).show();
//            setContentView(R.layout.wr);
//
//            swipe();


            Intent selectIntent = new Intent(t_Teacher_Profile.this,t_Signin.class);
            startActivity(selectIntent);
        }
        else{
            setContentView(R.layout.t_profile_lay);
            loadData();
            colorCheck();
        }



    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (appFirstUse) {
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // Left to Right swipe action
                        if (x2 > x1) {

                            intro--;
                            swipe();



                        }

                        // Right to left swipe action
                        else {
                            if (intro != 5) {  //next
                                intro++;
                                swipe();

                            }
                        }

                    } else {
                        // consider as something else - a screen tap for example
                    }
                    break;
                }
        }
        return super.onTouchEvent(event);
    }


    public void loadData() {

        //Loading Data via ShredPreferences
        SharedPreferences mPrefs = getSharedPreferences("label", 0);


        String FirstName = mPrefs.getString("tFirstName", "default_value_if_variable_not_found");
        String LastName = mPrefs.getString("tLastName", "default_value_if_variable_not_found");
        String PhoneNo = mPrefs.getString("tPhone", "default_value_if_variable_not_found");
        String Email = mPrefs.getString("tEmail", "default_value_if_variable_not_found");
        String Id = mPrefs.getString("ttag", "default_value_if_variable_not_found");
        String Pass = mPrefs.getString("tpass", "default_value_if_variable_not_found");


        //Initializing using above variables


        firstName = FirstName;
        lastName = LastName;
        phoneNo = PhoneNo;
        id = Id;
        email = Email;
        pass = Pass;


        IntializeData();


    }

    public void IntializeData() {
        TextView fName = (TextView) findViewById(R.id.pFirstName);
        TextView lName = (TextView) findViewById(R.id.pLastName);
        TextView Email = (TextView) findViewById(R.id.pEmail);
        TextView Phone = (TextView) findViewById(R.id.pPhoneNo);
        TextView TextView_id = (TextView) findViewById(R.id.pUser_id);


        fName.setText(firstName);
        lName.setText(lastName);
        Email.setText(email);
        Phone.setText(phoneNo);
        TextView_id.setText(id);


    }

    public void onClickintro(View v) {

        Intent selectIntent = new Intent(t_Teacher_Profile.this, t_EditProfileActivity.class);
        startActivity(selectIntent);


    }

    public void onClickChangePassword(View v) {

        Intent selectIntent = new Intent(t_Teacher_Profile.this, t_current_password_check.class);
        startActivity(selectIntent);


    }

    public void onClickAttendance(View v) {
        Intent selectIntent = new Intent(t_Teacher_Profile.this, t_Attendance.class);
        startActivity(selectIntent);


    }

    public void onClickAnnouncement(View v) {
        Intent selectIntent = new Intent(t_Teacher_Profile.this, t_Announcement_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickNotes(View v) {
        Intent selectIntent = new Intent(t_Teacher_Profile.this, t_notes_Viewer.class);
        startActivity(selectIntent);


    }

    public void onClickEvents(View v) {
        Intent selectIntent = new Intent(t_Teacher_Profile.this, EventViewer.class);
        startActivity(selectIntent);


    }

    public void onClickProfile(View v) {
        Intent selectIntent = new Intent(t_Teacher_Profile.this, t_Teacher_Profile.class);
        startActivity(selectIntent);


    }


    private void swipe() {

        TextView head2 = (TextView) findViewById(R.id.head2);
        TextView head1 = (TextView) findViewById(R.id.head);

        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);



        if(intro==0){
            head1.setText("Instant Attendance");
            head2.setText("Get Attendance using \n the instructor guided code (IGC)");

            button1.setBackgroundResource(R.drawable.grey_button);
            button2.setBackgroundResource(R.drawable.blue_button);
            button3.setBackgroundResource(R.drawable.blue_button);


        }


        if(intro==1) {
            head1.setText("Onetime Signup");
            head2.setText("Easy and Quick Signup \n with Unique ID");
            button1.setBackgroundResource(R.drawable.blue_button);
            button2.setBackgroundResource(R.drawable.grey_button);
            button3.setBackgroundResource(R.drawable.blue_button);


        }

        if(intro==2) {
            head1.setText("Innovation");
            head2.setText("New Update every Month on \n  basis of Your Feedback");

            button1.setBackgroundResource(R.drawable.blue_button);
            button2.setBackgroundResource(R.drawable.blue_button);
            button3.setBackgroundResource(R.drawable.grey_button);
        }

        if(intro==3) {
            Intent selectIntent = new Intent(t_Teacher_Profile.this,t_Signin.class);
            startActivity(selectIntent);
        }









    }

    public void loadUse()
    {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        boolean firstUse = mPrefs.getBoolean("firstUse", true);


        appFirstUse = firstUse;

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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
