package com.theironfoundry8890.stjohnsteacher;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class Random extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wr);
        new OnSwipeTouchListener2(Random.this);



    }

    public class OnSwipeTouchListener2 implements OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener2 (Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
            Toast.makeText(getApplicationContext(), "Your toast message.", Toast.LENGTH_SHORT).show();

        }

        public void onSwipeLeft() {
            Log.v("MainActivity" , "Wofdad");
            Toast.makeText(getApplicationContext(), "Your toast message.", Toast.LENGTH_SHORT).show();
        }

        public void onSwipeTop() {
            Log.v("MainActivity" , "Wofdad");
            Toast.makeText(getApplicationContext(), "Your toast message.", Toast.LENGTH_SHORT).show();
        }

        public void onSwipeBottom() {
            Log.v("MainActivity" , "Wofdad");
            Toast.makeText(getApplicationContext(), "Your toast message.", Toast.LENGTH_SHORT).show();
        }
    }


}
