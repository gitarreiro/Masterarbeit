package bayern.mimo.masterarbeit.exception;

import android.content.Context;
import android.content.Intent;

import android.os.Process;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created by MiMo on 02.04.2017.
 */

public class MAExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    private final Context context;
    private final Class<?> activityClass;

    public MAExceptionHandler(Context context, Class<?> c) {

        this.context = context;
        activityClass = c;
    }

    public void uncaughtException(Thread thread, Throwable exception) {


        //TODO umbaun
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        System.err.println(stackTrace);// You can use LogCat too
        Intent intent = new Intent(context, activityClass);
        String s = stackTrace.toString();
        //you can use this String to know what caused the exception and in which Activity
        intent.putExtra("uncaughtException",
                "Exception is: " + stackTrace.toString());
        intent.putExtra("stacktrace", s);
        context.startActivity(intent);
        //for restarting the Activity
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}