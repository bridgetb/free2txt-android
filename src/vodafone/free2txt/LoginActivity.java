package vodafone.free2txt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private Button closeButton;
	private Button loginButton;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		this.closeButton = (Button)this.findViewById(R.id.cancelButton);
        this.closeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}          
        });
        this.closeButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				finish();
				return false;
			}          
        });
        
        this.loginButton = (Button)this.findViewById(R.id.loginButton);
        this.loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				System.out.println("Logging in");
				// Get input from username and password fields
				EditText username = (EditText)findViewById(R.id.username);
				EditText password = (EditText)findViewById(R.id.password);
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
				Editor e = sharedPref.edit();
				e.putString("username", username.getText().toString());
	        	e.putString("password", password.getText().toString());
	        	e.commit();     	
	        					
	        	// Login
	        	Intent myIntent = new Intent(v.getContext(), MainActivity.class);
	            v.getContext().startActivity(myIntent);
			}          
        });
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.findViewById(R.id.loginButton).getContext());
        if (sharedPref.contains("username") && sharedPref.contains("password")) {
        	//finish();
        	// Login
        	Intent myIntent = new Intent(this.findViewById(R.id.loginButton).getContext(), MainActivity.class);
        	this.findViewById(R.id.loginButton).getContext().startActivity(myIntent);
        }
	}
}
