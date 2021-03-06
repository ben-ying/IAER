package com.yjh.iaer.injection;

import android.app.Application;
import androidx.room.Room;

import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.AboutDao;
import com.yjh.iaer.room.dao.CategoryDao;
import com.yjh.iaer.room.dao.FundDao;
import com.yjh.iaer.room.dao.SettingDao;
import com.yjh.iaer.room.dao.TransactionDao;
import com.yjh.iaer.room.dao.UserDao;
import com.yjh.iaer.room.db.MyDatabase;
import com.yjh.iaer.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
//    private static final String BASE_URL = "http://bensbabycare.com/webservice/";
    private static final String BASE_URL = "http://mybackend.online:8000/iaer/api/";
//    private static final String BASE_URL = "http://107.182.27.191:8080/iaer/api/";
//    private static final String BASE_URL = "http://raspbian-backend.ddns.net:8000/webservice/";

    @Singleton
    @Provides
    Webservice provideWebservice() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(Webservice.class);

//        final OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(20, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS)
//                .writeTimeout(20, TimeUnit.SECONDS)
//                .build();
//
//        return new Retrofit.Builder()
//                .client(client)
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
//                .build()
//                .create(Webservice.class);
    }

    @Singleton
    @Provides
    MyDatabase provideDb(Application application) {
        return Room.databaseBuilder(application, MyDatabase.class, "iaer.db").build();
    }

    @Singleton
    @Provides
    TransactionDao provideTransactionDao(MyDatabase db) {
        return db.transactionDao();
    }

    @Singleton
    @Provides
    UserDao provideUserDao(MyDatabase db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    CategoryDao provideCategoryDao(MyDatabase db) {
        return db.categoryDao();
    }

    @Singleton
    @Provides
    FundDao provideFundDao(MyDatabase db) {
        return db.fundDao();
    }

    @Singleton
    @Provides
    SettingDao provideSettingDao(MyDatabase db) {
        return db.settingDao();
    }

    @Singleton
    @Provides
    AboutDao provideAboutDao(MyDatabase db) {
        return db.aboutDao();
    }
}
