package bayern.mimo.masterarbeit.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import bayern.mimo.masterarbeit.activity.ConnectSensorsActivity;

/**
 * Created by MiMo on 22.02.2017.
 */

public class OnButtonConnectSensorsClickListener implements View.OnClickListener {

    private Context context;

    public OnButtonConnectSensorsClickListener(Context context){
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent connectSensorsIntent = new Intent(this.context, ConnectSensorsActivity.class);
        this.context.startActivity(connectSensorsIntent);
    }
}
