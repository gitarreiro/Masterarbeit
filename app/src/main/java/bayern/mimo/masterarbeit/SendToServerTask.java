package bayern.mimo.masterarbeit;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import net.gotev.uploadservice.*;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
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


            OutputStream os = urlConn.getOutputStream();
            printout = new DataOutputStream(os);
            printout.writeBytes(args[1]);
            printout.flush();
            printout.close();
            os.close();

            System.out.println("UPLOAD _ _ _ : closed all");
            int status = urlConn.getResponseCode();
            System.out.println("http status code is " + status);

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
                drr.setServerID(json.getInt("ID"));//TODO crashsicher
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

    private void requestSucceeded(DataRecordingRequest drr) {
        System.out.println("in requestSucceeded(drr)");

        //AppSensors.setDrr(drr);

        List<String> fileNames = new ArrayList<>();

        int i = 0;

        String path = context.getCacheDir() + "/ma/tmp/";
        File pathFile = new File(path);

        System.out.println("cache dir path: " + path);

        if (!pathFile.exists())
            pathFile.mkdirs();

        for (String shimmerAddress : record.getShimmerValues().keySet()) {

            i++;

            List<ShimmerValue> values = record.getShimmerValues().get(shimmerAddress);

            System.out.println("packing " + values.size() + " values");

            byte[] sensorMacAsBytes = shimmerAddress.getBytes();

            byte[] shimmerBytes = new byte[8 + sensorMacAsBytes.length + values.size() * 144];
            ByteBuffer shimmerBuffer = ByteBuffer.wrap(shimmerBytes).order(ByteOrder.LITTLE_ENDIAN);


            shimmerBuffer.putInt(sensorMacAsBytes.length);
            shimmerBuffer.put(sensorMacAsBytes);

            shimmerBuffer.putInt(drr.getServerID());

            System.out.println("writing " + values.size() + " Shimmer values for SMAC " + shimmerAddress);

            for (ShimmerValue value : values) {

                if(value == null)
                    System.out.println("bla"); //TODO for testing. Werte werden direkt nach der Aufnahme nicht richtig gespeichert.j

                shimmerBuffer.putDouble(value.getAccelLnX());
                shimmerBuffer.putDouble(value.getAccelLnY());
                shimmerBuffer.putDouble(value.getAccelLnZ());
                shimmerBuffer.putDouble(value.getAccelWrX());
                shimmerBuffer.putDouble(value.getAccelWrY());
                shimmerBuffer.putDouble(value.getAccelWrZ());
                shimmerBuffer.putDouble(value.getGyroX());
                shimmerBuffer.putDouble(value.getGyroY());
                shimmerBuffer.putDouble(value.getGyroZ());
                shimmerBuffer.putDouble(value.getMagX());
                shimmerBuffer.putDouble(value.getMagY());
                shimmerBuffer.putDouble(value.getMagZ());
                shimmerBuffer.putDouble(value.getTemperature());
                shimmerBuffer.putDouble(value.getPressure());
                shimmerBuffer.putDouble(value.getTimestamp());
                shimmerBuffer.putDouble(value.getRealTimeClock());
                shimmerBuffer.putDouble(value.getTimestampSync());
                shimmerBuffer.putDouble(value.getRealTimeClockSync());
            }

            shimmerBytes = shimmerBuffer.array();


            File theShimmerFile = new File(pathFile.getAbsolutePath() + "shimmer_" + i);
            if (theShimmerFile.exists()) {
                theShimmerFile.delete();
                System.out.println("shimmer file got deleted");
            }

            try {

                theShimmerFile.createNewFile();
                BufferedOutputStream bosShimmer = new BufferedOutputStream(new FileOutputStream(theShimmerFile, false));
                bosShimmer.write(shimmerBytes);
                bosShimmer.flush();
                bosShimmer.close();

                fileNames.add(theShimmerFile.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        byte[] locationBytes = new byte[4 + record.getLocations().size() * 56];

        ByteBuffer locationBuffer = ByteBuffer.wrap(locationBytes).order(ByteOrder.LITTLE_ENDIAN);

        locationBuffer.putInt(drr.getServerID());

        for (Location location : record.getLocations()) {

            locationBuffer.putDouble(location.getLatitude());
            locationBuffer.putDouble(location.getLongitude());
            locationBuffer.putLong(location.getTime());
            locationBuffer.putDouble(location.getAccuracy());
            locationBuffer.putDouble(location.getAltitude());
            locationBuffer.putLong(location.getElapsedRealtimeNanos());
            locationBuffer.putDouble(location.getSpeed());

        }

        locationBytes = locationBuffer.array();

        File locationFile = new File(pathFile.getAbsolutePath() + "location_" + System.currentTimeMillis());
        if (locationFile.exists()) {
            locationFile.delete();
            System.out.println("location file got deleted");
        }

        try {

            locationFile.createNewFile();
            BufferedOutputStream bosLocation = new BufferedOutputStream(new FileOutputStream(locationFile, false));
            bosLocation.write(locationBytes);
            bosLocation.flush();
            bosLocation.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        String uploadPath = Config.SERVER_API_URL + Config.UPLOAD_PATH;

        UploadService.NAMESPACE = net.gotev.uploadservice.BuildConfig.APPLICATION_ID;
        UploadService.HTTP_STACK = new OkHttpStack();


        try {




            MultipartUploadRequest request = new MultipartUploadRequest(context, uploadPath);
            // starting from 3.1+, you can also use content:// URI string instead of absolute file
            //.addFileToUpload(theShimmerFile.getAbsolutePath(), "shimmer")
            //.addFileToUpload(locationFile.getAbsolutePath(), "location")


            request.setNotificationConfig(new UploadNotificationConfig()); //TODO UploadNotificationConfig anpassen für besseres "Aussehen"
            request.setMaxRetries(2);
            request.setDelegate(new UploadStatusDelegate() {
                @Override
                public void onProgress(Context context, UploadInfo uploadInfo) {
                    // your code here
                }

                @Override
                public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                    // your code here
                    exception.printStackTrace();
                }

                @Override
                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                    // your code here
                    // if you have mapped your server response to a POJO, you can easily get it:
                    // YourClass obj = new Gson().fromJson(serverResponse.getBodyAsString(), YourClass.class);
                    System.out.println("finished upload - file should be deleted");
                }

                @Override
                public void onCancelled(Context context, UploadInfo uploadInfo) {
                    // your code here
                }
            });


            i = 0;
            for (String fileName : fileNames) {
                i++;
                request.addFileToUpload(fileName, "shimmer" + i);
                System.out.println("added " + fileName + " to upload");
            }

            request.addFileToUpload(locationFile.getAbsolutePath(), "location");



            String uploadId = request.startUpload();


        } catch (Exception e) {
            Log.e("AndroidUploadService", e.getMessage(), e);
        }












        /*

        JSONObject jsonToSend = new JSONObject();


        JSONArray shimmerValuesJson = new JSONArray();

        for (String shimmerAddress : record.getShimmerValues().keySet()) {

            List<ShimmerValue> values = record.getShimmerValues().get(shimmerAddress);

            //System.out.println("Number of values is " + values.size());

            System.out.println("creating " + values.size() + " for Shimmer " + shimmerAddress);

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
                valueAsJson.put("LONGITUDE", location.getLongitude());
                valueAsJson.put("TIMESTAMP", location.getTime());
                valueAsJson.put("ACCURACY", location.getAccuracy());
                valueAsJson.put("ALTITUDE", location.getAltitude());
                valueAsJson.put("ELAPSED_REAL_TIME_NANOS", location.getElapsedRealtimeNanos());
                valueAsJson.put("SPEED", location.getSpeed());
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
/*
        new SendToServerTask(record, context).execute(uploadPath, shimmerValuesJson.toString());

        */

    }


}
