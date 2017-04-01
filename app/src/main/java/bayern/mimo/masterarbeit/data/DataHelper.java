package bayern.mimo.masterarbeit.data;

import java.util.List;

/**
 * Created by MiMo on 01.04.2017.
 */

public class DataHelper {

    private static List<DataRecording> recordings;

    private DataHelper(){}

    public static List<DataRecording> getDataRecordings(){
        return recordings;
    }
}
