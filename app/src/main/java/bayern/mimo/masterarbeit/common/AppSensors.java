package bayern.mimo.masterarbeit.common;

import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.handler.ShimmerHandler;

/**
 * Created by MiMo
 */

public class AppSensors {

    private static boolean isInitialized;
    private static Map<Shimmer, ShimmerHandler> shimmerSensors;

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

    public static void removeSensor(Shimmer sensor) {
        //TODO check if this works
        shimmerSensors.remove(sensor);
    }

    public static boolean startRecording() {
        if (!isInitialized) return false; //TODO Fehlermeldung ausgeben
        for (Shimmer shimmer : shimmerSensors.keySet()) {
            shimmer.startStreaming();
        }
        return true;
    }

    public static void stopRecording() {
        if (!isInitialized) return;
        for (Shimmer shimmer : shimmerSensors.keySet()) {
            shimmer.stopStreaming();
        }
    }

    public static Map<Shimmer, ShimmerHandler> getShimmerValues() {
        return shimmerSensors;
    }

    public static List<Shimmer> getShimmerSensors(){
        return new ArrayList<>(shimmerSensors.keySet());
    }
}
