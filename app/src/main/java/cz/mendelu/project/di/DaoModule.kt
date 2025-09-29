package cz.mendelu.project.di

import cz.mendelu.project.database.CarExpenseDao
import cz.mendelu.project.database.CarExpenseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideItemsDao(database: CarExpenseDatabase): CarExpenseDao {
        return database.carExpenseDao()
    }
}