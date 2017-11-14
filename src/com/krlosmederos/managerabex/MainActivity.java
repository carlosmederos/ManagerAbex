package com.krlosmederos.managerabex;

import java.util.Timer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;


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
    }


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
}
