package com.example.nailsync.di

import android.content.Context
import androidx.room.Room
import com.example.nailsync.data.local.NailSyncDatabase
import com.example.nailsync.data.local.dao.CustomerDao
import com.example.nailsync.data.local.dao.ServiceDao
import com.example.nailsync.data.local.dao.TechnicianDao
import com.example.nailsync.data.local.dao.TicketDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NailSyncDatabase =
        Room.databaseBuilder(context, NailSyncDatabase::class.java, "nailsync.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides fun provideTicketDao(db: NailSyncDatabase): TicketDao = db.ticketDao()
    @Provides fun provideTechnicianDao(db: NailSyncDatabase): TechnicianDao = db.technicianDao()
    @Provides fun provideServiceDao(db: NailSyncDatabase): ServiceDao = db.serviceDao()
    @Provides fun provideCustomerDao(db: NailSyncDatabase): CustomerDao = db.customerDao()
}
