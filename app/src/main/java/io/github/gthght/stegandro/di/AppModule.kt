package io.github.gthght.stegandro.di

import io.github.gthght.stegandro.data.locale.Steganography
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.gthght.stegandro.data.locale.SteganographyInterface
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSteganography(@ApplicationContext appContext: Context): SteganographyInterface{
        return Steganography(appContext)
    }
}