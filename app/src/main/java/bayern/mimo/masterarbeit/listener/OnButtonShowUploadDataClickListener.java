package bayern.mimo.masterarbeit.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import bayern.mimo.masterarbeit.activity.ShowAndUploadDataActivity;

/**
 * Created by MiMo
 */

public class OnButtonShowUploadDataClickListener implements View.OnClickListener {

    private Context context;

    public OnButtonShowUploadDataClickListener(Context context){
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.context, ShowAndUploadDataActivity.class);
        this.context.startActivity(intent);
    }
}
