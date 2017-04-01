package bayern.mimo.masterarbeit.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.listener.OnButtonConnectSensorsClickListener;
import bayern.mimo.masterarbeit.listener.OnButtonSettingsClickListener;
import bayern.mimo.masterarbeit.listener.OnButtonShowUploadDataClickListener;
import bayern.mimo.masterarbeit.listener.OnButtonStartRideClickListener;
import bayern.mimo.masterarbeit.util.Util;

public class MainActivity extends AppCompatActivity {

    /*
    private final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                System.out.println("starting scan");
                Toast.makeText(MainActivity.this, "Starting scan", Toast.LENGTH_LONG).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                System.out.println("finished scan");
                Toast.makeText(MainActivity.this, "Finished scan", Toast.LENGTH_LONG).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println("found device: " + device.getName());
                MainActivity.this.deviceAdapter.add(device);
                MainActivity.this.deviceAdapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, "Found device " + device.getName(), Toast.LENGTH_LONG).show();
            }
        }
    };
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        -----------------------------------------------TESTING AREA------------------------------------------------------------------

        */

        String datestring = "2017-03-23T13:36:51";

        datestring = datestring.split("\\.")[0];

        Date timestamp = new Date();


        try {
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            timestamp = format.parse(datestring);
        }
        catch(ParseException pe) {
            pe.printStackTrace();
        }


        System.out.println("Timestamp: "+timestamp);



        /*
        -----------------------------------------------------------------------------------------------------------------------------
         */






        String[] permissions = new String[5];
        permissions[0] = Manifest.permission.BLUETOOTH;
        permissions[1] = Manifest.permission.BLUETOOTH_ADMIN;
        permissions[2] = Manifest.permission.ACCESS_COARSE_LOCATION;
        permissions[3] = Manifest.permission.INTERNET;
        permissions[4] = Manifest.permission.ACCESS_NETWORK_STATE;
        Util.checkPermissions(this, permissions);

        initView();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //TODO mal schaun

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay! Do the
            // contacts-related task you need to do.

        } else {

            // permission denied, boo! Disable the
            // functionality that depends on this permission.

            //Util.showInfoDialog("Info", "You have to grant all permissions in order to be able to use this app!", this);

        }
    }

    private void initView() {
        Button buttonConnectSensors = (Button) findViewById(R.id.buttonConnectSensors);
        buttonConnectSensors.setOnClickListener(new OnButtonConnectSensorsClickListener(this));

        Button buttonStartDrive = (Button) findViewById(R.id.buttonStartDrive);
        buttonStartDrive.setOnClickListener(new OnButtonStartRideClickListener(this));

        Button buttonShowUploadData = (Button) findViewById(R.id.buttonShowAndUploadData);
        buttonShowUploadData.setOnClickListener(new OnButtonShowUploadDataClickListener(this));

        Button buttonSettings = (Button) findViewById(R.id.buttonRecordingSettings);
        buttonSettings.setOnClickListener(new OnButtonSettingsClickListener(this));
    }


}
