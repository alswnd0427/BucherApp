package com.jungbedded.bucherapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import java.net.Socket;

import org.oroca.teleopclient.R;

import java.io.IOException;
import java.io.OutputStream;

public class BucherMain extends Activity implements OnClickListener{

	private EditText editTextIPAddress;
	private TextView textViewStatus;
	private Button buttonConnect;
	private Button buttonClose;
	private Button buttonLed;
	private Button buttonBuzzer;
	private InputMethodManager imm;
	private String server = "172.31.36.241";
	private int port = 8888;
	private Socket socket;
	private OutputStream outs;
	private Thread bucherThread;
	public logger logger;
	boolean LedIsOn = false;
	boolean BuzzerIsOn = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			
		editTextIPAddress = (EditText)this.findViewById(R.id.editTextIPAddress);
		editTextIPAddress.setText(server);
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				
		textViewStatus = (TextView)this.findViewById(R.id.textViewStatus);
		textViewStatus.setText("Press 'Connect' to connect.");
		
		logger = new logger(textViewStatus);
		
		buttonConnect = (Button)this.findViewById(R.id.buttonConnect);
		buttonClose = (Button)this.findViewById(R.id.buttonClose);
		buttonLed = (Button)this.findViewById(R.id.buttonLed);
		buttonBuzzer = (Button)this.findViewById(R.id.buttonBuzzer);
		
		buttonConnect.setOnClickListener(this);
		buttonClose.setOnClickListener(this);
		buttonLed.setOnClickListener(this);
		buttonBuzzer.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View clicked) {
		if(clicked == buttonConnect)
		{
			imm.hideSoftInputFromWindow(editTextIPAddress.getWindowToken(), 0);
			
			try{
				if(socket!=null)
				{
					socket.close();
					socket = null;
				}
				
				server = editTextIPAddress.getText().toString();
				socket = new Socket(server, port);
				outs = socket.getOutputStream();

				bucherThread = new Thread(new BucherThread(logger, socket));
				bucherThread.start();
				logger.log("Connected");
			} catch (IOException e){
				logger.log("Fail to connect");
				e.printStackTrace();
			}
		}
		
		if(clicked == buttonClose)
		{
			imm.hideSoftInputFromWindow(editTextIPAddress.getWindowToken(), 0);
			
			if(socket!=null)
			{
				exitFromRunLoop();
				try{
					socket.close();
					socket = null;
					logger.log("Closed!");
					bucherThread = null;
				} catch (IOException e){
					logger.log("Fail to close");
					e.printStackTrace();
				}
			}
		}
		
		if(clicked == buttonLed || clicked == buttonBuzzer)
		{
			String sndOpkey = "CMD";
			
			if(clicked == buttonLed) {
				if(LedIsOn) {
					sndOpkey = "LEDOFF";
					LedIsOn = false;
				} else {
					sndOpkey = "LEDON";
					LedIsOn = true;
				}
			}
			
			if(clicked == buttonBuzzer) {
				if(BuzzerIsOn) {
					sndOpkey = "BUZZEROFF";
					BuzzerIsOn = false;
				} else {
					sndOpkey = "BUZZERON";
					BuzzerIsOn = true;
				}
			}

			try{
				outs.write(sndOpkey.getBytes("UTF-8"));
				outs.flush();
			} catch (IOException e){
				logger.log("Fail to send");
				e.printStackTrace();
			}
		}		
	}
	
    void exitFromRunLoop(){
    	try {
    		String sndOpkey = "[close]";
    		outs.write(sndOpkey.getBytes("UTF-8"));
    		outs.flush();
    	} catch (IOException e) {
			logger.log("Fail to send");
			e.printStackTrace();
    	}
    }
}
