package medi.mouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
//import org.apache.commons.codec.binary.Base64;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.webkit.WebView;
//import android.widget.ImageView;
import android.widget.TextView;

public class medi_post extends AsyncTask<medi_person,Integer,medi_person>{

	public static String SITE= "https://www.meditech.com/employees/RATweb/RATWeb.mps";
	public static String SITE2="http://www.meditech.com/employees/RATweb/RATWeb.mps";
	public static String BASE_URL="http://www.meditech.com"; 
	private Map<String,String> data;
	
	public medi_post(Map<String, String> data){
		this.data=data;
	}
	public medi_post(){
		this.data=new HashMap<String, String>();
	}
	public static DefaultHttpClient connect(String username,String password){
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), 
                new UsernamePasswordCredentials(username, password));
		
		return httpclient;
		
	}
	
	public static String submit(HttpClient client, Map<String, String> data,String method){
		return null;
	}
	public static void disconnect(HttpClient client){
		client.getConnectionManager().shutdown();
		
	}
	public String doSubmit(HttpClient httpclient,
			String method, 
			String username,
			String password,
			Activity context) throws unauthorized {		
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(data.size());
		Set<String> keys = data.keySet();
		Iterator<String> keyIter = keys.iterator();
		String post_data="";
		for(int i=0; keyIter.hasNext(); i++) {
			String key = (String) keyIter.next();
			System.out.println("posting key "+i+": "+key+" value: "+data.get(key));
			if (key.length()>0){
				nameValuePairs.add(new BasicNameValuePair(key, data.get(key)));
			}
		}
		
		String url = SITE+"?"+post_data;
		System.out.println(method+":"+url);
		
		HttpResponse response;
		try {
			if(method=="GET"){
				HttpGet get = new HttpGet(url);
				response = httpclient.execute(get);
			}else{
				
				HttpPost post = new HttpPost(SITE);
				post.setHeader("Content-Type","application/x-www-form-urlencoded");
				post.setEntity(new  UrlEncodedFormEntity(nameValuePairs));
				response = httpclient.execute(post);
			}
			
			String file = "";
			String line = "";
			
			//webview.setHttpAuthUsernamePassword(SITE, "meditech.com", username, password);
			//webview.postUrl(SITE, EncodingUtils.getBytes(post_data, "BASE64"));
			
	        BufferedReader in = new BufferedReader(
	        		new InputStreamReader(response.getEntity().getContent()));
			
	        
			while((line=in.readLine())!=null) {
				file += line;				
			}
			
			if(file.contains("not authorized")){
				
				//throw new unauthorized();
			}
			
			in.close();
			System.out.println(file);
			
			return file;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(":::"+e.getMessage());
			
			//startActivity(new Intent(this, EditPreferences.class));
			e.printStackTrace();
		}

		return null;
	}


	@Override
	protected medi_person doInBackground(medi_person... params) {
		if(params.length>0){
			medi_person me = params[0];
			System.out.println(params.length);
			String ret = "";
			if(this.data.containsKey("TYPE")){
				try {
					int t = 0;
			    	int MAX = 100;
			    	while(me.network_lock&&t<MAX){
			    		try {
			    			synchronized (me) {
			    				  me.wait(1000);
			    				}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}
			    	if(!me.network_lock){
			    		me.network_lock=true;
						me.loadauth();
						ret=doSubmit(me.client,"POST",me.username,me.password,me.context);
						if(ret!=null){
							me.parseresponse(ret,this.data.get("TYPE"));
						}
						me.webview=ret;
						me.network_lock=false;
			    	}
				} catch (unauthorized e) {
					//me.context.startActivity(new Intent(me.context, EditPreferences.class));
				} catch (IllegalStateException e) {
					//Toast.makeText(me.context, "Oh no! " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				return me;
			} else{
				try {
					if(!me.hasStafflink()){
						int t = 0;
				    	int MAX = 100;
				    	while(me.network_lock&&t<MAX){
				    		try {
				    			synchronized (me) {
				    				  me.wait(1000);
				    				}
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    	}
				    	if(t!=MAX){
				    		me.network_lock=true;
							me.primaryLoad();
							this.data = me.data;
							ret=doSubmit(me.client,"POST",me.username,me.password,me.context);
							if(ret!=null){
								me.parseresponse(ret,this.data.get("TYPE"));
							}
							me.network_lock=false;
							me.webview=ret;
							
				    	}
					}
					
					if(me.stafflink!=null&&!me.network_lock){
						SharedPreferences spref=PreferenceManager.getDefaultSharedPreferences(me.context);
				    	spref.edit().putString("stafflink", me.stafflink);
				    	spref.edit().commit();
				    	System.out.println(":----:"+me.stafflink);
						
						me.secondaryLoad();
						this.data = me.data;
						ret=doSubmit(me.client,"POST",me.username,me.password,me.context);
						if(ret!=null){
							me.parseresponse(ret,this.data.get("TYPE"));
						}
						me.webview=ret;
					}			    	
				} catch (unauthorized e) {
					
			    	//me.context.startActivity(new Intent(me.context, EditPreferences.class));
				} catch (IllegalStateException e) {
					//Toast.makeText(me.context, "Oh no! " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
			System.out.println("123");
			
			return me;
		}
		return null;
	}
	@Override
	protected void onPostExecute(medi_person result )  {
		
	    
		Activity context = result.context;
		
		TextView name_view = (TextView) context.findViewById(R.id.name_view);
		TextView status_view = (TextView) context.findViewById(R.id.status_view);
		TextView date_view = (TextView) context.findViewById(R.id.date_view);
		//not implemented 
		//ImageView picture = (ImageView) context.findViewById(R.id.picture_view);
		WebView web_view = (WebView) context.findViewById(R.id.webview);

		medi_person me = result;
		
		name_view.setText(me.full_name);
		status_view.setText(me.status);
		date_view.setText(me.date);
		//web_view.loadData(me.webview, "text/html", "");
		int bad = me.webview.indexOf("<img");
		int end;
		while (bad != -1){
			end = me.webview.indexOf(">", bad);
			me.webview = me.webview.substring(0, bad)+me.webview.substring(end+1, me.webview.length());
			bad = me.webview.indexOf("<img");
		}
		System.out.println("====================>>>\n"+me.webview);
		web_view.loadDataWithBaseURL(BASE_URL, me.webview, "text/html", "", SITE);
		super.onPostExecute(me);
	}
}

