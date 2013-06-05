package vodafone.free2txt;

import java.util.Hashtable;

import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, Object, Object> {

	private Hashtable<String, String> logindata;

	public LoginTask(String username, String password) {
		logindata = new Hashtable<String, String>();
		logindata.put("username", username);
		logindata.put("password", password);
	}

	@Override
	protected Object doInBackground(String... arg0) {
		HttpPoster login = new HttpPoster();
		HttpGetter loginPageGet = new HttpGetter(arg0[0]);
		String loginPageString = null;
		try {
			loginPageString = loginPageGet.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ret = login.getResponse(arg0[0], loginPageString, "loginHandler", logindata);
		return ret;        
	}
}
