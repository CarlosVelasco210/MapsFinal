package com.example.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animacion
        Animation animacion1= AnimationUtils.loadAnimation(this,R.anim.desplazamiento_arriba);
        Animation animacion2 = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_abajo);
        Animation animacion3 = AnimationUtils.loadAnimation(this,R.anim.fade);

        TextView appnombre = findViewById(R.id.appnombre);
        ImageView applogo = findViewById(R.id.logoimage);
        ImageView bglaunch = findViewById(R.id.bg_launch);

        appnombre.setAnimation(animacion2);
        applogo.setAnimation(animacion1);
        bglaunch.setAnimation(animacion3);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
                finish();
            }
        },3500);

    }
}