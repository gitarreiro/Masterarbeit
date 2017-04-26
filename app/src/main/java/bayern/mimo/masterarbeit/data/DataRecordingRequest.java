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

    public DataRecordingRequest(String guid, int serverID, String username, Date timestamp,
                                String shimmer1MAC,
                                String shimmer2MAC,
                                String heatMAC, boolean isUploaded) {
        this.guid = guid;
        this.serverID = serverID;
        this.username = username;
        this.timestamp = timestamp;
        this.shimmer1MAC = shimmer1MAC;
        this.shimmer2MAC = shimmer2MAC;
        this.heatMAC = heatMAC;
        this.isUploaded = isUploaded;
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
}
