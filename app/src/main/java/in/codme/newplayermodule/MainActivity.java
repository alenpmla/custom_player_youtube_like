package in.codme.newplayermodule;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.google.android.gms.cast.framework.CastButtonFactory;

public class MainActivity extends AppCompatActivity   {
    private static final String TAG ="MainActivity" ;
    VideoView videoView;
    MediaController mController;
    ImageView play_pause;
    SeekBar seekbar;
    Boolean byuser=false;
int savepos=0;
    Boolean completed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        videoView=(VideoView) findViewById(R.id.videoview);
        play_pause=(ImageView)findViewById(R.id.play_pause);
        seekbar=(SeekBar)findViewById(R.id.seekbar);
        seekbar.setVisibility(View.GONE);
        byuser=false;
        mController = new MediaController(this){
            @Override
            public void hide() {
                super.hide();
                seekbar.setVisibility(View.GONE);
                play_pause.setVisibility(View.INVISIBLE);
                getSupportActionBar().hide();

            }



            @Override
            public void show() {
                super.show();
                seekbar.setVisibility(View.VISIBLE);
                play_pause.setVisibility(View.VISIBLE);
                if(videoView.isPlaying()){
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                }
                else{
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                }
                getSupportActionBar().show();
            }
        };
        mController.setAlpha(0);
        mController.setClickable(false);

        videoView.setMediaController(mController);
        Uri uri= Uri.parse("http://www.html5videoplayer.net/videos/toystory.mp4");
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG,"onPrepared ");
                videoView.seekTo(savepos);
                videoView.start();

                seekbar.setMax(videoView.getDuration());
                seekbar.postDelayed(onEverySecond, 1000);

            }
        });

        try{
            savepos=savedInstanceState.getInt("pos");
            Log.d(TAG,"Oncreate read position is "+savepos);
            videoView.seekTo(savepos);
            videoView.start();
            Log.d(TAG,"Started");
        }
        catch (NullPointerException n){
            n.printStackTrace();
        }

        play_pause.setVisibility(View.INVISIBLE);


        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoView.isPlaying()){
                    videoView.pause();
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                }
                else{
                    videoView.start();
                    seekbar.postDelayed(onEverySecond, 1000);
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                }

            }
        });

        seekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                byuser=true;
                return false;
            }
        });

   seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                boolean prevstate=videoView.isPlaying();
                if(byuser){
                    videoView.seekTo(i);
                }




            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                return false;
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {

                seekbar.setProgress(mediaPlayer.getCurrentPosition());
                return false;

            }
        });
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.player_menu, menu);
        CastButtonFactory.setUpMediaRouteButton(menu, R.id.media_route_menu_item);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private Runnable onEverySecond=new Runnable() {

        @Override
        public void run() {

            byuser=false;
            if(seekbar != null) {
                seekbar.setProgress(videoView.getCurrentPosition());
            }

            if(videoView.isPlaying()) {
                seekbar.postDelayed(onEverySecond, 1000);
            }

        }


    };
    @Override
    protected void onSaveInstanceState (Bundle outState){

        Log.d(TAG,"Current position is "+videoView.getCurrentPosition());
        outState.putInt("pos", videoView.getCurrentPosition()); // save it here
    }

}
