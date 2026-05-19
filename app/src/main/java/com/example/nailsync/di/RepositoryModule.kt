package com.example.nailsync.di

import com.example.nailsync.data.repository.CustomerRepositoryImpl
import com.example.nailsync.data.repository.ServiceRepositoryImpl
import com.example.nailsync.data.repository.TechnicianRepositoryImpl
import com.example.nailsync.data.repository.TicketRepositoryImpl
import com.example.nailsync.domain.repository.CustomerRepository
import com.example.nailsync.domain.repository.ServiceRepository
import com.example.nailsync.domain.repository.TechnicianRepository
import com.example.nailsync.domain.repository.TicketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTicketRepository(impl: TicketRepositoryImpl): TicketRepository

    @Binds
    @Singleton
    abstract fun bindTechnicianRepository(impl: TechnicianRepositoryImpl): TechnicianRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(impl: ServiceRepositoryImpl): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository
}
