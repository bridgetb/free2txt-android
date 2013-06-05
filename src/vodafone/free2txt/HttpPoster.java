package vodafone.free2txt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpResponse;
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
		HttpResponse response;
		DefaultHttpClient client = new DefaultHttpClient();
		BasicHttpContext localContext = new BasicHttpContext();
		
		try
		{
			List<BasicNameValuePair> nvps = getFormElements(html, element, attributes);
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = client.execute(httpost,localContext);
			return EntityUtils.toString(response.getEntity());

		}
		catch(Exception e)
		{
			return "";
		}
	}
}
