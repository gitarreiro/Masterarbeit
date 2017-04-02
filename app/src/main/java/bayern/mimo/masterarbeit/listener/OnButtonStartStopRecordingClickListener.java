package bayern.mimo.masterarbeit.listener;

import android.view.View;
import android.widget.ToggleButton;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.activity.StartRideActivity;
import bayern.mimo.masterarbeit.common.AppSensors;
import bayern.mimo.masterarbeit.view.MADialog;

/**
 * Created by MiMo
 */
public class OnButtonStartStopRecordingClickListener implements View.OnClickListener {

    private StartRideActivity caller;
    private boolean isRecording = false;

    public OnButtonStartStopRecordingClickListener(StartRideActivity caller) {
        this.caller = caller;
    }

    @Override
    public void onClick(View v) {
        if (isRecording) {
            AppSensors.stopRecording();
            isRecording = false;

        } else {


            if (!AppSensors.isReadyForRecording()) {
                ((ToggleButton) caller.findViewById(R.id.buttonStartRecording)).setChecked(false);
                //TODO Meldung anzeigen
                //
            } else {


                View.OnClickListener positive = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v == null){
                            System.out.println("v is null");
                            return;
                        }

                        //System.out.println("Tag: " + v.getTag());
                        //startRecording(v.getTag().toString());
                    }
                };

                new MADialog(caller, positive, null).show();


            }
        }
    }

    private void startRecording(String info) {
        AppSensors.startRecording(info);

        isRecording = true;
    }
}
