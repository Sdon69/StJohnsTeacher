package com.theironfoundry8890.stjohnsteacher;

import android.app.Activity;

/**
 * Created by Sdon69 on 18-08-2017.
 */

public class students {
    private String mDefaultTranslation;

    private String mMiwokTranslation;

    private String mPublishDate;

    private String mEventDate;

    private String mLastDateofRegistration;

    private String mPublisher;

    private String mEntryFees;

    private String mFullName;

    private int mImageResourceId = -1 ;

    private int mSongResourceId ;

    private Activity context;


    public students(String defaultTranslation) {

        mDefaultTranslation = defaultTranslation;

    }


    public students(String defaultTranslation, String eventDate) {

        mDefaultTranslation = defaultTranslation;
        mEventDate = eventDate;
    }


    public students(String defaultTranslation, String miwokTranslation , String publishDate ,
                    String eventDate , String lastDateofRegistration, String entryFees , String fullName )
    {
        mDefaultTranslation = defaultTranslation;
        mMiwokTranslation = miwokTranslation;
        mPublishDate = publishDate;
        mEventDate = eventDate;
        mLastDateofRegistration = lastDateofRegistration;
        mEntryFees = entryFees;
        mFullName = fullName;
    }

    public students(String defaultTranslation, String miwokTranslation , String publishDate ,
                    String eventDate , String lastDateofRegistration , String fullName)
    {
        mDefaultTranslation = defaultTranslation;
        mMiwokTranslation = miwokTranslation;
        mPublishDate = publishDate;

        mFullName = fullName;

    }






    public String getDefaultTranslation()
    {
        return mDefaultTranslation;
    }

    public String getMiwokTranslation() {
        return mMiwokTranslation;
    }


    public String getPublishDate() {
        return mPublishDate;
    }

    public String getEventDate() {
        return mEventDate;
    }

    public String getLastDateofRegistration() {
        return mLastDateofRegistration;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getfullName (){return  mFullName;}



    public String getEntryFees() {
        return mEntryFees ;
    }





    public int getImageResourceId() {
        return mImageResourceId;
    }

    public int getsongId() {
        return mSongResourceId;
    }

    public Activity getContext() {
        return context;
    }

    public boolean hasImage(){
        return mImageResourceId != -1;


    }
}
