/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.alex.balyschev.emanager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
implements OnClickListener {
	
	// emanager 
	private ExpenseManager emanager;
	
	// login UI elements
	private EditText etLogin;
	private EditText etPassword;
	private Button btnEnter;
	private Button btnNewUser;
	
	// new user UI elements
	private ScrollView scrlNewUser;
	private EditText etNewUserLogin;
	private EditText etNewUserPassword;
	private EditText etNewUserPasswordRep;
	private EditText etNewUserFirstname;
	private EditText etNewUserMiddlename;
	private EditText etNewUserSurname;
	private EditText etNewUserEmail;
	private TextView tvNewError;
	private Button btnNewUserSubmit;
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
				
		// Invoke a parent method
		super.onCreate(savedInstanceState);
		
		// fixed orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Load user interface
		setContentView(R.layout.lt_auth);
		
		// create emanager
		emanager = new ExpenseManager(this);
		
		// initialize UI components
		etLogin 	= (EditText) findViewById(R.id.etLogin);
		etPassword 	= (EditText) findViewById(R.id.etPassword);
		btnEnter 	= (Button) findViewById(R.id.btnEnter);
		btnNewUser	= (Button) findViewById(R.id.btnNewUser);
		
		scrlNewUser		= (ScrollView)findViewById(R.id.scrlNewUser);
		scrlNewUser.setVisibility(View.INVISIBLE);
		
		etNewUserLogin			= (EditText)findViewById(R.id.etNewUserLogin);
		etNewUserPassword		= (EditText)findViewById(R.id.etNewUserPassword);
		etNewUserPasswordRep	= (EditText)findViewById(R.id.etNewUserPasswordRep);
		etNewUserFirstname		= (EditText)findViewById(R.id.etNewUserFirstname);
		etNewUserMiddlename		= (EditText)findViewById(R.id.etNewUserMiddlename);
		etNewUserSurname		= (EditText)findViewById(R.id.etNewUserSurname);
		etNewUserEmail			= (EditText)findViewById(R.id.etNewUserEmail);
		tvNewError				= (TextView)findViewById(R.id.tvNewError);
		tvNewError.setVisibility(View.GONE);
		btnNewUserSubmit		= (Button)findViewById(R.id.btnNewUserSubmit);
		
		// add event listener
		btnEnter.setOnClickListener(this);
		btnNewUser.setOnClickListener(this);
		btnNewUserSubmit.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		Log.i("MAINACTIVITY", "trying to auth");
		switch(v.getId()) {
			
			case R.id.btnEnter:
				// enter to system
				final String login  	= etLogin.getText().toString();
				final String password 	= etPassword.getText().toString(); 
				
				if ( AppController.userExists(emanager, login, password) ) {
					// go to application
					doEntrance(login, PasswordCodec.getMD5(password));
				} else {
					// show error message
					Toast.makeText(this, "Error! No such user!", Toast.LENGTH_SHORT).show();
				}
				
				//doEntrance(login, PasswordCodec.getMD5(password));
				break;
			
			case R.id.btnNewUser:
				// show/hide create user layout
				int visibility = ( scrlNewUser.getVisibility() == View.INVISIBLE ) 
					? View.VISIBLE : View.INVISIBLE;
				scrlNewUser.setVisibility(visibility);
				break;
			
			case R.id.btnNewUserSubmit:
				// submit new user data
				StringBuilder error = new StringBuilder();
				
				String uLogin		= etNewUserLogin.getText().toString();
				String uPassword 	= etNewUserPassword.getText().toString(); 
				String uPasswordR 	= etNewUserPasswordRep.getText().toString();
				String uMd5Password = PasswordCodec.getMD5(uPassword);
				Log.i("NEWUSERSUBMIT", "login: " + uLogin + " password: " + uMd5Password);
				
				if ( ! uPassword.trim().toLowerCase().equals(uPasswordR.trim().toLowerCase()) ) {
					// password error
					error.append("Password don't matching!\n");
				}
				if ( uLogin.trim().equals("") || uPassword.trim().equals("") ) {
					error.append("Empty data has been entered!\n");
				}
				if ( AppController.userExists(emanager, uLogin, uMd5Password) ) {
					error.append("User with such login already exists!\n");
				}
				
				if ( error.length() > 0 ) {
					// print found errors
					AppController.printError(tvNewError, error.toString());
					break;
				}
				
				// no errors - create user
				long userId = emanager.getUniqueNumber(ExpenseManager.EMANAGER_TABLE_USERS);
				if ( userId > 0 ) {
					User user = new User(userId, uLogin, uMd5Password);
					emanager.addUser(user);
				}
				// go to application
				doEntrance(uLogin, uMd5Password);
				break;
		}
	}
	
	/**
	 * go to application 
	 */
	private void doEntrance(final String login, final String password) {
		// create intent
		Intent intent = new Intent(this, AppActivity.class);
		
		// put extra data and start new activity
		intent.putExtra("login", login);
		intent.putExtra("password", password);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
