package com.restituyo.opencvmobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    private Button optionBtn1;
    private Button optionBtn2;
    private int[] numberResources = {
            R.drawable.count1,
            R.drawable.count2,
            R.drawable.count3,
            R.drawable.count4,
            R.drawable.count5,
            R.drawable.count6,
            R.drawable.count7,
            R.drawable.count8,
            R.drawable.count9
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        optionBtn1 = (Button)findViewById(R.id.menuButton1);
        optionBtn2 = (Button) findViewById(R.id.menuButton2);

        optionBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionClick(v,numberResources);
            }
        });

        /*
        optionBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionClick(v,alphabetResources);
            }
        });*/
    }
    private void optionClick(View v, int[] resources)
    {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        bundle.putIntArray("ResourcesArray",resources);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
