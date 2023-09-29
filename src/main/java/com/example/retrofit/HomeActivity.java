package com.example.retrofit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {
    private Button button;
    private TextView responseTV;
    private ImageView img;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        button = findViewById(R.id.button1);
        img = findViewById(R.id.idImageView);
        responseTV = findViewById(R.id.idTVResponse);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        try {
            JSONObject jsonname = new JSONObject(result);
            String jname = jsonname.getString("name");
            String avatar = jsonname.getString("avatar");
            responseTV.setText("Witaj " + jname + "!");
            Picasso.get().load(avatar).into(img);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("check", "false");
                editor.apply();
                Toast.makeText(HomeActivity.this, "Wylogowano", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}