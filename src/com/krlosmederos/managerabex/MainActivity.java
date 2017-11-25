package com.krlosmederos.managerabex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	//CONSTANTES
    private static final int WAIT_TIMER = 5000;
    private static final int WAIT_CONEXION = 5000;
    
    //DEBUG
    private static final String IP_UIJ = "10.10.3.46";
    private static final String WEB_UIJ = "http://intranet.uij.edu.cu/";
    
	
	private Timer _timer; 
	private TimerTask timerTask;
	private Handler handler = new Handler();
	private static int _intentosConexion = -1 ;
	private static String _PingCadlog = IP_UIJ;
    private static String _PortCadlog = "";
    private static String _UrlCadlog = WEB_UIJ;
    private static String _User = "";
    
    // Controles
    private TextView txtMensaje;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webView = (WebView) findViewById(R.id.webView);
        txtMensaje = (TextView) findViewById(R.id.txtMensaje);
        
        txtMensaje.setText("INICIANDO APLICACION...");
        
        timerTick();
        startTimer();
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	//LogOff(_User);
    	_timer.cancel();
    	_timer.purge();
    }
    
    @Override
    protected void onStop() {
    	super.onStop(); 	
    	_timer.cancel();
    	_timer.purge();
    	_timer = null;
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();	
    	startTimer();
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

    private boolean GetCadlogParams()
    {
    	_UrlCadlog = WEB_UIJ;
		_PingCadlog = IP_UIJ;//reader.readLine();
		return true;
		/*
    	try
    	{
    		//String path = getApplicationInfo().dataDir;
    		InputStreamReader input = new InputStreamReader(openFileInput("ConfigCadlogManager.txt"));
    		BufferedReader reader = new BufferedReader(input);

    		_UrlCadlog = "http://" + reader.readLine();
    		_PingCadlog = "200.14.49.67";//reader.readLine();
    		_PortCadlog = reader.readLine();
    		reader.close();
    		return true;
    	}
    	catch(Exception ex)
    	{
    		return true;
    	}
    	*/
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
        		String cad_url = _UrlCadlog + "/Administracion/Menu?sUser=" + _User + "&LogOff=true&iOpcion=-1";
        		
        		URL url = new URL(cad_url);  
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(WAIT_TIMER);
                urlc.connect();
            	/*
            	HttpParams httpParam = new BasicHttpParams();
            	HttpConnectionParams.setConnectionTimeout(httpParam, 5000);
            	DefaultHttpClient httpClient = new DefaultHttpClient(httpParam);
            	HttpGet httpGet = new HttpGet(url);
            	HttpResponse response = httpClient.execute(httpGet);
            	httpGet.abort();
            	*/
        	}
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(getApplicationContext(), "Tiempo de espera de conexion excedido", Toast.LENGTH_SHORT).show();
    	}
    	
    	
    }
    
    private void startTimer() {
    	 _timer = new Timer();
         timerTask = new TimerTask() {
 			public void run() {
 				// Usar el manipulador para el Toast
 				handler.post(new Runnable() {
 					public void run() {
 						timerTick();
 						Toast.makeText(getApplicationContext(), "Probando Timer cada 5 seg", Toast.LENGTH_SHORT).show();		
 					}
 				});
 			}
 		};
 		
 		_timer.schedule(timerTask, 0, WAIT_TIMER);
    }
    
    private void timerTick() {
    	try {
    		//txtMensaje.setVisibility(View.VISIBLE);
    		//txtMensaje.setText("CARGANDO CONFIGURACION...");
    		if(GetCadlogParams()) {
    			if( _intentosConexion != 0 ) {
    				txtMensaje.setText("VERIFICANDO CONEXION...");
    				txtMensaje.setVisibility(View.VISIBLE);
    				webView.setVisibility(View.INVISIBLE);
    			}
    			if(isOnline(getApplicationContext())) {
    				new PingAsyncTask().execute();
    			}
    			else {
    				_intentosConexion++;
    				txtMensaje.setText("DISPOSITIVO SIN CONEXION...");
    				webView.setVisibility(View.INVISIBLE);
                	txtMensaje.setVisibility(View.VISIBLE);
    			}
    		}
    		else {
    			_intentosConexion++;
    			txtMensaje.setVisibility(View.VISIBLE);
    			txtMensaje.setText("REVISAR ARCHIVO DE CONFIGURACION...");
    			webView.setVisibility(View.INVISIBLE);
    		}
    	}
    	catch(Exception e) {
    		_intentosConexion++;
    		webView.setVisibility(View.INVISIBLE);
    		txtMensaje.setVisibility(View.VISIBLE);
    		txtMensaje.setText("ERROR EN CONFIGURACION...");
    	}
    }
    
    private static boolean isOnline( Context context ) {
    	ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
    
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	/*
            if (Uri.parse(url).getHost().equals(_UrlCadlog)) {
                // Estoy navegando por mi sitio
                return false;
            }
            // Navegacion sitio externo
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
            */
        	return false;
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            _intentosConexion = 0;
            webView.setVisibility(View.VISIBLE);
        	txtMensaje.setVisibility(View.INVISIBLE);
            GetUserIdFromUrl(url.toString());
            //Toast.makeText(getApplicationContext(), "Sitio cargado completamente", Toast.LENGTH_SHORT).show();
        }
    }
    
    public class PingAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
        	try {
        		Runtime runtime = Runtime.getRuntime();
                Process proc = runtime.exec("ping -c 1 " + _PingCadlog);
                proc.waitFor();     
                int exit = proc.exitValue();
                return exit;
        	}
        	catch(Exception e) {
        		return -1;
        	}	
        }

        @Override
        protected void onPostExecute(Integer okPing) {
            if(okPing != 0){  					         
                _intentosConexion++;
 				txtMensaje.setText("RECONECTANDO SERVIDOR ("+_intentosConexion+")...");
 				webView.setVisibility(View.INVISIBLE);
             	txtMensaje.setVisibility(View.VISIBLE);
            } 
            else {
            	new SiteAsyncTask().execute();
    		}
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }
    
    
    public class SiteAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
        	try {
        		URL url = new URL(_UrlCadlog);
    			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(WAIT_CONEXION);
            	urlc.connect();
            	//urlc.disconnect();
            	return urlc.getResponseMessage();
        	}
        	catch(Exception e) {
        		return e.getMessage();
        	}	
        	
        }

        @Override
        protected void onPostExecute(String okSite) {
        	if(true){  					         
            	_intentosConexion++;
            	webView.setVisibility(View.INVISIBLE);
            	txtMensaje.setText("RECONECTANDO SITIO ("+_intentosConexion+")..."+okSite);
            	txtMensaje.setVisibility(View.VISIBLE);
            }
            else {
                if(_intentosConexion != 0) {  		// En caso que se haya perdido la conexion en algun momento
                	webView.loadUrl(_UrlCadlog); 	// cargo el sitio
                    _intentosConexion = 0;
                    txtMensaje.setVisibility(View.VISIBLE);
                    txtMensaje.setText("CARGANDO...");
                }
    		}
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }
    
    
}



