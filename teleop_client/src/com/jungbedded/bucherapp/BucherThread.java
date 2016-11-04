package com.jungbedded.bucherapp;

import java.io.IOException;
import java.net.Socket;

public class BucherThread implements Runnable {

	private logger logger;
	private final int sizeBuf = 50;
	private int flag;
	private Socket socket;
	private String bucherData = "Error";
	private byte[] bucherBuf = new byte[sizeBuf];
	private int bucherBufSize;
	
	public BucherThread(logger logger, Socket socket){
		this.logger = logger;
		flag = 1;
		this.socket = socket;
	}

	public void setFlag(int setflag) {
		flag = setflag;
	}
	
	public void run() {
		while(flag == 1){		
			try{	
				bucherBufSize = socket.getInputStream().read(bucherBuf);
				bucherData = new String(bucherBuf, 0, bucherBufSize, "UTF-8");
				
				if(bucherData.compareTo("[close]")==0){
					flag = 0;
				}
				logger.log("Recive Data : " + bucherData);
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		logger.log("Exit loop");
	}		
}

