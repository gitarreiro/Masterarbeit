package bayern.mimo.masterarbeit.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bayern.mimo.masterarbeit.activity.ConnectSensorsActivity;
import bayern.mimo.masterarbeit.data.ShimmerValue;

/**
 * Created by MiMo
 */

public class ShimmerHandler extends Handler {

    private ConnectSensorsActivity caller;

    private List<ShimmerValue> values;

    public ShimmerHandler(ConnectSensorsActivity caller) {
        this.caller = caller;
        this.values = new ArrayList<>();
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Shimmer.MESSAGE_READ:
                if ((msg.obj instanceof ObjectCluster)) {

                    Double accelLnX = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_LN_X);
                    Double accelLnY = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_LN_Y);
                    Double accelLnZ = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_LN_Z);

                    Double accelWrX = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_X);
                    Double accelWrY = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_Y);
                    Double accelWrZ = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_Z);

                    System.out.println("accelLnX: " + accelLnX);
                    System.out.println("accelWrX: " + accelWrX);



/*
                    ObjectCluster objectCluster =  (ObjectCluster) msg.obj;
                    Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get(Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR);  // first retrieve all the possible formats for the current sensor device
                    FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
                    if (formatCluster!=null){
                        Log.d("CalibratedData",objectCluster.mMyName + " AccelX: " + formatCluster.mData + " "+ formatCluster.mUnits);
                    }else{
                        System.out.println("formatCluster is null");
                    }
*/


                    Double gyroX = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.GYRO_X);
                    Double gyroY = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.GYRO_Y);
                    Double gyroZ = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.GYRO_Z);

                    Double magX = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.MAG_X);
                    Double magY = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.MAG_Y);
                    Double magZ = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.MAG_Z);

                    Double temperature = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.TEMPERATURE_BMP180);

                    Double pressure = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.PRESSURE_BMP180);

                    Double timestamp = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.TIMESTAMP);
                    Double realTimeClock = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.REAL_TIME_CLOCK);

                    Double timestampSync = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.TIMESTAMP_SYNC);
                    Double realTimeClockSync = getValue(msg, Configuration.Shimmer3.ObjectClusterSensorName.REAL_TIME_CLOCK_SYNC);

                    ShimmerValue value = new ShimmerValue(accelLnX, accelLnY, accelLnZ,
                            accelWrX, accelWrY, accelWrZ,
                            gyroX, gyroY, gyroZ,
                            magX, magY, magZ,
                            temperature, pressure,
                            timestamp, realTimeClock,
                            timestampSync, realTimeClockSync, null, null);

                    this.values.add(value);
                    System.out.println("added ShimmerValue");
                }
                break;
            case Shimmer.MESSAGE_TOAST:
                Log.d("toast", msg.getData().getString(Shimmer.TOAST));
                //Toast.makeText(getApplicationContext(), msg.getData().getString(Shimmer.TOAST),
                //        Toast.LENGTH_SHORT).show();
                break;

            case Shimmer.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                    case Shimmer.MSG_STATE_FULLY_INITIALIZED:
                        caller.notifyShimmerConnected();
                        Log.d("ConnectionStatus", "Might have been connected");
                        break;
                    case Shimmer.STATE_CONNECTING:
                        Log.d("ConnectionStatus", "Connecting");
                        break;
                    case Shimmer.STATE_NONE:
                        Log.d("ConnectionStatus", "No State");
                        break;
                    default:
                        System.out.println("ConnectionStatus: " + msg.arg1);
                }
                break;
            default:
                System.out.println("shimmer what: " + msg.what);

        }
    }

    private Double getValue(Message msg, String configKey) {
        Double value = null;

        ObjectCluster objectCluster = (ObjectCluster) msg.obj;
        Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get(configKey);
        FormatCluster formatCluster = ObjectCluster.returnFormatCluster(accelXFormats, "CAL");
        if (formatCluster != null)
            value = formatCluster.mData;
        else {
            if (configKey.equals(Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_X))
                System.out.println("getValue(): formatcluster is null");
        }

        return value;
    }

    public List<ShimmerValue> getValues() {
        System.out.println("returning " + this.values.size() + " ShimmerValues");
        return this.values;
    }

    public void reset() {
        this.values.clear();
    }

}
