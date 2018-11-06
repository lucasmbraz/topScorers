package com.lucasmbraz.topscorers

import com.lucasmbraz.topscorers.data.TopScorersRepository
import com.lucasmbraz.topscorers.data.ProductionTopScorersRepository
import com.lucasmbraz.topscorers.network.FootballApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Injection {

    private val client by lazy {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor { chain: Interceptor.Chain ->
            val request = chain.request().newBuilder()
                    .header("X-Auth-Token", "b7fd32e724454d3bbee138fe39150328")
                    .build()

            chain.proceed(request)
        }
        builder.build()
    }

    private val api by lazy {
        Retrofit.Builder()
                .baseUrl("http://api.football-data.org/v2/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(FootballApi::class.java)
    }

    fun provideTopScorersRepository(): TopScorersRepository = ProductionTopScorersRepository(api)
}