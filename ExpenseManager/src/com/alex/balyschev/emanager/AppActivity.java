/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.alex.balyschev.emanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alex.balyschev.emanager.Record.Operation;

public class AppActivity extends Activity
implements OnTabChangeListener {
	
	private TabHost tabhost;
	private ListView lvList;
	private ExpandableListView elvAccounts;
	private SimpleExpandableListAdapter adapter;
	private AlertDialog.Builder builderRemove;
	private AlertDialog dlgRemove;
	private DialogInterface.OnClickListener clickListenerRecordDlg;
	private DialogInterface.OnClickListener clickListenerAccountDlg;
	
	// current group and child positions (for removal) 
	private int currentGroupPos = -1;
	private int currentChildPos = -1;
	
	// user data
	private String userLogin;
	private String userPassword;
	private User currentUser;
	
	// user login
	private TextView tvInfoLogin;
	// out of program, change user button
	private Button btnInfoChange;
	private Button btnInfoExit;
	
	// change user dialog
	private AlertDialog.Builder builderChange;
	private AlertDialog dlgChange;
	private DialogInterface.OnClickListener clickListenerChangeDlg;
	private LinearLayout llLogin;
	private EditText etLogin;
	private EditText etPassword;
	
	// expandable list view widget
	private ExpenseManager emanager; 
	// group data
	private ArrayList<Map<String, String>> groupData;
	// child data item container
	private ArrayList<ArrayList<Map<String, String>>> childData;
	// group and child data tags
	private enum AdapterDataTag {
		CHILD, GROUP
	}
	
	// add tab content
	private LinearLayout llAccount;
	private LinearLayout llRecord;
	private LinearLayout llNew;
	
	// new account elements
	private EditText etNewAccountBalance;
	private EditText etNewAccountDesc;
	
	// new record elements
	private EditText etNewRecDesc;
	private EditText etNewRecAmount;
	private ToggleButton tbtnNewRecOp;
	private Spinner spnNewRecAccount;
	private Spinner spnNewRecCategory;
	
	// error line
	private TextView tvNewError;
	
	// common elements
	private Button btnNewCreate;
	private RadioGroup rdNewGroup;
	private RadioButton rdNewAccount;
	private RadioButton rdNewRecord;
	
	// spinner data containers
	ArrayList<Map<String, Object>> categoryItems	= new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> accountItems 	= new ArrayList<Map<String, Object>>();
	
	// spinner data lists
	ArrayList<String> accList = new ArrayList<String>();
	ArrayList<String> catList = new ArrayList<String>();
	
	// spinner adapters
	ArrayAdapter<String> categoryAdapter;
	ArrayAdapter<String> accountAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
				
		// Invoke a parent method
		super.onCreate(savedInstanceState);
		
		// fixed orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Load user interface
		setContentView(R.layout.lt_app);
		
		// expense manager
		emanager = new ExpenseManager(this);
		
		// get intent data
		userLogin 		= getIntent().getExtras().getString("login");
		userPassword 	= getIntent().getExtras().getString("password");
		Log.i("INTENT DATA", userLogin + ":" + userPassword);
			
		// @TODO take off this stub
		//userLogin 	= "AlexDiaz";
		//userPassword 	= PasswordCodec.getMD5("liberty");
		
		// get current user 
		currentUser = emanager.getUser(userLogin);
				
		// load data for expense manager
		// emanager.loadTestData();
		
		// Initialize UI components
		//lvList = (ListView) findViewById(R.id.lvList);
		tvInfoLogin		= (TextView)findViewById(R.id.tvInfoLogin);
		tvInfoLogin.setText(userLogin);
		btnInfoChange 	= (Button)findViewById(R.id.btnInfoChange);
		btnInfoExit		= (Button)findViewById(R.id.btnInfoExit);
		llLogin 		= (LinearLayout) getLayoutInflater().inflate(R.layout.lt_login, null);
		
		btnInfoChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// show dialog
				dlgChange.show();
			}
		});
		
		builderChange = new AlertDialog.Builder(this);
		builderChange.setTitle("User change");
		builderChange.setMessage("");
		builderChange.setView(llLogin);
		
		// click listener
		clickListenerChangeDlg = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
					case Dialog.BUTTON_POSITIVE:
						// change user
						etLogin 	= (EditText)AppActivity.this.llLogin.findViewById(R.id.etLogin);
						etPassword	= (EditText)AppActivity.this.llLogin.findViewById(R.id.etPassword);
						String login = etLogin.getText().toString();
						String pswrd = etPassword.getText().toString();
						String md5Password = PasswordCodec.getMD5(pswrd);
						Log.i("CHANGEUSER", "login: " + login + " password: " + pswrd);
						
						if ( AppController.userExists(AppActivity.this.emanager, login, md5Password) ) {
							// enter with other user
							Intent intent = new Intent(AppActivity.this, AppActivity.class);
							
							// put extra data and start new activity
							intent.putExtra("login", login);
							intent.putExtra("password", md5Password);
							startActivity(intent);
							
						} else {
							Toast.makeText(AppActivity.this, "Error! Nu such user!", Toast.LENGTH_SHORT).show();
						}	
						break;
						
					case Dialog.BUTTON_NEGATIVE:
						// out of dialog
						break;
				}
				dialog.dismiss();
			}
		};
		
		builderChange.setPositiveButton(android.R.string.ok, clickListenerChangeDlg);
		builderChange.setNegativeButton(android.R.string.cancel, clickListenerChangeDlg);
		dlgChange = builderChange.create();
		
		btnInfoExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// go out of program
				AppActivity.this.onBackPressed();
			}
		});
		
		// expandable list view with account and records
		elvAccounts = (ExpandableListView) findViewById(R.id.elvAccounts);
		elvAccounts.setVisibility(View.VISIBLE);
		// group click
		elvAccounts.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				if ( childData.size() <= groupPosition || childData.isEmpty() ) {
					Log.i("ONGROUPCLICK", "empty child");
					Toast.makeText(AppActivity.this, "No records in account...", Toast.LENGTH_SHORT).show();
					return true;
				}
				if ( childData.get(groupPosition) == null || childData.get(groupPosition).isEmpty() ) {
					// child is not empty - resolve handle
					Log.i("ONGROUPCLICK", "empty child");
					Toast.makeText(AppActivity.this, "No records in account...", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});
		
		// group long click
		elvAccounts.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
	        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
	                // Your code with group long click 
	            	Log.i("ONITEMLONGCLICK", "group pos: " + position);
	            	
	            	// set dialog listener
					builderRemove.setPositiveButton(android.R.string.ok, clickListenerAccountDlg);
					builderRemove.setNegativeButton(android.R.string.cancel, clickListenerAccountDlg);
					builderRemove.setTitle("Remove account");
					builderRemove.setMessage("Do you really want to remove this account?");
					dlgRemove = builderRemove.create();	
					
					// save group position
					AppActivity.this.currentGroupPos = position;
					
					// show dialog;
					dlgRemove.show();
					
	            	//Toast.makeText(AppActivity.this, "Long click on group pos: " + position + "...", Toast.LENGTH_SHORT).show();
	            	return true;
	            }
	            return false;
	        }
		});
		
		// child click
		elvAccounts.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// set dialog listener
				builderRemove.setPositiveButton(android.R.string.ok, clickListenerRecordDlg);
				builderRemove.setNegativeButton(android.R.string.cancel, clickListenerRecordDlg);
				builderRemove.setTitle("Remove record");
				builderRemove.setMessage("Do you really want to remove this record?");
				dlgRemove = builderRemove.create();	
				
				// save positions
				AppActivity.this.currentChildPos = childPosition;
				AppActivity.this.currentGroupPos = groupPosition;
				// show confirm dialog
				dlgRemove.show();
				return false;
			}
		});
		
		// dialog parameters
		builderRemove = new AlertDialog.Builder(this);
		builderRemove.setCancelable(true);

		// record click listener
		clickListenerRecordDlg = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
					case Dialog.BUTTON_POSITIVE:
						// delete record
						int groupPos = AppActivity.this.currentGroupPos;
						int childPos = AppActivity.this.currentChildPos;
						String msg = "Delete record...";
						AppActivity.this.deleteRecord(groupPos, childPos);
						Toast.makeText(AppActivity.this, msg, Toast.LENGTH_SHORT).show();
						break;
						
					case Dialog.BUTTON_NEGATIVE:
						// out from dialog
						break;
				}
				dialog.dismiss();
			}
		};
		// account click listener
		clickListenerAccountDlg = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
					case Dialog.BUTTON_POSITIVE:
						// delete account
						int groupPos = AppActivity.this.currentGroupPos;
						String msg = "Delete account...";
						AppActivity.this.deleteAccount(groupPos);
						Toast.makeText(AppActivity.this, msg, Toast.LENGTH_SHORT).show();
						break;
						
					case Dialog.BUTTON_NEGATIVE:
						// out from dialog
						break;
				}
				dialog.dismiss();
			}
		};
		
		// add tab elements
		llAccount	= (LinearLayout) findViewById(R.id.ltNewAccount);
		llRecord 	= (LinearLayout) findViewById(R.id.ltNewRecord);
		llNew		= (LinearLayout) findViewById(R.id.ltNew);
		llRecord.setVisibility(View.GONE);
		llNew.setVisibility(View.GONE);
		
		// new account
		etNewAccountBalance	= (EditText) findViewById(R.id.etNewAccountBalance);
		etNewAccountDesc 	= (EditText) findViewById(R.id.etNewAccountDesc);
		
		// new record
		etNewRecDesc 		= (EditText) findViewById(R.id.etNewRecDesc);
		etNewRecAmount		= (EditText) findViewById(R.id.etNewRecAmount);
		tbtnNewRecOp		= (ToggleButton) findViewById(R.id.tbtnNewRecOp);
		spnNewRecAccount	= (Spinner) findViewById(R.id.spnNewRecAccounts);
		spnNewRecCategory	= (Spinner) findViewById(R.id.spnNewRecCategory);
		
		// error
		tvNewError			= (TextView) findViewById(R.id.tvNewError);
		
		// create and load spinners data
		loadSpinnerData();
				
		// create and set spinner adapters
		categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catList);
		accountAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accList);
		
		spnNewRecAccount.setAdapter(accountAdapter);
		spnNewRecCategory.setAdapter(categoryAdapter);
		
		// common elements
		btnNewCreate	= (Button) findViewById(R.id.btnNewCreate);
		rdNewGroup 		= (RadioGroup) findViewById(R.id.rdNewGroup);
		rdNewAccount	= (RadioButton) findViewById(R.id.rdNewAccount);
		rdNewRecord		= (RadioButton) findViewById(R.id.rdNewRecord);
		
		rdNewGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
					case R.id.rdNewAccount:
						llAccount.setVisibility(View.VISIBLE);
						llRecord.setVisibility(View.GONE);
						Toast.makeText(AppActivity.this, "Account", Toast.LENGTH_SHORT).show();
						break;
	
					case R.id.rdNewRecord:
						llRecord.setVisibility(View.VISIBLE);
						llAccount.setVisibility(View.GONE);
						Toast.makeText(AppActivity.this, "Record", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});
		
		btnNewCreate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				// get checked radio id
				int checkedId = rdNewGroup.getCheckedRadioButtonId();
				// error buffer
				StringBuilder errors = new StringBuilder();
				
				switch (checkedId) {
					case R.id.rdNewAccount:
						Log.i("CREATE NEW", "create account");
						// get parameters
						String paramAccountBalance = etNewAccountBalance.getText().toString(); 
						double balance = 0.0;
						
						// validate input data...
						if ( ! paramAccountBalance.trim().equals("") ) {
							balance = Double.valueOf(paramAccountBalance);
						} else {
							errors.append("Error! Input account balance...\n");
						}
						String paramAccountDesc	= etNewAccountDesc.getText().toString();
						if ( paramAccountDesc.trim().equals("") ) {
							errors.append("Error! Input any description...\n");
						}
						
						if ( errors.length() <= 0 ) {
							// hide error line
							tvNewError.setVisibility(View.INVISIBLE);
							
							// no errors - create account
							long accId = emanager.getUniqueNumber(ExpenseManager.EMANAGER_TABLE_ACCOUNTS);;
							
							// create account
							Account newAccount = new Account(userLogin, accId, Math.abs(balance), paramAccountDesc);
							
							// add account
							emanager.addAccount(currentUser, newAccount);
							
							// update group  in adapter
							AppActivity.this.updateAdapter();
							
							// show message
							Toast.makeText(AppActivity.this, "New account(" + accId + ") has been inserted...", Toast.LENGTH_LONG).show();
						} else {
							// we've got errors - print them
							AppController.printError(tvNewError, errors.toString());
						}
						
						break;
					case R.id.rdNewRecord:
						Log.i("CREATE NEW", "create record");
						// get params
						String paramRecAccount	= (String)spnNewRecAccount.getSelectedItem();
						String paramRecAmount 	= etNewRecAmount.getText().toString();
						String paramRecOp 		= tbtnNewRecOp.getText().toString();
						String paramRecCategory = (String)spnNewRecCategory.getSelectedItem();
						String paramRecDesc 	= (String)etNewRecDesc.getText().toString();
						
						// get account id
						long paramRecAccountId	= -1;
						int accountPos = spnNewRecAccount.getSelectedItemPosition();
						if ( accountPos >= 0 ) {
							Map<String, Object> accountMap = accountItems.get(accountPos);
							paramRecAccountId = ((Long)accountMap.get("id")).longValue();
						} else {
							errors.append("Error! Impossible create record without account...\n");
						}
						Log.i("RECORD INFO", "id: " + paramRecAccountId);
						
						// find account by id
						Account account = AppActivity.this.findAccountById(paramRecAccountId);
						if ( account != null ) {
							// we've got account - create record
							long recordId = emanager.getUniqueNumber(ExpenseManager.EMANAGER_TABLE_RECORDS);
							
							Operation operation = ( paramRecOp.equals("+") ) 
									? Operation.RECHARGE : Operation.WITHDRAW;

							double amount = 0.0;
							if ( ! paramRecAmount.trim().equals("") ) {
								amount = Double.valueOf(paramRecAmount);
							} else {
								errors.append("Error! Input record amount...\n");
							}
							if ( operation == Operation.WITHDRAW ) {
								if ( account.getBalance() < amount ) {
									errors.append("Error! There is not means on the account...\n");
								}
							}
							
							// create category from spinner data
							long catId = -1;
							Category recordCategory = null;
							int categoryPos = spnNewRecCategory.getSelectedItemPosition();
							if ( categoryPos >= 0 ) {
								// category exists
								Map<String, Object> categoryMap = categoryItems.get(categoryPos);
								catId = ((Long)categoryMap.get("id")).longValue();
								recordCategory = AppController.getCategoryById(emanager, catId);
							}
							
							if ( errors.length() <= 0 ) {
								// no errors - create record
								tvNewError.setVisibility(View.INVISIBLE);
								
								Record record = new Record(
										recordId, 
										operation,
										Math.abs(amount),
										new Date(),
										recordCategory,
										paramRecDesc);
								// add record to manager
								emanager.addRecord(account, record); 
								// update data child in adapter
								AppActivity.this.updateAdapter();
								
								// show message
								Toast.makeText(AppActivity.this, "New record(" + recordId + ") has been inserted...", Toast.LENGTH_LONG).show();
							} else {
								AppController.printError(tvNewError, errors.toString());
							}				
						}
						break;
				}
			}
		});

		// data containers
		groupData		= new ArrayList<Map<String, String>>();
		childData 		= new ArrayList<ArrayList<Map<String, String>>>();
		
		// создание вкладки
		tabhost = (TabHost) this.findViewById(android.R.id.tabhost);
		
		// инициализация 
		tabhost.setup();
		
		TabHost.TabSpec tabSpec;
        
        // создаем вкладку и указываем тег
        tabSpec = tabhost.newTabSpec("tab1");
        
        // название вкладки        
        tabSpec.setIndicator(getString(R.string.tab_header1));
        
        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(R.id.textViewTab1);
        
        // добавляем в корневой элемент
        tabhost.addTab(tabSpec);
        
        tabSpec = tabhost.newTabSpec("tab2");
        
        // указываем название и картинку
        // в нашем случае вместо картинки идет xml-файл, 
        // который определяет картинку по состоянию вкладки
        tabSpec.setIndicator(getString(R.string.tab_header2), getResources().getDrawable(R.drawable.tab_icon_selector));
        tabSpec.setContent(R.id.textViewTab2);        
        tabhost.addTab(tabSpec);
        
        // вторая вкладка будет выбрана по умолчанию
        tabhost.setCurrentTabByTag("tab1");
        
        // обработчик переключения вкладок
        tabhost.setOnTabChangedListener(this);
        
        // создадим содержимое вкладок
        createAccountTree();
	}
	
	@Override
	public void onBackPressed() {
		finish();
		System.exit(0);
	}
	
	@Override
	public void onTabChanged(String tabId)
	{	
		if (tabId == "tab1") 
		{// дерeво счетов и записей
			showAccountTree();
		}else if (tabId == "tab2")
		{// создание новой записи
			showNew();
		}
	}
	
	/**
	 * find account
	 */
	public Account findAccountById(final long id) {
		Account account = null;
		for ( Account acc : emanager.getAccounts(currentUser) ) {
			// trying find account by selected id
			Log.i("ACCOUNT SEARCH", "param: " + id + " acc: " + acc.getId() );
			if ( acc.getId() == id ) {
				account = acc;
			}
		}
		return account;
	}
	
	/**
	 * delete account
	 */
	public void deleteAccount(final int groupPos) {
		Log.i("APPACTIVITY", "delete account pos: " + groupPos);
		
		if ( groupData.size() > groupPos ) {
			// find account
			Map<String, String> mgroup = groupData.get(groupPos);
			long accountId = Long.valueOf(mgroup.get("number")).longValue();
			Log.i("DELETEACCOUNT", "accountid: " + accountId);
			Account account = findAccountById(accountId);
			Account removedAccount = null;
			if ( account != null  ) {
				// now delete account
				removedAccount = emanager.removeAccount(currentUser, account);
			}
			
			if ( removedAccount != null ) {
				// account has been removed - update adapter
				updateAdapter();
			}
		}
	}
	
	/**
	 * delete record
	 */
	public void deleteRecord(final int groupPos, final int childPos) {
		Log.i("APPACTIVITY", "delete record group: " + groupPos + " child: " + childPos);
		
		if ( groupData.size() > groupPos ) {
			// find account 
			Map<String, String> mgroup = groupData.get(groupPos);
			long accountId = Long.valueOf(mgroup.get("number")).longValue();
			Log.i("DELETERECORD", "accountid: " + accountId);
			Account account = findAccountById(accountId);
			if ( account != null && childData.get(groupPos).size() > childPos ) {
				// find record
				Map<String, String> mchild = childData.get(groupPos).get(childPos);
				long recordId = Long.valueOf(mchild.get("number")).longValue();
				Log.i("DELETERECORD", "recordid: " + recordId);
				Record record = null;
				for ( Record rec : account.getRecords() ) {
					if ( recordId == rec.getId() ) {
						record = rec;
					}
				}
				if ( record != null ) {
					// wev'e got data - remove record and update adapter
					emanager.removeRecord(account, record);
					updateAdapter();
				}
			}	 
		}
	}
	
	/**
	 * Вывод счетов
	 */
	public void showAccounts()
	{							
		// account data for listview
		ArrayList<Map<String , String>> data = AppController.getAccountData(emanager, userLogin);
				
		// Create arrays of columns and UI elements
		String[] from = {
				ExpenseManager.LV_KEY_ACCOUNT_ID, 
				ExpenseManager.LV_KEY_ACCOUNT_BALANCE,
				ExpenseManager.LV_KEY_ACCOUNT_TITLE,
				ExpenseManager.LV_KEY_ACCOUNT_COUNT};
		
		int[] to = {R.id.tvAccountId, 
				R.id.tvAccountBalance, 
				R.id.tvAccountTitle,
				R.id.tvAccountRecords};
		
		// Create simple Cursor adapter
		SimpleAdapter lvAdapter = new SimpleAdapter(this, data, R.layout.list_account, 
				from, to); 
		
		// setting up adapter to list view
		lvList.setAdapter(lvAdapter);
	}
	
	/**
	 * show tree with accounts and records
	 */
	public void showAccountTree() {
		llNew.setVisibility(View.GONE);
		elvAccounts.setVisibility(View.VISIBLE);
	}
	
	/**
	 * show new record tab
	 */
	public void showNew() {	
		elvAccounts.setVisibility(View.INVISIBLE);
		llNew.setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * load spinners data
	 */
	private void loadSpinnerData() {
		// buffer collection
		ArrayList<String> list = new ArrayList<String>();
		
		// clear lists
		accList.clear();
		catList.clear();
		
		// get spinners data
		categoryItems	= AppController.getCategoryList(emanager);
		accountItems	= AppController.getAccountList(emanager, currentUser);
		for ( Map<String, Object> map : accountItems ) {
			Log.i("ACCOUNT ITEMS", "");
		}
				
		
		for ( Map<String, Object> map : categoryItems ) {
			StringBuilder buf = new StringBuilder();
			buf.append("#");
			buf.append(map.get("id"));
			buf.append("-");
			buf.append(map.get("name"));
			list.add(buf.toString());
		}
		catList.addAll(list);
				
		list.clear();
		for ( Map<String, Object> map : accountItems ) {
			StringBuilder buf = new StringBuilder();
			buf.append("#");
			buf.append(map.get("id"));
			buf.append("-");
			buf.append(map.get("description"));
			list.add(buf.toString());
		}
		accList.addAll(list);
	}
	
	/**
	 * update expandable list adapter
	 */
	private void updateAdapter() {
		// update list data
		childData.clear();
		childData.addAll(AppController.getRecordData(emanager, userLogin));
		groupData.clear();
		groupData.addAll(AppController.getAccountData(emanager, userLogin));
		
		// update spinner data
		loadSpinnerData();
		if ( accountAdapter != null ) {
			accountAdapter.notifyDataSetInvalidated();
			accountAdapter.notifyDataSetChanged();
		}
		
		if ( adapter != null ) {
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * create account tree
	 */
	public void createAccountTree() {			
						
		// group data for exlistview
		groupData = AppController.getAccountData(emanager, userLogin);
				
		// child data for exlistview
		childData = AppController.getRecordData(emanager, userLogin);
								
		// list elements for group
		String[] groupFrom = {
				ExpenseManager.LV_KEY_ACCOUNT_ID, 
				ExpenseManager.LV_KEY_ACCOUNT_BALANCE,
				ExpenseManager.LV_KEY_ACCOUNT_TITLE,
				ExpenseManager.LV_KEY_ACCOUNT_COUNT};
					
		int[] groupTo = {
				R.id.tvAccountId, 
				R.id.tvAccountBalance, 
				R.id.tvAccountTitle,
				R.id.tvAccountRecords};
				
		// list elements for child
		String[] childFrom = {
				ExpenseManager.LV_KEY_RECORD_ID,
				ExpenseManager.LV_KEY_RECORD_AMOUNT,
				ExpenseManager.LV_KEY_RECORD_TITLE,
				ExpenseManager.LV_KEY_RECORD_DATE
		};
				
		int[] childTo = {
				R.id.tvRecordId,
				R.id.tvRecordAmount,
				R.id.tvRecordTitle,
				R.id.tvRecordDate
		};
				
		// create adapter
		adapter = new SimpleExpandableListAdapter(
				this,
				groupData,
				R.layout.list_account,
				groupFrom,
				groupTo,
				childData,
				R.layout.list_record,
				childFrom,
				childTo);
				
		// set adapter
		elvAccounts.setAdapter(adapter);
	}
}