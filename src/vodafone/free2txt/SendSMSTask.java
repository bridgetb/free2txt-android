package vodafone.free2txt;

import java.util.Hashtable;

import org.jsoup.nodes.Element;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

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
		HttpGetter free2txtPageGet = new HttpGetter(arg0[0]);
		
//		String ret = send.getResponse(arg0[0], smsAttributes);
		String ret = send.getResponse(arg0[0], htmlPage, "sendMessageForm", smsAttributes);
		/*String htmlPage = null;
		try {
			htmlPage = free2txtPageGet.execute();

		} catch (Exception e) {
		}*/
		return ret;
	}
	
	/*private void writeToFile(String contents, String path) {

		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(path));
			out.print(contents);
		} catch (FileNotFoundException e) {
		}
		if (out != null) out.close();
	}*/
}
