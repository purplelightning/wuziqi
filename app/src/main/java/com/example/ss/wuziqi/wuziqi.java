package com.example.ss.wuziqi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class wuziqi extends AppCompatActivity {

    private WuziqiPanel wuziqiPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wuziqi_layout);

        wuziqiPanel=(WuziqiPanel) findViewById(R.id.id_wuziqi);
    }


    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    //响应菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch(id)
        {
            case R.id.action_settings:
                wuziqiPanel.start();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
