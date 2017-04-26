package bayern.mimo.masterarbeit;

import android.os.AsyncTask;

import com.shimmerresearch.android.Shimmer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.UUID;

import bayern.mimo.masterarbeit.common.AppSensors;
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
            System.out.println("found DRRID");
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

        if(result == null){
            System.out.println("Result is null");
            return;
        }

        if(result.isEmpty()){
            System.out.println("Result is empty");
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
                } catch(ParseException pe) {
                    throw new IllegalArgumentException();
                }

                String shimmer1MAC = json.getString("Shimmer1MAC");
                String shimmer2MAC = json.getString("Shimmer2MAC");
                String heatMAC = json.getString("HeatMAC");

                String guid = UUID.randomUUID().toString();

                DataRecordingRequest drr = new DataRecordingRequest(guid, id, username, timestamp, shimmer1MAC, shimmer2MAC, heatMAC, true); //TODO get from DataRecording oder so
                requestSucceeded(drr);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void requestSucceeded(DataRecordingRequest drr) {
        System.out.println("in requestSucceeded(drr)");

        AppSensors.setDrr(drr);

        //TODO upload result starts with: DRRID_SHIMMER_UPLOAD





        String uploadPath = Config.SERVER_API_URL + Config.SHIMMER_UPLOAD_PATH;

        //Map<Shimmer, ShimmerHandler> shimmerSensors = AppSensors.getShimmerValues();
        JSONArray allValuesJson = new JSONArray();


        for(Shimmer shimmer : record.getShimmerValues().keySet()){

            //himmerHandler handler = shimmerSensors.get(shimmer);
            //List<ShimmerValue> values = handler.getValues();

            List<ShimmerValue> values = record.getShimmerValues().get(shimmer);

            System.out.println("Number of values is " + values.size());

            //TODO JSON bauen
            for (ShimmerValue value : values) {
                if (value.getTimestamp() == null) continue;
                JSONObject valueAsJson = new JSONObject();
                try {
                    valueAsJson.put("ACCEL_LN_X", value.getAccelLnX());
                    valueAsJson.put("ACCEL_LN_Y", value.getAccelLnY());
                    valueAsJson.put("ACCEL_LN_Z", value.getAccelLnZ());
                    valueAsJson.put("ACCEL_WR_X", value.getAccelWrX());
                    valueAsJson.put("ACCEL_WR_Y", value.getAccelWrY());
                    valueAsJson.put("ACCEL_WR_Z", value.getAccelWrZ());
                    valueAsJson.put("GYRO_X", value.getGyroX());
                    valueAsJson.put("GYRO_Y", value.getGyroY());
                    valueAsJson.put("GYRO_Z", value.getGyroZ());
                    valueAsJson.put("MAG_X", value.getMagX());
                    valueAsJson.put("MAG_Y", value.getMagY());
                    valueAsJson.put("MAG_Z", value.getMagZ());
                    valueAsJson.put("TEMPERATURE", value.getTemperature());
                    valueAsJson.put("PRESSURE", value.getPressure());
                    valueAsJson.put("TIMESTAMP", value.getTimestamp());
                    valueAsJson.put("REAL_TIME_CLOCK", value.getRealTimeClock());
                    valueAsJson.put("TIMESTAMP_SYNC", value.getTimestampSync());
                    valueAsJson.put("REAL_TIME_CLOCK_SYNC", value.getRealTimeClockSync());
                    valueAsJson.put("DataRecordingRequestID", drr.getGuid());
                    valueAsJson.put("SensorMAC", shimmer.getBluetoothAddress());

                    allValuesJson.put(valueAsJson);

                    //System.out.println(jsonValues.toString(2));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            System.out.println("-_-_-_-_-_-_-UPLOADING-_-_-_-_-_-_-");

            System.out.println("Upload path: " + uploadPath);
            System.out.println("data: \n");

            try {
                System.out.println(allValuesJson.toString(2));
            }catch (JSONException e){
                e.printStackTrace();
            }



            new SendToServerTask(record).execute(uploadPath, allValuesJson.toString());
            //this.execute(uploadPath, allValuesJson.toString());


            //System.out.println("-_-_-_-_-_-_-UPLOADING DONE-_-_-_-_-_-_-");

            //TODO send
        }

    }


}
