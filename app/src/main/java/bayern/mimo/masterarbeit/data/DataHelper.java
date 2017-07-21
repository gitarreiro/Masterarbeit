package bayern.mimo.masterarbeit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bayern.mimo.masterarbeit.util.Config;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by MiMo
 */

public class DataHelper {

    //TODO check closeDB()

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

    /**
     * TODO Reload from databsae using the same instance of the list
     *
     * @param context
     * @return
     */
    public static List<DataRecording> getDataRecordings(Context context, List<DataRecording> recordings){
        recordings.clear();

        records = new ArrayList<>();
        records.addAll(getRecordsFromDB(context));

        recordings.addAll(records);

        return recordings;
    }

    public static boolean addRecord(DataRecording record, boolean shouldAddToDB, Context context) {

        System.out.println("adding record with " + record.getSensorCount() + " sensors.");

        System.out.println("Record to store is: " + record.toString());

        records.add(record);
        if (shouldAddToDB)
            addRecordToDB(record, context);
        return true;
    }

    private static void addRecordToDB(DataRecording record, Context context) {
        SQLiteDatabase maDB = context.openOrCreateDatabase(Config.DB_NAME, MODE_PRIVATE, null);

        maDB.beginTransaction();

        createTableDRR(maDB);

        //maDB.insert(table, null, ContentValues) TODO Umbau: schönere Möglichkeit

        ContentValues drrValues = new ContentValues();
        drrValues.put("GUID", record.getDrr().getGuid());
        drrValues.put("SERVERID", record.getDrr().getServerID());
        drrValues.put("USERNAME", "Testuser");//TODO replace by app user
        drrValues.put("TIMESTAMP", record.getDrr().getTimestamp().getTime());
        drrValues.put("SHIMMER1MAC", record.getDrr().getShimmer1MAC());
        drrValues.put("SHIMMER2MAC", record.getDrr().getShimmer2MAC());
        drrValues.put("HEATMAC", record.getDrr().getHeatMAC());
        drrValues.put("ISUPLOADED", record.getDrr().isUploaded());
        drrValues.put("CATEGORY", record.getDrr().getCategory());
        drrValues.put("DETAIL", record.getDrr().getDetail());
        drrValues.put("STARTDATE", record.getDrr().getStartTime().getTime());
        drrValues.put("ENDDATE", record.getDrr().getEndTime().getTime());

        maDB.insert("DataRecordingRequest", null, drrValues);

        createTableShimmerValues(maDB);



        Map<String, List<ShimmerValue>> map = record.getShimmerValues();
        for (String shimmerMAC : map.keySet()) {
            List<ShimmerValue> values = map.get(shimmerMAC);
            for (ShimmerValue value : values) {

                ContentValues shimmerValues = new ContentValues();
                shimmerValues.put("ACCEL_LN_X", value.getAccelLnX());
                shimmerValues.put("ACCEL_LN_Y", value.getAccelLnY());
                shimmerValues.put("ACCEL_LN_Z", value.getAccelLnZ());
                shimmerValues.put("ACCEL_WR_X", value.getAccelWrX());
                shimmerValues.put("ACCEL_WR_Y", value.getAccelWrY());
                shimmerValues.put("ACCEL_WR_Z", value.getAccelWrZ());
                shimmerValues.put("GYRO_X", value.getGyroX());
                shimmerValues.put("GYRO_Y", value.getGyroY());
                shimmerValues.put("GYRO_Z", value.getGyroZ());
                shimmerValues.put("MAG_X", value.getMagX());
                shimmerValues.put("MAG_Y", value.getMagY());
                shimmerValues.put("MAG_Z", value.getMagZ());
                shimmerValues.put("TEMPERATURE", value.getTemperature());
                shimmerValues.put("PRESSURE", value.getPressure());
                shimmerValues.put("TIMESTAMP", value.getTimestamp());
                shimmerValues.put("REAL_TIME_CLOCK", value.getRealTimeClock());
                shimmerValues.put("TIMESTAMP_SYNC", value.getTimestampSync());
                shimmerValues.put("REAL_TIME_CLOCK_SYNC", value.getRealTimeClockSync());
                shimmerValues.put("DataRecordingRequestID", record.getDrr().getGuid());
                shimmerValues.put("SensorMAC", shimmerMAC);

                maDB.insert("ShimmerValues", null, shimmerValues);

                //System.out.println("should have added single ShimmerValue, drrId = " + record.getDrr().getGuid());
                /*
                maDB.execSQL("INSERT INTO ShimmerValues (" +
                        "ACCEL_LN_X, ACCEL_LN_Y, ACCEL_LN_Z, ACCEL_WR_X, ACCEL_WR_Y, ACCEL_WR_Z, GYRO_X, GYRO_Y, GYRO_Z, MAG_X, MAG_Y, MAG_Z,"
                        + " TEMPERATURE, PRESSURE, TIMESTAMP, REAL_TIME_CLOCK, TIMESTAMP_SYNC, REAL_TIME_CLOCK_SYNC,DataRecordingRequestID, SensorMAC)"
                        + " VALUES("
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
                        + shimmerMAC + "'"
                        + ");"
                );*/
            }
        }

        //System.out.println("should have added all ShimmerValues");


        createTableLocations(maDB);

        for (Location location : record.getLocations()) {

            ContentValues locationValues = new ContentValues();
            locationValues.put("LATITUDE", location.getLatitude());
            locationValues.put("LONGITUDE", location.getLongitude());
            locationValues.put("TIMESTAMP", location.getTime());
            locationValues.put("ACCURACY", location.getAccuracy());
            locationValues.put("ALTITUDE", location.getAltitude());
            locationValues.put("ELAPSEDREALTIMENANOS", location.getElapsedRealtimeNanos());
            locationValues.put("SPEED", location.getSpeed());
            locationValues.put("DataRecordingRequestID", record.getDrr().getGuid());

            maDB.insert("Locations", null, locationValues);


        }

        //System.out.println("should have added all Locations");

        maDB.setTransactionSuccessful();
        maDB.endTransaction();

        maDB.close();

    }

    private static List<DataRecording> getRecordsFromDB(Context context) {
        List<DataRecording> dbRecords = new ArrayList<>();


        SQLiteDatabase maDB = context.openOrCreateDatabase(Config.DB_NAME, MODE_PRIVATE, null);

        createTableDRR(maDB);

        Cursor cursorDRRs = maDB.rawQuery("SELECT * FROM DataRecordingRequest", null);

        System.out.println("in maDB: found " + cursorDRRs.getCount() + " DRRs");

        List<DataRecordingRequest> drrs = new ArrayList<>();


        while (cursorDRRs.moveToNext()) {
            //TODO hole DRRS, baue DRR, füge in DRR Liste hinzu

            String guid = cursorDRRs.getString(0);
            int serverID = cursorDRRs.getInt(1);
            String username = cursorDRRs.getString(2);
            long timestamp = cursorDRRs.getLong(3);
            String shimmer1MAC = cursorDRRs.getString(4);
            String shimmer2MAC = cursorDRRs.getString(5);
            String heatMAC = cursorDRRs.getString(6);
            boolean isUploaded = cursorDRRs.getInt(7) == 1;
            String category = cursorDRRs.getString(8);
            String detail = cursorDRRs.getString(9);
            Date startDate = new Date(cursorDRRs.getLong(10));
            Date endDate = new Date(cursorDRRs.getLong(11));

            DataRecordingRequest drr = new DataRecordingRequest(guid, serverID, username, new Date(timestamp), shimmer1MAC, shimmer2MAC, heatMAC, isUploaded, category, detail, startDate, endDate);

            drrs.add(drr);

            //System.out.println("found drr with isUploaded = " + isUploaded);


        }

        cursorDRRs.close();



        /*
        TEST
         */


//        Cursor cursorShimmerValues = maDB.rawQuery("SELECT * FROM ShimmerValues", null);
//        System.out.println("in Table ShimmerValues: " + cursorShimmerValues.getCount() + " values");


        /*
        TEST ENDE
         */


        createTableShimmerValues(maDB);


        for (DataRecordingRequest drr : drrs) {

            Map<String, List<ShimmerValue>> shimmers = new HashMap<>();

            //System.out.println("AKTUELL S1MAC = " + drr.getShimmer1MAC());
            //System.out.println("AKTUELL S2MAC = " + drr.getShimmer2MAC());

            if (drr.getShimmer1MAC() != null && !drr.getShimmer1MAC().equals("null")) {
                //System.out.println("called for S1 = '" + drr.getShimmer1MAC() + "'");
                List<ShimmerValue> shimmerValues = getShimmerValues(maDB, drr.getGuid(), drr.getShimmer1MAC());
                shimmers.put(drr.getShimmer1MAC(), shimmerValues);
            }

            if (drr.getShimmer2MAC() != null && !drr.getShimmer2MAC().equals("null")) {
                //System.out.println("called for S2 = '" + drr.getShimmer2MAC() + "'");
                List<ShimmerValue> shimmerValues = getShimmerValues(maDB, drr.getGuid(), drr.getShimmer2MAC());
                shimmers.put(drr.getShimmer2MAC(), shimmerValues);
            }

            List<Location> locations = getLocations(maDB, drr.getGuid());

            DataRecording record = new DataRecording(shimmers, locations, drr);
            dbRecords.add(record);
        }


        maDB.close();
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


    private static List<ShimmerValue> getShimmerValues(SQLiteDatabase db, String drrGuid, String sensorMAC) {

        //System.out.println("getShimmerValues(db, drrGuid=" + drrGuid + ", sensorMac=" + sensorMAC + ")");

        //Cursor test = db.rawQuery("SELECT * FROM ShimmerValues", null);
        //System.out.println("gesamte ShimmerValue-Tabelle hat 0" + test.getCount() + " Einträge.");
        //test.moveToFirst();
        //System.out.println("found Shimmer MAC:" + test.getString(test.getColumnIndex("SensorMAC")));
        //System.out.println("DRR GUID is: " + test.getString(test.getColumnIndex("DataRecordingRequestID")));


        Cursor cursorShimmerValues = db.rawQuery("SELECT * FROM ShimmerValues WHERE DataRecordingRequestID=? AND SensorMAC=?", new String[]{drrGuid, sensorMAC});

        //System.out.println("Shimmer Values for DRR GUID " + drrGuid + " and SensorMAC " + sensorMAC + ": " + cursorShimmerValues.getCount());

        List<ShimmerValue> shimmerValues = new ArrayList<>();
        //TODO evtl. null-check
        while (cursorShimmerValues.moveToNext()) {

            /*

            ID INTEGER PRIMARY KEY AUTOINCREMENT, "
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
                + "SensorMAC VARCHAR"

             */


            cursorShimmerValues.getColumnIndex("SensorMAC");
            double id = cursorShimmerValues.getDouble(0);
            double accelLnX = cursorShimmerValues.getDouble(cursorShimmerValues.getColumnIndex("ACCEL_LN_X"));
            double accelLnY = cursorShimmerValues.getDouble(cursorShimmerValues.getColumnIndex("ACCEL_LN_Y"));
            double accelLnZ = cursorShimmerValues.getDouble(cursorShimmerValues.getColumnIndex("ACCEL_LN_Z"));
            double accelWrX = cursorShimmerValues.getDouble(4);
            double accelWrY = cursorShimmerValues.getDouble(5);
            double accelWrZ = cursorShimmerValues.getDouble(6);
            double gyroX = cursorShimmerValues.getDouble(7);
            double gyroY = cursorShimmerValues.getDouble(8);
            double gyroZ = cursorShimmerValues.getDouble(9);
            double magX = cursorShimmerValues.getDouble(10);
            double magY = cursorShimmerValues.getDouble(11);
            double magZ = cursorShimmerValues.getDouble(12);
            double temperature = cursorShimmerValues.getDouble(13);
            double pressure = cursorShimmerValues.getDouble(14);
            double timestamp = cursorShimmerValues.getDouble(15);
            double realTimeClock = cursorShimmerValues.getDouble(16);
            double timestampSync = cursorShimmerValues.getDouble(17);
            double realTimeClockSync = cursorShimmerValues.getDouble(18);
            int drrID = cursorShimmerValues.getInt(19);
            String shimmerMAC = cursorShimmerValues.getString(20);

            ShimmerValue value = new ShimmerValue(accelLnX, accelLnY, accelLnZ, accelWrX, accelWrY, accelWrZ, gyroX, gyroY, gyroZ, magX, magY, magZ, temperature, pressure, timestamp, realTimeClock, timestampSync, realTimeClockSync, drrID, shimmerMAC);
            shimmerValues.add(value);
        }

        cursorShimmerValues.close();

        return shimmerValues;
    }

    private static List<Location> getLocations(SQLiteDatabase db, String drrGuid) {
        Cursor cursorLocations = db.rawQuery("SELECT * FROM Locations WHERE DataRecordingRequestID=?", new String[]{drrGuid});

        //System.out.println("Locations for DRR GUID " + drrGuid + ": " + cursorLocations.getCount());

        List<Location> locations = new ArrayList<>();
        //TODO evtl. null-check
        /*
        (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "LATITUDE REAL, "
                + "LONGITUDE REAL, "
                + "TIMESTAMP REAL, "
                + "ACCURACY REAL, "
                + "ALTITUDE REAL, "
                + "ELAPSEDREALTIMENANOS REAL, "
                + "SPEED REAL, "
                + "DataRecordingRequestID VARCHAR, "
         */


        System.out.println("starting finding locations........");
        while (cursorLocations.moveToNext()) {

            int id = cursorLocations.getInt(0);
            double latitude = cursorLocations.getDouble(1);
            double longitude = cursorLocations.getDouble(2);
            long timestamp = cursorLocations.getLong(3);
            float accuracy = cursorLocations.getFloat(4);
            double altitude = cursorLocations.getDouble(5);
            long elapsedRealTimeNanos = cursorLocations.getLong(6);
            float speed = cursorLocations.getFloat(7);

            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setTime(timestamp);
            location.setAccuracy(accuracy);
            location.setAltitude(altitude);
            location.setElapsedRealtimeNanos(elapsedRealTimeNanos);
            location.setSpeed(speed);


            locations.add(location);
        }

        cursorLocations.close();

        return locations;
    }

    public static void updateDrrServerID(DataRecordingRequest drr, Context context) {
        SQLiteDatabase maDB = context.openOrCreateDatabase(Config.DB_NAME, MODE_PRIVATE, null);

        createTableDRR(maDB);

        maDB.execSQL("UPDATE DataRecordingRequest SET SERVERID=? WHERE GUID=?", new String[]{String.valueOf(drr.getServerID()), drr.getGuid()});
        maDB.close();
    }

    public static void updateDrrUploaded(int drrID, Context context) {
        SQLiteDatabase maDB = context.openOrCreateDatabase(Config.DB_NAME, MODE_PRIVATE, null);

        createTableDRR(maDB);

        maDB.execSQL("UPDATE DataRecordingRequest SET ISUPLOADED=1 WHERE SERVERID=?", new String[]{String.valueOf(drrID)});
        maDB.close();
    }


    private static void createTableDRR(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS DataRecordingRequest ("
                + "GUID VARCHAR,"
                + "SERVERID INTEGER,"
                + "USERNAME VARCHAR,"
                + "TIMESTAMP INT,"
                + "SHIMMER1MAC VARCHAR,"
                + "SHIMMER2MAC VARCHAR,"
                + "HEATMAC VARCHAR,"
                + "ISUPLOADED INTEGER,"
                + "CATEGORY VARCHAR,"
                + "DETAIL VARCHAR,"
                + "STARTDATE INT,"
                + "ENDDATE INT"
                + ");"
        );
    }

    private static void createTableShimmerValues(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ShimmerValues"
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
                + "SensorMAC VARCHAR"
                + ");"
        );
    }

    private static void createTableLocations(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Locations"
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "LATITUDE REAL, "
                + "LONGITUDE REAL, "
                + "TIMESTAMP INTEGER, "
                + "ACCURACY REAL, "
                + "ALTITUDE REAL, "
                + "ELAPSEDREALTIMENANOS INTEGER, "
                + "SPEED REAL, "
                + "DataRecordingRequestID VARCHAR"
                + ");"
        );
    }


/*
rumpfuschen
 */

    public static void dropAll(Context context) {
        SQLiteDatabase maDB = context.openOrCreateDatabase(Config.DB_NAME, MODE_PRIVATE, null);
        maDB.execSQL("DROP TABLE IF EXISTS DataRecordingRequest");
        maDB.execSQL("DROP TABLE IF EXISTS ShimmerValues");
        maDB.execSQL("DROP TABLE IF EXISTS Locations");
        maDB.close();
    }


}

//TODO DRR mitschreiben: startDate, endDate, category, detail
//TODO umbauen auf getColumnIndex(String columnName)