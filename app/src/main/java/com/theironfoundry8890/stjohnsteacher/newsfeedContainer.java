package com.theironfoundry8890.stjohnsteacher;


public class newsfeedContainer {
    private String mDefaultTranslation;

    private String mMiwokTranslation;

    private String mPublishDate;

    private String mEventDate;

    private String mLastDateofRegistration;

    private String mEntryFees;

    private String mFullName;

    private String mFileAttachment;

    private String mMode;







    public newsfeedContainer(String defaultTranslation, String miwokTranslation , String publishDate ,
                             String eventDate , String lastDateofRegistration,
                             String entryFees , String fullName, String mode )
    {
        mDefaultTranslation = defaultTranslation;
        mMiwokTranslation = miwokTranslation;
        mPublishDate = publishDate;
        mEventDate = eventDate;
        mLastDateofRegistration = lastDateofRegistration;
        mEntryFees = entryFees;
        mFullName = fullName;
        mMode = mode;
    }


    public newsfeedContainer(String defaultTranslation, String miwokTranslation , String publishDate ,
                             String eventDate , String lastDateofRegistration, String entryFees ,
                             String fullName , String fileAttachment , String mode)
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
    }






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

    public String toString(){

        if(mMode.equals("ANNOUNCEMENTS") || mMode.equals("NOTES"))
        {



            return mMode  + "%" + mDefaultTranslation  + "%" + mMiwokTranslation + "%" + mPublishDate
                    + "%" + mEventDate  + "%" +  mLastDateofRegistration  + "%" +  mEntryFees
                    + "%" + mFullName  + "%" + mFileAttachment;
        }else
        {
            return mMode  + "%" + mDefaultTranslation  + "%" + mMiwokTranslation + "%" + mPublishDate
                    + "%" + mEventDate  + "%" +  mLastDateofRegistration  + "%" +
                    mEntryFees  + "%" + mFullName;
        }

    }





}
