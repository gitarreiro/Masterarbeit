package bayern.mimo.masterarbeit.data;

import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    public static void init() {
        if (isInitialized) return;

        records = new ArrayList<>();
        records.addAll(getRecordsFromDB());
        isInitialized = true;
    }

    public static List<DataRecording> getDataRecordings() {

        if(DEBUG){
            DataRecording record = new DataRecording("Test", new HashMap<Shimmer, List<ShimmerValue>>(), new Date(), new Date());
            List<DataRecording> tmpRecords = new ArrayList<>();
            tmpRecords.add(record);
            return tmpRecords;
        }



        init();
        return records;
    }

    public static boolean addRecord(DataRecording record, boolean shouldAddToDB) {
        records.add(record);
        if (shouldAddToDB)
            addRecordToDB(record);
        return true;
    }

    private static void addRecordToDB(DataRecording record) {
        //TODO store in SQLite
    }

    private static List<DataRecording> getRecordsFromDB() {
        List<DataRecording> dbRecords = new ArrayList<>();


        return dbRecords;
    }

}
