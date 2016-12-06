package com.hudongwx.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hudongwx.test.db.SqliteUtil;
import com.hudongwx.test.instance.History;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private ListView lv;
    myAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        SysApplication.getInstance().addActivity(this);
        adapter =new myAdapter(SqliteUtil.getInstance(this).query("history"));
        lv= (ListView) findViewById(R.id.history_list);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(HistoryActivity.this,MainActivity.class);
                Uri uri=Uri.parse(adapter.getHistories().get(position).getUrl());
                intent.setData(uri);

                startActivity(intent);
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(2,7,7,"设为首页");
        menu.add(2,8,8,"改名");
        menu.add(2,9,9,"删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final HistoryActivity t=HistoryActivity.this;
        //获取到position
        int position =( (AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        final String url = adapter.getHistories().get(position).getUrl();
        switch (item.getItemId()){
            case 7:
                saveFirstPage(url);
                break;
            case 8:
                AlertDialog.Builder builder=new AlertDialog.Builder(t);
                final EditText editText = new EditText(t);
                editText.setHint("请输入新的名字");
                builder.setView(editText);

                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.update(url,editText.getText()+"","history");
                        adapter.setHistories(SqliteUtil.getInstance(t).query("history"));

                    }
                });
                builder.show();

                break;
            case  9:
                adapter.del(url,"history");
                adapter.setHistories(SqliteUtil.getInstance(t).query("history"));
                break;
        }
        return true;
    }

    public void deleteAll(View view) {
        AlertDialog.Builder builder =new AlertDialog.Builder(HistoryActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否删除所有历史记录？");
        builder.setPositiveButton("是的，我要删除！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SqliteUtil.getInstance(HistoryActivity.this).deleteAll();
                adapter.setHistories(SqliteUtil.getInstance(HistoryActivity.this).query("history"));
            }
        });
        builder.setNegativeButton("还是留着吧",null);
        builder.show();
    }

    class myAdapter extends BaseAdapter {
        List<History> histories;

        public myAdapter(List<History> list) {
            histories=list;

        }

        public List<History> getHistories() {
            return histories;
        }

        public void setHistories(List<History> histories) {
            this.histories = histories;
            notifyDataSetChanged();
        }

        public long insert(History history, String tableName){
            long l = SqliteUtil.getInstance(HistoryActivity.this).insert(history, tableName);
            notifyDataSetChanged();
            return l;
        }

        public int del(String url,String tableName){
            int i=SqliteUtil.getInstance(HistoryActivity.this).del(url,tableName);
            notifyDataSetChanged();
            return i;
        }
        public int update(String url,String newName,String tableName){
            int i= SqliteUtil.getInstance(HistoryActivity.this).update(url,newName,tableName);
            notifyDataSetChanged();
            return i;
        }
        @Override
        public int getCount() {
            return histories.size();
        }

        @Override
        public Object getItem(int position) {
            return histories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (null==convertView){
                viewHolder=new ViewHolder();
                convertView=getLayoutInflater().inflate(R.layout.bookmarks_item,null);
                viewHolder.tvTitle= (TextView) convertView.findViewById(R.id.tv_item_title);
                viewHolder.tvUrl= (TextView) convertView.findViewById(R.id.tv_item_url);
                viewHolder.tvDate= (TextView) convertView.findViewById(R.id.tv_item_date);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            final History history =histories.get(position);

            viewHolder.tvTitle.setText(history.getName());
            viewHolder.tvUrl.setText(history.getUrl());
            viewHolder.tvDate.setText(history.getDate());
            return convertView;
        }
        public class ViewHolder{
            TextView tvTitle;
            TextView tvUrl;
            TextView tvDate;
        }

    }

    public void saveFirstPage(String url){
        SharedPreferences info = getSharedPreferences("headPage", MODE_PRIVATE);
        SharedPreferences.Editor editor=info.edit();
        editor.putString("url",url);
        editor.apply();
    }
}
