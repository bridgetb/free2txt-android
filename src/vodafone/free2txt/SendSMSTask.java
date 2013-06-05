package vodafone.free2txt;

import java.util.Hashtable;
import android.os.AsyncTask;

public class SendSMSTask extends AsyncTask<String, Object, Object> {

	private Hashtable<String, String> smsAttributes;
	private String htmlPage;

	public SendSMSTask(String to, String message, String html, String path) {
		smsAttributes = new Hashtable<String, String>();
		smsAttributes.put("T13200352151265603977335{actionForm.messageBody}", message);
		smsAttributes.put("T13200352151265603977335{actionForm.destinationMsisdn}", to);
		htmlPage = html;
	}

	@Override
	protected Object doInBackground(String... arg0) {
		HttpPoster send = new HttpPoster();
		HttpGetter free2txtPageGet = new HttpGetter(arg0[0]);
		try {
			free2txtPageGet.execute();
		} catch (Exception e) {
		}

		String ret = send.getResponse(arg0[0], htmlPage, "sendMessageForm", smsAttributes);
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
