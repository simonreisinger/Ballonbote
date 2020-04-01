package at.ac.tuwien.pointnerreisinger.ballonbote;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, View.OnClickListener, View.OnTouchListener {

    VideoView myVideoView;
    ImageButton skip;

    /**
     * Creates the Activity
     *
     * @param savedInstanceState Saved instance state
     * @author Simon Reisinger
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Fullscreen setzen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);


        //Layout setzen
        setContentView(R.layout.activity_video);

        //VideoView holen
        myVideoView = (VideoView) findViewById(R.id.videoView);
        skip = (ImageButton) findViewById(R.id.skipButton);

        //Video Uri setzen
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);

        myVideoView.setVideoURI(video);

        skip.setOnClickListener(this);
        skip.setOnTouchListener(this);

        //end of media listener
        myVideoView.setOnCompletionListener(this);

        //start the video
        myVideoView.start();

        //skipVideo();
    }

    /**
     * Skips the the next activity when the video ended
     *
     * @param mp MediaPlayer
     * @author Simon Reisinger
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        skipVideo();
    }

    /**
     * Click event of the skip button
     *
     * @param v View
     * @author Simon Reisinger
     */
    @Override
    public void onClick(View v) {
        skipVideo();
    }

    private void skipVideo() {
        startActivity(new Intent(this, MainActivity.class));

    }

    /**
     * Called on touch events
     *
     * @param v     View
     * @param event MotionEvent
     * @return touch event performed
     * @author Simon Reisinger
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            skip.setImageResource(R.drawable.skiponclick);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            skip.setImageResource(R.drawable.skip);
        }
        return false;
    }
}
