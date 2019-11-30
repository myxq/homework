package edu.neu.homework.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.neu.homework.util.ActivityController;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    void autoStartActivity(Class T){
        Intent intent = new Intent(this,T);
        startActivity(intent);
    }

}
