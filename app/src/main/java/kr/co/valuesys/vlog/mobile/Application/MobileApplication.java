package kr.co.valuesys.vlog.mobile.Application;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Date;

//import io.realm.Realm;
//import io.realm.RealmConfiguration;

public class MobileApplication extends Application {

    private static MobileApplication mobileApplication;

    public static MobileApplication getContext() {
        return mobileApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mobileApplication = this;
//        Realm.init(this);
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(realmConfiguration);
    }



    public String convertDateToString(Date date, String pattern) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String str = sdf.format(date);
        return str;
    }

}
