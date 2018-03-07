package com.rohan.mercedeshack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class AuthenticationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final String userName = getIntent().getExtras().getString("userName");
        final String phNum = getIntent().getExtras().getString("phoneNum");
        final String cvv = getIntent().getExtras().getString("cvv");
        final String cardNum = getIntent().getExtras().getString("cardNo");
        setContentView(R.layout.activity_authentication);
        final TextView welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);
        welcomeMessage.setText("Welcome, "+userName.split(" ")[0].toString()+".");
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1500);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(2000);
        fadeOut.setDuration(2000);
        fadeOut.setFillAfter(true);
        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation){
            }
            @Override
            public void onAnimationRepeat(Animation animation){
            }
            @Override
            public void onAnimationEnd(Animation animation){
                welcomeMessage.setVisibility(View.GONE);
                Intent openHomePage = new Intent(AuthenticationActivity.this, HomePageActivity.class);
                openHomePage.putExtra("userName", userName);
                openHomePage.putExtra("cvv", cvv);
                openHomePage.putExtra("phNum", phNum);
                openHomePage.putExtra("cardNo", cardNum);
                setResult(Activity.RESULT_OK, openHomePage);
                finish();
                startActivityForResult(openHomePage, 200);

            }
        });
        welcomeMessage.setAnimation(animation);
    }
}
