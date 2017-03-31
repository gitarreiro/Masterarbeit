package bayern.mimo.masterarbeit.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import bayern.mimo.masterarbeit.activity.StartRideActivity;

/**
 * Created by MiMo on 22.02.2017.
 */

public class OnButtonStartRideClickListener implements View.OnClickListener {

    private Context context;

    public OnButtonStartRideClickListener(Context context){
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent startRideIntent = new Intent(this.context, StartRideActivity.class);
        this.context.startActivity(startRideIntent);
    }
}
