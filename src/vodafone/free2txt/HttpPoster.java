package vodafone.free2txt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HttpPoster {

	public List<BasicNameValuePair> getFormElements(String html, String element, Hashtable<String, String> attributes)
	{
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Element form = doc.getElementById(element);
		if (form == null) {
			form = doc.body();
			Elements els = form.children();
			form = els.get(4);
			form = form.getElementById(element);
			System.out.println(form);
		}
		Elements inputElements = form.getAllElements();
		List<BasicNameValuePair>  nvps = new ArrayList<BasicNameValuePair> ();

		for(Element inputElement : inputElements)
		{
			String key = inputElement.attr("name");
			if (key == null || key.equals(""))
				continue;
			String value = inputElement.attr("value");
			Enumeration<String> e = attributes.keys();
			while (e.hasMoreElements()) {
				String k = e.nextElement();
				String v = attributes.get(k);
				if(key.equals(k)) {
					value = v;
				}
			}
			nvps.add(new BasicNameValuePair(key,value));
		}
		return nvps;
	}

	public String getResponse(String uri, String html, String element, Hashtable<String, String> attributes)
	{
		HttpPost httpost = new HttpPost(uri);
		HttpResponse response = null;
		DefaultHttpClient client = new DefaultHttpClient();
		BasicHttpContext localContext = new BasicHttpContext();

		List<BasicNameValuePair> nvps = getFormElements(html, element, attributes);
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			response = client.execute(httpost,localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public String getResponse(String uri, Hashtable<String, String> attributes)
	{
		HttpPost httpost = new HttpPost(uri);
		HttpResponse response = null;
		DefaultHttpClient client = new DefaultHttpClient();
		BasicHttpContext localContext = new BasicHttpContext();

		List<BasicNameValuePair>  nvps = new ArrayList<BasicNameValuePair> ();
		Enumeration<String> en = attributes.keys();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			String v = attributes.get(k);
			nvps.add(new BasicNameValuePair(k,v));
		}
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			response = client.execute(httpost,localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
}
