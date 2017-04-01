package bayern.mimo.masterarbeit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.adapter.RecordingAdapter;
import bayern.mimo.masterarbeit.data.DataRecording;

/**
 * Created by MiMo on 31.03.2017.
 */

public class ShowAndUploadDataActivity extends AppCompatActivity {

    private List<DataRecording> recordings;
    private ArrayAdapter<DataRecording> recordingAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_upload_data);



        this.recordingAdapter = new RecordingAdapter(this, DataHelper.getDataRecordings());

        ListView listViewData = (ListView) findViewById(R.id.listViewData);
    }
}
