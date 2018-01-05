package com.tiange.retrofitdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tiange.test.HttpServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *Created by 徐大哈 on 2018/1/3.
 *
 * Retrofit 是一个 Restful 的 HTTP 网络请求框架的封装。
 * 网络请求的工作本质上是 OkHttp 完成，而 Retrofit 仅负责 网络请求接口的封装
 * App应用程序通过 Retrofit 请求网络，实际上是使用 Retrofit 接口层封装请求参数、Header、Url 等信息，
 * 之后由 OkHttp 完成后续的请求操作。在服务端返回数据之后，OkHttp 将原始的结果交给 Retrofit，
 * Retrofit根据用户的需求对结果进行解析
 *
 *
 * 使用 Retrofit 的步骤共有7个：
 * 步骤1：添加Retrofit库的依赖
 * 步骤2：创建 接收服务器返回数据 的类
 * 步骤3：创建 用于描述网络请求 的接口
 * 步骤4：创建 Retrofit 实例
 * 步骤5：创建 网络请求接口实例 并 配置网络请求参数
 * 步骤6：发送网络请求（异步 / 同步）
 * 步骤7：处理服务器返回的数据
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://xxxx/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //创建网络请求接口实例
        final HttpServer httpServer = retrofit.create(HttpServer.class);
        //对网络请求接口封装
        final Call<ResponseBody> call = httpServer.getCall();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //java.lang.IllegalStateException: Already executed.
                Call<ResponseBody> call1 = call.clone();//多次调用这个请求
                //发送网络请求（异步）
                call1.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //成功的回调
                        try {
                            Log.d("xudaha", response.message() + "|"+new String(response.body().bytes()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //失败回调
                    }
                });

               // call.execute(); 这个是同步请求方法
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
    }

    /**
     * 文件下载
     * 在方法上加上@Streaming注解,加上这个注解以后就不会讲文件内容加载到内存中,
     * 而是在通过ResponseBody 去读取文件的时候才从网络文件去下载文件.所以应该在保存文件的时候放到异步线程
     */
    private void download(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http:/xxx/").build();
        HttpServer httpServer = retrofit.create(HttpServer.class);
        Call<ResponseBody> call = httpServer.download("http://xxx/9158.apk");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if(response.isSuccessful()){

                    new AsyncTask<Void, Long, Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean saveSuccess = saveFile(response.body(), "保存的文件.apk");
                            return null;
                        }
                    }.execute();
                }else
                    Log.d("xudaha", "下载失败");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private boolean saveFile(ResponseBody responseBody, String saveName){
        boolean saveSuccess = false;
        File saveFile;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            saveFile = ContextCompat.getExternalCacheDirs(this)[0];
        }else{
            saveFile = this.getCacheDir();
        }
        saveFile = new File(saveFile.getAbsolutePath() + File.separator + saveName);
        InputStream inputStream = responseBody.byteStream();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(saveFile);
            byte [] bytes = new byte[1024];
            int lengh;
            while ((lengh = inputStream.read(bytes)) != -1){
                outputStream.write(bytes);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
            Log.d("xudaha", "下载成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  saveSuccess;
    }

    /**
     * 文件上传
     */
    private void uploadFile(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("xxxx")
                .build();
        HttpServer httpServer = retrofit.create(HttpServer.class);
        File file = new File("xxx");
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ResponseBody> call = httpServer.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
