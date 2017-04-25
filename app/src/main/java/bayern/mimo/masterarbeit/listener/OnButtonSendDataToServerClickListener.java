package bayern.mimo.masterarbeit.listener;

import android.view.View;

import com.shimmerresearch.android.Shimmer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import bayern.mimo.masterarbeit.SendToServerTask;
import bayern.mimo.masterarbeit.activity.StartRideActivity;
import bayern.mimo.masterarbeit.common.AppSensors;
import bayern.mimo.masterarbeit.data.DataRecordingRequest;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.handler.ShimmerHandler;
import bayern.mimo.masterarbeit.util.Config;

/**
 * Created by MiMo
 */

public class OnButtonSendDataToServerClickListener implements View.OnClickListener {

    private StartRideActivity caller;
    private SendToServerTask task;

    public OnButtonSendDataToServerClickListener(StartRideActivity caller) {
        this.caller = caller;
    }

    @Override
    public void onClick(View v) {
/*
        List<List<ShimmerValue>> values = AppSensors.getShimmerValues();

        int max = 0;
        for (List<ShimmerValue> list : values) {
            System.out.println("List size: " + list.size());
            max = Math.max(max, list.size());
        }

        Toast.makeText(caller, max + " Sensordatensätze (maximal)", Toast.LENGTH_LONG).show();
*/



    }




    /*public void requestSucceeded(DataRecordingRequest drr) {


        System.out.println("in requestSucceeded(drr)");
        //TODO send all the shit
        Map<Shimmer, ShimmerHandler> shimmerSensors = AppSensors.getShimmerValues();
        JSONArray jsonValues = new JSONArray();


        for (Shimmer shimmer : shimmerSensors.keySet()) {

            ShimmerHandler handler = shimmerSensors.get(shimmer);
            List<ShimmerValue> values = handler.getValues();

            //TODO JSON bauen
            for (ShimmerValue value : values) {
                if (value.getTimestamp() == null) continue;
                JSONObject jsonValue = new JSONObject();
                try {
                    jsonValue.put("AccelLnX", value.getAccelLnX());
                    jsonValue.put("AccelLnY", value.getAccelLnY());
                    jsonValue.put("AccelLnZ", value.getAccelLnZ());
                    jsonValue.put("AccelWrX", value.getAccelWrX());
                    jsonValue.put("AccelWrY", value.getAccelWrY());
                    jsonValue.put("AccelWrZ", value.getAccelWrZ());
                    jsonValue.put("GyroX", value.getGyroX());
                    jsonValue.put("GyroY", value.getGyroY());
                    jsonValue.put("GyroZ", value.getGyroZ());
                    jsonValue.put("MagX", value.getMagX());
                    jsonValue.put("MagY", value.getMagY());
                    jsonValue.put("MagZ", value.getMagZ());
                    jsonValue.put("Temperature", value.getTemperature());
                    jsonValue.put("RealTimeClock", value.getRealTimeClock());
                    jsonValue.put("TimestampSync", value.getTimestampSync());
                    jsonValue.put("RealTimeClockSync", value.getRealTimeClockSync());
                    jsonValue.put("DataRecordingRequestID", drr.getGuid());

                    jsonValues.put(jsonValue);

                    //System.out.println(jsonValues.toString(2));

                } catch(JSONException e){
                    e.printStackTrace();
                }

                //TODO send

            }
        }
    }*/


}
