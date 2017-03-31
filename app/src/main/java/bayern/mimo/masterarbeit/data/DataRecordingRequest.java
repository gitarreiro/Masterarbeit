package bayern.mimo.masterarbeit.data;

import java.util.Date;

/**
 * Created by MiMo
 */

public class DataRecordingRequest {

    private int id;
    private String username;
    private Date timestamp;
    private String shimmer1MAC;
    private String shimmer1GUID;
    private String shimmer2MAC;
    private String shimmer2GUID;
    private String heatMAC;
    private String heatGUID;

    public DataRecordingRequest(int id, String username, Date timestamp,
                                String shimmer1MAC, String shimmer1GUID,
                                String shimmer2MAC, String shimmer2GUID,
                                String heatMAC, String heatGUID) {
        this.id = id;
        this.username = username;
        this.timestamp = timestamp;
        this.shimmer1MAC = shimmer1MAC;
        this.shimmer1GUID = shimmer1GUID;
        this.shimmer2MAC = shimmer2MAC;
        this.shimmer2GUID = shimmer2GUID;
        this.heatMAC = heatMAC;
        this.heatGUID = heatGUID;
    }

    public int getId() {
        return id;
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

    public String getShimmer1GUID() {
        return shimmer1GUID;
    }

    public String getShimmer2MAC() {
        return shimmer2MAC;
    }

    public String getShimmer2GUID() {
        return shimmer2GUID;
    }

    public String getHeatMAC() {
        return heatMAC;
    }

    public String getHeatGUID() {
        return heatGUID;
    }
}
