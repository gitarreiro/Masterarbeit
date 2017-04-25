package bayern.mimo.masterarbeit.data;

import android.location.Location;

import com.shimmerresearch.android.Shimmer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import bayern.mimo.masterarbeit.common.AppSensors;

/**
 * Created by MiMo
 */
public class DataRecording {

    private DataRecordingRequest drr;
    private Map<Shimmer, List<ShimmerValue>> shimmerValues;
    private Date startDate;
    private Date endDate;
    private String category;
    private String detail;

    private List<Location> locations;

    public DataRecording(String category, String detail, Map<Shimmer, List<ShimmerValue>> shimmerValues, List<Location> locations, Date startDate, Date endDate, DataRecordingRequest drr){
        this.drr = drr;
        this.category = category;
        this.detail = detail;
        this.shimmerValues = shimmerValues;
        this.locations = locations;
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

    public DataRecordingRequest getDrr() {
        return drr;
    }

    public boolean isUploaded() {
        return drr != null && drr.isUploaded();
    }

    public void setUploaded() {
        if(this.drr != null)
            this.drr.setUploaded();
    }

    public List<Location> getLocations() {
        return locations;
    }
}
