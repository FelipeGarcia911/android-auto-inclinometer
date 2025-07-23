package com.felipeg.common.di

import com.felipeg.common.data.repository.FSensorRepositoryImpl
import com.felipeg.common.repository.FSensorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FSensorModule {
    @Binds
    @Singleton
    abstract fun bindFSensorRepository(
        impl: FSensorRepositoryImpl
    ): FSensorRepository
}
