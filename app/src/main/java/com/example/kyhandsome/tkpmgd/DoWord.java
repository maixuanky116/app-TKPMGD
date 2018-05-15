package com.example.kyhandsome.tkpmgd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyhandsome.tkpmgd.Adapter.WordAdapter;
import com.example.kyhandsome.tkpmgd.Database.Database;
import com.example.kyhandsome.tkpmgd.Model.Word;

import java.util.ArrayList;
import java.util.Locale;

public class DoWord extends AppCompatActivity{
    private static final String TAG = "DoWord";
    SharedPreferences sp;
    final String DATABASE_NAME = "DBtkpm.sqlite";
    SQLiteDatabase database;
    ArrayList<Word> list = new ArrayList<Word>();
    WordAdapter adapter;
    public Context context;
    android.support.v7.widget.Toolbar toolbar;
    TextView textView;
    TextToSpeech toSpeech;
    int result;
    ListView listView;
    ImageButton micro;
    TextView tv1;
    View layout;
    TextView tv2;
    TextView tv3;
    ImageView btnnext;
    ImageView nextword;
    int correct = 0;
    int wrong = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_screen);
        sp = DoWord.this.getSharedPreferences("saveSound", Context.MODE_PRIVATE);
        addControls();
        readData();
        getSpeechInput();

        textView = findViewById(R.id.word);
        toolbar = findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Phát âm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = toSpeech.setLanguage(Locale.US);
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    public void getSpeechInput() {
        micro = findViewById(R.id.micro);
        micro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(DoWord.this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                }
            }
        });
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tv1 = findViewById(R.id.word);
        tv2 = findViewById(R.id.star);
        tv3 = findViewById(R.id.textnotification);
        nextword = findViewById(R.id.nextword);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String res = result.get(0);
                    nextword.setVisibility(View.VISIBLE);

                    Log.d(TAG, "lenghtttttttttt " + res.length());
                    Log.d(TAG, "lenghtttttttttt " + tv1.getText().toString());
                    Log.d(TAG, "lenghtttttttttt2222222222222 " + tv1.getText().toString().length());
                    if (res.length() != tv1.getText().toString().length()) {
                        tv3.setText("TRY AGAIN");
                        tv3.setTextSize(30);
                        tv3.setTextColor(getResources().getColor(R.color.red));
                        tv2.setText(result.get(0));
                        tv2.setTextColor(getResources().getColor(R.color.red));
                        wrong++;
                    } else {
                        char[] rescharArray = res.toCharArray();
                        char[] wordcharArray = tv1.getText().toString().toCharArray();

                        int temp = 0;
                        for (int i = 0; i < rescharArray.length; i++) {
                            if (wordcharArray[i] == rescharArray[i]) {
                                temp++;
                            }
                        }
                        if (temp == rescharArray.length) {
                            tv2.setText(result.get(0));
                            tv2.setTextColor(getResources().getColor(R.color.green));
                            tv3.setText("EXCELLENT");
                            tv3.setTextSize(30);
                            tv3.setTextColor(getResources().getColor(R.color.green));
                            correct++;

                        } else {
                            tv2.setText(result.get(0));
                            tv2.setTextColor(getResources().getColor(R.color.red));
                            tv3.setText("TRY AGAIN");
                            tv3.setTextSize(30);
                            tv3.setTextColor(getResources().getColor(R.color.red));
                            wrong++;
                        }
                    }


                }
                break;
        }
        Log.d(TAG, "wrong " + wrong);
        Log.d(TAG, "correct " + correct);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("correct", correct);
        editor.putInt("wrong", wrong);
        editor.apply();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ex, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.itemprofile:
                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemhistory:
                Intent intent = new Intent(DoWord.this, History.class);
                startActivity(intent);
                return true;
            case R.id.itemlogout:
                Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemsearch:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemshare:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addControls() {
        listView = (ListView) findViewById(R.id.listdoword);
        listView.getCount();
        list = new ArrayList<>();
        adapter = new WordAdapter(DoWord.this, list);
        listView.setAdapter(adapter);
    }
    // Hàm đọc dữ liệu
    private void readData() {
        int idSound = sp.getInt("id_sound", 1);
        database = Database.initDatabase(this, DATABASE_NAME);
        Cursor cursor = database.rawQuery("Select * from word where word.id_sound = '" + idSound +"'" , null);
        list.clear();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int id_sound = cursor.getInt(2);
            String mean = cursor.getString(3);
            String decription = cursor.getString(4);
            int complete = cursor.getInt(5);

            list.add(new Word(id, name, id_sound, mean, decription, complete));

        }

//        adapter.notifyDataSetChanged();
        cursor.close();
        database.close();
    }
}
