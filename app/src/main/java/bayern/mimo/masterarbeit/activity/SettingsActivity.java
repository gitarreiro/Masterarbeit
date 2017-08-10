package bayern.mimo.masterarbeit.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;
import java.util.List;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.common.AppSensors;
import bayern.mimo.masterarbeit.util.Config;

/**
 * Created by MiMo
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final List<String> sensorMACs = new ArrayList<>();
        for (Shimmer shimmer : AppSensors.getShimmerSensors())
            sensorMACs.add(shimmer.getBluetoothAddress());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorMACs);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerSensorFront);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences prefs = getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Config.PREFS_KEY_FRONT_SENSOR, sensorMACs.get(position));
                editor.apply();

                // TODO beim Erzeugen des DRR schaun, dass der richtige Sensor an der Front ist

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        SharedPreferences prefs = getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE);

        String frontSensorMAC = prefs.getString(Config.PREFS_KEY_FRONT_SENSOR, null);

        /*if (frontSensorMAC != null && sensorMACs.contains(frontSensorMAC)) {
            int pos;
            for (pos = 0; pos < sensorMACs.size(); pos++) {
                if (sensorMACs.get(pos).equals(frontSensorMAC))
                    break;
                pos++;
            }
            spinner.setSelection(pos);
        }*/

        System.out.println("frontsensormac is " + frontSensorMAC);

        if (frontSensorMAC != null) {
            int spinnerPosition = adapter.getPosition(frontSensorMAC);
            spinner.setSelection(spinnerPosition);
        }


    }
}
