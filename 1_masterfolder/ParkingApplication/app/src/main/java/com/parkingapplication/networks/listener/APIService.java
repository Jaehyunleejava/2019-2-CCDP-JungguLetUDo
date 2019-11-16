package com.parkingapplication.networks.listener;

import com.google.gson.JsonObject;
import com.parkingapplication.networks.dataModel.TestModel;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * mj_parking_app
 * Class: APIService
 * Created by jsieu on 2019-11-12.
 * <p>
 * Description: API Service Class
 */
public interface APIService {

    @GET("index.php")
    Call<TestModel> requestIndex();

    // 예시 예시.
    @FormUrlEncoded
    @POST("test")
    Call<JsonObject> requestTest(@Query("test") String test,
                                 @Query(value="encodedTest", encoded = true) String encodedTest);

    /**
     * 예시 예시 걍 블로그에 따옴...(귀찮아서...)
     *  @GET("api/users?")
     *     Call<UserList> doGetUserList(@Query("page") String page);
     *
     *     @FormUrlEncoded
     *     @POST("api/users?")
     *     Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
     *  이런식으로 포멧 형식은
     *  {@Method(경로)}
     *  Call<{데이터 모델}> {알맞은 이름} {@Query...., encode..}
     */

}
