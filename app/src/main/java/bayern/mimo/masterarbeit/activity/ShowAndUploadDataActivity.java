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

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.adapter.RecordingAdapter;
import bayern.mimo.masterarbeit.data.DataHelper;
import bayern.mimo.masterarbeit.data.DataRecording;

/**
 * Created by MiMo
 */

public class ShowAndUploadDataActivity extends AppCompatActivity {

    private ArrayAdapter<DataRecording> recordingAdapter;

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

        System.out.println("ShowAndUploadDataActivity.onCreate(); Records: " + DataHelper.getDataRecordings().size());

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
                //TODO do upload if data hasn't been uploaded yet
                break;
            default:
                //TODO DAS sollte nicht passieren
        }


        //TODO maybe replace
        return super.onContextItemSelected(item);
    }
}
