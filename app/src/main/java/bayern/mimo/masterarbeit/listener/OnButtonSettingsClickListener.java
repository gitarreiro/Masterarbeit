package bayern.mimo.masterarbeit.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import bayern.mimo.masterarbeit.activity.SettingsActivity;

/**
 * Created by MiMo on 22.02.2017.
 */

public class OnButtonSettingsClickListener implements View.OnClickListener {

    private Context context;

    public OnButtonSettingsClickListener(Context context){
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent startSettingsActivityIntent = new Intent(this.context, SettingsActivity.class);
        this.context.startActivity(startSettingsActivityIntent);
    }
}
