package bayern.mimo.masterarbeit;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.location.Location;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.DataRecording;
import bayern.mimo.masterarbeit.data.DataRecordingRequest;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.util.Config;

/**
 * Created by MiMo
 */

public class SendToServerTask extends AsyncTask<String, Void, String> {

    private DataRecording record;
    private Context context;

    public SendToServerTask(DataRecording record, Context context) {
        this.record = record;
        this.context = context;
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
            //urlConn.setRequestProperty("Accept", "*/*");
            //urlConn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
            //urlConn.setChunkedStreamingMode(0);



            OutputStream os  = urlConn.getOutputStream();
            printout = new DataOutputStream(os);
            printout.writeBytes(args[1]);
            printout.flush();
            printout.close();
            os.close();

            System.out.println("UPLOAD _ _ _ : closed all");
            int status = urlConn.getResponseCode();
            System.out.println("http status code is "+status);

            InputStream in = urlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            //TODO evtl. umschreiben - und nicht zeichenweise lesen...

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }

            urlConn.connect();
            //TODO disconnect again?





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


        if (result.startsWith("DRRID")) {
            System.out.println("found DRRID");
            String[] split = result.split(":");
            int drrID = -1;
            try {
                drrID = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (drrID != -1) {
                DataHelper.setUploadCompleted(drrID);
            }

            return;
        }

        if (result == null) {
            System.out.println("Result is null");
            return;
        }

        if (result.isEmpty()) {
            System.out.println("Result is empty");
            return;
        }

        try {
            JSONObject json = new JSONObject(result);
            if (json.has("Shimmer1MAC") && json.has("Shimmer2MAC")) { //TODO besseren case definieren. für jetzt reicht das auf alle fälle
                /*
                int id = json.getInt("ID");

                String username = json.getString("Username");

                Date timestamp = new Date();


                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    timestamp = format.parse(json.getString("Timestamp").split("\\.")[0]);
                } catch(ParseException pe) {
                    throw new IllegalArgumentException();
                }

                String shimmer1MAC = json.getString("Shimmer1MAC");
                String shimmer2MAC = json.getString("Shimmer2MAC");
                String heatMAC = json.getString("HeatMAC");

                String guid = UUID.randomUUID().toString();
                */

                //DataRecordingRequest drr = AppSensors.getDrr();
                DataRecordingRequest drr = record.getDrr();
                drr.setServerID(json.getInt("ID"));
                DataHelper.updateDrrServerID(drr, context);
                //TODO schreibe ID in lokale Datenbank

                System.out.println("UPLOAD _ _ _ : DRR ID received from server is " + drr.getServerID());


                requestSucceeded(drr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (result.contains("DRRID")) {
            //Upload completed
            String[] res = result.split(":");
            System.out.println("Wir haben was tolles gefunden!");
            if (res.length == 2) {
                System.out.println("Die Länge passt!");
                //DRR ID auslesen
                int drrID = Integer.parseInt(res[1].replace("\"", ""));
                System.out.println("wir haben gelesen, und zwar die ID " + drrID);
                DataHelper.updateDrrUploaded(drrID, context);
            } else {
                System.out.println("Länge ist " + res.length);
            }
        }

        System.out.println("nachm if");
    }

    private void  requestSucceeded(DataRecordingRequest drr) {
        System.out.println("in requestSucceeded(drr)");

        //AppSensors.setDrr(drr);


        String uploadPath = Config.SERVER_API_URL + Config.SHIMMER_UPLOAD_PATH;


        JSONObject jsonToSend = new JSONObject();


        JSONArray shimmerValuesJson = new JSONArray();

        for (String shimmerAddress : record.getShimmerValues().keySet()) {

            List<ShimmerValue> values = record.getShimmerValues().get(shimmerAddress);

            System.out.println("Number of values is " + values.size());

            System.out.println("creating " + values.size() +" for Shimmer " +shimmerAddress);

            //TODO JSON bauen
            for (ShimmerValue value : values) {
                if (value.getTimestamp() == null) continue;
                JSONObject valueAsJson = new JSONObject();
                try {
                    valueAsJson.put("ALX", value.getAccelLnX());
                    valueAsJson.put("ALY", value.getAccelLnY());
                    valueAsJson.put("ALZ", value.getAccelLnZ());
                    valueAsJson.put("AWX", value.getAccelWrX());
                    valueAsJson.put("AWY", value.getAccelWrY());
                    valueAsJson.put("AWZ", value.getAccelWrZ());
                    valueAsJson.put("GX", value.getGyroX());
                    valueAsJson.put("GY", value.getGyroY());
                    valueAsJson.put("GZ", value.getGyroZ());
                    valueAsJson.put("MX", value.getMagX());
                    valueAsJson.put("MY", value.getMagY());
                    valueAsJson.put("MZ", value.getMagZ());
                    valueAsJson.put("TEMP", value.getTemperature());
                    valueAsJson.put("PRS", value.getPressure());
                    valueAsJson.put("TS", value.getTimestamp());
                    valueAsJson.put("RTC", value.getRealTimeClock());
                    valueAsJson.put("TSS", value.getTimestampSync());
                    valueAsJson.put("RTCS", value.getRealTimeClockSync());
                    valueAsJson.put("DRRID", drr.getServerID());
                    valueAsJson.put("SMAC", shimmerAddress);

                    shimmerValuesJson.put(valueAsJson);

                    //System.out.println(jsonValues.toString(2));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }

        JSONArray locationValuesJson = new JSONArray();

        for (Location location : record.getLocations()) {
            JSONObject valueAsJson = new JSONObject();
            try {
                valueAsJson.put("LATITUDE", location.getLatitude());
                valueAsJson.put("LONGITUDE", location.getLatitude());
                valueAsJson.put("TIMESTAMP", location.getLatitude());
                valueAsJson.put("ACCURACY", location.getLatitude());
                valueAsJson.put("ALTITUDE", location.getLatitude());
                valueAsJson.put("ELAPSED_REAL_TIME_NANOS", location.getLatitude());
                valueAsJson.put("SPEED", location.getLatitude());
                valueAsJson.put("DataRecordingRequestID", drr.getServerID());

                locationValuesJson.put(valueAsJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        try {
            jsonToSend.put("ShimmerData", shimmerValuesJson);
            jsonToSend.put("LocationData", locationValuesJson);

            //System.out.println("GESENDET WIRD: \n" + jsonToSend.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("-_-_-_-_-_-_-UPLOADING-_-_-_-_-_-_-");

        System.out.println("Upload path: " + uploadPath);
        System.out.println("data: \n");

        /*
        try {
            System.out.println(shimmerValuesJson.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        new SendToServerTask(record, context).execute(uploadPath, shimmerValuesJson.toString());


    }


}
