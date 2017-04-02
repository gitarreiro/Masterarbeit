package bayern.mimo.masterarbeit.data;

import com.shimmerresearch.android.Shimmer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by MiMo
 */
public class DataRecording {

    private Map<Shimmer, List<ShimmerValue>> shimmerValues;
    private Date startDate;
    private Date endDate;

    public DataRecording(Map<Shimmer, List<ShimmerValue>> shimmerValues, Date startDate, Date endDate){
        this.shimmerValues = shimmerValues;
        this.startDate = startDate;
        this.endDate = endDate;
    }



    public String getStartDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");

        return sdf.format(this.startDate);
    }

    public int getSensorCount(){

        return shimmerValues.size();

    }

}
