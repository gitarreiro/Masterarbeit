package bayern.mimo.masterarbeit.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.listener.OnButtonBackupDataClickListener;
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


                ShimmerValue testValueShimmer = new ShimmerValue(1.0d,
                        2.0d, 3.0d, 4.0d, 5.0d, 6.0d, 7.0d, 8.0d,
                        9.0d, 10.0d, 11.0d, 12.0d, 13.0d, 14.0d,
                        15.0d, 16.0d, 17.0d, 18.0d, 1076,
                        "just a MAC");

                ShimmerValue testValueShimmer2 = new ShimmerValue(111.0d,
                        112.0d, 113.0d, 114.0d, 115.0d, 116.0d, 117.0d, 118.0d,
                        119.0d, 1110.0d, 1111.0d, 1112.0d, 1113.0d, 1114.0d,
                        1115.0d, 1116.0d, 1117.0d, 1118.0d, 1076,
                        "just another MAC");

                Location testValueLocation = new Location("dummy");
                testValueLocation.setLatitude(1);
                testValueLocation.setLongitude(2);
                testValueLocation.setTime(3);
                testValueLocation.setAccuracy(4);
                testValueLocation.setAltitude(5);
                testValueLocation.setElapsedRealtimeNanos(6);
                testValueLocation.setSpeed(7);

                Location testValueLocation2 = new Location("dummy");
                testValueLocation2.setLatitude(111);
                testValueLocation2.setLongitude(112);
                testValueLocation2.setTime(113);
                testValueLocation2.setAccuracy(114);
                testValueLocation2.setAltitude(115);
                testValueLocation2.setElapsedRealtimeNanos(116);
                testValueLocation2.setSpeed(117);

                //List<Byte> byteList = new ArrayList<>();


                byte[] locationBytes = new byte[4 + 2 * 56];


                byte[] sensorMacAsBytes = testValueShimmer.getShimmerMac().getBytes();

                byte[] shimmerBytes = new byte[8 + sensorMacAsBytes.length + 2 * 144];
                ByteBuffer shimmerBuffer = ByteBuffer.wrap(shimmerBytes).order(ByteOrder.LITTLE_ENDIAN);


                shimmerBuffer.putInt(sensorMacAsBytes.length);
                shimmerBuffer.put(sensorMacAsBytes);

                shimmerBuffer.putInt(testValueShimmer.getDrrId());

                //TODO hier schleife

                shimmerBuffer.putDouble(testValueShimmer.getAccelLnX());
                shimmerBuffer.putDouble(testValueShimmer.getAccelLnY());
                shimmerBuffer.putDouble(testValueShimmer.getAccelLnZ());
                shimmerBuffer.putDouble(testValueShimmer.getAccelWrX());
                shimmerBuffer.putDouble(testValueShimmer.getAccelWrY());
                shimmerBuffer.putDouble(testValueShimmer.getAccelWrZ());
                shimmerBuffer.putDouble(testValueShimmer.getGyroX());
                shimmerBuffer.putDouble(testValueShimmer.getGyroY());
                shimmerBuffer.putDouble(testValueShimmer.getGyroZ());
                shimmerBuffer.putDouble(testValueShimmer.getMagX());
                shimmerBuffer.putDouble(testValueShimmer.getMagY());
                shimmerBuffer.putDouble(testValueShimmer.getMagZ());
                shimmerBuffer.putDouble(testValueShimmer.getTemperature());
                shimmerBuffer.putDouble(testValueShimmer.getPressure());
                shimmerBuffer.putDouble(testValueShimmer.getTimestamp());
                shimmerBuffer.putDouble(testValueShimmer.getRealTimeClock());
                shimmerBuffer.putDouble(testValueShimmer.getTimestampSync());
                shimmerBuffer.putDouble(testValueShimmer.getRealTimeClockSync());

                shimmerBuffer.putDouble(testValueShimmer2.getAccelLnX());
                shimmerBuffer.putDouble(testValueShimmer2.getAccelLnY());
                shimmerBuffer.putDouble(testValueShimmer2.getAccelLnZ());
                shimmerBuffer.putDouble(testValueShimmer2.getAccelWrX());
                shimmerBuffer.putDouble(testValueShimmer2.getAccelWrY());
                shimmerBuffer.putDouble(testValueShimmer2.getAccelWrZ());
                shimmerBuffer.putDouble(testValueShimmer2.getGyroX());
                shimmerBuffer.putDouble(testValueShimmer2.getGyroY());
                shimmerBuffer.putDouble(testValueShimmer2.getGyroZ());
                shimmerBuffer.putDouble(testValueShimmer2.getMagX());
                shimmerBuffer.putDouble(testValueShimmer2.getMagY());
                shimmerBuffer.putDouble(testValueShimmer2.getMagZ());
                shimmerBuffer.putDouble(testValueShimmer2.getTemperature());
                shimmerBuffer.putDouble(testValueShimmer2.getPressure());
                shimmerBuffer.putDouble(testValueShimmer2.getTimestamp());
                shimmerBuffer.putDouble(testValueShimmer2.getRealTimeClock());
                shimmerBuffer.putDouble(testValueShimmer2.getTimestampSync());
                shimmerBuffer.putDouble(testValueShimmer2.getRealTimeClockSync());

                shimmerBytes = shimmerBuffer.array();


                ByteBuffer locationBuffer = ByteBuffer.wrap(locationBytes).order(ByteOrder.LITTLE_ENDIAN);

                locationBuffer.putInt(testValueShimmer.getDrrId());

                //TODO hier schleife

                locationBuffer.putDouble(testValueLocation.getLatitude());
                locationBuffer.putDouble(testValueLocation.getLongitude());
                locationBuffer.putLong(testValueLocation.getTime());
                locationBuffer.putDouble(testValueLocation.getAccuracy());
                locationBuffer.putDouble(testValueLocation.getAltitude());
                locationBuffer.putLong(testValueLocation.getElapsedRealtimeNanos());
                locationBuffer.putDouble(testValueLocation.getSpeed());

                locationBuffer.putDouble(testValueLocation2.getLatitude());
                locationBuffer.putDouble(testValueLocation2.getLongitude());
                locationBuffer.putLong(testValueLocation2.getTime());
                locationBuffer.putDouble(testValueLocation2.getAccuracy());
                locationBuffer.putDouble(testValueLocation2.getAltitude());
                locationBuffer.putLong(testValueLocation2.getElapsedRealtimeNanos());
                locationBuffer.putDouble(testValueLocation2.getSpeed());

                locationBytes = locationBuffer.array();

                String path = getCacheDir() + "/ma/tmp/";
                //path = Environment.getExternalStorageDirectory() + "/ma/tmp/";
                File pathFile = new File(path);



                System.out.println("cache dir path: " + path);

                if (!pathFile.exists())
                    pathFile.mkdirs();


                final File theShimmerFile = new File(pathFile.getAbsolutePath() + "shimmer_" + System.currentTimeMillis());
                if (theShimmerFile.exists()) {
                    theShimmerFile.delete();
                    System.out.println("shimmer file got deleted");
                }

                final File theLocationFile = new File(pathFile.getAbsolutePath() + "location_" + System.currentTimeMillis());
                if (theLocationFile.exists()) {
                    theLocationFile.delete();
                    System.out.println("location file got deleted");
                }


                try {
                    theShimmerFile.createNewFile();
                    BufferedOutputStream bosShimmer = new BufferedOutputStream(new FileOutputStream(theShimmerFile, false));
                    bosShimmer.write(shimmerBytes);
                    //bosShimmer.write
                    bosShimmer.flush();
                    bosShimmer.close();


                    /*
                    Uri U = Uri.fromFile(theShimmerFile);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("image/png");
                    i.putExtra(Intent.EXTRA_STREAM, U);
                    startActivityForResult(Intent.createChooser(i, "Email:"),0);
*/

                    theLocationFile.createNewFile();
                    BufferedOutputStream bosLocation = new BufferedOutputStream(new FileOutputStream(theLocationFile, false));
                    bosLocation.write(locationBytes);
                    bosLocation.flush();
                    bosLocation.close();


                    /*


                    sending files via email, maybe useful for later










                    Uri U2 = Uri.fromFile(theLocationFile);
                    Intent i2 = new Intent(Intent.ACTION_SEND);
                    i2.setType("image/png");
                    i2.putExtra(Intent.EXTRA_STREAM, U2);
                    startActivityForResult(Intent.createChooser(i2, "Email:"),1);
*/

/*
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                            new String[]{"gitarreiro@gmail.com"});
                    //emailIntent.putExtra(android.content.Intent.EXTRA_CC, new String[]{emailCC});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Binärdateien");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "In dieser E-ail stehen hoffentlich die Binärdateien");
                    //has to be an ArrayList
                    ArrayList<Uri> uris = new ArrayList<Uri>();
                    //convert from paths to Android friendly Parcelable Uri's

                    Uri u = Uri.fromFile(theShimmerFile);
                    uris.add(u);

                    Uri u2 = Uri.fromFile(theLocationFile);
                    uris.add(u2);

                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));


                    if (true)
                        return;
*/

                    try {
                        String uploadId =
                                new MultipartUploadRequest(MainActivity.this, "http://37.221.199.137:8085/api/upload/Test")
                                        // starting from 3.1+, you can also use content:// URI string instead of absolute file
                                        .addFileToUpload(theShimmerFile.getAbsolutePath(), "shimmer")
                                        //.addFileToUpload(theLocationFile.getAbsolutePath(), "location")
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
                                                exception.printStackTrace();
                                            }

                                            @Override
                                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                                // your code here
                                                // if you have mapped your server response to a POJO, you can easily get it:
                                                // YourClass obj = new Gson().fromJson(serverResponse.getBodyAsString(), YourClass.class);
                                                theShimmerFile.delete();
                                                theLocationFile.delete();
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


        String[] permissions = new String[7];
        permissions[0] = Manifest.permission.BLUETOOTH;
        permissions[1] = Manifest.permission.BLUETOOTH_ADMIN;
        permissions[2] = Manifest.permission.ACCESS_COARSE_LOCATION;
        permissions[3] = Manifest.permission.INTERNET;
        permissions[4] = Manifest.permission.ACCESS_NETWORK_STATE;
        permissions[5] = Manifest.permission.ACCESS_FINE_LOCATION;
        permissions[6] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
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

        Button buttonBackupData = (Button) findViewById(R.id.buttonBackupRestore);
        buttonBackupData.setOnClickListener(new OnButtonBackupDataClickListener(this));

    }


}
