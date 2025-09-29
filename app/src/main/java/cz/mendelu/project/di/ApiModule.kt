package cz.mendelu.project.di
import cz.mendelu.project.communication.OverpassAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideOverpassAPI(retrofit: Retrofit): OverpassAPI {
        return retrofit.create(OverpassAPI::class.java)
    }

}