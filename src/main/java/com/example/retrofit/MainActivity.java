package com.example.retrofit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private EditText emailedt, passedt;
    private ProgressBar loadingPB;
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
        @Override
        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                    access_token != null ? access_token : "");

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }
    }).build();

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://uke.matcom.com.pl/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();
    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailedt = findViewById(R.id.idEdtEmail);
        passedt = findViewById(R.id.idEdtPass);
        Button postDataBtn = findViewById(R.id.idBtnPost);
        loadingPB = findViewById(R.id.idLoadingPB);
        CheckBox check = findViewById(R.id.checkBox1);
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("check", "");
        if(checkbox.equals("true")) {
            SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
            String jstring = prefs.getString("key", "");
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("result", jstring);
            startActivity(intent);
        }

        postDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                requestPost(emailedt.getText().toString(), passedt.getText().toString());
            }
        });
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("check", "true");
                    editor.apply();
                } else {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("check", "false");
                    editor.apply();
                }
            }
        });
    }
    String access_token = null;
    private void requestPost(String email, String password) {
        RequestPost requestPost = new RequestPost(email, password);
        Call<ResponsePost> call = retrofitAPI.postUser(requestPost);
        call.enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                loadingPB.setVisibility(View.GONE);
                ResponsePost resource = response.body();
                if (response.body().error == null) {
                    ResponsePost.Result result = resource.result;
                    Toast.makeText(MainActivity.this, result.token_type + " " + result.access_token, Toast.LENGTH_SHORT).show();
                    access_token = result.token_type + " " + result.access_token;
                    getAccess(access_token);
                }
                else {
                    ResponsePost.Result error = resource.error;
                    Toast.makeText(MainActivity.this, error.message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAccess(String access_token) {
        if (access_token != null) {
            Call<ResponseBody> call = retrofitAPI.getAccess(access_token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    loadingPB.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Zalogowano pomyslnie", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        String responseString = null;
                        try {
                            responseString = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            String jstring = jsonObject.getString("result");
                            intent.putExtra("result", jstring);
                            startActivity(intent);
                            SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("key", jstring);
                            editor.commit();
                        } catch (IOException | JSONException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "Token error", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Token error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            loadingPB.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Token error", Toast.LENGTH_SHORT).show();
        }
    }
}
