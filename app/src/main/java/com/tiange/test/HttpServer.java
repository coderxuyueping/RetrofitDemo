package com.tiange.test;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by 徐大哈 on 2018/1/5.
 */

public interface HttpServer {
    @GET("live/getad.aspx")
    Call<ResponseBody> getCall();

    //Streaming在下载的文件大的时候有用，因为避免将文件写入内存
    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url);

    @POST("upload.php")
    @Multipart
    Call<ResponseBody> upload(@Part MultipartBody.Part file);
}
