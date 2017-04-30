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

import com.shimmerresearch.android.Shimmer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.SendToServerTask;
import bayern.mimo.masterarbeit.adapter.RecordingAdapter;
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


        System.out.println("ShowAndUploadActivity(): "+ DataHelper.getDataRecordings(this).size()+" DataRecordings found");

        this.recordingAdapter = new RecordingAdapter(this, DataHelper.getDataRecordings(this));

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
                recordingAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 2000);
                //System.out.println("updated Recording list");
            }
        };


        handler.post(updateListRunnable);



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

                JSONObject jsonRequest = createRequest(record);
                String url = Config.SERVER_API_URL + Config.DATA_RECORDING_REQUEST_PATH;


                this.task = new SendToServerTask(record);
                task.execute(url, jsonRequest.toString());


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
        return request;
    }
}
