package bayern.mimo.masterarbeit.data;

/**
 * Created by MiMo
 */

public class ShimmerValue {

    private Double accelLnX;
    private Double accelLnY;
    private Double accelLnZ;

    private Double accelWrX;
    private Double accelWrY;
    private Double accelWrZ;

    private Double gyroX;
    private Double gyroY;
    private Double gyroZ;

    private Double magX;
    private Double magY;
    private Double magZ;

    private Double temperature;

    private Double pressure;

    private Double timestamp;
    private Double realTimeClock;

    private Double timestampSync;
    private Double realTimeClockSync;

    private Integer drrId;
    private String shimmerMac;

    public ShimmerValue(Double accelLnX, Double accelLnY, Double accelLnZ,
                        Double accelWrX, Double accelWrY, Double accelWrZ,
                        Double gyroX, Double gyroY, Double gyroZ,
                        Double magX, Double magY, Double magZ,
                        Double temperature, Double pressure,
                        Double timestamp, Double realTimeClock,
                        Double timestampSync, Double realTimeClockSync, Integer drrId, String shimmerMac) {
        this.accelLnX = accelLnX;
        this.accelLnY = accelLnY;
        this.accelLnZ = accelLnZ;

        this.accelWrX = accelWrX;
        this.accelWrY = accelWrY;
        this.accelWrZ = accelWrZ;

        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;

        this.magX = magX;
        this.magY = magY;
        this.magZ = magZ;

        this.temperature = temperature;

        this.pressure = pressure;

        this.timestamp = timestamp;
        this.realTimeClock = realTimeClock;

        this.timestampSync = timestampSync;
        this.realTimeClockSync = realTimeClockSync;

        this.drrId = drrId;
        this.shimmerMac = shimmerMac;
    }

    public Double getAccelLnX() {
        return accelLnX;
    }

    public Double getAccelLnY() {
        return accelLnY;
    }

    public Double getAccelLnZ() {
        return accelLnZ;
    }

    public Double getAccelWrX() {
        return accelWrX;
    }

    public Double getAccelWrY() {
        return accelWrY;
    }

    public Double getAccelWrZ() {
        return accelWrZ;
    }

    public Double getGyroX() {
        return gyroX;
    }

    public Double getGyroY() {
        return gyroY;
    }

    public Double getGyroZ() {
        return gyroZ;
    }

    public Double getMagX() {
        return magX;
    }

    public Double getMagY() {
        return magY;
    }

    public Double getMagZ() {
        return magZ;
    }

    public Double getPressure() {
        return pressure;
    }

    public Double getRealTimeClock() {
        return realTimeClock;
    }

    public Double getRealTimeClockSync() {
        return realTimeClockSync;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public Double getTimestampSync() {
        return timestampSync;
    }

    public Integer getDrrId() {
        return drrId;
    }

    public String getShimmerMac() {
        return shimmerMac;
    }
}
