package ru.ifedr.issa;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class Issa extends Activity implements OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button goButton = (Button)findViewById(R.id.go_button);
        goButton.setOnClickListener(this);
    }
    
    public void onClick(View v) {
        EditText loginValue = (EditText)findViewById(R.id.login_value);
        EditText passwordValue = (EditText)findViewById(R.id.password_value);
        
		try 
		{
			int l = Integer.parseInt(loginValue.getText().toString());
			String p = passwordValue.getText().toString();
			String balance = new IssaHelper(l, p).getBalance();
		
			Intent intent = new Intent(this, Balance.class);
			intent.putExtra(Balance.KEY_BALANCE, balance);
			startActivity(intent);
		} 
		catch (NumberFormatException e) 
		{
			System.err.println("Логин должен быть числом.");
			System.exit(1);
		}
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