package com.krlosmederos.managerabex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	private Timer _timer = new Timer(); 
	private static int _intentosConexion = 0;
	private static String _PingCadlog = "";
    private static String _PortCadlog = "";
    private static String _UrlCadlog = "";
    private static String _User = "";
    
    // Controles
    private Button btnClose;
    private TextView txtMensaje;
    private WebView webView;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webView = (WebView) findViewById(R.id.webView);
        txtMensaje = (TextView) findViewById(R.id.txtMensaje);
        
        txtMensaje.setText("INICIANDO APLICACION...");
        
        _timer.schedule(new MyTimerTask(), 1000, 5000);
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	//LogOff(_User);
    	// _timer.cancel();
    	
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Verificar evento del boton de atras y que hay historial de navegacion
        if((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // En caso contrario le pasamos el evento al padre
        return super.onKeyDown(keyCode, event);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
    private boolean GetCadlogParams()
    {
    	try
    	{
    		//String path = getApplicationInfo().dataDir;
    		InputStreamReader input = new InputStreamReader(openFileInput("ConfigCadlogManager.txt"));
    		BufferedReader reader = new BufferedReader(input);

    		_UrlCadlog = "http://" + reader.readLine();
    		_PingCadlog = reader.readLine();
    		_PortCadlog = reader.readLine();
    		reader.close();
    		return true;
    	}
    	catch(Exception ex)
    	{
    		return false;
    	}
    }
    
    private void GetUserIdFromUrl(String sUrl)
    {
        if(_User == "" && sUrl.indexOf("sUser=") > -1)
        {
            _User = sUrl.split("?")[1].toString().split("&")[0].toString().substring(6);
        }
        else if(sUrl.indexOf("Login") > -1)
        {
            _User = "";
        }
    }
    
    private void LogOff(String sUserId)
    {
    	try
    	{     		
        	if(sUserId == "")
        	{
        		String url = _UrlCadlog + "/Administracion/Menu?sUser=" + _User + "&LogOff=true&iOpcion=-1";
            	
            	HttpParams httpParam = new BasicHttpParams();
            	HttpConnectionParams.setConnectionTimeout(httpParam, 5000);
            	DefaultHttpClient httpClient = new DefaultHttpClient(httpParam);
            	HttpGet httpGet = new HttpGet(url);
            	HttpResponse response = httpClient.execute(httpGet);
            	httpGet.abort();
        	}
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(getApplicationContext(), "Tiempo de espera de conexion excedido", Toast.LENGTH_SHORT).show();
    	}
    	
    	
    }
    
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(_UrlCadlog)) {
                // Estoy navegando por mi sitio
                return false;
            }
            // No es mi sitio
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
    
    private class MyTimerTask extends TimerTask {
    	
    	@Override
		public void run() {
			Toast.makeText(getApplicationContext(), "Probando Timer cada 5 seg", Toast.LENGTH_SHORT).show();
			
		}
    }
    
    
}



