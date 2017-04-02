package bayern.mimo.masterarbeit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ToggleButton;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.listener.OnButtonSendDataToServerClickListener;
import bayern.mimo.masterarbeit.listener.OnButtonStartStopRecordingClickListener;

/**
 * Created by MiMo
 */

public class StartRideActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ride);

        init();
    }

    private void init() {
        ToggleButton buttonStartRecording = (ToggleButton) findViewById(R.id.buttonStartRecording);
        buttonStartRecording.setOnClickListener(new OnButtonStartStopRecordingClickListener(this));



        //TODO replace (wandert in eigene Activity)
        Button buttonSendDataToServer = (Button) findViewById(R.id.buttonSendDataToServer);
        buttonSendDataToServer.setOnClickListener(new OnButtonSendDataToServerClickListener(this));
    }


}
