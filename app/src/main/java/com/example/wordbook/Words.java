package com.example.wordbook;

import android.net.Uri;
import android.provider.BaseColumns;


public class Words {
    public static final String AUTHORITY = "cn.edu.bistuer.cs.wordsprovider";
    public Words() {
    }
    public static abstract class Word implements BaseColumns{
        public static final String TABLE_NAME="words";
        public static final String COLUMN_NAME_WORD="word";//列：单词
        public static final String COLUMN_NAME_MEANING="meaning";//列：单词含义
        public static final String COLUMN_NAME_SIMPLE="simple";//单词示例
        public static final String COLUMN_NAME_SMEANING="smeaning";//单词例句翻译
        //MIME类型
        public static final String MIME_DIR_PREFIX="vnd.android.cursor.dir";
        public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item";
        public static final String MINE_ITEM = "vnd.bistu.cs.se.word";
        public static final String MINE_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MINE_ITEM;
        public static final String MINE_TYPE_MULTIPLE = MIME_DIR_PREFIX + "/" + MINE_ITEM;
        public static final String PATH_SINGLE = "word/*";
        public static final String PATH_MULTIPLE = "word";
        public static final String CONTENT_URI_STRING = "content://" + AUTHORITY + "/" + PATH_MULTIPLE;
        public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);
    }
}
