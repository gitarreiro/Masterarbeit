package bayern.mimo.masterarbeit.listener;

import android.view.View;
import android.widget.ToggleButton;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.activity.StartRideActivity;
import bayern.mimo.masterarbeit.common.AppSensors;

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










            /*
            List<List<ShimmerValue>> valuesList = AppSensors.getShimmerValues();
            for(List<ShimmerValue> values : valuesList){
                for(ShimmerValue value : values){
                    System.out.println("realtime at time " + value.getTimestamp()+" is "+ value.getRealTimeClock());
                }
            }
*/


        } else {
            if (!AppSensors.startRecording()) {
                ((ToggleButton) caller.findViewById(R.id.buttonStartRecording)).setChecked(false);
                //TODO Meldung anzeigen
                //
            } else {
                isRecording = true;

            }
        }
    }
}
