package bayern.mimo.masterarbeit.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bayern.mimo.masterarbeit.view.MADialog;

/**
 * Created by MiMo
 */

public class MALocationListener implements LocationListener {

    private List<Location> locations;

    public MALocationListener(){
        this.locations = new ArrayList<>();
    }

    @Override
    public void onLocationChanged(Location location) {
        //if(location != null) locations.add(location);

        //System.out.println("onLocationChanged()");

        if(location != null){
            locations.add(location);
          //  System.out.println("Location gefunden!");
        }else{
            //System.out.println("location is null");
        }

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
