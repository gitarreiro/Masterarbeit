package bayern.mimo.masterarbeit.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.gotev.uploadservice.BuildConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.SendToServerTask;
import bayern.mimo.masterarbeit.adapter.RecordingAdapter;
import bayern.mimo.masterarbeit.common.ClassificationHelper;
import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.DataRecording;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import bayern.mimo.masterarbeit.exception.MAExceptionHandler;
import bayern.mimo.masterarbeit.util.Config;
import bayern.mimo.masterarbeit.util.Util;

/**
 * Created by MiMo
 */

public class ShowAndUploadDataActivity extends AppCompatActivity {

    private ArrayAdapter<DataRecording> recordingAdapter;
    private SendToServerTask task;
    private List<DataRecording> recordings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_upload_data);

        if(getIntent().hasExtra("stacktrace")){
            String exception = getIntent().getStringExtra("stacktrace");
            System.out.println("STACKTRACE : "+exception);
        }

        Thread.setDefaultUncaughtExceptionHandler(new MAExceptionHandler(this,
                ShowAndUploadDataActivity.class));

        //UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.HTTP_STACK = new OkHttpStack();

        System.out.println("ShowAndUploadActivity(): " + DataHelper.getDataRecordings(this).size() + " DataRecordings found");
/*
        List<DataRecording> records = DataHelper.getDataRecordings(this);
        List<ShimmerValue> shimmerValues = records.get(0).getShimmerValues().get(((String)(records.get(0).getShimmerValues().keySet().toArray()[0])));
        System.out.println("Shimmer values: "+shimmerValues.size());
        List<Location> locations = records.get(0).getLocations();
        System.out.println("Locations: "+locations.size());
        DataRecordingRequest drr = records.get(0).getDrr();
        System.out.println("DRR is " + drr.toString());
*/

        this.recordings = DataHelper.getDataRecordings(this);
        this.recordingAdapter = new RecordingAdapter(this, recordings);

        ListView listViewData = (ListView) findViewById(R.id.listViewData);
        listViewData.setAdapter(this.recordingAdapter);
        /*listViewData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO show dialog to upload data . vllt. auch nicht
                return false;
            }
        });*/

        registerForContextMenu(listViewData);

        final Handler handler = new Handler();

        Runnable updateListRunnable = new Runnable() {
            @Override
            public void run() {
                DataHelper.getDataRecordings(ShowAndUploadDataActivity.this, recordings);
                recordingAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 2000);
                //System.out.println("updated Recording list");
            }
        };


        //handler.post(updateListRunnable);


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewData) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            DataRecording record = this.recordingAdapter.getItem(info.position);
            menu.setHeaderTitle(record.getStartDate());

//TODO custom header?

            String[] menuItems = {"Upload data", "Classify"};
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }


        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getTitle().toString()) {
            case "Upload data":
                DataRecording recordToUpload = this.recordingAdapter.getItem(info.position);
                if(recordToUpload.isUploaded()){
                    //TODO richtigen Dialog anzeigen
                    Toast.makeText(ShowAndUploadDataActivity.this, "Already uploaded!", Toast.LENGTH_LONG).show();
                    return super.onContextItemSelected(item);
                }
                //TODO do upload only if data hasn't been uploaded yet

                System.out.println("Sensoren: " + recordToUpload.getShimmerValues().keySet().size());
                for(String mac : recordToUpload.getShimmerValues().keySet()){
                    List<ShimmerValue> values = recordToUpload.getShimmerValues().get(mac);
                    System.out.println("Anzahl Werte für " + mac + ": " + values.size());
                }


                JSONObject jsonRequest = createRequest(recordToUpload);
                String url = Config.SERVER_API_URL + Config.DATA_RECORDING_REQUEST_PATH;

                this.task = new SendToServerTask(recordToUpload, this);
                try{
                    task.execute(url, jsonRequest.toString());
                }catch(Exception e){
                    //TODO http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a/2033124#2033124

                    Util.sendSMS(this, "+4915112488224", e.getStackTrace().toString()); //TODO unbedingt entfernen
                }

                break;

            case "Classify":

                DataRecording recordToClassify = this.recordingAdapter.getItem(info.position);
                classifyRecord(recordToClassify);

                break;

            default:
                //TODO DAS sollte nicht passieren
        }


        //TODO maybe replace
        return super.onContextItemSelected(item);
    }

    private JSONObject createRequest(DataRecording record) {


        List<String> shimmerSensors = new ArrayList<>(record.getShimmerValues().keySet());

        JSONObject request = new JSONObject();
        try {
            request.put("Username", "testuser");
            if (shimmerSensors.size() > 0)
                request.put("Shimmer1MAC", shimmerSensors.get(0));

            if (shimmerSensors.size() > 1)
                request.put("Shimmer2MAC", shimmerSensors.get(1));

            request.put("Category", record.getCategory());
            request.put("Detail", record.getDetail());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //TODO testing only
        try {
            System.out.println("request is \n" + request.toString(2));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return request;
    }

    private void classifyRecord(DataRecording record){

        String classes = ClassificationHelper.classify(record);
        Toast.makeText(this,classes,Toast.LENGTH_LONG).show();

    }




}
