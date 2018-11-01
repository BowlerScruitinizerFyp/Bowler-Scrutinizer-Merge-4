package application.scrutinizer.bowler.bowlerscrutinizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.overridePendingTransition(R.anim.left,
                R.anim.right);
        this.overridePendingTransition(R.anim.right,
                R.anim.left);


        final ImageView iv = (ImageView) findViewById(R.id.imageView);
        final ImageView iv2 = (ImageView) findViewById(R.id.bowler);
        final ImageView iv3 = (ImageView) findViewById(R.id.scrutinizer);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation an3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.left);
        final Animation an4 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.right);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);


        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv3.startAnimation(an3);
                iv2.startAnimation(an3);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.startAnimation(an2);

                finish();
                Intent i = new Intent(getBaseContext(), SliderActivity.class);
                startActivity(i);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
