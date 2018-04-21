package com.theironfoundry8890.stjohnsteacher.youtubeDataUploader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.theironfoundry8890.stjohnsteacher.R;
import com.theironfoundry8890.stjohnsteacher.t_VideoInfoWriter;

public class ReviewActivity extends Activity {
    VideoView mVideoView;
    MediaController mc;
    private String mChosenAccountName;
    private Uri mFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.youtube_uploader_activity_review);
        Button uploadButton = (Button) findViewById(R.id.upload_button);
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            uploadButton.setVisibility(View.GONE);
            setTitle(R.string.playing_the_video_in_upload_progress);
        }
        mFileUri = intent.getData();
        loadAccount();

        reviewVideo(mFileUri);
    }

    private void reviewVideo(Uri mFileUri) {
        try {
            mVideoView = (VideoView) findViewById(R.id.videoView);
            mc = new MediaController(this);
            mVideoView.setMediaController(mc);
            mVideoView.setVideoURI(mFileUri);
            mc.show();
            mVideoView.start();
        } catch (Exception e) {
            Log.e(this.getLocalClassName(), e.toString());
        }
    }

    private void loadAccount() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        mChosenAccountName = sp.getString(youtubeUploadMainActivity.ACCOUNT_KEY, null);
        invalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    public void uploadVideo() {
        if (mChosenAccountName == null) {
            return;
        }
        // if a video is picked or recorded.
        if (mFileUri != null) {
            Intent uploadIntent = new Intent(this, UploadService.class);
            uploadIntent.setData(mFileUri);
            uploadIntent.putExtra(youtubeUploadMainActivity.ACCOUNT_KEY, mChosenAccountName);
            startService(uploadIntent);


            Toast.makeText(this, R.string.youtube_upload_started,
                    Toast.LENGTH_LONG).show();

            Intent selectIntent = new Intent(ReviewActivity.this, t_VideoInfoWriter.class);
            startActivity(selectIntent);

            // Go back to youtubeUploadMainActivity after upload
//            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void submitInfo(View view) {



        //Retriving data from layout
        EditText lEventTitle =  (EditText) findViewById(R.id.videoTitle);
        EditText lEventDesc =  (EditText) findViewById(R.id.videoDescription);

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
        String title = String.valueOf(lEventTitle.getText());
        String description = String.valueOf(lEventDesc.getText());


        //Checkboxes for Genre
        boolean art = lGArt.isChecked();
        boolean education= lGEducation.isChecked();
        boolean commerce = lGCommerce.isChecked();
        boolean otherSubjects = lGOther.isChecked();
        boolean management = lGManagement.isChecked();
        boolean science = lGScience.isChecked();

        //Checkboxes for Semesters
        boolean semesterOne = lSemster1.isChecked();
        boolean semesterTwo= lSemster2.isChecked();
        boolean semesterThree = lSemster3.isChecked();
        boolean semesterFour = lSemster4.isChecked();
        boolean semesterFive = lSemster5.isChecked();
        boolean semesterSix = lSemster6.isChecked();



        //Password Security Check

        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        String subCataegories = "All Departments All Semesters";

        if (title.length() >= 1) {
            if (description.length() >= 1) {
                if (description.length() <= 49000) {
                    if (true) {
                        if (true) {
                            if(art || commerce || management ||  science || education|| otherSubjects ) {
                                if(semesterOne || semesterTwo || semesterThree ||  semesterFour || semesterFive|| semesterSix ) {

//Concatenating a string to check which cataegory is present
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


                                    SharedPreferences.Editor mEditor = mPrefs.edit();
                                    mEditor.putString("videoTitle", title).commit();
                                    mEditor.putString("videoDescription", description).commit();
                                    mEditor.putString("videoCataegories", subCataegories).commit();


                                    uploadVideo();


                                }else {
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

                    Toast.makeText(getApplicationContext(), "Description field character limit is 49000",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Description field is blank",
                        Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(getApplicationContext(), "Title field is blank",
                    Toast.LENGTH_SHORT).show();
        }







    }



}
