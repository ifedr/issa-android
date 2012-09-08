package ru.ifedr.issa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class Issa extends Activity implements OnClickListener {
	EditText etLogin, etPassword;
	Button btnStart;
	CheckBox cbRemember;
	
	SharedPreferences sPref;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        cbRemember = (CheckBox) findViewById(R.id.cbRemember);
        
        loadData();
    }
    
	@Override
    protected void onDestroy() {
		saveData( cbRemember.isChecked() );
        super.onDestroy();
      }
	
	@Override
    public void onClick(View v) {
    	switch(v.getId()) {
    	case R.id.btnStart:
			try {
				int l = Integer.parseInt(etLogin.getText().toString());
				String p = etPassword.getText().toString();
				String balance = new IssaHelper(l, p).getBalance();
				Toast.makeText(this, balance, Toast.LENGTH_SHORT).show();
			} catch (NumberFormatException e) {
				System.err.println("Логин должен быть числом.");
				System.exit(1);
			}
			break;
    	}
    }
    
    private void saveData(boolean remember) {
    	sPref = getPreferences(MODE_PRIVATE);
    	Editor edit = sPref.edit();
    	if (remember) {
	        edit.putString("KEY_LOGIN", etLogin.getText().toString());
	        edit.putString("KEY_PASSWORD", etPassword.getText().toString());
    	} else {
    		edit.clear();
    	}
        edit.commit();

    }
    
    private void loadData() {
    	sPref = getPreferences(MODE_PRIVATE);
    	String login = sPref.getString("KEY_LOGIN", "");
    	String password = sPref.getString("KEY_PASSWORD", "");
    	etLogin.setText(login);
    	etPassword.setText(password);
    }
    
    // Original class from Kevin http://www.kamzilla.ru/forum/106-10372-111167-16-1290944697
    public class IssaHelper
    {
    	protected int login;
    	protected String password;
    	protected String balance;
    	
    	protected String urlAuth = "http://issa.kamchatka.ru/cgi-bin/cgi.exe?function=is_login&mobnum=%d&Password=%s";
    	protected String urlBalanse = "http://issa.kamchatka.ru/cgi-bin/cgi.exe?function=is_account";
    	
    	
    	public IssaHelper(int l, String p)
    	{
    		login = l;
    		password = p;
    		
    		String cookies = cookiesRequest();
    		this.balance = balanseRequest(cookies);
    		
    	}
    	
    	protected String getBalance() {
    		return balance;
    	}
    	
    	protected String cookiesRequest() 
    	{
    		List<String> cookies = new ArrayList<String>();
    		
    		try 
    		{
    			URL url = new URL( String.format(urlAuth, login, password) );
    			URLConnection conn = url.openConnection();
    			
    			for (int i=1;; i++) 
    			{
    				String header = conn.getHeaderField(i);
    				String key = conn.getHeaderFieldKey(i);
    				if (header == null)
    				{
    					break;
    				}
    				if (key.equals("Set-cookie")) 
    				{
    					cookies.add(header);
    				}
    				
    			}
    		}
    		catch (MalformedURLException e)
    		{
    			System.err.println("Не удалось распознать URL.");
    		}
    		catch (IOException e)
    		{
    			System.err.println(e);
    		}
    		
    		return join(cookies, "; ");
    	}
    	
    	protected String balanseRequest(String cookies)
    	{
    		String balanse = "";
    		
    		try 
    		{
    			URL url = new URL(urlBalanse);
    			URLConnection conn = url.openConnection();
    			conn.setRequestProperty("Cookie", cookies);
    			conn.connect();
    			
    			InputStream inStream = conn.getInputStream();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));			
    			String line;
    			while ((line = reader.readLine()) != null) 
    			{
    				if (line.indexOf("<!--Balanse:#") != -1) 
    				{
    					balanse = line;
    					break;
    				}
    			}
    			if (balanse != "")
    			{
    				balanse = balanse.substring(balanse.indexOf("#")+1, balanse.lastIndexOf("#"));
    			}
    			else 
    			{
    				balanse = "Не удалось распознать ответ. Вероятно, вы ошиблись с логином или паролем.";
    			}

    		} 
    		catch (MalformedURLException e)
    		{
    			System.err.println("Не удалось распознать URL.");
    		}
    		catch (IOException e)
    		{
    			System.err.println(e);
    		}
    		
    		return balanse;
    	}
    	
    	public String join(Collection s, String delimiter) 
    	{
    		StringBuffer buffer = new StringBuffer();
    		Iterator iter = s.iterator();
    		while (iter.hasNext()) 
    		{
    			buffer.append(iter.next());
    			if (iter.hasNext())
    				buffer.append(delimiter);
    		}
    		return buffer.toString();
    	}
    	
    }
    
}