package vodafone.free2txt;

import java.util.Hashtable;
import android.os.AsyncTask;

public class SendSMSTask extends AsyncTask<String, Object, Object> {

	private Hashtable<String, String> smsAttributes;
	private String htmlPage;
	
	public SendSMSTask(String to, String message, int sent, String html, String path) {
		smsAttributes = new Hashtable<String, String>();
		smsAttributes.put("T13200352151265603977335{actionForm.messageBody}", message);
		smsAttributes.put("T13200352151265603977335{actionForm.destinationMsisdn}", to);
		smsAttributes.put("T13200352151265603977335{actionForm.messageCounter}", Integer.toString(sent));
		htmlPage = html;
	}

	@Override
	protected Object doInBackground(String... arg0) {
		HttpPoster send = new HttpPoster();		
		String ret = send.getResponse(arg0[0], htmlPage, "sendMessageForm", smsAttributes);
		return ret;
	}
	

}
