package com.theironfoundry8890.stjohnsteacher;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sdon69 on 18-08-2017.
 */

public class newsfeedAdapter extends ArrayAdapter<newsfeedPublic> {

    private int mColorResourceId;

    public newsfeedAdapter(Activity context, ArrayList<newsfeedPublic> words ) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, words);



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_newsfeed, parent, false);


        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        newsfeedPublic currentword = getItem(position);

        String mode = currentword.getMode();
        if(mode.equals("EVENTS")) {

            TextView entryFeesTitleTextView = (TextView) listItemView.findViewById(R.id.eTextView);

            entryFeesTitleTextView.setText("Entry Fees : ");

            TextView dateTitleTextView = (TextView) listItemView.findViewById(R.id.eDateTextView);

            dateTitleTextView.setText("Date : ");

            TextView modeTextView = (TextView) listItemView.findViewById(R.id.modeTextView);

            modeTextView.setText("EVENT");



            // Find the TextView in the list_item.xml layout with the ID version_name
            TextView miwokTextView = (TextView) listItemView.findViewById(R.id.miwok_text_view);
            // Get the version name from the current AndroidFlavor object and
            // set this text on the name TextView
            miwokTextView.setText(currentword.getMiwokTranslation());

            // Find the TextView in the list_item.xml layout with the ID version_number
            TextView defaultTextView = (TextView) listItemView.findViewById(R.id.default_text_view);
            // Get the version number from the current AndroidFlavor object and
            // set this text on the number TextView
            String description = currentword.getDefaultTranslation();
            if (description.length() > 68) {
                String trimmedDescription = description.substring(0, 60);
                description = trimmedDescription.concat("....");

            }
            defaultTextView.setText(description);


            TextView entryFeesTextView = (TextView) listItemView.findViewById(R.id.entry_fees);

            entryFeesTextView.setText(currentword.getEntryFees());

            TextView eventDateTextView = (TextView) listItemView.findViewById(R.id.event_date);

            eventDateTextView.setText(currentword.getEventDate());

            TextView datePublishedTextView = (TextView) listItemView.findViewById(R.id.date_published);

            datePublishedTextView.setText(currentword.getPublishDate());

        }else if(mode.equals("NOTES"))
        {
            TextView entryFeesTitleTextView = (TextView) listItemView.findViewById(R.id.eTextView);

            entryFeesTitleTextView.setText("Author : ");

            TextView dateTitleTextView = (TextView) listItemView.findViewById(R.id.eDateTextView);

            dateTitleTextView.setText("");

            TextView modeTextView = (TextView) listItemView.findViewById(R.id.modeTextView);

            modeTextView.setText("NOTES");

            TextView eventDateTextView = (TextView) listItemView.findViewById(R.id.event_date);

            eventDateTextView.setText("");

            TextView datePublishedTextView = (TextView) listItemView.findViewById(R.id.date_published);

            datePublishedTextView.setText("");




            TextView miwokTextView = (TextView) listItemView.findViewById(R.id.miwok_text_view);
            // Get the version name from the current AndroidFlavor object and
            // set this text on the name TextView
            miwokTextView.setText(currentword.getMiwokTranslation());

            // Find the TextView in the list_item.xml layout with the ID version_number
            TextView defaultTextView = (TextView) listItemView.findViewById(R.id.default_text_view);
            // Get the version number from the current AndroidFlavor object and
            // set this text on the number TextView
            String description = currentword.getDefaultTranslation();
            if (description.length() > 68) {
                String trimmedDescription = description.substring(0, 60);
                description = trimmedDescription.concat("....");

            }
            defaultTextView.setText(description);


            TextView entryFeesTextView = (TextView) listItemView.findViewById(R.id.entry_fees);

            entryFeesTextView.setText(currentword.getLastDateofRegistration());

        }else if(mode.equals("ANNOUNCEMENTS"))
        {
            TextView entryFeesTitle = (TextView) listItemView.findViewById(R.id.eTextView);

            entryFeesTitle.setText("Author : ");


            TextView dateTitleTextView = (TextView) listItemView.findViewById(R.id.eDateTextView);

            dateTitleTextView.setText("");


            TextView modeTextView = (TextView) listItemView.findViewById(R.id.modeTextView);

            modeTextView.setText("ANNOUNCEMENT");

            TextView eventDateTextView = (TextView) listItemView.findViewById(R.id.event_date);

            eventDateTextView.setText("");

            TextView datePublishedTextView = (TextView) listItemView.findViewById(R.id.date_published);

            datePublishedTextView.setText("");



            TextView miwokTextView = (TextView) listItemView.findViewById(R.id.miwok_text_view);
            // Get the version name from the current AndroidFlavor object and
            // set this text on the name TextView
            miwokTextView.setText(currentword.getMiwokTranslation());

            // Find the TextView in the list_item.xml layout with the ID version_number
            TextView defaultTextView = (TextView) listItemView.findViewById(R.id.default_text_view);
            // Get the version number from the current AndroidFlavor object and
            // set this text on the number TextView
            String description = currentword.getDefaultTranslation();
            if (description.length() > 68) {
                String trimmedDescription = description.substring(0, 60);
                description = trimmedDescription.concat("....");

            }
            defaultTextView.setText(description);


            TextView entryFeesTextView = (TextView) listItemView.findViewById(R.id.entry_fees);

            entryFeesTextView.setText(currentword.getLastDateofRegistration());
        }





        String fileUrl =  currentword.getFileAttachment();

        ImageView vidOnPhoneImageView = (ImageView) listItemView.findViewById(R.id.vidOnPhone_ImageView);

        if(fileUrl != null)
        {

            if(fileUrl.length()>4 && fileUrl.length()<20)
            {
                vidOnPhoneImageView.setVisibility(View.VISIBLE);
            }else{
                vidOnPhoneImageView.setVisibility(View.GONE);
            }
        }












        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}

