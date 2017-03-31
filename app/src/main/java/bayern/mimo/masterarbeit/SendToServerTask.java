package bayern.mimo.masterarbeit;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bayern.mimo.masterarbeit.activity.StartRideActivity;
import bayern.mimo.masterarbeit.data.DataRecordingRequest;
import bayern.mimo.masterarbeit.listener.OnButtonSendDataToServerClickListener;

/**
 * Created by MiMo
 */

public class SendToServerTask extends AsyncTask<String, Void, String> {

    private OnButtonSendDataToServerClickListener caller;

    public SendToServerTask(OnButtonSendDataToServerClickListener caller){
        this.caller = caller;
    }

    @Override
    protected String doInBackground(String... args) {

        URL url = null;
        try {
            url = new URL(args[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String data = "";

        try {
            HttpURLConnection urlConn;
            DataOutputStream printout;

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept-Encoding", "gzip");
            urlConn.setRequestProperty("Accept", "application/json");

            printout = new DataOutputStream(urlConn.getOutputStream());
            printout.writeBytes(args[1]);
            printout.flush();
            printout.close();


            InputStream in = urlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }

            urlConn.connect();


        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            //TODO log properly
            return "Sending: Error! \n" + e.getLocalizedMessage();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("Got result: " + result);
        try{
            JSONObject json = new JSONObject(result);
            if(json.has("Shimmer1MAC") && json.has("Shimmer2MAC") && json.has("Shimmer1GUID") && json.has("Shimmer2GUID")) { //TODO besseren cae definieren. für jetzt reucht das auf alle fällte
                int id = json.getInt("ID");

                String username = json.getString("Username");

                Date timestamp = new Date();


                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    timestamp = format.parse(json.getString("Timestamp").split("\\.")[0]);
                }
                catch(ParseException pe) {
                    throw new IllegalArgumentException();
                }

                String shimmer1MAC = json.getString("Shimmer1MAC");
                String shimmer1GUID = json.getString("Shimmer1GUID");
                String shimmer2MAC = json.getString("Shimmer2MAC");
                String shimmer2GUID = json.getString("Shimmer2GUID");
                String heatMAC = json.getString("HeatMAC");
                String heatGUID = json.getString("HeatGUID");






                DataRecordingRequest drr = new DataRecordingRequest(id, username, timestamp, shimmer1MAC, shimmer1GUID, shimmer2MAC, shimmer2GUID, heatMAC, heatGUID);
                caller.requestSucceeded(drr);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }


}
