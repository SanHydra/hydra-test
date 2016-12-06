package com.hudongwx.test;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
    }

    public void returnHistory(View view) {
        onBackPressed();
    }

    public void tan(View view) {
        Intent intent =new Intent(this,AboutUsActivity.class);
        startActivity(intent);

    }
}
