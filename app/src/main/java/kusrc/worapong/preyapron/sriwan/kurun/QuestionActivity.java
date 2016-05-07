package kusrc.worapong.preyapron.sriwan.kurun;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    //Explicit
    private ImageView imageView;
    private TextView titleTextView, questionTextView;
    private RadioGroup radioGroup;
    private RadioButton choice1RadioButton, choice2RadioButton,
            choice3RadioButton, choice4RadioButton;
    private String titleString;
    private int iconAnInt;
    private String[] questionStrings, choice1Strings, choice2Strings,
            choice3Strings, choice4Strings, answerStrings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        //Bind Widget
        bindWidget();

        //Show View
        titleString = getIntent().getStringExtra("Base");
        iconAnInt = getIntent().getIntExtra("Icon", R.drawable.base1);
        titleTextView.setText(titleString);
        imageView.setImageResource(iconAnInt);

        ConnectedQuestionJSON connectedQuestionJSON = new ConnectedQuestionJSON();
        connectedQuestionJSON.execute();

    }   // Main Method

    public class ConnectedQuestionJSON extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://swiftcodingthai.com/keng/php_get_question.php").build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                return null;
            }
        } //doInback

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONArray jsonArray = new JSONArray(s);

                int intCount = jsonArray.length();
                questionStrings = new String[intCount];
                choice1Strings = new String[intCount];
                choice2Strings = new String[intCount];
                choice3Strings = new String[intCount];
                choice4Strings = new String[intCount];
                answerStrings = new String[intCount];

                for (int i = 0; i < intCount; i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    questionStrings[i] = jsonObject.getString("Question");
                    choice1Strings[i] = jsonObject.getString("Choice1");
                    choice2Strings[i] = jsonObject.getString("Choice2");
                    choice3Strings[i] = jsonObject.getString("Choice3");
                    choice4Strings[i] = jsonObject.getString("Choice4");
                    answerStrings[i] = jsonObject.getString("Answer");

                }//for
                changeView();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } //OnPost
    }//Connected Class

    private void changeView() {
        Random random = new Random();
        int intIndex = random.nextInt(20);

        Log.d("7MayV2","intIndex ==>" + intIndex);

        questionTextView.setText(questionStrings[intIndex]);
        choice1RadioButton.setText(choice1Strings[intIndex]);
        choice2RadioButton.setText(choice2Strings[intIndex]);
        choice3RadioButton.setText(choice3Strings[intIndex]);
        choice4RadioButton.setText(choice4Strings[intIndex]);

    }// changeView

    private void bindWidget() {

        imageView = (ImageView) findViewById(R.id.imageView7);
        titleTextView = (TextView) findViewById(R.id.textView11);
        questionTextView = (TextView) findViewById(R.id.textView10);
        radioGroup = (RadioGroup) findViewById(R.id.ragChoice);
        choice1RadioButton = (RadioButton) findViewById(R.id.radioButton6);
        choice2RadioButton = (RadioButton) findViewById(R.id.radioButton7);
        choice3RadioButton = (RadioButton) findViewById(R.id.radioButton8);
        choice4RadioButton = (RadioButton) findViewById(R.id.radioButton9);

    }   // bindWidget

    public void clickAnswer(View view) {
        if (checkChooseChoice()) {
            //not choose
            MyAlertDialog myAlertDialog = new MyAlertDialog();
            myAlertDialog.myDialog(this, "ยังไม่มีการเลือกคำตอบ",
                    "โปรดเลือกคำตอบด้วยค่ะ");

        } else {
            //have choose
            changeView();


        }   //if

    } //ClickAnswer

    private boolean checkChooseChoice() {

        boolean bolChoose = true; // some button not choose

        if (choice1RadioButton.isChecked() ||
                choice2RadioButton.isChecked()||
                choice3RadioButton.isChecked() ||
                choice4RadioButton.isChecked()) {
            bolChoose = false; //Have Choose
        }
        return bolChoose;
    }


}   // Main Class