package bayern.mimo.masterarbeit;

import android.os.AsyncTask;

import com.shimmerresearch.android.Shimmer;

import org.json.JSONArray;
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
import java.util.List;
import java.util.Map;

import bayern.mimo.masterarbeit.activity.StartRideActivity;
import bayern.mimo.masterarbeit.common.AppSensors;
import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.DataRecording;
import bayern.mimo.masterarbeit.data.DataRecordingRequest;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.handler.ShimmerHandler;
import bayern.mimo.masterarbeit.listener.OnButtonSendDataToServerClickListener;
import bayern.mimo.masterarbeit.util.Config;

/**
 * Created by MiMo
 */

public class SendToServerTask extends AsyncTask<String, Void, String> {

    private DataRecording record;

    public SendToServerTask(DataRecording record){
        this.record = record;
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

            //TODO evtl. umschreiben - und nicht zeichenweise lesen...

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

        if(result.startsWith("DRRID")){
            String[] split = result.split(":");
            int drrID = -1;
            try {
                drrID = Integer.parseInt(split[1]);
            }catch(NumberFormatException e){
                e.printStackTrace();
            }

            if(drrID != -1){
                DataHelper.setUploadCompleted(drrID);
            }

            return;
        }

        try{
            JSONObject json = new JSONObject(result);
            if(json.has("Shimmer1MAC") && json.has("Shimmer2MAC") ) { //TODO besseren case definieren. für jetzt reicht das auf alle fälle
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
                /*caller.*/requestSucceeded(drr);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void requestSucceeded(DataRecordingRequest drr) {
        System.out.println("in requestSucceeded(drr)");
        //TODO send all the shit

        String uploadPath = Config.SERVER_API_URL + Config.SHIMMER_UPLOAD_PATH;

        //Map<Shimmer, ShimmerHandler> shimmerSensors = AppSensors.getShimmerValues();
        JSONArray allValuesJson = new JSONArray();


        for(Shimmer shimmer : record.getShimmerValues().keySet()){




            //himmerHandler handler = shimmerSensors.get(shimmer);
            //List<ShimmerValue> values = handler.getValues();

            List<ShimmerValue> values = record.getShimmerValues().get(shimmer);
            JSONArray sensorAsJson = new JSONArray();

            //TODO JSON bauen
            for (ShimmerValue value : values) {
                if (value.getTimestamp() == null) continue;
                JSONObject valueAsJson = new JSONObject();
                try {
                    valueAsJson.put("AccelLnX", value.getAccelLnX());
                    valueAsJson.put("AccelLnY", value.getAccelLnY());
                    valueAsJson.put("AccelLnZ", value.getAccelLnZ());
                    valueAsJson.put("AccelWrX", value.getAccelWrX());
                    valueAsJson.put("AccelWrY", value.getAccelWrY());
                    valueAsJson.put("AccelWrZ", value.getAccelWrZ());
                    valueAsJson.put("GyroX", value.getGyroX());
                    valueAsJson.put("GyroY", value.getGyroY());
                    valueAsJson.put("GyroZ", value.getGyroZ());
                    valueAsJson.put("MagX", value.getMagX());
                    valueAsJson.put("MagY", value.getMagY());
                    valueAsJson.put("MagZ", value.getMagZ());
                    valueAsJson.put("Temperature", value.getTemperature());
                    valueAsJson.put("Pressure", value.getPressure());
                    valueAsJson.put("Timestamp", value.getTimestamp());
                    valueAsJson.put("RealTimeClock", value.getRealTimeClock());
                    valueAsJson.put("TimestampSync", value.getTimestampSync());
                    valueAsJson.put("RealTimeClockSync", value.getRealTimeClockSync());
                    valueAsJson.put("DataRecordingRequestID", drr.getId());
                    valueAsJson.put("SensorMAC", shimmer.getBluetoothAddress());

                    sensorAsJson.put(valueAsJson);

                    //System.out.println(jsonValues.toString(2));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                allValuesJson.put(sensorAsJson);

            }

            System.out.println("-_-_-_-_-_-_-UPLOADING-_-_-_-_-_-_-");

            System.out.println("Upload path: " + uploadPath);
            System.out.println("data: \n");

            try {
                System.out.println(allValuesJson.toString(2));
            }catch (JSONException e){
                e.printStackTrace();
            }



            this.execute(uploadPath, allValuesJson.toString());


            //System.out.println("-_-_-_-_-_-_-UPLOADING DONE-_-_-_-_-_-_-");

            //TODO send
        }
    }
}
