package kr.co.valuesys.vlog.mobile;

import android.app.Application;

//import io.realm.Realm;
//import io.realm.RealmConfiguration;

public class MobileApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        Realm.init(this);
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
