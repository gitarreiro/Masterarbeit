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
import java.util.List;
import java.util.Set;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.adapter.DeviceListAdapter;
import bayern.mimo.masterarbeit.common.AppSensors;
import bayern.mimo.masterarbeit.handler.ShimmerHandler;
import bayern.mimo.masterarbeit.wrapper.BluetoothDeviceWrapper;

/**
 * Created by MiMo on 11.03.2017.
 */

public class ConnectSensorsActivity extends AppCompatActivity {

    private Handler handler;

    //TODO evtl. auslagern
    private static final int REQUEST_BLUETOOTH = 1;
    private List<BluetoothDeviceWrapper> deviceList;
    private ArrayAdapter<BluetoothDeviceWrapper> deviceAdapter;

    //TODO umschrieben auf Maps, eindeutige Zuordnung möglich
    private List<Shimmer> pendingShimmerSensors;
    private List<ShimmerHandler> pendingShimmerHandlers;
    private List<Shimmer> shimmerSensors;
    private BluetoothAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_sensors);

        AppSensors.init();


        init();


    }


    private void init() {
        initVariables();
        initDeviceList();
    }

    private void initVariables() {
        this.pendingShimmerSensors = new ArrayList<>();
        this.pendingShimmerHandlers = new ArrayList<>();
        this.shimmerSensors = new ArrayList<>();
    }

    private void initDeviceList() {

        this.adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        ListView lv = (ListView) findViewById(R.id.listDevices);

        deviceList = new ArrayList<>();

        this.deviceAdapter = new DeviceListAdapter(this, deviceList, adapter);
        lv.setAdapter(this.deviceAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                BluetoothDevice device = deviceAdapter.getItem(i).getDevice();

                System.out.println("clicked " + device.getName());
                //new ConnectBluetoothDeviceThread().connect(deviceAdapter.getItem(i), UUID.randomUUID());

                System.out.println("device's MAC: " + device.getAddress());


                // try to connect Shimmmer sensorr
                Shimmer shimmer = null;
                ShimmerHandler handler = null;
                try {
                    handler = new ShimmerHandler(ConnectSensorsActivity.this);

                    shimmer = new Shimmer(ConnectSensorsActivity.this, handler, device.getAddress(), 51.2, 0, 0, Shimmer.SENSOR_ACCEL | Shimmer.SENSOR_GYRO | Shimmer.SENSOR_MAG, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String bluetoothAddress = device.getAddress();
                if (shimmer != null) {
                    shimmer.connect(bluetoothAddress, "default");
                    pendingShimmerSensors.add(shimmer);
                    pendingShimmerHandlers.add(handler);
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

        System.out.println("refreshng devices");

        refreshShimmer();

        System.out.println("connected Shimmer devices: " + shimmerSensors.size());

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
    }

    private void refreshShimmer(){
        List<Shimmer> disconnectedDevices = new ArrayList<>();

        for(Shimmer shimmer: shimmerSensors){
            //TODO grün wieder entfernen, wenn Sensor nicht mehr verbunden ist - wie auch immer wir das merken

            System.out.println("is initialized: " + shimmer.getInitialized());
            if(shimmer.getShimmerState() != Shimmer.STATE_CONNECTED)
                disconnectedDevices.add(shimmer);
        }

        System.out.println("removing "+disconnectedDevices.size()+" devices");

        shimmerSensors.removeAll(disconnectedDevices);
    }

    public void notifyShimmerConnected() {
        List<Shimmer> sensorToRemove = new ArrayList<>();
        for (int i = 0; i < pendingShimmerSensors.size(); i++) {

            Shimmer shimmer = pendingShimmerSensors.get(i);
            ShimmerHandler handler = pendingShimmerHandlers.get(i);
            if (shimmer.getInitialized()) {
                shimmerSensors.add(shimmer);
                sensorToRemove.add(shimmer);
                AppSensors.addSensor(shimmer, handler);
            }
        }

        pendingShimmerSensors.removeAll(sensorToRemove);

        refreshAndShowDevices();
    }

    private void stopHandler(){
        if(handler!=null)
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
