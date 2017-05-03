package bayern.mimo.masterarbeit.data;

import java.util.Date;

/**
 * Created by MiMo
 */

public class DataRecordingRequest {

    private String guid;
    private int serverID;
    private String username;
    private Date timestamp;
    private String shimmer1MAC;
    private String shimmer2MAC;
    private String heatMAC;
    private boolean isUploaded;
    private String category;
    private String detail;
    private Date startTime;
    private Date endTime;

    public DataRecordingRequest(String guid, int serverID, String username, Date timestamp,
                                String shimmer1MAC,
                                String shimmer2MAC,
                                String heatMAC,
                                boolean isUploaded,
                                String category,
                                String detail,
                                Date startTime,
                                Date endTime) {
        this.guid = guid;
        this.serverID = serverID;
        this.username = username;
        this.timestamp = timestamp;
        this.shimmer1MAC = shimmer1MAC;
        this.shimmer2MAC = shimmer2MAC;
        this.heatMAC = heatMAC;
        this.isUploaded = isUploaded;
        this.category = category;
        this.detail = detail;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getGuid() {
        return guid;
    }

    //public void setServerID(int serverID) {
    //    this.serverID = serverID;
    //}

    public int getServerID() {
        return serverID;
    }

    public String getUsername() {
        return username;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getShimmer1MAC() {
        return shimmer1MAC;
    }

    public String getShimmer2MAC() {
        return shimmer2MAC;
    }

    public String getHeatMAC() {
        return heatMAC;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(){
        this.isUploaded = true;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GUID: ").append(guid).append("\n");
        sb.append("Server ID: ").append(serverID).append("\n");
        sb.append("Username: ").append(username).append("\n");
        sb.append("Timestamp: ").append(timestamp).append("\n");
        sb.append("Shimmer1MAC: ").append(shimmer1MAC).append("\n");
        sb.append("Shimmer2MAC: ").append(shimmer2MAC).append("\n");
        sb.append("HeatMAC: ").append(heatMAC).append("\n");
        sb.append("is uploaded: ").append(isUploaded).append("\n");
        sb.append("Category: ").append(category).append("\n");
        sb.append("Detail: ").append(detail).append("\n");
        return sb.toString();
    }
}
