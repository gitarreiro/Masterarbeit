package bayern.mimo.masterarbeit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.shimmerresearch.android.Shimmer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.SendToServerTask;
import bayern.mimo.masterarbeit.adapter.RecordingAdapter;
import bayern.mimo.masterarbeit.common.AppSensors;
import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.DataRecording;
import bayern.mimo.masterarbeit.util.Config;

/**
 * Created by MiMo
 */

public class ShowAndUploadDataActivity extends AppCompatActivity {

    private ArrayAdapter<DataRecording> recordingAdapter;
    private SendToServerTask task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_upload_data);


        this.recordingAdapter = new RecordingAdapter(this, DataHelper.getDataRecordings());

        ListView listViewData = (ListView) findViewById(R.id.listViewData);
        listViewData.setAdapter(this.recordingAdapter);
        /*listViewData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO show dialog to upload data
                return false;
            }
        });*/

        registerForContextMenu(listViewData);


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewData) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            DataRecording record = this.recordingAdapter.getItem(info.position);
            menu.setHeaderTitle(record.getStartDate());

//TODO custom header?

            String[] menuItems = {"Upload data"};
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
                DataRecording record = this.recordingAdapter.getItem(info.position);
                //TODO do upload only if data hasn't been uploaded yet


                //TODO request schicken

                JSONObject jsonRequest = createRequest();
                String url = Config.SERVER_API_URL + Config.DATA_RECORDING_REQUEST_PATH;

                this.task = new SendToServerTask();
                task.execute(url, jsonRequest.toString());


                //TODO Gesamt-JSON pro Sensor schicken






                break;
            default:
                //TODO DAS sollte nicht passieren
        }


        //TODO maybe replace
        return super.onContextItemSelected(item);
    }

    private JSONObject createRequest() {
        List<Shimmer> shimmerSensors = AppSensors.getShimmerSensors();

        JSONObject request = new JSONObject();
        try {
            request.put("Username", "testuser");
            if (shimmerSensors.size() > 0)
                request.put("Shimmer1MAC", shimmerSensors.get(0).getBluetoothAddress());

            if (shimmerSensors.size() > 1)
                request.put("Shimmer2MAC", shimmerSensors.get(1).getBluetoothAddress());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return request;
    }
}
