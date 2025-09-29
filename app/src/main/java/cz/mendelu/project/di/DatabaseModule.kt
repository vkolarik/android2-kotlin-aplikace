package cz.mendelu.project.di

import android.content.Context
import cz.mendelu.project.database.CarExpenseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCarExpenseDatabase(@ApplicationContext context: Context): CarExpenseDatabase {
        return CarExpenseDatabase.getDatabase(context)
    }
}