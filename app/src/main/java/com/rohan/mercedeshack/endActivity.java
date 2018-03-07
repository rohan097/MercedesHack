package com.rohan.mercedeshack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class endActivity extends Activity {

    private String cvv;
    private String cardNum;
    private String userName;
    private String exp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        userName = getIntent().getExtras().getString("userName");
        cvv = getIntent().getExtras().getString("cvv");
        cardNum = getIntent().getExtras().getString("cardNo");

        TextView farewellView = (TextView) findViewById(R.id.farewell);
        farewellView.setText("Bye "+userName.split(" ")[0].toString()+".");

        TextView creditNumberView = (TextView) findViewById(R.id.creditNumber);
        creditNumberView.setText(cardNum);


    }

    public void payNow(View view)
    {
        EditText cvvView = (EditText) findViewById(R.id.creditCVV);
        Toast toast = Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}
