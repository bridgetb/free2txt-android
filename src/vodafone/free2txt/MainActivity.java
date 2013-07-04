package vodafone.free2txt;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		/*try {
			refreshFields();
		} catch (NullPointerException e) {
			Editor ed = sp.edit();
			ed.remove("username");
			ed.remove("password");
			ed.commit();
			Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
			finish();
//			startActivity(myIntent);
			this.findViewById(R.id.smsTextbox).getContext().startActivity(myIntent);

			//setContentView(R.layout.login);
			return;
		}*/

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
				//login();
				sendSms(c, message.getText().toString());

				// Reset number of remaining texts
				refreshFields();
			}
		});
		
		refreshFields();
	}

	private String getRecipient() {
		String c = recipient.getText().toString();
		if (c.contains("<")) 
			c = c.substring(c.indexOf('<'), c.indexOf('>'));
		if (c.length() < 9) {
			//|| !recipient.getText().subSequence(0, 4).toString().equals(("+642")) || !recipient.getText().subSequence(0, 2).toString().equals(("02"))) {
			Toast.makeText(this, "Invalid recipient number", Toast.LENGTH_SHORT).show();
			return null;
		}		
		if (c.startsWith("0"))
			c = "64" + c.substring(1);

		if (c.startsWith("+"))
			c = c.substring(1);

		return c;
	}
	
	private void sendSms(String toNumber, String sms) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String html = sp.getString("sendHtml", getSendPage());
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		
		Element form = doc.getElementById("sendMessageForm");
		String action = form.attr("action");
		if (action == null) {
			return;
		}
		SendSMSTask txtTask = new SendSMSTask(toNumber, sms, remainingTxts, html, getBaseContext().getFilesDir().getPath().toString());
		txtTask.execute(action);
		Object result;
		try {
			result = txtTask.get();
			Editor ed = sp.edit();
			ed.putString("sendHtml", result.toString());
			ed.commit();
		} catch (InterruptedException e1) {
		} catch (ExecutionException e1) {
		}
	}

	private String getSendPage() {
		HttpGetter getter = new HttpGetter("https://www.vodafone.co.nz/myvodafone/free2TXT");
		String html = null;
		try {
			html = getter.execute();
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}	
		return html;
	}

	private void changeRemainingChars(int change) {
		remainingChars -= change;
		TextView tv = (TextView)this.findViewById(R.id.remainingCharsBox);
		tv.setText(remainingChars + " remaining characters");
	}

	private void refreshFields() throws NullPointerException {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String html = sp.getString("sendHtml", getSendPage());
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Element form = doc.body();
		if (form.select("#sendMessageForm").first() != null) {
			form = form.select("#sendMessageForm").first();
		} else {
			Elements els = form.children();
			form = els.get(4);
			form = form.select("#sendMessageForm").first();
		}
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
			Toast.makeText(this, "Sorry, your message was not sent", Toast.LENGTH_SHORT).show();
		}
		remainingTxts = remaining;
		
		if (remainingTxts == 0) {
			// Exit
			Toast.makeText(this, "Sorry, you have used all 20 free texts today", Toast.LENGTH_SHORT).show();
			
		}
	}
}

