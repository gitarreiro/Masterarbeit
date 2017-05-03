package bayern.mimo.masterarbeit.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MiMo on 17.04.2017.
 */

public class MALocationListener implements LocationListener {

    private List<Location> locations;

    public MALocationListener(){
        this.locations = new ArrayList<>();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) locations.add(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public List<Location> getRecordedLocations() {
        return locations;
    }

    public void reset(){
        this.locations.clear();
    }
}
