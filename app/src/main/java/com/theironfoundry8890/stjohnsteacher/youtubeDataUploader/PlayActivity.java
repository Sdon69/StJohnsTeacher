package com.theironfoundry8890.stjohnsteacher.youtubeDataUploader;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.theironfoundry8890.stjohnsteacher.R;
import com.theironfoundry8890.stjohnsteacher.youtubeDataUploader_util.VideoData;

/*
 * Copyright (c) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * @author Ibrahim Ulukaya <ulukaya@google.com>
 *         <p/>
 *         Main fragment showing YouTube Direct Lite upload options and having
 *         YT Android Player.
 */

public class PlayActivity extends Activity implements
        PlayerStateChangeListener, OnFullscreenListener {

    private static final String YOUTUBE_FRAGMENT_TAG = "youtube";
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();
    GoogleAccountCredential credential;
    private YouTubePlayer mYouTubePlayer;
    private boolean mIsFullScreen = false;
    private Intent intent;
    private String expTitle;
    private String expDesc;
    private String expFullName;
    private String expFileAttachment;

    public PlayActivity() {
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void directLite(View view) {
        this.setResult(RESULT_OK, intent);
        finish();
    }

    public void panToVideo(final String youtubeId) {
        popPlayerFromBackStack();
        YouTubePlayerFragment playerFragment = YouTubePlayerFragment
                .newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_container, playerFragment,
                        YOUTUBE_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();
        playerFragment.initialize(Auth.KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b) {
//                        youTubePlayer.loadVideo(youtubeId);
                        Log.v("expFileAttachment",expFileAttachment);
                        youTubePlayer.loadVideo(expFileAttachment);
                        mYouTubePlayer = youTubePlayer;
                        youTubePlayer
                                .setPlayerStateChangeListener(PlayActivity.this);
                        youTubePlayer
                                .setOnFullscreenListener(PlayActivity.this);
                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider,
                            YouTubeInitializationResult result) {
                        showErrorToast(result.toString());
                    }
                });
    }

    public boolean popPlayerFromBackStack() {
        if (mIsFullScreen) {
            mYouTubePlayer.setFullscreen(false);
            return false;
        }
        if (getFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG) != null) {
            getFragmentManager().popBackStack();
            return false;
        }
        return true;
    }

    @Override
    public void onAdStarted() {
    }


    public void loadData() {

        //Loading Data via ShredPreferences
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        String TitleString = mPrefs.getString("title", "default_value_if_variable_not_found");
        String DescriptionString = mPrefs.getString("desc", "default_value_if_variable_not_found");
        String fullName = mPrefs.getString("fullName", "default_value_if_variable_not_found");
        String fileAttachment = mPrefs.getString("fileAttachment", "default_value_if_variable_not_found");


        //Initializing using above variables
        expTitle = TitleString;
        expDesc = DescriptionString;
        expFullName = fullName;
        expFileAttachment = fileAttachment;

        TextView titleTextView = (TextView) findViewById(R.id.detailedlaytitle);
        TextView descTextView = (TextView) findViewById(R.id.detailedlaydesc);
        TextView fullNameTextView = (TextView) findViewById(R.id.publisher);

        //Setting values to layouts
        titleTextView.setText(expTitle);
        descTextView.setText(expDesc);
        fullNameTextView.setText(expFullName);
    }

        @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        showErrorToast(errorReason.toString());
    }

    private void showErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onLoaded(String arg0) {
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void onVideoEnded() {
        // popPlayerFromBackStack();
    }

    @Override
    public void onVideoStarted() {
    }

    @Override
    public void onFullscreen(boolean fullScreen) {
        Log.v("onfullscreen", String.valueOf(mIsFullScreen));
        mIsFullScreen = fullScreen;
        if(!mIsFullScreen)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.youtube_uploader_activity_play);
        intent = getIntent();
        Button submitButton = (Button) findViewById(R.id.submit_button);
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            submitButton.setVisibility(View.GONE);
            setTitle(R.string.playing_uploaded_video);
        }
        loadData();
        String youtubeId = intent.getStringExtra(youtubeUploadMainActivity.YOUTUBE_ID);
        panToVideo(youtubeId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
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

    @Override
    public void onBackPressed() {
        Log.v("fullscreen", String.valueOf(mIsFullScreen));
        if(!mIsFullScreen){
            super.onBackPressed();
            NavUtils.navigateUpFromSameTask(this);
        }else
        {
            mYouTubePlayer.setFullscreen(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }

    }

    public interface Callbacks {

        public void onVideoSelected(VideoData video);

        public void onResume();

    }
}
