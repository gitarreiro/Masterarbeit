package bayern.mimo.masterarbeit.data;

import android.location.Location;

import com.shimmerresearch.android.Shimmer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by MiMo
 */
public class DataRecording {

    private int drrID;
    private boolean isUploaded;
    private Map<Shimmer, List<ShimmerValue>> shimmerValues;
    private Date startDate;
    private Date endDate;
    private String category;
    private String detail;

    private List<Location> locations;

    public DataRecording(String category, String detail, Map<Shimmer, List<ShimmerValue>> shimmerValues, Date startDate, Date endDate){
        this.drrID = -1;
        this.isUploaded = false; //TODO vllt. ändern
        this.category = category;
        this.detail = detail;
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

    public String getCategory() {
        return category;
    }

    public String getDetail() {
        return detail;
    }

    public Map<Shimmer, List<ShimmerValue>> getShimmerValues() {
        return shimmerValues;
    }

    public void setDrrID(int drrID) {
        this.drrID = drrID;
    }

    public int getDrrID() {
        return drrID;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}
