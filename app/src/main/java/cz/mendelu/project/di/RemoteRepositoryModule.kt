package cz.mendelu.project.di



import cz.mendelu.project.communication.IOverpassRemoteRepository
import cz.mendelu.project.communication.OverpassAPI
import cz.mendelu.project.communication.OverpassRemoteRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteRepositoryModule {

    @Provides
    @Singleton
    fun provideOverpassRepository(overpassAPI: OverpassAPI): IOverpassRemoteRepository {
        return OverpassRemoteRepositoryImpl(overpassAPI)
    }

}
