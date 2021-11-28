package com.example.wordbook;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    WordsDBHelper mDbHelper;
    private static ListView listView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu, menu);
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        TextView textWord = null;
        TextView textMeaning = null;
        TextView textSimple = null;
        TextView textSmeaning = null;
        AdapterView.AdapterContextMenuInfo info = null;
        View itemView = null;
        switch (item.getItemId()) {
            case R.id.delete: {
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                textWord = (TextView) itemView.findViewById(R.id.word);
                if (textWord != null) {
                    String strWord = textWord.getText().toString();
                    DeleteDialog(strWord);
                }
                break;
            }
            case R.id.update: {
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                textWord = (TextView) itemView.findViewById(R.id.word);
                textMeaning = (TextView) itemView.findViewById(R.id.meaning);
                textSimple = (TextView) itemView.findViewById(R.id.simple);
                textSmeaning = (TextView) itemView.findViewById(R.id.smeaning);
                if (textWord != null && textMeaning != null && textSimple != null && textSmeaning != null) {
                    String strWord = textWord.getText().toString();
                    String strMeaning = textMeaning.getText().toString();
                    String strSimple = textSimple.getText().toString();
                    String strSmeaning = textSmeaning.getText().toString();
                    UpdateDialog(strWord, strMeaning, strSimple, strSmeaning);
                }
                break;
            }
            default:
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                InsertDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lv);
        registerForContextMenu(listView);
        mDbHelper = new WordsDBHelper(this);
        ArrayList<Map<String, String>> items = getAll();
        setWordsListView(items);
        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchWord = ((EditText) findViewById(R.id.searchWord)).getText().toString();
                setWordsListView(SearchSql(searchWord));
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    //插入对话框
    private void InsertDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        new AlertDialog.Builder(this)
                .setTitle("新增单词")
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strSimple = ((EditText) tableLayout.findViewById(R.id.txtSimple)).getText().toString();
                        String strSmeaning = ((EditText) tableLayout.findViewById(R.id.txtSmeaning)).getText().toString();
                        InsertUserSql(strWord, strMeaning, strSimple, strSmeaning);
                        ArrayList<Map<String, String>> items = getAll();
                        setWordsListView(items);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create().show();
    }

    //插入语句
    public void InsertUserSql(String strWord, String strMeaning, String strSimple, String strSmeaning) {
        if (SearchSql(strWord).isEmpty()) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Words.Word.COLUMN_NAME_WORD, strWord);
            values.put(Words.Word.COLUMN_NAME_MEANING, strMeaning);
            values.put(Words.Word.COLUMN_NAME_SIMPLE, strSimple);
            values.put(Words.Word.COLUMN_NAME_SMEANING, strSmeaning);
            db.insert(Words.Word.TABLE_NAME, null, values);
        }else{
            Toast.makeText(this,"单词已存在",Toast.LENGTH_LONG).show();
        }
    }

    public void setWordsListView(ArrayList<Map<String, String>> items) {
        SimpleAdapter adapter = new SimpleAdapter(this, items,
                R.layout.listview_item, new String[]{Words.Word.COLUMN_NAME_WORD, Words.Word.COLUMN_NAME_MEANING,
                Words.Word.COLUMN_NAME_SIMPLE, Words.Word.COLUMN_NAME_SMEANING}, new int[]{R.id.word,
                R.id.meaning, R.id.simple, R.id.smeaning});
        listView.setAdapter(adapter);
    }

    //删除对话框
    private void DeleteDialog(String strWord) {
        new AlertDialog.Builder(this).setTitle("删除单词").setMessage("是否真的删除单词？").
                setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteSql(strWord);
                        setWordsListView(getAll());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }

    //删除语句
    public void DeleteSql(String strWord) {
        String sql = "delete from words where word='" + strWord + "'";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql);
    }

    public ArrayList<Map<String, String>> getAll() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(Words.Word.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        int wordInt = cursor.getColumnIndex(Words.Word.COLUMN_NAME_WORD);
        int meaningInt = cursor.getColumnIndex(Words.Word.COLUMN_NAME_MEANING);
        int simpleInt = cursor.getColumnIndex(Words.Word.COLUMN_NAME_SIMPLE);
        int smeaningInt = cursor.getColumnIndex(Words.Word.COLUMN_NAME_SMEANING);
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<String, String>();
            String word = cursor.getString(wordInt);
            String meaning = cursor.getString(meaningInt);
            String simple = cursor.getString(simpleInt);
            String smeaning = cursor.getString(smeaningInt);
            map.put(Words.Word.COLUMN_NAME_WORD, word);
            map.put(Words.Word.COLUMN_NAME_MEANING, meaning);
            map.put(Words.Word.COLUMN_NAME_SIMPLE, simple);
            map.put(Words.Word.COLUMN_NAME_SMEANING, smeaning);
            list.add(map);
        }
        cursor.close();
        return list;
    }

    //修改对话框
    private void UpdateDialog(String strWord, String strMeaning, String strSimple, String strSmeaning) {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        ((EditText) tableLayout.findViewById(R.id.txtWord)).setText(strWord);
        ((EditText) tableLayout.findViewById(R.id.txtMeaning)).setText(strMeaning);
        ((EditText) tableLayout.findViewById(R.id.txtSimple)).setText(strSimple);
        ((EditText) tableLayout.findViewById(R.id.txtSmeaning)).setText(strSmeaning);
        new AlertDialog.Builder(this).setTitle("修改单词").setView(tableLayout)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strNewWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strNewMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strNewSimple = ((EditText) tableLayout.findViewById(R.id.txtSimple)).getText().toString();
                        String strNewSmeaning = ((EditText) tableLayout.findViewById(R.id.txtSmeaning)).getText().toString();
                        UpdateSql(strNewWord, strNewMeaning, strNewSimple, strNewSmeaning, strWord);
                        setWordsListView(getAll());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }

    private void UpdateSql(String strWord, String strMeaning, String strSimple, String strSmeaning, String oldWord) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "update words set word=?,meaning=?,simple=?,smeaning=? where word=?";
        db.execSQL(sql, new String[]{strWord, strMeaning, strSimple, strSmeaning, oldWord});
    }

    //查询语句
    public ArrayList<Map<String, String>> SearchSql(String searchWord) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (searchWord.equals("")) {
            Toast.makeText(this, "请输入单词", Toast.LENGTH_LONG).show();
            return getAll();
        } else {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String sql = "select * from words where word like ? order by word desc";
            Cursor cursor = db.rawQuery(sql, new String[]{"%" + searchWord + "%"});
            int wordInt = cursor.getColumnIndex(Words.Word.COLUMN_NAME_WORD);
            int meaningInt = cursor.getColumnIndex(Words.Word.COLUMN_NAME_MEANING);
            int simpleInt = cursor.getColumnIndex(Words.Word.COLUMN_NAME_SIMPLE);
            int smeaningInt = cursor.getColumnIndex(Words.Word.COLUMN_NAME_SMEANING);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                String word = cursor.getString(wordInt);
                String meaning = cursor.getString(meaningInt);
                String simple = cursor.getString(simpleInt);
                String smeaning = cursor.getString(smeaningInt);
                map.put(Words.Word.COLUMN_NAME_WORD, word);
                map.put(Words.Word.COLUMN_NAME_MEANING, meaning);
                map.put(Words.Word.COLUMN_NAME_SIMPLE, simple);
                map.put(Words.Word.COLUMN_NAME_SMEANING, smeaning);
                list.add(map);
            }
            cursor.close();
            return list;
        }
    }


}