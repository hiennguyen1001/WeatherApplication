package com.example.riteshkumarsingh.weatherapplication.service

import com.example.riteshkumarsingh.weatherapplication.BuildConfig
import com.example.riteshkumarsingh.weatherapplication.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


/**
 * Created by riteshkumarsingh on 13/10/17.
 */
class Injection {
    companion object {

        private val USER_AGENT = "User-Agent"
        private val ADEPT_ANDROID_APP = "Adept-Android-App"

        fun getOkHttpClient(): OkHttpClient {
            val okHttpClient: OkHttpClient =
                    OkHttpClient.Builder()
                            .addInterceptor(provideHttpLoggingInterceptor())
                            .addInterceptor(provideUrlAndHeaderInterceptor())
                            .build()

            return okHttpClient
        }

        private fun provideUrlAndHeaderInterceptor(): Interceptor {
            return Interceptor { chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                        .addHeader(USER_AGENT, ADEPT_ANDROID_APP)
                chain.proceed(builder.build())
            }
        }

        private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Timber.d(message) })
            httpLoggingInterceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
            return httpLoggingInterceptor
        }

        fun provideRetrofit(): Retrofit {
            val httpClient = getOkHttpClient()
            val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()

            return retrofit
        }

        fun provideApiService(): ApiService {
            return provideRetrofit()
                    .create(ApiService::class.java)
        }
    }
}