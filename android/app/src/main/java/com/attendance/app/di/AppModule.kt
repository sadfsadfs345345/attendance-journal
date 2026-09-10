package com.attendance.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.attendance.app.data.local.AppDatabase
import com.attendance.app.data.remote.AuthInterceptor
import com.attendance.app.data.remote.api.AttendanceApi
import com.attendance.app.data.remote.api.AuthApi
import com.attendance.app.data.repository.AttendanceRepositoryImpl
import com.attendance.app.domain.repository.AttendanceRepository
import com.attendance.app.domain.usecase.ExportCsvUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> = ctx.dataStore

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "attendance_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideStudentDao(db: AppDatabase) = db.studentDao()
    @Provides fun provideLessonDao(db: AppDatabase)  = db.lessonDao()
    @Provides fun provideAttendanceDao(db: AppDatabase) = db.attendanceDao()

    @Provides @Singleton
    fun provideOkHttp(interceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://your-backend.example.com/api/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideAttendanceApi(retrofit: Retrofit): AttendanceApi =
        retrofit.create(AttendanceApi::class.java)

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides @Singleton
    fun provideRepository(
        attendanceDao: com.attendance.app.data.local.dao.AttendanceDao,
        studentDao: com.attendance.app.data.local.dao.StudentDao,
        lessonDao: com.attendance.app.data.local.dao.LessonDao,
        api: AttendanceApi
    ): AttendanceRepository = AttendanceRepositoryImpl(attendanceDao, studentDao, lessonDao, api)

    @Provides @Singleton
    fun provideExportCsvUseCase(@ApplicationContext ctx: Context): ExportCsvUseCase =
        ExportCsvUseCase(ctx)
}
