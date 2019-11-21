package com.parkingapplication.networks.controller;

import com.parkingapplication.BuildConfig;
import com.parkingapplication.utils.Logger;
import com.parkingapplication.utils.RetrofitLogger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * mj_parking_app
 * Class: ClientUtil
 * Created by MinjinPark on 2019-11-12.
 * <p>
 * Description: Retrofit2 Client SingleTon Class
 * 서버와의 데이터 통신을 관리하는/도와주는 클래스.
 */
public class ClientUtil {

    // [s] Define Value
    private final String baseUrl = "http://ec2-15-164-211-230.ap-northeast-2.compute.amazonaws.com";
    private final String baseSslUrl = "https://ec2-15-164-211-230.ap-northeast-2.compute.amazonaws.com";

    private final long TIME_OUT_CONNECT = 5;
    private final long TIME_OUT_READ = 5;
    private final long TIME_OUT_WRITE = 5;
    private final int MAX_CONNECTION = 5;
    private final long CONNECTION_DURATION = 1;

    private final String KEY_ACCEPT = "accept";
    private final String CONTENT_TYPE = "Content-Type";
    private final String VALUE_ACCEPT = "application/json; charset=utf-8";
    // [e] Define Value

    // [s] Http Code Enum Class
    public enum Rest {

        OK(200),
        BAD_REQUEST(400),
        FORBIDDEN(403),
        NOT_FOUND(404),
        SERVER_ERROR(500),
        SERVER_BAD_GATEWAY(502);

        public final int code;

        Rest(int code) {
            this.code = code;
        }
    }
    // [s] Http Code Enum Class

    private Retrofit mRetrofit = null;
    private Retrofit mSslRetrofit = null;

    // [s] Single Ton 클래스 영역
    private static class LazyHolder {
        static final ClientUtil instance = new ClientUtil();
    }

    public static ClientUtil getInstance() {
        return LazyHolder.instance;
    }
    // [e] Single Ton 클래스 영역

    private ClientUtil() {
        // init Retrofit
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getBaseClient())
                    .build();
        }

        // init Ssl Retrofit
        if (mSslRetrofit == null) {
            mSslRetrofit = new Retrofit.Builder()
                    .baseUrl(baseSslUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getBaseClient())
                    .build();
        }
    }
    // [e] Single Ton 클래스 영역

    /**
     * get Retrofit.
     *
     * @return Retrofit
     * @author MinjinPark
     */
    public Retrofit getRetrofit() {

        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getBaseClient())
                    .build();
        }

        return mRetrofit;
    }

    /**
     * get Ssl Retrofit
     *
     * @return Ssl Retrofit
     * @author MinjinPark
     */
    public Retrofit getSslRetrofit() {
        if (mSslRetrofit == null) {
            mSslRetrofit = new Retrofit.Builder()
                    .baseUrl(baseSslUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getBaseClient())
                    .build();
        }

        return mSslRetrofit;
    }

    /**
     * Header 세팅 하는 Interceptor Class.
     */
    private final class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request origin = chain.request();

            Request request = origin.newBuilder()
                    .header(CONTENT_TYPE, VALUE_ACCEPT)
                    .header(KEY_ACCEPT, VALUE_ACCEPT)
                    .method(origin.method(), origin.body())
                    .build();
            return chain.proceed(request);
        }
    }

    /**
     * 서버 에러등 Not Found Error 발생시 처리 하는 Interceptor Class
     */
    private final class ForbiddenInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            int httpCode = response.code();

            if (httpCode == Rest.SERVER_ERROR.code ||
                    httpCode == Rest.SERVER_BAD_GATEWAY.code) {
                Logger.d("서버 에러 발생 에러 코드\t" + httpCode);
            }
            return response;
        }
    }

    /**
     * Default Base Http Client Func.
     *
     * @return OkHttpClient
     * @author hmju
     */
    private OkHttpClient getBaseClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(MAX_CONNECTION, CONNECTION_DURATION, TimeUnit.SECONDS))
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new ForbiddenInterceptor());

        // Retrofit Logger -> Debug Mode 인경우에만 활성화.
        if (BuildConfig.DEBUG) {
            client.addInterceptor(new RetrofitLogger().setLevel(RetrofitLogger.Level.BODY));
        }
        return client.build();

    }

}
