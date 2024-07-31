package com.ketchupzzz.isaom.di


import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ketchupzzz.isaom.repository.dictionary.DictionaryRepository
import com.ketchupzzz.isaom.repository.dictionary.DictionaryRepositoryImpl
import com.ketchupzzz.isaom.repository.auth.AuthRepository
import com.ketchupzzz.isaom.repository.auth.AuthRepositoryImpl
import com.ketchupzzz.isaom.repository.sections.SectionRepository
import com.ketchupzzz.isaom.repository.sections.SectionRepositoryImpl
import com.ketchupzzz.isaom.repository.translator.TranslatorRepository
import com.ketchupzzz.isaom.repository.translator.TranslatorRepositoryImpl
import com.ketchupzzz.isaom.services.TranslatorService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage : FirebaseStorage,
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore, storage)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(
        firestore: FirebaseFirestore,
    ): SectionRepository {
        return SectionRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideDictionaryRepository(

        firestore: FirebaseFirestore
    ) : DictionaryRepository {
        return DictionaryRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TranslatorService.API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTranslatorService(retrofit: Retrofit): TranslatorService {
        return retrofit.create(TranslatorService::class.java)
    }

    @Provides
    @Singleton
    fun provideTranslatorRepository(translatorService: TranslatorService) :TranslatorRepository {
       return TranslatorRepositoryImpl(translatorService)
    }
 }