package bayern.mimo.masterarbeit.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MiMo
 */

public class Util {

    private static Map<Integer, String> permissions;

    private Util() {
    }

    public static void checkPermissions(Activity activity, String[] perms) {

        //TODO get this properly to work

        if(permissions == null)
            permissions = new HashMap<>();

        for (int i = 0; i < perms.length; i++) {
            permissions.put(i, perms[i]);
            requestPermission(perms[i], i, activity);
            System.out.println("requested for "+perms[i]);
        }

        //requestPermission(perms[0], 0, activity);



    }

    public static AlertDialog showInfoDialog(String title, String message, Context context){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // to do - nothing?
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static AlertDialog showDecisionDialog(String title, String message, Context context){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO JA-Action
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO NEIN-Action
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static void requestPermission(String permission, int requestCode, Activity activity) {



        if (ContextCompat.checkSelfPermission(activity,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    requestCode);
            System.out.println("Requested permission for "+permission);
        }else{
            System.out.println("permission already granted for " + permission);
        }
    }


}
