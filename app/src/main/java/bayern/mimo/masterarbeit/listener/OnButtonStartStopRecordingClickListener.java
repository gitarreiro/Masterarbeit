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
    private MADialog dialog;


    public OnButtonStartStopRecordingClickListener(StartRideActivity caller) {
        this.caller = caller;
    }

    @Override
    public void onClick(View v) {
        if (isRecording) {
            AppSensors.stopRecording();
            isRecording = false;

            View.OnClickListener positive = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == null) {
                        System.out.println("v is null");
                        return;
                    }


                    System.out.println("Tag: " + v.getTag());

                    String[] split = v.getTag().toString().split("|");
                    String category = split[0];
                    String detail = "";
                    if(split.length>1)
                        detail = split[1];

                    AppSensors.setCategory(category);
                    AppSensors.setDetail(detail);
                    AppSensors.commit(caller);

                    if (dialog != null) dialog.dismiss();
                }
            };

            this.dialog = new MADialog(caller, positive, null);
            this.dialog.show();



        } else {


            if (!AppSensors.isReadyForRecording()) {
                 ((ToggleButton) caller.findViewById(R.id.buttonStartRecording)).setChecked(false);
                //TODO Meldung anzeigen
                //
            } else {

                AppSensors.startRecording();

                isRecording = true;


            }
        }
    }

}
