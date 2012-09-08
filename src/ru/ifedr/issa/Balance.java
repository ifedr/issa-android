package ru.ifedr.issa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Balance extends Activity {
	protected static final String KEY_BALANCE = "ru.ifedr.issa.key.balance";
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);
        String balance = getIntent().getStringExtra(KEY_BALANCE);
        TextView message = (TextView)findViewById(R.id.balance_content);
        message.setText(balance);
    }
}