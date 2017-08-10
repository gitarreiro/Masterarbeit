package bayern.mimo.masterarbeit.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.shimmerresearch.android.Shimmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.adapter.DeviceListAdapter;
import bayern.mimo.masterarbeit.common.AppSensors;
import bayern.mimo.masterarbeit.handler.ShimmerHandler;
import bayern.mimo.masterarbeit.util.Constants;
import bayern.mimo.masterarbeit.wrapper.BluetoothDeviceWrapper;

/**
 * Created by MiMo
 */

public class ConnectSensorsActivity extends AppCompatActivity {

    private Handler handler;


    private List<BluetoothDeviceWrapper> deviceList;
    private ArrayAdapter<BluetoothDeviceWrapper> deviceAdapter;

    //TODO umschrieben auf Maps, eindeutige Zuordnung möglich
    private Map<Shimmer, ShimmerHandler> pendingShimmerSensors;
    //private Map<String, ShimmerHandler> pendingShimmerHandlers;
    private List<Shimmer> shimmerSensors;
    private BluetoothAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_sensors);

        AppSensors.init(this);


        init();


    }


    private void init() {
        initVariables();
        initDeviceList();
    }

    private void initVariables() {
        this.pendingShimmerSensors = new HashMap<>();
        //this.pendingShimmerHandlers = new HashMap<>();
        this.shimmerSensors = new ArrayList<>();
    }

    private void initDeviceList() {

        this.adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, Constants.REQUEST_BLUETOOTH);
        }

        ListView lv = (ListView) findViewById(R.id.listDevices);

        deviceList = new ArrayList<>();

        this.deviceAdapter = new DeviceListAdapter(this, deviceList, adapter);
        lv.setAdapter(this.deviceAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                BluetoothDevice device = deviceAdapter.getItem(i).getDevice();

                //System.out.println("clicked " + device.getName());
                //new ConnectBluetoothDeviceThread().connect(deviceAdapter.getItem(i), UUID.randomUUID());

                //System.out.println("device's MAC: " + device.getAddress());


                // try to connect Shimmmer sensorr
                Shimmer shimmer = null;
                ShimmerHandler handler = null;
                try {
                    handler = new ShimmerHandler(ConnectSensorsActivity.this, device.getAddress());

                    //TODO hier accel range ändern
                    shimmer = new Shimmer(ConnectSensorsActivity.this, handler, device.getAddress(), 100, 1000000, 0, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_DACCEL | Shimmer.SENSOR_GYRO | Shimmer.SENSOR_MAG, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String bluetoothAddress = device.getAddress();


                List<String> addresses = new LinkedList<>();

                for (Shimmer s : pendingShimmerSensors.keySet())
                    addresses.add(s.getBluetoothAddress());


                if (shimmer != null && !addresses.contains(bluetoothAddress)) {


                    shimmer.connect(bluetoothAddress, "default");
                    pendingShimmerSensors.put(shimmer, handler);
                    //pendingShimmerHandlers.put(shimmer.getBluetoothAddress(), handler);

                    //System.out.println("pending shimmers: " + pendingShimmerSensors.size());
                    //System.out.println("pending handlers: " + pendingShimmerHandlers.size());

                    /*if(shimmer.getInitialized())
                        System.out.println("is initialized!");
                    else
                        System.out.println("is not initialized");
                    AppSensors.addSensor(shimmer);
                    Toast.makeText(ConnectSensorsActivity.this, "Shimmer connected successfully!", Toast.LENGTH_SHORT).show();
                */
                }


            }
        });

        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                refreshAndShowDevices();
                handler.postDelayed(this, 2000);
            }
        };
        r.run();
    }


    private void refreshAndShowDevices() {

        //System.out.println("refreshng devices");

        //System.out.println("pending Shimmer before: ");

        //for(Shimmer shimmer : pendingShimmerSensors)
        //    System.out.println("SHIMMER "+shimmer.getBluetoothAddress());

        //for(String key : pendingShimmerHandlers.keySet())
        //    System.out.println("HANDLER for " + key);


        refreshShimmer();

        //System.out.println("connected Shimmer devices: " + shimmerSensors.size());

        deviceAdapter.clear();


        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {

            String btAddress = device.getAddress();
            boolean connected = false;

            for (Shimmer shimmer : shimmerSensors)
                if (shimmer.getBluetoothAddress().equals(btAddress))
                    connected = true;


            deviceAdapter.add(new BluetoothDeviceWrapper(device, connected));
        }

        deviceAdapter.notifyDataSetChanged();

        //System.out.println("pending Shimmer after: ");

        //for(Shimmer shimmer : pendingShimmerSensors)
        //    System.out.println("SHIMMER "+shimmer.getBluetoothAddress());

        //for(String key : pendingShimmerHandlers.keySet())
        //    System.out.println("HANDLER for " + key);


        System.out.println("pending Shimmer: ");

        for (Shimmer shimmer2 : pendingShimmerSensors.keySet())
            System.out.println("SHIMMER " + shimmer2.getBluetoothAddress());

        for (Shimmer s : pendingShimmerSensors.keySet())
            System.out.println("HANDLER for " + s.getBluetoothAddress());

    }

    private void refreshShimmer() {
        List<Shimmer> disconnectedDevices = new ArrayList<>();

        System.out.println("refreshShimmer: " + shimmerSensors.size() + " sensors fount");

        for (Shimmer shimmer : shimmerSensors) {
            //TODO grün wieder entfernen, wenn Sensor nicht mehr verbunden ist - wie auch immer wir das merken

            if(shimmer==null){
                System.out.println("hier");
                continue;
            }

            System.out.println(shimmer);

            //System.out.println("is initialized: " + shimmer.getInitialized());
            if (shimmer.getShimmerState() != Shimmer.STATE_CONNECTED)
                disconnectedDevices.add(shimmer);
        }

        //System.out.println("removing " + disconnectedDevices.size() + " devices");

        System.out.println("disconnecting " + disconnectedDevices.size() + " shimmer device(s)");

        shimmerSensors.removeAll(disconnectedDevices);
    }

    public void notifyShimmerConnected(String bluetoothAddress) {
        List<Shimmer> sensorToRemove = new ArrayList<>();

        /*
        for (int i = 0; i < pendingShimmerSensors.size(); i++) {

            Shimmer shimmer = pendingShimmerSensors.get(i);
            ShimmerHandler handler = pendingShimmerHandlers.get(shimmer.getBluetoothAddress());
            if (shimmer.getInitialized()) {
                shimmerSensors.add(shimmer);
                sensorToRemove.add(shimmer);
                AppSensors.addSensor(shimmer, handler);
            }
        }
        */
/*
        for (Shimmer shimmer : pendingShimmerSensors.keySet()) {
            ShimmerHandler handler = pendingShimmerSensors.get(shimmer);
            if (shimmer.getInitialized()) {
                shimmerSensors.add(shimmer);
                sensorToRemove.add(shimmer);
                AppSensors.addSensor(shimmer, handler);
            }
        }
*/

        Shimmer shimmer = null;

        System.out.println();//TODO test here: wann wird NULL reingeschrieben?

        for (Shimmer s : pendingShimmerSensors.keySet()) {
            System.out.println("comparing " + s.getBluetoothAddress() + " with " + bluetoothAddress );
            if (s.getBluetoothAddress().equals(bluetoothAddress)) {
                shimmer = s;
                break;
            }
        }

        System.out.println("found Shimmer? " + (shimmer == null ? "null" : shimmer.getBluetoothAddress()));


        ShimmerHandler handler = pendingShimmerSensors.get(shimmer);

/*
        for (Shimmer shimmer : sensorToRemove) {
            pendingShimmerSensors.remove(shimmer);
        }
*/

        System.out.println("adding " + shimmer);
        shimmerSensors.add(shimmer);
        AppSensors.addSensor(shimmer, handler);



        pendingShimmerSensors.remove(shimmer);

        /*
        pendingShimmerSensors.removeAll(sensorToRemove);
        for(Shimmer shimmer : sensorToRemove){
            pendingShimmerHandlers.remove(shimmer.getBluetoothAddress());
        }
        */

        System.out.println("pending Shimmer: ");

        for (Shimmer shimmer2 : pendingShimmerSensors.keySet())
            System.out.println("SHIMMER " + shimmer2.getBluetoothAddress());

        for (Shimmer s : pendingShimmerSensors.keySet())
            System.out.println("HANDLER for " + s.getBluetoothAddress());


        refreshAndShowDevices();
    }

    public void notifyShimmerConnectionFailed(String bluetoothAddress) {
        Shimmer toRemove = null;
        for (Shimmer s : pendingShimmerSensors.keySet()) {
            if(s.getBluetoothAddress().equals(bluetoothAddress)){
                toRemove = s;
                break;
            }
        }

        pendingShimmerSensors.remove(toRemove);

        //refreshAndShowDevices(); //TODO wahrscheinlich unnötig
    }


    private void stopHandler() {
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopHandler();
    }

}
