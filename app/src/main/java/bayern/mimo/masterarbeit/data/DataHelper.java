package bayern.mimo.masterarbeit.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bayern.mimo.masterarbeit.util.Config;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by MiMo
 */

public class DataHelper {

    //TODO entfernen, wenn nicht mehr gebraucht
    private static final boolean DEBUG = true;


    private static List<DataRecording> records;
    private static boolean isInitialized = false;

    private DataHelper() {
    }

    public static void init(Context context) {

        System.out.println("initializing DataHelper...");
        if (isInitialized) return;

        System.out.println("initialization in progress! (did not return)");

        records = new ArrayList<>();
        records.addAll(getRecordsFromDB(context));

        isInitialized = true;
    }

    public static List<DataRecording> getDataRecordings(Context context) {

        init(context);
        System.out.println("recordings vor DEBUG: " + records.size());

        if (DEBUG) {
            /*DataRecording record = new DataRecording("Test", "Desc", new HashMap<Shimmer, List<ShimmerValue>>(), new Date(), new Date());
            List<DataRecording> tmpRecords = new ArrayList<>();
            tmpRecords.add(record);
            //return tmpRecords;
            records.addAll(tmpRecords);*/
        }


        return records;
    }

    public static boolean addRecord(DataRecording record, boolean shouldAddToDB, Context context) {

        System.out.println("adding record with " + record.getSensorCount() + " sensors.");

        records.add(record);
        if (shouldAddToDB)
            addRecordToDB(record, context);
        return true;
    }

    private static void addRecordToDB(DataRecording record, Context context) {
        SQLiteDatabase maDB = context.openOrCreateDatabase(Config.DB_NAME, MODE_PRIVATE, null);

/*
private String guid;
    private int serverID;
    private String username;
    private Date timestamp;
    private String shimmer1MAC;
    private String shimmer2MAC;
    private String heatMAC;
    private boolean isUploaded;
 */

        maDB.execSQL("CREATE TABLE IF NOT EXISTS DataRecordingRequest ("
                + "GUID VARCHAR,"
                + "SERVERID INTEGER,"
                + "USERNAME VARCHAR"
                + "TIMESTAMP REAL"
                + "SHIMMER1MAC VARCHAR"
                + "SHIMMER2MAC VARCHAR"
                + "HEATMAC VARCHAR"
                + "ISUPLOADED INTEGER"
                + ");"
        );

        maDB.execSQL("INSERT INTO DataRecordingRequest (GUID, SERVERID, USERNAME, TIMESTAMP, SHIMMER1MAC, SHIMMER2MAC, HEATMAC, ISUPLOADED)"
                + "VALUES(" + record.getDrr().getGuid()
                + "," + record.getDrr().getServerID()
                + "," + record.getDrr().getUsername()
                + "," + record.getDrr().getTimestamp()
                + "," + record.getDrr().getShimmer1MAC()
                + "," + record.getDrr().getShimmer2MAC()
                + "," + record.getDrr().getHeatMAC()
                + "," + "0"
                + ");"
        );


        maDB.execSQL("CREATE TABLE IF NOT EXISTS ShimmerValues"
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ACCEL_LN_X REAL, "
                + "ACCEL_LN_Y REAL, "
                + "ACCEL_LN_Z REAL, "
                + "ACCEL_WR_X REAL, "
                + "ACCEL_WR_Y REAL, "
                + "ACCEL_WR_Z REAL, "
                + "GYRO_X REAL, "
                + "GYRO_Y  REAL, "
                + "GYRO_Z REAL, "
                + "MAG_X REAL, "
                + "MAG_Y REAL, MAG_Z REAL, "
                + "TEMPERATURE REAL, "
                + "PRESSURE REAL, "
                + "TIMESTAMP REAL, "
                + "REAL_TIME_CLOCK REAL, "
                + "TIMESTAMP_SYNC REAL, "
                + "REAL_TIME_CLOCK_SYNC REAL,"
                + "DataRecordingRequestID VARCHAR, "
                + "SensorMAC VARCHAR "
                + ");"
        );

        Map<Shimmer, List<ShimmerValue>> map = record.getShimmerValues();
        for (Shimmer shimmer : map.keySet()) {
            List<ShimmerValue> values = map.get(shimmer);
            for (ShimmerValue value : values) {
                maDB.execSQL("INSERT INTO ShimmerValues (" +

                        "ACCEL_LN_X, ACCEL_LN_Y, ACCEL_LN_Z, ACCEL_WR_X, ACCEL_WR_Y, ACCEL_WR_Z, GYRO_X, GYRO_Y, GYRO_Z, MAG_X, MAG_Y, MAG_Z,"
                        + " TEMPERATURE, PRESSURE, TIMESTAMP, REAL_TIME_CLOCK, TIMESTAMP_SYNC, REAL_TIME_CLOCK_SYNC,DataRecordingRequestID, SensorMAC)"


                        +
                        " VALUES("
                        + value.getAccelLnX() + ","
                        + value.getAccelLnY() + ","
                        + value.getAccelLnZ() + ","
                        + value.getAccelWrX() + ","
                        + value.getAccelWrY() + ","
                        + value.getAccelWrZ() + ","
                        + value.getGyroX() + ","
                        + value.getGyroY() + ","
                        + value.getGyroZ() + ","
                        + value.getMagX() + ","
                        + value.getMagY() + ","
                        + value.getMagZ() + ","
                        + value.getTemperature() + ","
                        + value.getPressure() + ","
                        + value.getTimestamp() + ","
                        + value.getRealTimeClock() + ","
                        + value.getTimestampSync() + ","
                        + value.getRealTimeClockSync() + ","
                        + record.getDrr().getGuid() + ",'"
                        + shimmer.getBluetoothAddress() + "'"
                        + ");"
                );
            }
        }


        maDB.execSQL("CREATE TABLE IF NOT EXISTS Locations"
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "LATITUDE REAL, "
                + "LONGITUDE REAL, "
                + "TIMESTAMP REAL, "
                + "ACCURACY REAL, "
                + "ALTITUDE REAL, "
                + "ELAPSEDREALTIMENANOS REAL, "
                + "SPEED REAL, "
                + "DataRecordingRequestID VARCHAR, "
                + ");"
        );

        for (Location location : record.getLocations()) {
            maDB.execSQL(
                    "INSERT INTO Locations (LATITUDE, LONGITUDE, TIMESTAMP, ACCURACY, ALTITUDE, ELAPSEDREALTIMENANOS, SPEED, DataRecordingRequestID)"
                            +" VALUES ("+location.getLatitude()
                            + ","+location.getLongitude()
                            + ","+location.getTime()
                            + ","+location.getAccuracy()
                            + ","+location.getAltitude()
                            + ","+location.getElapsedRealtimeNanos()
                            + ","+location.getSpeed()
                            + ","+record.getDrr().getGuid()
                            + ");"
            );
        }


    }

    private static List<DataRecording> getRecordsFromDB(Context context) {
        List<DataRecording> dbRecords = new ArrayList<>();


        SQLiteDatabase maDB = context.openOrCreateDatabase(Config.DB_NAME, MODE_PRIVATE, null);
        maDB.execSQL("CREATE TABLE IF NOT EXISTS ShimmerValues(ID INTEGER PRIMARY KEY AUTOINCREMENT, ACCEL_LN_X REAL, ACCEL_LN_Y REAL, ACCEL_LN_Z REAL, ACCEL_WR_X REAL, ACCEL_WR_Y REAL, ACCEL_WR_Z REAL, GYRO_X REAL, GYRO_Y  REAL, GYRO_Z REAL, MAG_X REAL, MAG_Y REAL, MAG_Z REAL, TEMPERATURE REAL, PRESSURE REAL, TIMESTAMP REAL, REAL_TIME_CLOCK REAL, TIMESTAMP_SYNC REAL, REAL_TIME_CLOCK_SYNC REAL,DataRecordingRequestID INTEGER, SensorMAC VARCHAR, Uploaded INTEGER );");

        // TODO fetch restults
        Cursor resultSet = maDB.rawQuery("Select * from ShimmerValues", null);


        if (resultSet != null && resultSet.getCount() > 0) {

            resultSet.moveToFirst();
            double id = resultSet.getDouble(0);
            double accelLnX = resultSet.getDouble(1);
            double accelLnY = resultSet.getDouble(2);
            double accelLnZ = resultSet.getDouble(3);
            double accelWrX = resultSet.getDouble(4);
            double accelWrY = resultSet.getDouble(5);
            double accelWrZ = resultSet.getDouble(6);
            double gyroX = resultSet.getDouble(7);
            double gyroY = resultSet.getDouble(8);
            double gyroZ = resultSet.getDouble(9);
            double magX = resultSet.getDouble(10);
            double magY = resultSet.getDouble(11);
            double magZ = resultSet.getDouble(12);
            double temperature = resultSet.getDouble(13);
            double pressure = resultSet.getDouble(14);
            double timestamp = resultSet.getDouble(15);
            double realTimeClock = resultSet.getDouble(16);
            double timestampSync = resultSet.getDouble(17);
            double realTimeClockSync = resultSet.getDouble(18);
            int drrID = resultSet.getInt(19);
            String shimmerMAC = resultSet.getString(20);


            ShimmerValue value = new ShimmerValue(accelLnX, accelLnY, accelLnZ, accelWrX, accelWrY, accelWrZ, gyroX, gyroY, gyroZ, magX, magY, magZ, temperature, pressure, timestamp, realTimeClock, timestampSync, realTimeClockSync, drrID, shimmerMAC);
        }

        return dbRecords;
    }


    public static void setUploadCompleted(int drrID) {
        for (DataRecording record : records) {
            if (record.getDrr().getServerID() == drrID) {
                record.setUploaded();
                break;
            }
        }

        //TODO set upload completed in database

    }

}
