package vodafone.free2txt;

import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private AutoCompleteTextView recipient;
	private EditText message;
//	private ArrayAdapter<String> contactslist;
	
	private int remainingChars = 100;
	private int remainingTxts = -1;

	private String free2txtHtml;
	private String username;
	private String password;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		username = sp.getString("username", null);
		password = sp.getString("password", null);
		setContentView(R.layout.main);
		try {
			refreshFields();
		} catch (NullPointerException e) {
			Editor ed = sp.edit();
			ed.remove("username");
			ed.remove("password");
			ed.commit();
			//finish();
			Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
			this.findViewById(R.id.smsTextbox).getContext().startActivity(myIntent);
			//setContentView(R.layout.login);
			return;
		}

		this.recipient = (AutoCompleteTextView)this.findViewById(R.id.recipientNumber);
		/*Cursor contactCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				null, null, null);
		contactCursor.moveToFirst();
		contactslist = new ArrayAdapter<String>(this, R.layout.main, R.id.recipientNumber);
		//BaseAdapter b = new BaseAdapter();this.findViewById(R.id.recipientNumber).getContext(), R.id.recipientNumber, 
		do {
			String num = contactCursor.getString(contactCursor
					.getColumnIndex(CommonDataKinds.Phone.NUMBER));
			String name = contactCursor.getString(contactCursor
					.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME));
			String contact = name + " <" + num + ">";
			contactslist.add(contact);
			System.out.println(contact);
		} while (contactCursor.moveToNext());
		contactCursor.close();
		
		contactslist.setNotifyOnChange(true);
		
		recipient.setAdapter(contactslist);		
		recipient.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.print("on item click");
				String c = parent.getItemAtPosition(position).toString();
				System.out.println(c);
				recipient.setText(c);
				recipient.refreshDrawableState();
			}			
		});
//		recipient.setActivated(true);
		recipient.setListSelection(0);*/
		
		this.message = (EditText)this.findViewById(R.id.smsTextbox);
		message.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int change = count - before;
				changeRemainingChars(change);
			}
		});


		Button sendButton = (Button)this.findViewById(R.id.sendButton);
		sendButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String c = getRecipient();
				if (c == null) 
					return;
				// Get action to execute (form id sendMessageForm)
				login();
				sendSms(c, message.getText().toString());

				// Reset number of remaining texts
				refreshFields();
			}
		});
	}

	private String getRecipient() {
		String c = recipient.getText().toString();
		if (c.contains("<")) 
			c = c.substring(c.indexOf('<'), c.indexOf('>'));
		if (c.length() < 9) {
			//|| !recipient.getText().subSequence(0, 4).toString().equals(("+642")) || !recipient.getText().subSequence(0, 2).toString().equals(("02"))) {
			// TODO: Handle invalid recipient number

			return null;
		}		
		if (c.startsWith("0"))
			c = "64" + c.substring(1);

		if (c.startsWith("+"))
			c = c.substring(1);
		
		return c;
	}
	private void login() {
		LoginTask task = new LoginTask(username, password);
		task.execute("https://www.vodafone.co.nz/knox/login_handler.jsp?pcode=mvf&template=myvodafone&url=https://www.vodafone.co.nz/myvodafone/free2TXT");
		Object result;
		try {
			result = task.get();
			if (result == null) {
				return;
			}
			free2txtHtml = result.toString();

		} catch (InterruptedException e1) {
		} catch (ExecutionException e1) {
		}
	}

	private void sendSms(String toNumber, String sms) {
		org.jsoup.nodes.Document doc = Jsoup.parse(free2txtHtml);
		Element form = doc.getElementById("sendMessageForm");
		String action = form.attr("action");
		SendSMSTask txtTask = new SendSMSTask(toNumber, sms, free2txtHtml, getBaseContext().getFilesDir().getPath().toString());
		txtTask.execute(action);
		Object result;
		try {
			result = txtTask.get();
			if (result == null) {
				login();
				doc = Jsoup.parse(free2txtHtml);
				form = doc.getElementById("sendMessageForm");
				action = form.attr("action");
				SendSMSTask txtTask1 = new SendSMSTask(toNumber, sms, free2txtHtml, getBaseContext().getFilesDir().getPath().toString());
				txtTask1.execute(action);
				result = txtTask.get();
			}

		} catch (InterruptedException e1) {
		} catch (ExecutionException e1) {
		}
	}

	private void changeRemainingChars(int change) {
		remainingChars -= change;
		TextView tv = (TextView)this.findViewById(R.id.remainingCharsBox);
		tv.setText(remainingChars + " remaining characters");
	}

	private void refreshFields() throws NullPointerException {
		login();
		org.jsoup.nodes.Document doc = Jsoup.parse(free2txtHtml);
		Element form = doc.body();
		Elements els = form.children();
		form = els.get(4);
		form = form.getElementById("sendMessageForm");
		form = form.getElementsByClass("formCopyLeft").get(1);
		String sentTxts = form.text();
		String[] sentTxtsSentence = sentTxts.split(" ");
		TextView tv = (TextView)this.findViewById(R.id.remainingTextsBox);
		int remaining = -1;
		for (String s : sentTxtsSentence) {
			try {
				remaining = 20 - Integer.parseInt(s);
				break;
			} catch (Exception e ){
			}
		}

		tv.setText("You have " + remaining + " remaining texts today.");

		// Check if message was sent - clear fields if sent
		if (remainingTxts != -1 && remaining < remainingTxts) {
			recipient.setText("");
			message.setText("");
			Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();

		} else if (remaining == remainingTxts) {
			String rec = getRecipient();
			if (rec != null) {				
				String msg = message.getText().toString();
				// Resend text?
				sendSms(rec, msg);
				refreshFields();
			}
		}
		remainingTxts = remaining;
	}
}

