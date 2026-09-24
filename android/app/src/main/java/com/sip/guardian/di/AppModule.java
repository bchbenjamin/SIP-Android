package com.sip.guardian.di;

import android.content.Context;

import androidx.room.Room;

import com.sip.guardian.data.local.SipDatabase;
import com.sip.guardian.data.local.dao.IncidentDao;
import com.sip.guardian.data.local.dao.NodeDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    SipDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, SipDatabase.class, "sip.db")
                .fallbackToDestructiveMigration() // prototype: acceptable; add migrations for prod
                .build();
    }

    @Provides
    IncidentDao provideIncidentDao(SipDatabase db) { return db.incidentDao(); }

    @Provides
    NodeDao provideNodeDao(SipDatabase db) { return db.nodeDao(); }
}
