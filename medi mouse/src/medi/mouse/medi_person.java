package medi.mouse;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.HttpClient;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class medi_person {
	//hidden
	String username,full_name,stafflink,imglink;
	
	//postable
	String status,date,out,in,loc,bldg,YYYYmmdd;

	//this could be big
	String text;
	
	//ui stuff
	Activity context;
	public String humanDate;
	
	boolean network_lock = false;
	boolean network_auth = false;
	
	/*what to post 
	 * im not sure if i can throw more then one object through
	 * the AsyncTask gateway so im internalizing everything ill need.
	 */
	public Map<String, String> data = new HashMap<String, String>();
	
	HttpClient client;

	
	String password;

	public String webview;
	public void loadauth(){
		SharedPreferences spref=PreferenceManager.getDefaultSharedPreferences(context);
        password = spref.getString("user_password","");
	}
	
	public medi_person(MedibugsActivity context){
        this.context=context;

        
        SharedPreferences spref=PreferenceManager.getDefaultSharedPreferences(context);
        username = spref.getString("user_name", "");
    	stafflink = spref.getString("stafflink", "");
    	
		client = context.client;
        data = new HashMap<String, String>();
    	medi_post post = new medi_post(data);
    	post.execute(this);
	    		
	}
	
	public void secondaryLoad(){
		data = new HashMap<String, String>();
		if(stafflink!=null){
			data.put("TYPE","ViewUser");
			data.put("view","Photo");	
			data.put("stafflink",stafflink);
		}
	}
	public void primaryLoad(){
		data = new HashMap<String, String>();
		data.put("TYPE","TraxFrame");
		data.put("User",username);
	}
	public boolean hasStafflink(){
		return stafflink!=null&&(stafflink.length()>9);
	}
	public void submit(Activity context){
	    
        data = new HashMap<String, String>();
		//actual important stuff
		data.put("loc", loc);
		data.put("bldg", bldg);
		data.put("out", out);
		data.put("date", date);
		data.put("text", "");
		data.put("TextEdit","false");
		data.put("stafflink",stafflink);
		data.put("mystafflink",stafflink);
		data.put("YYYYMMDDdate", YYYYmmdd);
		data.put("TYPE","Save");
		medi_post post = new medi_post(data);
    	
		post.execute(this);
	}
	public static String parse(String htmlobject, String pre,String post){
		
		if (htmlobject!=null){
			int index = htmlobject.indexOf(pre, 0);
			int end = htmlobject.indexOf(post, index);
			System.out.print(index+":"+pre+":"+end+":"+htmlobject.length());
			if (index!=-1 && index+pre.length()<end) {
				System.out.println(index+","+end+":::"+htmlobject.substring(index,end));
				
				String test = htmlobject.substring(index+pre.length(), end);
				System.out.println(index+","+end+":"+test);
				return test;
			}
		}		return "";
		
	}

	public void parseresponse(String output, String type) {
		if(output.length()>0){
			if(type=="ViewUser"){
				full_name = parse(output, "<td valign=middle align=center>","<br>");
				imglink = "Photos\\"+medi_person.parse(output,"Photos\\","\"");
			}
			if (stafflink.length()==0||stafflink==null||stafflink=="stafflink"){
				String stafflink;
				stafflink = medi_person.parse(output, "stafflink = 'stafflink\\","';");
				int bad = stafflink.indexOf("\\",1);
				while (bad != -1){
					stafflink = stafflink.substring(0, bad)+ stafflink.substring(bad+1, stafflink.length());;
					bad = stafflink.indexOf("\\",1);
				}

				System.out.println(stafflink);
				this.stafflink = "stafflink"+stafflink;
			} 
			date = medi_person.parse(output, "date = '","';");
			out = medi_person.parse(output, "out = '","';");
			loc = medi_person.parse(output, "loc = '","';");
			bldg = medi_person.parse(output, "bldg = '","';");
			status = loc.length()>0?loc+", "+bldg:out;
			
			System.out.println("full name: "+full_name);
		}
	}
}
