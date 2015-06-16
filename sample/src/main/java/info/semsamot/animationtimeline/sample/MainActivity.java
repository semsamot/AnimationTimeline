package info.semsamot.animationtimeline.sample;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.semsamot.animationtimeline.library.TimelinedAnimatorSet;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.img_circle) ImageView imgCircle;
    @InjectView(R.id.skb_bottom) SeekBar skbBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final TimelinedAnimatorSet animatorSet = new TimelinedAnimatorSet();

        ObjectAnimator animAlpha = ObjectAnimator.ofFloat(imgCircle, "alpha", 1.0f, 0.0f);
        ObjectAnimator animX = ObjectAnimator.ofFloat(imgCircle, "translationX", 0, 300);
        ObjectAnimator animRot = ObjectAnimator.ofFloat(imgCircle, "rotation", 0, 300);
        ObjectAnimator animY = ObjectAnimator.ofFloat(imgCircle, "translationY", 0, 300);

        animatorSet.play(animAlpha).with(animY).after(animX).with(animRot);

        skbBottom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                animatorSet.seekTimeline((float) progress / 100.0f);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
