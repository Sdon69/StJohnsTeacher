package com.theironfoundry8890.stjohnsteacher;

import android.util.Log;

/**
 * Created by Sdon69 on 18-08-2017.
 */

public class newsfeedPublic {
    private String mDefaultTranslation;

    private String mMiwokTranslation;

    private String mPublishDate;

    private String mEventDate;

    private String mLastDateofRegistration;

    private String mEntryFees;

    private String mFullName;

    private String mFileAttachment;

    private String mMode;

    private String mEliminationMode;

    private String mEliminationUniqueId;

    private String mTimestamp;








    public newsfeedPublic(String defaultTranslation, String miwokTranslation , String publishDate ,
                          String eventDate , String lastDateofRegistration,
                          String entryFees , String fullName,String mode ,String timestamp )
    {
        mDefaultTranslation = defaultTranslation;
        mMiwokTranslation = miwokTranslation;
        mPublishDate = publishDate;
        mEventDate = eventDate;
        mLastDateofRegistration = lastDateofRegistration;
        mEntryFees = entryFees;
        mFullName = fullName;
        mMode = mode;
        mTimestamp = timestamp;
    }


    public newsfeedPublic(String defaultTranslation, String miwokTranslation , String publishDate ,
                          String eventDate , String lastDateofRegistration, String entryFees ,
                          String fullName , String fileAttachment ,String mode,String timestamp)
    {
        mDefaultTranslation = defaultTranslation;
        mMiwokTranslation = miwokTranslation;
        mPublishDate = publishDate;
        mEventDate = eventDate;
        mLastDateofRegistration = lastDateofRegistration;
        mEntryFees = entryFees;
        mFullName = fullName;
        mFileAttachment = fileAttachment;
        mMode = mode;
        mTimestamp = timestamp;
    }

    public newsfeedPublic(String eliminationMode, String eliminationUniqueId )
    {

        mEliminationMode = eliminationMode;
        mEliminationUniqueId = eliminationUniqueId;

    }




    public String getEliminationMode(){return mEliminationMode;}

    public String getEliminationUniqueId(){return mEliminationUniqueId;}


    public String getDefaultTranslation()
    {
        return mDefaultTranslation;
    }

    public String getMiwokTranslation() {
        return mMiwokTranslation;
    }

    public String getMode() {return  mMode;}


    public String getPublishDate() {
        return mPublishDate;
    }

    public String getEventDate() {
        return mEventDate;
    }

    public String getLastDateofRegistration() {
        return mLastDateofRegistration;
    }



    public String getfullName (){return  mFullName;}

    public String getFileAttachment (){return  mFileAttachment;}


    public String getEntryFees() {
        return mEntryFees ;
    }

    public String getEliminationDetails() {
        return mEntryFees ;
    }

    public String toString(){
            Log.v("mMode",mMode);
        if(mMode.contains("ANNOUNCEMENTS"))
        {



            return mMode  + "%" + mDefaultTranslation  + "%" + mMiwokTranslation + "%" + mPublishDate
                    + "%" + mEventDate  + "%" +  mLastDateofRegistration  + "%" +  mEntryFees
                    + "%" + mFullName  + "%" + mFileAttachment  + "%" + mTimestamp + " " ;
        }else if(mMode.contains("EVENTS") )
        {
            return mMode  + "%" + mDefaultTranslation  + "%" + mMiwokTranslation + "%" + mPublishDate
                    + "%" + mEventDate  + "%" +  mLastDateofRegistration  + "%" +
                    mEntryFees  + "%" + mFullName + "%" + mTimestamp + " " ;
        }else
        {



            return mMode  + "%" + mDefaultTranslation  + "%" + mMiwokTranslation + "%" + mPublishDate
                    + "%" + mEventDate  + "%" +  mLastDateofRegistration  + "%" +  mEntryFees
                    + "%" + mFullName  + "%" + mFileAttachment+ "%" + mTimestamp + " "  ;
        }

    }










}
