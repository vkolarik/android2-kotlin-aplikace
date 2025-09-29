package cz.mendelu.project.di

import cz.mendelu.project.database.ILocalCarExpenseRepository
import cz.mendelu.project.database.CarExpenseDao
import cz.mendelu.project.database.LocalCarExpenseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCarExpenseRepository(dao: CarExpenseDao): ILocalCarExpenseRepository {
        return LocalCarExpenseRepositoryImpl(dao)
    }
}