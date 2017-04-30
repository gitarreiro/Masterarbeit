package bayern.mimo.masterarbeit.data;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by MiMo
 */
public class DataRecording {

    private DataRecordingRequest drr;
    private Map<String, List<ShimmerValue>> shimmerValues;
    private Date startDate;
    private Date endDate;
    //private String category;
    //private String detail;

    private List<Location> locations;

    public DataRecording(Map<String, List<ShimmerValue>> shimmerValues, List<Location> locations, DataRecordingRequest drr){
        this.drr = drr;
      //  this.category = category;
        //this.detail = detail;
        this.shimmerValues = shimmerValues;
        this.locations = locations;
        this.startDate = startDate;
        this.endDate = endDate;
    }



    public String getStartDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");

        return sdf.format(this.drr.getStartTime());
    }

    public int getSensorCount(){

        return shimmerValues.size();

    }

    public String getCategory() {
        return drr.getCategory();
    }

    public String getDetail() {
        return drr.getDetail();
    }

    public Map<String, List<ShimmerValue>> getShimmerValues() {
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
