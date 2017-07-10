package bayern.mimo.masterarbeit.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import net.gotev.uploadservice.BuildConfig;
import net.gotev.uploadservice.Logger;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.listener.OnButtonConnectSensorsClickListener;
import bayern.mimo.masterarbeit.listener.OnButtonSettingsClickListener;
import bayern.mimo.masterarbeit.listener.OnButtonShowUploadDataClickListener;
import bayern.mimo.masterarbeit.listener.OnButtonStartRideClickListener;
import bayern.mimo.masterarbeit.util.Util;

public class MainActivity extends AppCompatActivity {
//TODO long time todo: Auto-Connect?
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


        Button buttonRumpfuschen = (Button) findViewById(R.id.buttonRumpfuschen);
        buttonRumpfuschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO HIER WIRD RUMGEPFUSCHT - mit Vorsicht genießen!
                DataHelper.dropAll(MainActivity.this);
            }
        });

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.HTTP_STACK = new OkHttpStack();

        Logger.setLogLevel(Logger.LogLevel.DEBUG);


        Button buttonTestUploadService = (Button) findViewById(R.id.buttonTestUploadService);
        buttonTestUploadService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ShimmerValue testValue = new ShimmerValue(1.0d,
                        2.0d, 3.0d, 4.0d, 5.0d, 6.0d, 7.0d, 8.0d,
                        9.0d, 10.0d, 11.0d, 12.0d, 13.0d, 14.0d,
                        15.0d, 16.0d, 17.0d, 18.0d, 100,
                        "just another MAC");


                //List<Byte> byteList = new ArrayList<>();




                byte[] bytes = new byte[8];


                ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

                byte[] sensorMacAsBytes = testValue.getShimmerMac().getBytes();


                buffer.putInt(sensorMacAsBytes.length);

                buffer.put(sensorMacAsBytes);



                buffer.putDouble(testValue.getAccelLnX());
                buffer.putDouble(testValue.getAccelLnY());
                buffer.putDouble(testValue.getAccelLnZ());
                buffer.putDouble(testValue.getAccelWrX());
                buffer.putDouble(testValue.getAccelWrY());
                buffer.putDouble(testValue.getAccelWrZ());
                buffer.putDouble(testValue.getGyroX());
                buffer.putDouble(testValue.getGyroY());
                buffer.putDouble(testValue.getGyroZ());
                buffer.putDouble(testValue.getMagX());
                buffer.putDouble(testValue.getMagY());
                buffer.putDouble(testValue.getMagZ());
                buffer.putDouble(testValue.getTemperature());
                buffer.putDouble(testValue.getPressure());
                buffer.putDouble(testValue.getTimestamp());
                buffer.putDouble(testValue.getRealTimeClock());
                buffer.putDouble(testValue.getTimestampSync());
                buffer.putDouble(testValue.getRealTimeClockSync());
                buffer.putDouble(testValue.getDrrId());
                ;

                bytes = buffer.array();
                //bytes = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putDouble(3.1415).array();

                //bytes = Util.reverse(bytes);


                System.out.println("converted is ");

                for (Byte b : bytes)
                    System.out.println("\t" + String.format("%02x", b));


                String path = getCacheDir() + "/ma/tmp/";
                File pathFile = new File(path);


                System.out.println("cache dir path: " + path);

                if (!pathFile.exists())
                    pathFile.mkdirs();


                final File theFile = new File(pathFile + "shimmer");
                if (theFile.exists()) {
                    theFile.delete();
                    System.out.println("file got deleted");
                }


                try {
                    theFile.createNewFile();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(theFile, false));
                    bos.write(bytes);
                    //bos.write
                    bos.flush();
                    bos.close();

                    try {
                        String uploadId =
                                new MultipartUploadRequest(MainActivity.this, "http://37.221.199.137:8085/api/upload/Test")
                                        // starting from 3.1+, you can also use content:// URI string instead of absolute file
                                        .addFileToUpload(theFile.getAbsolutePath(), "shimmer")
                                        .setNotificationConfig(new UploadNotificationConfig())
                                        .setMaxRetries(2)
                                        .setDelegate(new UploadStatusDelegate() {
                                            @Override
                                            public void onProgress(Context context, UploadInfo uploadInfo) {
                                                // your code here
                                            }

                                            @Override
                                            public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                                                // your code here
                                            }

                                            @Override
                                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                                // your code here
                                                // if you have mapped your server response to a POJO, you can easily get it:
                                                // YourClass obj = new Gson().fromJson(serverResponse.getBodyAsString(), YourClass.class);
                                                theFile.delete();
                                                System.out.println("finished upload - file should be deleted");
                                            }

                                            @Override
                                            public void onCancelled(Context context, UploadInfo uploadInfo) {
                                                // your code here
                                            }
                                        })
                                        .startUpload();
                    } catch (Exception e) {
                        Log.e("AndroidUploadService", e.getMessage(), e);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });






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
        } catch (ParseException pe) {
            pe.printStackTrace();
        }


        System.out.println("Timestamp: " + timestamp);



        /*
        -----------------------------------------------------------------------------------------------------------------------------
         */


        String[] permissions = new String[6];
        permissions[0] = Manifest.permission.BLUETOOTH;
        permissions[1] = Manifest.permission.BLUETOOTH_ADMIN;
        permissions[2] = Manifest.permission.ACCESS_COARSE_LOCATION;
        permissions[3] = Manifest.permission.INTERNET;
        permissions[4] = Manifest.permission.ACCESS_NETWORK_STATE;
        permissions[5] = Manifest.permission.ACCESS_FINE_LOCATION;
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
