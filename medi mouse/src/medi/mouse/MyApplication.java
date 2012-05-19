package medi.mouse;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;
//mailTo = "crashreports@williamcohen.com",

@ReportsCrashes(formKey = "dFhnRHR6Qy02aFZnSE9LU2NEVDRuY1E6MQ", 
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crashing) 
public class MyApplication extends Application {
	@Override
    public void onCreate() {
		
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
}