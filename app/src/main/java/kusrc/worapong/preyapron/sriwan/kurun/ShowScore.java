package kusrc.worapong.preyapron.sriwan.kurun;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowScore extends AppCompatActivity{
    //Explicit
    private TextView titleTextView, scoreTextView, detailTextView;
    private String titleString, scoreString, detailString;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);

        //bindWidget
        bindWidget();

        //Show View
        titleString = getIntent().getStringExtra("Base");
        scoreString = Integer.toString(getIntent().getIntExtra("score", 0));
        titleTextView.setText(scoreString + "/5");
        imageView.setImageResource(getIntent().getIntExtra("icon", R.drawable.base1));

        if (Integer.parseInt(scoreString) >= 3) {
            detailTextView.setText("ยินดีด้วยคุณได้ไปต่อ");

        } else {
            detailTextView.setText("ลองใหม่อีกครั้ง");
        }
    } //Main Method

    private void bindWidget() {
        titleTextView = (TextView) findViewById(R.id.textView12);
        scoreTextView = (TextView) findViewById(R.id.textView13);
        detailTextView = (TextView) findViewById(R.id.textView14);
        imageView = (ImageView) findViewById(R.id.imageView8);
    }

    public void clickNextShowScore(View view) {

    }   //ClickNext
}   //Main Class
