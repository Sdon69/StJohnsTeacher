package com.theironfoundry8890.stjohnsteacher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedEvent extends AppCompatActivity {

    private String expTitle;
    private String expDesc;
    private String expPublishDate;
    private String expEventDate;
    private String expLastDateofRegistration;
    private String expEntryFees;
    private String expFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_event);
        loadData();
        colorCheck();




    }

    public void loadData(){

        //Loading Data via ShredPreferences
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        String TitleString = mPrefs.getString("title", "default_value_if_variable_not_found");
        String DescriptionString = mPrefs.getString("desc", "default_value_if_variable_not_found");
        String publishString = mPrefs.getString("publishDate", "default_value_if_variable_not_found");
        String eventDateString = mPrefs.getString("eventDate", "default_value_if_variable_not_found");
        String lastDateofRegString = mPrefs.getString("lastDateOfRegistration", "default_value_if_variable_not_found");
        String feesString = mPrefs.getString("fees", "default_value_if_variable_not_found");
        String fullName = mPrefs.getString("fullName", "default_value_if_variable_not_found");


        //Initializing using above variables
        expTitle = TitleString;
        expDesc = DescriptionString;
        expPublishDate = publishString;
        expEventDate = eventDateString;
        expLastDateofRegistration = lastDateofRegString;
        expEntryFees = feesString;
        expFullName = fullName;

        IntializeData();





        Log.v(TitleString , DescriptionString);
    }

    public void IntializeData()
    {
        //Calling ids from layout
        TextView titleTextView = (TextView) findViewById(R.id.detailedlaytitle);
        TextView descTextView = (TextView) findViewById(R.id.detailedlaydesc);
        TextView publishDateTextView = (TextView) findViewById(R.id.detailedlay_publishDate);
        TextView eventDateTextView = (TextView) findViewById(R.id.detailedlay_EventDate);
        TextView lastDateofRegTextView = (TextView) findViewById(R.id.detailedlay_EndDateofReg);
        TextView entryfeesTextView = (TextView) findViewById(R.id.detailedlay_entryfees);
        TextView fullNameTextView = (TextView) findViewById(R.id.detailedlay_fullName);

        //Setting values to layouts
        titleTextView.setText(expTitle);
        descTextView.setText(expDesc);
        publishDateTextView.setText(expPublishDate);
        eventDateTextView.setText(expEventDate);
        lastDateofRegTextView.setText(expLastDateofRegistration);
        entryfeesTextView.setText(expEntryFees);
        fullNameTextView.setText(expFullName);



    }

    private void colorCheck() {

        ImageView attendanceImageView = (ImageView) findViewById(R.id.attendance);
        ImageView announcementImageView = (ImageView) findViewById(R.id.announcement);
        ImageView notesImageView = (ImageView) findViewById(R.id.notes);
        ImageView eventsImageView = (ImageView) findViewById(R.id.events);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile);

        int a = 3;




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
}
