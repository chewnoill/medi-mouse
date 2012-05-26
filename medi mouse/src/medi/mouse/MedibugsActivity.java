package medi.mouse;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.impl.client.DefaultHttpClient;

 
public class MedibugsActivity extends Activity implements OnSharedPreferenceChangeListener{

	TextView name_view;
	TextView status_view;
	TextView date_view;
	ImageView picture;
	
	Spinner in_spinner,out_spinner,bldg_spinner,date_spinner;
	
	ArrayList<myDate> inputDates;
	medi_person me;
	public DefaultHttpClient client;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyWebViewClient (this));
        //webview.loadUrl(medi_post.SITE);
        //build some layout spinners
        ArrayAdapter<CharSequence> in_adapter = ArrayAdapter.createFromResource(
        		this, R.array.in, android.R.layout.simple_spinner_item );
        in_adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        in_spinner = (Spinner) findViewById( R.id.in_edit );
        in_spinner.setAdapter( in_adapter );
        in_spinner.setOnItemSelectedListener(new in_spin_listener());
        
        ArrayAdapter<CharSequence> bldg_adapter = ArrayAdapter.createFromResource(
                this, R.array.bldg, android.R.layout.simple_spinner_item );
        bldg_adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        bldg_spinner = (Spinner) findViewById( R.id.building_edit );
        bldg_spinner.setAdapter( bldg_adapter );
        bldg_spinner.getSelectedItem();
        
        bldg_spinner.setOnItemSelectedListener(new in_spin_listener());
        ArrayAdapter<CharSequence> out_adapter = ArrayAdapter.createFromResource(
        		this, R.array.out, android.R.layout.simple_spinner_item );
        out_adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        out_spinner = (Spinner) findViewById( R.id.out_edit );
        out_spinner.setAdapter( out_adapter );
        
        out_spinner.setOnItemSelectedListener(new out_spin_listener());
        
        // setup display info about the current user
        name_view = (TextView) findViewById(R.id.name_view);
        status_view = (TextView) findViewById(R.id.status_view);
        date_view = (TextView) findViewById(R.id.date_view);
        picture = (ImageView) findViewById(R.id.picture_view);
        
        
        //the date spinners
        
        ArrayAdapter<CharSequence> date_adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        
        inputDates = new ArrayList<myDate>();
        Calendar cal = Calendar.getInstance();
        for(int x = 0; x<7;x++){
        	inputDates.add(x, new myDate(cal));
        	date_adapter.add(inputDates.get(x).human);
            cal.add(Calendar.DATE,1);
        }
        date_spinner = (Spinner) findViewById(R.id.date_edit);
        date_spinner.setAdapter(date_adapter);
       
        //submit button
        Button submit = (Button) findViewById(R.id.submit);
        Button refresh = (Button) findViewById(R.id.refresh);
        
        submit.setOnClickListener(new submit_listener());
        refresh.setOnClickListener(new refresh_listener());
        //------------------------------------------------------------------------------------------
        //establish connection to server
        
        
    	SharedPreferences spref=PreferenceManager.getDefaultSharedPreferences(this);
    	
    	String username = spref.getString("user_name", "");
    	String password = spref.getString("user_password","");
    	spref.registerOnSharedPreferenceChangeListener(this);
    	
    	client = medi_post.connect(username, password);
		
    	me = new medi_person(this);
    	
    	name_view = (TextView) findViewById(R.id.name_view);
        status_view = (TextView) findViewById(R.id.status_view);
        date_view = (TextView) findViewById(R.id.date_view);
        picture = (ImageView) findViewById(R.id.picture_view);
        
    	name_view.setText(me.full_name);
    	status_view.setText(me.status);
    	date_view.setText(me.date);
    	
        
    }
    
    @Override
    public void onDestroy(){
    	//disconnect
    	medi_post.disconnect(client);
    	super.onDestroy();
    }
    public class myDate{
    	Calendar date;
    	String human;
    	String YYYYmmdd;
    	public myDate(Calendar date){
    		this.date = date;
    		human = this.toString();
    		YYYYmmdd = this.toString(1);
    	}
    	
    	public String toString(){
    		int day = date.get(Calendar.DAY_OF_WEEK);
        	String day_of_week="";
        	switch(day){
        		case Calendar.SUNDAY:
        			day_of_week="Sun";
        			break;
        		case Calendar.MONDAY:
        			day_of_week="Mon";
        			break;
        		case Calendar.TUESDAY:
        			day_of_week="Tue";
        			break;
        		case Calendar.WEDNESDAY:
        			day_of_week="Wed";
        			break;
        		case Calendar.THURSDAY:
        			day_of_week="Thu";
        			break;
        		case Calendar.FRIDAY:
        			day_of_week="Fri";
        			break;
        		case Calendar.SATURDAY:
        			day_of_week="Sat";
        			break;
        	}
        	day =  date.get(Calendar.MONTH);
        	String month="";
        	String mm="";
        	switch(day){
        		case Calendar.JANUARY:
        			month="Jan";
        			mm="01";
        			break;
        		case Calendar.FEBRUARY:
        			month="Feb";
        			mm="02";
        			break;
        		case Calendar.MARCH:
        			month="Mar";
        			mm="03";
        			break;
        		case Calendar.APRIL:
        			month="Apr";
        			mm="04";
        			break;
        		case Calendar.MAY:
        			month="May";
        			mm="05";
        			break;
        		case Calendar.JUNE:
        			month="Jun";
        			mm="06";
        			break;
        		case Calendar.JULY:
        			month="Jul";
        			mm="07";
        			break;
        		case Calendar.AUGUST:
        			month="Aug";
        			mm="08";
        			break;
        		case Calendar.SEPTEMBER:
        			month="Sep";
        			mm="09";
        			break;
        		case Calendar.OCTOBER:
        			month="Oct";
        			mm="10";
        			break;
        		case Calendar.NOVEMBER:
        			month="Nov";
        			mm="11";
        			break;
        		case Calendar.DECEMBER:
        			month="Dec";
        			mm="12";
        			break;
        	}
        	
        	
        	int day_of_month = date.get(Calendar.DAY_OF_MONTH);
        	String yyyy = date.get(Calendar.YEAR)+"";
        	
        	String dd = day_of_month+"";
        	if(dd.length()==1){
        		dd="0"+dd;
        	}
        	this.YYYYmmdd=yyyy+mm+dd;
        	System.out.println(yyyy+mm+dd);
        	return month+" "+day_of_month+" "+day_of_week;

    	}
    	public String toString(int cmp){
    		this.toString();
    		return this.YYYYmmdd;
    		
    	}
    }
    class refresh_listener implements OnClickListener{

		public void onClick(View arg0) {
			System.out.println(":::here:::");

	    	reload();
	    	
			
		}
    	
    }
	public void reload(){
		SharedPreferences spref=PreferenceManager.getDefaultSharedPreferences(MedibugsActivity.this);
    	
		String username = spref.getString("user_name", "");
    	String password = spref.getString("user_password","");
    	System.out.println("user "+username);
    	//release lock when you close connection
    	me.network_lock = false;
    	client.getConnectionManager().shutdown();
    	client = medi_post.connect(username, password);
    	me.client=client;
    	medi_post postme;
    	
    	if (!me.hasStafflink()||
    			me.username!=username||
    			!me.network_auth){
    		me.username=username;
    		me.data = new HashMap<String, String>();
    		postme = new medi_post(me.data);
    		postme.execute(me);
    	} else {	
			me.secondaryLoad();
			postme = new medi_post(me.data);
	    	postme.execute(me);
    	}
	}
    class submit_listener implements OnClickListener{

		public void onClick(View arg0) {
			boolean post = false;
			if(out_spinner.getSelectedItemPosition()!=0){
				
				me.out=(String) out_spinner.getSelectedItem();
				me.loc=me.bldg=me.in="";
				myDate date = inputDates.get(date_spinner.getSelectedItemPosition());

				me.date = date.human;
				me.YYYYmmdd = date.YYYYmmdd;
				post = true;
				System.out.println("-->out"+me.out);
			} else if (in_spinner.getSelectedItemPosition()!=0&&bldg_spinner.getSelectedItemPosition()!=0){
				
				
				me.bldg = (String) bldg_spinner.getSelectedItem();
				me.loc = me.in = (String) in_spinner.getSelectedItem();
				me.out="";
				
				post = true;
				System.out.println("-->in"+me.loc);
				System.out.println("-->in"+me.bldg);
			}
			System.out.println("in: "+in_spinner.getSelectedItemPosition());
			System.out.println("out: "+out_spinner.getSelectedItemPosition());
			System.out.println("post: "+post);
			if (post){
				
				me.submit(MedibugsActivity.this);
				
			}	
			//me.secondaryLoad();
			//medi_post postme = new medi_post(me.data);
	    	//postme.execute(me);
	    	
	    	
			
		}
    	
    }
    
	private class MyWebViewClient extends WebViewClient {
    	Activity activity;
    	public MyWebViewClient(Activity activity){
    		super();
    		this.activity=activity;
    	}
    	@Override
    	public void onReceivedHttpAuthRequest(WebView view,
    	        HttpAuthHandler handler, String host, String realm) {

        	SharedPreferences spref=MedibugsActivity.this.getPreferences(0);
        	String username = spref.getString("user_name", "");
        	String password = spref.getString("user_password","");
        		
    	    handler.proceed(username, password);
    	    
    	}
    @Override
	 public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
		  handler.proceed() ;
		  }
	 
	 @Override
	 public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		 Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
	   	}
	 
	 @Override
     public boolean shouldOverrideUrlLoading( WebView view, String url )
     {
         return false;
     }

    }
    class in_spin_listener implements AdapterView.OnItemSelectedListener{
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg2!=0){
				out_spinner.setSelection(0, true);
				out_spinner.setSelected(false);
				
			}
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    class out_spin_listener implements AdapterView.OnItemSelectedListener{
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg2!=0){
				in_spinner.setSelection(0, true);
				in_spinner.setSelected(false);
				bldg_spinner.setSelection(0, true);
				bldg_spinner.setSelected(false);
				
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "login");
        menu.add(Menu.NONE, 1, 1, "about");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, EditPreferences.class));
                return true;
            case 1:
            	new AlertDialog.Builder(this)
            	  .setTitle(R.string.help_about).setMessage(R.string.help_about_message)
            	  .setPositiveButton(R.string.OK,
            	   new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
            	    
            	   }
            	    )
            	  .show();
            	return true;
        }
        return false;
    }

	public void onSharedPreferenceChanged(SharedPreferences spref, String arg1) {    	
		String username = spref.getString(arg1, "");
		String value = "user_password";
    	
    	char[] t = arg1.toCharArray();
    	
    	boolean equals = arg1.length()== value.length();
    	int len = arg1.length();
    	//not sure why this is false
    	//System.out.println(arg1+"==user_name? "+(arg1==value));
    	for (int x=0; x<len&&equals;x++){
    		if((arg1.charAt(x)!=(value.charAt(x)))){ equals=false;}
    	}
    	
		if(equals){
			//password has been updated
			//refresh
			reload();
		}
	}
    
}

class unauthorized extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}