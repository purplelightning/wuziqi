package com.example.ss.wuziqi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnClickListener{

    private ImageButton qibutton;
    private Button tuibutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qibutton=(ImageButton) findViewById(R.id.qi_btn);
        tuibutton=(Button) findViewById(R.id.tuijian_btn);
        qibutton.setOnClickListener(this);
        tuibutton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.qi_btn:
                Intent intent1=new Intent(MainActivity.this,wuziqi.class);
                startActivity(intent1);
                break;
            case R.id.tuijian_btn:
                Intent intent2=new Intent(MainActivity.this,HandlerImg.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

}
