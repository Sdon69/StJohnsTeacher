package com.theironfoundry8890.stjohnsteacher;

import android.app.Activity;
import android.content.SharedPreferences;

public class sheetsIdCollection extends Activity {



    public void setSheetIds()
    {
        String mode = "test";

        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();

        if(mode.equals("test")) {
            mEditor.putString("eventSheetId", "1tFhDy9sR9dlJ0jwNbqbcq3TnFpViMHJOi2xeOv_Wqqw").apply();
            mEditor.putString("notesSheetId", "1pAZtRVUuQFuGoUiWjiZRwXbrfju3ZcJgR0Lq6mBmmW0").apply();
            mEditor.putString("announcementSheetId", "1P0iFk6F9AHddLOM4N_8NbMVVByz671rbzDikJIbcsS0").apply();
            mEditor.putString("miscSheetId", "10PpNnvF4j5GNlbGrP4vPoPV8pQhix_9JP5kK9zlQDmY").apply();
            mEditor.putString("uploadedVideoInfoSheetId", "17o0GpXwWZskawufsMj-iH8wdbFx_2HZ6Jfjtc1JjjfU").apply();
        }else if(mode.equals("release"))
        {
            mEditor.putString("eventSheetId", "1SC0UPYthsoS5NKDuC5oJt-y29__f0gm0wkIkJoDduWw").apply();
            mEditor.putString("notesSheetId", "1UDDtel5vAFBqVnaPZIZl20SwZEz_7fxGXYQOuKLvSmQ").apply();
            mEditor.putString("announcementSheetId", "116OBhXliG69OB5bKRAEwpmlOz21LCCWStniSuIR6wPI").apply();
            mEditor.putString("miscSheetId", "1nzKRlq7cQrI_XiJGxJdNax5oB91bR_SypiazWO2JTuU  ").apply();
            mEditor.putString("uploadedVideoInfoSheetId", "12C3ceqz_Fr7GmXpLxt-n4iMhbr86yluGqT4fno_CW-8").apply();
        }



    }

    public static String  getEventSheetId()
    {
        String mode = "test";
        if(mode.equals("test")) {
          return  "1tFhDy9sR9dlJ0jwNbqbcq3TnFpViMHJOi2xeOv_Wqqw";

        }else
        {
            return  "1SC0UPYthsoS5NKDuC5oJt-y29__f0gm0wkIkJoDduWw";
        }
    }

    public static String  getAnnouncementSheetId()
    {
        String mode = "test";
        if(mode.equals("test")) {
            return  "1P0iFk6F9AHddLOM4N_8NbMVVByz671rbzDikJIbcsS0";

        }else
        {
            return  "116OBhXliG69OB5bKRAEwpmlOz21LCCWStniSuIR6wPI";
        }
    }

    public static String  getNoteSheetId()
    {
        String mode = "test";
        if(mode.equals("test")) {
            return  "1pAZtRVUuQFuGoUiWjiZRwXbrfju3ZcJgR0Lq6mBmmW0";

        }else
        {
            return  "1UDDtel5vAFBqVnaPZIZl20SwZEz_7fxGXYQOuKLvSmQ";
        }

    }

    public static String  getMiscSheetId()
    {
        String mode = "test";
        if(mode.equals("test")) {
            return  "10PpNnvF4j5GNlbGrP4vPoPV8pQhix_9JP5kK9zlQDmY";

        }else
        {
            return  "1nzKRlq7cQrI_XiJGxJdNax5oB91bR_SypiazWO2JTuU";
        }

    }

    public static String getUploadedVideoInfoSheetId()
    {
        String mode = "test";
        if(mode.equals("test")) {
            return  "17o0GpXwWZskawufsMj-iH8wdbFx_2HZ6Jfjtc1JjjfU";

        }else
        {
            return  "12C3ceqz_Fr7GmXpLxt-n4iMhbr86yluGqT4fno_CW-8";
        }
    }

    public static String getApiKey()
    {
        String mode = "test";
        if(mode.equals("test")) {
            return  "AIzaSyDhyP7p8FDixgOyGy0KdbHMXRRFCvaXpWc";

        }else
        {
            return  "AIzaSyDxQgIdYx9B_OA8cDuwSSHHCJ8kUDhL_YY";
        }
    }

}
