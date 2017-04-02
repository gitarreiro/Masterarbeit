package bayern.mimo.masterarbeit.common;

import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.DataRecording;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.handler.ShimmerHandler;

/**
 * Created by MiMo
 */

public class AppSensors {

    private static boolean isInitialized;
    private static Map<Shimmer, ShimmerHandler> shimmerSensors;
    private static DataRecording record;
    private static Date startTime;
    private static String info;
    //TODO add category choice

    private AppSensors() {
    }

    public static void init() {
        if (isInitialized) return;
        shimmerSensors = new HashMap<>();
        isInitialized = true;
    }

    public static void addSensor(Shimmer sensor, ShimmerHandler handler) {

        shimmerSensors.put(sensor, handler);
    }

    public static boolean isReadyForRecording(){
        return isInitialized;
    }

    public static void removeSensor(Shimmer sensor) {
        //TODO check if this works
        shimmerSensors.remove(sensor);
    }

    public static boolean startRecording() {
        if (!isInitialized) return false; //TODO Fehlermeldung ausgeben

        for (Shimmer shimmer : shimmerSensors.keySet()) {
            shimmer.startStreaming();
        }

        startTime = new Date();

        return true;
    }

    public static void stopRecording() {
        if (!isInitialized) return; //TODO sollte eigentlich ned passieren; prüfen
        for (Shimmer shimmer : shimmerSensors.keySet()) {
            shimmer.stopStreaming();
        }

        //DataHelper.init();
        //DataHelper.addRecord(record, true);
    }



    public static Map<Shimmer, ShimmerHandler> getShimmerValues() {
        return shimmerSensors;
    }

    public static List<Shimmer> getShimmerSensors() {
        return new ArrayList<>(shimmerSensors.keySet());
    }

    public static DataRecording getLastRecord(){
        return record;
    }

    public static void setInfo(String info){
        AppSensors.info = info;
    }

    public static void commit(){
        System.out.println("AppSensors.commit()");
        Map<Shimmer, List<ShimmerValue>> recordings = new HashMap<>();
        for(Shimmer key : shimmerSensors.keySet())
            recordings.put(key, shimmerSensors.get(key).getValues());

        record = new DataRecording(info, recordings, startTime, new Date());

        DataHelper.init();
        DataHelper.addRecord(record, true);
    }
}
