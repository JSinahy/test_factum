package com.itcomca.testfactum.repositories.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.itcomca.testfactum.BuildConfig
import com.itcomca.testfactum.utils.UtilNetworking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class APIMoviesDB {
    fun GetInstance() : Retrofit {
        /** Se agrega el LogginInterceptor para tener un log de las solicitudes de red de retrofit **/
        //val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        /** Interceptor onLine **/
        var ONLINE_INTERCEPTOR = object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val response = chain.proceed(chain.request())
                val maxAge = 60
                return response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .removeHeader("Pragma")
                    .build();
            }
        }

        /** Interceptor OfLine **/
        var OFFLINE_INTERCEPTOR = object: Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                var request: Request = chain.request()

                /** Se verifica si hay internet **/
                if(!UtilNetworking.hasNetwork()!!) {

                    /** Cache de 30 dias **/
                    val maxStale = 60 * 60 * 24 * 30

                    /** Se setea el control de cache en los headers de la peticion **/
                    request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
                }
                return chain.proceed(request);
            }
        }

        /** Se crea el objeto client que pasa como parametro para la creacion del objeto de retrofit, trae la config de conexion **/
        val client : OkHttpClient.Builder = OkHttpClient.Builder()
                                                .cache(UtilNetworking.getCache())
                                                //.addInterceptor(logging)
                                                .addInterceptor(OFFLINE_INTERCEPTOR)
                                                .addInterceptor(ONLINE_INTERCEPTOR)
                                                .connectTimeout(60, TimeUnit.SECONDS)


        /** Regreso el objeto Builder de retrofit con la configuracion y log para realizar los llamados de red a la API **/
        return Retrofit.Builder()

            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(NullOrEmptyConverterFactory().converterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())

            .build()
    }
}