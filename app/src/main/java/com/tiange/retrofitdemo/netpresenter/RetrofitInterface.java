package com.tiange.retrofitdemo.netpresenter;

import com.tiange.retrofitdemo.netbean.Reception;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by 徐大哈 on 2018/1/3.
 *
 * 创建用于描述网络请求的接口
 */

public interface RetrofitInterface {

    /**
     * 1.简单例子
     * @GETs是表明采用get请求
     * getCall得到的是接口请求返回的数据
     * Call<Reception>中的Reception是返回数据封装的类，如果想直接获得整个返回的结构图可以用Call<ResponseBody>
     * @return
     */
    @GET("live/getad.aspx")
    Call<Reception> getCall();


    /**
     * URL的组成：头url是通过baseUrl来设置，其余参数部分的url是在注解中设置的
     *
     * 网络请求的完整 Url =在创建Retrofit实例时通过.baseUrl()设置 +网络请求接口的注解设置（称 “path“ ）
     * 如果在接口中定义的URL为全路径，将用这个全路径作为请求URL，BaseUrl将不起作用
     */
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://xxx/")//设置网络请求的url头
            .addConverterFactory(GsonConverterFactory.create())//设置数据解析器
            .build();


    /**
     * 2.注解说明
     * 第一类：网络请求方法
     * @GET   @POST   @PUT   @DELETE    @PATH    @HEAD    @OPTIONS  :这些方法http的网络请求方法，都接受一个网络地址url
     * ,也可以不指定，通过@HTTP来设置
     * @HTTP ：用于替换以上7个注解的作用，及更多拓展功能
     *
     */

    /**
     * @HTTP注解
     * method代表的是网络请求方法，区分大小写
     * path代表的是路径，这里的是去除了头url的路径
     * hasBody代表是否有请求体
     *
     * {id}代表一个变量
     * 这个方法的参数代表：?id=1
     *
     * @return
     */
    @HTTP(method = "GET" , path = "live/getad.aspx/{id}", hasBody = false)
    Call<Reception> getCallForHttp(@Path("id") int id);


    /**
     * 第二类：标记
     * @FormUrlEncoded :表明请求体是一个表单  作用：表示发送form-encoded的数据
     * 每个键值对需要用@Filed来注解键名，随后的对象需要提供值
     *
     * @Multipart :表明请求体是一个支持文件上传的Form表单  作用：表示发送form-encoded的数据（适用于 有文件 上传的场景）
     * 每个键值对需要用@Part来注解键名，随后的对象需要提供值
     *
     * @Streaming :表明返回的数据以流的形式返回，适用于数据量大的情况下，如果没有使用该注解，默认把数据全部载入内存，
     * 之后获取数据也是从内存中取。
     */


    /**
     * 表明是一个表单格式的请求（Content-Type:application/x-www-form-urlencoded）
     * 将idx的值作为id的参数带过去，类似：?id=idx的值
     * @POST通常和FormUrlEncoded一起使用
     * 少量参数用Field，大量参数用FieldMap
     * @param idx
     * @return
     */
    @POST("live/getad.aspx")
    @FormUrlEncoded
    Call<ResponseBody> getCallForFormUrlEncoded(@Field("id")  int idx);

    //也可以用@FidldMap提交整个map
    @POST("live/getad.aspx")
    @FormUrlEncoded
    Call<ResponseBody> getCallForFormUrlEncoded1(@FieldMap Map<String, String> map);

    //也可以封装成一个实体提交
    @POST("live/getad.aspx")
    @FormUrlEncoded
    Call<ResponseBody> getCallForFormUrlEncoded2(@Body Reception reception);


    /**
     * {@link Part} 后面支持三种类型，{@link RequestBody}、{@link MultipartBody.Part} 、任意类型
     * 除 {@link okhttp3.MultipartBody.Part} 以外，其它类型都必须带上表单字段({@link MultipartBody.Part} 中已经包含了表单字段的信息)，
     * MultipartBody.Part多文件上传，可以将File转换为MultipartBody
     */
    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload1(@Part("name") RequestBody name, @Part("age") RequestBody age, @Part MultipartBody.Part file);


    /**
     * 第三类：网络请求参数
     */
    /**
     * @Header & @Headers
     * 作用：添加请求头 &添加不固定的请求头
     */
    @GET("live/getad.aspx")
    Call<Reception> headerTest(@Header("User-Agent") String params);

    @GET("live/getad.aspx")
    @Headers("User-Agent:xxxx")
    Call<Reception> headersTest();

    /**
     * @Query和@QueryMap
     * 用于 @GET 方法的查询参数（Query = Url 中 ‘?’ 后面的 key-value）
     */

    /**
     * @Url
     * 直接传入一个请求的 URL变量 用于URL设置
     */
    @GET
    Call<Reception> getText(@Url String url);
}
