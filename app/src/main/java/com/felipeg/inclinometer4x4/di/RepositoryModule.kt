package com.felipeg.inclinometer4x4.di

import com.felipeg.inclinometer4x4.data.repository.FSensorRepositoryImpl
import com.felipeg.inclinometer4x4.domain.repository.FSensorRepository
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
    abstract fun bindFSensorRepository(
        fSensorRepositoryImpl: FSensorRepositoryImpl
    ): FSensorRepository
}
