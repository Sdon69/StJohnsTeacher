package com.theironfoundry8890.stjohnsteacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class intro extends AppCompatActivity {

    private int a = 0;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wr);

        swipe();
    }



    private void swipe() {

        TextView head2 = (TextView) findViewById(R.id.head2);
        TextView head1 = (TextView) findViewById(R.id.head);

        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);



        if(a==0){
            head1.setText("Instant Attendance");
            head2.setText("Get Attendance using \n the instructor guided code (IGC)");

            button1.setBackgroundResource(R.drawable.grey_button);
            button2.setBackgroundResource(R.drawable.blue_button);
            button3.setBackgroundResource(R.drawable.blue_button);


        }


        if(a==1) {
            head1.setText("Onetime Signup");
            head2.setText("Easy and Quick Signup \n with Unique ID");
            button1.setBackgroundResource(R.drawable.blue_button);
            button2.setBackgroundResource(R.drawable.grey_button);
            button3.setBackgroundResource(R.drawable.blue_button);


        }

        if(a==2) {
            head1.setText("Innovation");
            head2.setText("New Update every Month on \n  basis of Your Feedback");

            button1.setBackgroundResource(R.drawable.blue_button);
            button2.setBackgroundResource(R.drawable.blue_button);
            button3.setBackgroundResource(R.drawable.grey_button);
        }

        if(a==3) {
            Intent selectIntent = new Intent(intro.this,t_Signin.class);
            startActivity(selectIntent);
        }









    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        if(a!=0  && a!=3) { //previous
                            a--;
                            swipe();

                            Toast.makeText(this, Integer.toString(a) , Toast.LENGTH_SHORT).show();
                        }
                    }

                    // Right to left swipe action
                    else
                    {
                        if(a!=3) {  //next
                            a++;
                            swipe();
                            Toast.makeText(this, Integer.toString(a), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    public void onClickButton0(View v) {

      a=0;
        swipe();


    }

    public void onClickButton1(View v) {

        a=1;
        swipe();


    }

    public void onClickButton2(View v) {

        a=2;
        swipe();


    }


}
