package bayern.mimo.masterarbeit.common;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;

import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.DataRecording;
import bayern.mimo.masterarbeit.data.DataRecordingRequest;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.handler.ShimmerHandler;
import bayern.mimo.masterarbeit.listener.MALocationListener;

/**
 * Created by MiMo
 */

public class AppSensors {

    private static boolean isInitialized;
    private static Map<Shimmer, ShimmerHandler> shimmerSensors;
    private static MALocationListener locationListener;
    private static LocationManager locationManager;

    private static DataRecording record;
    private static Date startTime;
    private static String category;
    private static String detail;
    private static DataRecordingRequest drr;
    //TODO add category choice

    private AppSensors() {
    }

    public static void init(Context context) {
        if (isInitialized) return;
        shimmerSensors = new HashMap<>();

        locationListener = new MALocationListener();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, false);

        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        System.out.println("is GPS enabled? "+ (enabled ? "ja" : "nein"));

        isInitialized = true;
    }

    public static void addSensor(Shimmer sensor, ShimmerHandler handler) {
        shimmerSensors.put(sensor, handler);
    }

    public static boolean isReadyForRecording() {
        return isInitialized;
    }

    public static void removeSensor(Shimmer sensor) {
        //TODO check if this works
        shimmerSensors.remove(sensor);
    }

    public static boolean startRecording() {
        if (!isInitialized) return false; //TODO Fehlermeldung ausgeben

        for(ShimmerHandler handler : shimmerSensors.values()){
            handler.reset();
        }

        locationListener.reset();

        startTime = new Date();

        for(Shimmer shimmer:shimmerSensors.keySet()){
            shimmer.startStreaming();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void stopRecording() {
        if (!isInitialized) return; //TODO sollte eigentlich ned passieren; prüfen
        for (Shimmer shimmer : shimmerSensors.keySet()) {
            shimmer.stopStreaming();
        }

        /* TODO remove (only testing) */
        for(Shimmer shimmer: shimmerSensors.keySet()){
            ShimmerHandler handler = shimmerSensors.get(shimmer);

            System.out.println("values recorded for " + shimmer.getBluetoothAddress() + ": " + handler.getValues().size());

        }

        /*until here TODO remove*/
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }


    public static Map<Shimmer, ShimmerHandler> getShimmerValues() {
        return shimmerSensors;
    }

    public static List<Shimmer> getShimmerSensors() {
        return new ArrayList<>(shimmerSensors.keySet());
    }

    public static DataRecording getLastRecord() {
        return record;
    }

    public static void setCategory(String category) {
        AppSensors.category = category;
//        drr.setCategory(category);
        //System.out.println("set category = " + category);
    }

    public static void setDetail(String detail) {
        AppSensors.detail = detail;
  //      drr.setDetail(detail);
        //System.out.println("set detail = " + detail);
    }

    public static void setDrr(DataRecordingRequest drr){
        AppSensors.drr = drr;
    }

    public static void commit(Context context) {
        //System.out.println("AppSensors.commit()");




        //TODO testuser ändern

        Object[] sensors = shimmerSensors.keySet().toArray();

        String shimmer1MAC = null;
        if (sensors.length > 0)
            shimmer1MAC = ((Shimmer) sensors[0]).getBluetoothAddress();

        String shimmer2MAC = null;
        if (sensors.length > 1)
            shimmer2MAC = ((Shimmer) sensors[1]).getBluetoothAddress();

        String guid = UUID.randomUUID().toString();
        DataRecordingRequest drr = new DataRecordingRequest(guid,-1, "testuser", startTime, shimmer1MAC, shimmer2MAC, null, false, null, null, null, null);//TODO no heat sensor supported at the moment

        Map<String, List<ShimmerValue>> recordings = new HashMap<>();
        for (Shimmer key : shimmerSensors.keySet())
            recordings.put(key.getBluetoothAddress(), shimmerSensors.get(key).getValues());

        drr.setCategory(category);
        drr.setDetail(detail);
        drr.setStartTime(startTime);
        drr.setEndTime(new Date());

        AppSensors.setDrr(drr);

        //System.out.println("AppSensors.commit(): " + shimmerSensors.get((Shimmer)shimmerSensors.keySet().toArray()[0]).getValues().size() + " ShimmerValues");

        record = new DataRecording(recordings, locationListener.getRecordedLocations(), drr);

        DataHelper.init(context);
        DataHelper.addRecord(record, true, context);
    }

    public static DataRecordingRequest getDrr(){
        return drr;
    }
}
