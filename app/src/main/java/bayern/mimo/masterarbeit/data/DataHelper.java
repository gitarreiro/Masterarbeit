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

        System.out.println("initializing DataHelper...");
        if (isInitialized) return;

        System.out.println("initialization in progress! (did not return)");

        records = new ArrayList<>();
        records.addAll(getRecordsFromDB());
        isInitialized = true;
    }

    public static List<DataRecording> getDataRecordings() {

        init();
        System.out.println("recordings vor DEBUG: " + records.size());

        if(DEBUG){
            DataRecording record = new DataRecording("Test", "Desc", new HashMap<Shimmer, List<ShimmerValue>>(), new Date(), new Date());
            List<DataRecording> tmpRecords = new ArrayList<>();
            tmpRecords.add(record);
            //return tmpRecords;
            records.addAll(tmpRecords);
        }



        return records;
    }

    public static boolean addRecord(DataRecording record, boolean shouldAddToDB) {

        System.out.println("adding record with " + record.getSensorCount() +" sensors.");

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


    public static void setUploadCompleted(int drrID){
        for(DataRecording record : records){
            if(record.getDrrID() == drrID){
                record.setUploaded(true);
                break;
            }
        }

        //TODO set upload completed in database
    }

}
