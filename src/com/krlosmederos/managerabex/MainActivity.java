package com.krlosmederos.managerabex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
import android.content.Intent;
import android.net.Uri;
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
	
	private Timer _timer; 
	private TimerTask timerTask;
	private Handler handler = new Handler();
	private static int _intentosConexion = 0;
	private static String _PingCadlog = "200.14.49.67";
    private static String _PortCadlog = "";
    private static String _UrlCadlog = "http://intranet.uij.edu.cu/";
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
        //webView.loadUrl("http://intranet.uij.edu.cu/");
        
        startTimer();
        
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	//LogOff(_User);
    	stopTimer();
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
    	_UrlCadlog = "http://intranet.uij.edu.cu/";
		_PingCadlog = "200.14.49.67";//reader.readLine();
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
                urlc.setConnectTimeout(5000);
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
    
    private void stopTimer() {
    	if(_timer != null) {
    		_timer.cancel();
    		_timer.purge();
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
 		
 		_timer.schedule(timerTask, 0, 5000);
    }
    
    private void timerTick() {
    	
    	//stopTimer();
    	try {
    		txtMensaje.setVisibility(View.VISIBLE);
    		//txtMensaje.setText("CARGANDO CONFIGURACION...");
    		if(GetCadlogParams()) {
    			txtMensaje.setText("VERIFICANDO CONEXION...");
    			if(Pinging(_PingCadlog) > 0) {
    				//_UrlCadlog = "http://intranet.uij.edu.cu/";
    				//_UrlCadlog = "http://192.168.0.102:8082";
    				URL url = new URL(_UrlCadlog);  
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(5000);
                    try {
                    	stopTimer();
                    	urlc.connect();
                    	_intentosConexion = 0;
                    	//webView.setVisibility(View.VISIBLE);
                    	webView.loadUrl(_UrlCadlog);
                    	urlc.disconnect();
                    }
                    catch(Exception e) {
                    	_intentosConexion++;
                    	//webView.setVisibility(View.INVISIBLE);
                    	txtMensaje.setText("RECONECTANDO SITIO ("+_intentosConexion+")...");
                    	//txtMensaje.setVisibility(View.VISIBLE);
                    	urlc.disconnect();
                    	//startTimer();
                    }
    			}
    			else {
    				_intentosConexion++;
    				txtMensaje.setText("RECONECTANDO SERVIDOR ("+_intentosConexion+")...");
                	//txtMensaje.setVisibility(View.VISIBLE);
                	//startTimer();
    			}
    		}
    		else {
    			//stopTimer();
    			//txtMensaje.setVisibility(View.VISIBLE);
    			txtMensaje.setText("REVISAR ARCHIVO DE CONFIGURACION");
    		}
    	}
    	catch(Exception e) {
    		//stopTimer();
    		//txtMensaje.setVisibility(View.VISIBLE);
    		txtMensaje.setText("ERROR EN CONFIGURACION...");
    	}
    }
    
    private int Pinging(String sIP) throws IOException, InterruptedException{
    	Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ping -c 1 " + sIP);
        proc.waitFor();     
        int exit = proc.exitValue();
        return exit;
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
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            txtMensaje.setText("CARGANDO...");
            //txtMensaje.setVisibility(View.INVISIBLE);
            _intentosConexion = 0;
            GetUserIdFromUrl(url.toString());
            Toast.makeText(getApplicationContext(), "Sitio cargado completamente", Toast.LENGTH_SHORT).show();
        }
    }
}



