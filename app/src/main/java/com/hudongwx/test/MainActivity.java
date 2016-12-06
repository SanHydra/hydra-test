package com.hudongwx.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hudongwx.test.db.SqliteUtil;
import com.hudongwx.test.instance.History;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG="WebViewActivity";
    private Button btnMenu,btnGo,btnBack,btnForward,btnRefresh,btnExit;
    private EditText et;
    private ProgressBar pb;
    private WebView wv;
    LinearLayout llHead,llFoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SysApplication.getInstance().addActivity(this);
        btnGo= (Button) findViewById(R.id.web_skip);
        btnBack= (Button) findViewById(R.id.web_back);
        btnForward= (Button) findViewById(R.id.web_forward);
        btnRefresh= (Button) findViewById(R.id.web_refresh);
        btnExit= (Button) findViewById(R.id.web_exit);
        pb= (ProgressBar) findViewById(R.id.web_progress);
        et= (EditText) findViewById(R.id.web_input);
        wv = (WebView) findViewById(R.id.web_wv_1);
        llHead= (LinearLayout) findViewById(R.id.web_head);
        llFoot= (LinearLayout) findViewById(R.id.web_foot);
        btnMenu= (Button) findViewById(R.id.web_menu);

        registerForContextMenu(btnMenu);
        btnMenu.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnGo.setOnClickListener(this);
        Intent intent = getIntent();
        Uri uri= intent.getData();
        if (null!=uri){
            String s=uri.toString();
            wv.loadUrl(s);
        }else {
            wv.loadUrl(getFirstPage());
        }

        wv.getSettings().setJavaScriptEnabled(true);

        wv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (View.VISIBLE==llHead.getVisibility()){
                    llHead.setVisibility(View.GONE);
                    llFoot.setVisibility(View.GONE);
                }else{
                    llHead.setVisibility(View.VISIBLE);
                    llFoot.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                History history=new History(wv.getTitle(),wv.getUrl(),getDate());
                SqliteUtil.getInstance(MainActivity.this).insert(history,"history");
            }
        });
        wv.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                if (100==pb.getProgress()){
                    pb.setVisibility(View.INVISIBLE);
                }else {
                    pb.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                et.setText(wv.getUrl());
            }

        });



    }

    public String getFirstPage() {
        SharedPreferences info = getSharedPreferences("headPage", MODE_PRIVATE);
       return info.getString("url","http://www.baidu.com");
    }
    public void saveFirstPage(String url){
        SharedPreferences info = getSharedPreferences("headPage", MODE_PRIVATE);
        SharedPreferences.Editor editor=info.edit();
        editor.putString("url",url);
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.web_menu:
                btnMenu.performLongClick();
                break;
            case R.id.web_skip:
                String url=et.getText()+"";
                if (url.startsWith("http://")||url.startsWith("https://")){
                    wv.loadUrl(url);
                }else {
                    wv.loadUrl("http://"+url);
                }

                break;
            case R.id.web_back:
                if (wv.canGoBack()){
                    wv.goBack();
                }else {
                    show("无法再后退惹QAQ");
                }
                break;
            case R.id.web_forward:
                if (wv.canGoForward()){
                    wv.goForward();
                }else {
                    show("无法再前进惹QAQ");
                }
                break;
            case R.id.web_refresh:
                btnGo.performClick();
                break;
            case R.id.web_exit:
                AlertDialog.Builder builder =new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("您要离开唧唧了吗ＱＡＱ");
                builder.setPositiveButton("是的！狠心离开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SysApplication.getInstance().exit();
                    }
                });
                builder.setNegativeButton("还是留下吧",null);
                builder.show();
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(1,1,1,"书签/历史");
        menu.add(1,2,2,"添加书签");
        menu.add(1,3,3,"设置");
        menu.add(1,5,5,"设为首页");
        menu.add(1,4,4,"刷新");
        menu.add(1,6,6,"退出");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                Intent intent =new Intent(this,BookmarksActivity.class);
                startActivity(intent);
                break;
            case 2:
                //新建书签实例
                History history=new History(wv.getTitle(),wv.getUrl(),getDate());
                //存入数据库
                long l = SqliteUtil.getInstance(getBaseContext()).insert(history, "bookmarks");
                if (-1==l){
                    show("已经添加过该页面了哦！QAQ");
                }
                show("添加书签成功惹！");
                break;
            case 3:
                Intent intent1 =new Intent(this,SetActivity.class);
                startActivity(intent1);
                break;
            case 4:
                btnRefresh.performClick();
                break;
            case 6:
                btnExit.performClick();
                break;
            case 5:
                saveFirstPage(wv.getUrl());
                break;
        }
        return true;
    }
    public void show(String str){
        Toast.makeText(this, str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (wv.canGoBack()){
            btnBack.performClick();
        }else {
            btnExit.performClick();
        }

    }

    private String getDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日HH:mm:ss");
        return sdf.format(new Date(System.currentTimeMillis()));

    }
}
