package com.yjh.iaer.injection;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.TransactionDao;
import com.yjh.iaer.room.db.MyDatabase;
import com.yjh.iaer.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
    private static final String BASE_URL = "http://bensbabycare.com/webservice/";

    @Singleton
    @Provides
    Webservice provideWebservice() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(Webservice.class);
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
}
