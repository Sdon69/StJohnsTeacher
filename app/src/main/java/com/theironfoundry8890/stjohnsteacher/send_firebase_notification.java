package com.theironfoundry8890.stjohnsteacher;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class send_firebase_notification {



    public static void sendGcmMessage(String messageTitle, String messageBody, String cataegories, String writer)
    {
        if(messageTitle!=null || messageBody!=null) {
            messageTitle = messageTitle.replace("<comma3384>", ".");
            messageBody = messageBody.replace("<comma3384>", ".");
            String envelope = messageTitle + "<comma3384>" + messageBody;

            if(!writer.equals("event"))
            {

                String topicOutput = getTopics(cataegories);
                Log.v("topicOutput",topicOutput);
                String topicArray[]= topicOutput.split(",");
                for (int i = 1; i < topicArray.length; i++)
                {
                    new RetrieveFeedTask().execute(envelope,"/topics/" +topicArray[i]);
                }
            }else
            {
                new RetrieveFeedTask().execute(envelope,"/topics/" +"global");
            }




        }
    }


    static class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;


        protected String doInBackground(String[] args) {


            if (args.length < 1 || args.length > 2 || args[0] == null) {
                System.err.println("usage: ./gradlew run -Pmsg=\"MESSAGE\" [-Pto=\"DEVICE_TOKEN\"]");
                System.err.println("");
                System.err.println("Specify a test message to broadcast via GCM. If a device's GCM registration token is\n" +
                        "specified, the message will only be sent to that device. Otherwise, the message \n" +
                        "will be sent to all devices subscribed to the \"global\" topic.");
                System.err.println("");
                System.err.println("Example (Broadcast):\n" +
                        "On Windows:   .\\gradlew.bat run -Pmsg=\"<Your_Message>\"\n" +
                        "On Linux/Mac: ./gradlew run -Pmsg=\"<Your_Message>\"");
                System.err.println("");
                System.err.println("Example (Unicast):\n" +
                        "On Windows:   .\\gradlew.bat run -Pmsg=\"<Your_Message>\" -Pto=\"<Your_Token>\"\n" +
                        "On Linux/Mac: ./gradlew run -Pmsg=\"<Your_Message>\" -Pto=\"<Your_Token>\"");
                System.exit(1);
            }
            try {
                // Prepare JSON containing the GCM message content. What to send and where to send.
                JSONObject jGcmData = new JSONObject();
                JSONObject jData = new JSONObject();
                try {
                    jData.put("message", args[0].trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Where to send GCM message.
                if (args.length > 1 && args[1] != null) {
                    try {
                        jGcmData.put("to", args[1].trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        jGcmData.put("to", "/topics/Management4");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // What to send in GCM message.
                try {
                    jGcmData.put("data", jData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Create connection to send GCM Message request.
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "key=" + "AIzaSyDhyP7p8FDixgOyGy0KdbHMXRRFCvaXpWc");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send GCM message content.
                OutputStream outputStream = conn.getOutputStream();
                Log.v("OutputStream", String.valueOf(conn));
                String jgcm = jGcmData.toString();
                jgcm = jGcmData.toString().replace("\\","");
                Log.v("jGcmData",jgcm);
                outputStream.write(jgcm.getBytes());

                // Read GCM response.
                InputStream inputStream = conn.getInputStream();
                String resp = IOUtils.toString(inputStream);
                System.out.println(resp);
                System.out.println("Check your device/emulator for notification or logcat for " +
                        "confirmation of the receipt of the GCM message.");
            } catch (IOException e) {
                System.out.println("Unable to send GCM message.");
                System.out.println("Please ensure that API_KEY has been replaced by the server " +
                        "API key, and that the device's registration token is correct (if specified).");
                e.printStackTrace();
            }
            return args[1];
        }
        protected void onPostExecute(String feed) {

            Log.v("postfeed",feed);

        }

    }

    
    public static String getTopics(String subCataegories)
    {
        String[] departmentCollection = new String[6];
        String outputTopic ="";
        int arrayIncrementer = 0;
        departmentCollection[arrayIncrementer] = "Art";
        departmentCollection[++arrayIncrementer] = "Commerce";
        departmentCollection[++arrayIncrementer] = "Management";
        departmentCollection[++arrayIncrementer] = "Education";
        departmentCollection[++arrayIncrementer] = "Science";
        departmentCollection[++arrayIncrementer] = "Other Subjects";

        String[] semesterCollection = new String[6];
        arrayIncrementer = 0;
        semesterCollection[arrayIncrementer] = "First Semester";
        semesterCollection[++arrayIncrementer] = "Second Semester";
        semesterCollection[++arrayIncrementer] = "Third Semester";
        semesterCollection[++arrayIncrementer] = "Fourth Semester";
        semesterCollection[++arrayIncrementer] = "Fifth Semester";
        semesterCollection[++arrayIncrementer] = "Sixth and Above Semesters";

        for(int i = departmentCollection.length-1;i>=0;i--)
        {
            
            
            Log.v("subcataegories",subCataegories + " / " + departmentCollection[i] );
            if(subCataegories.contains(departmentCollection[i]))
            {

//                    Log.v("deptCollection",semesterCollection[k]);

                String sSemester;
                if (subCataegories.contains("First Semester")) {
                    sSemester = "1";
                     outputTopic  = outputTopic + "," + departmentCollection[i]  + sSemester;
                }  if (subCataegories.contains("Second Semester")) {
                sSemester = "2";
                 outputTopic  = outputTopic + "," + departmentCollection[i]  + sSemester;
            }  if (subCataegories.contains("Third Semester")) {
                sSemester = "3";
                 outputTopic  = outputTopic + "," + departmentCollection[i]  + sSemester;
            }  if (subCataegories.contains("Fourth Semester")) {
                sSemester = "4";
                 outputTopic  = outputTopic + "," + departmentCollection[i]  + sSemester;
            }  if (subCataegories.contains("Fifth Semester")) {
                sSemester = "5";
                 outputTopic  = outputTopic + "," + departmentCollection[i]  + sSemester;
            }  if (subCataegories.contains("Sixth and Above Semesters")) {
                sSemester = "6";
                 outputTopic  = outputTopic + "," + departmentCollection[i]  + sSemester;
            }



            }
        }
        return outputTopic;
    }
}


