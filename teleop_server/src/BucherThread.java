import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class BucherThread implements Runnable{
    private static final int sizeBuf = 50;
    private Socket clientSock;
    private Logger logger;
    private SocketAddress clientAddress;

	final GpioController gpio = GpioFactory.getInstance();
	final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "PinLED", PinState.LOW);
	final GpioPinDigitalOutput pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "PinBUZZER", PinState.LOW);


	public BucherThread(Socket clntSock, SocketAddress clientAddress, Logger logger) {
    this.clientSock = clntSock;
    this.logger = logger;
    this.clientAddress = clientAddress;
    }

	 public void run(){
        try {
            InputStream ins   = clientSock.getInputStream();
            OutputStream outs = clientSock.getOutputStream();

            int bucherBufSize;
            byte[] bucherBuf = new byte[sizeBuf];
            while ((bucherBufSize = ins.read(bucherBuf)) != -1) {

                String bucherData = new String(bucherBuf, 0, bucherBufSize, "UTF-8");

                if (bucherData.compareTo("LEDON") == 0) {
                    pin.high();

                    System.out.println("LED_ON");
                }

                if (bucherData.compareTo("BUZZERON") == 0) {
                    pin2.high();

                    try{Thread.sleep(200);}
                    catch(InterruptedException e) {}

                    System.out.println("Buzzer_ON");
                }
                if (bucherData.compareTo("BUZZEROFF") == 0) {
                    pin2.low();

                    try{Thread.sleep(200);}
                    catch(InterruptedException e) {}

                    System.out.println("BUZZER_OFF");
                }               
			    if (bucherData.compareTo("LEDOFF") == 0) {
                    pin.low();
                    try{Thread.sleep(200);}
                    catch(InterruptedException e) {}

                    System.out.println("LED_OFF");
                }

                logger.info("Received data : " + bucherData + " (" + clientAddress + ")");
                outs.write(bucherBuf, 0, bucherBufSize);
            }
            logger.info(clientSock.getRemoteSocketAddress() + " Closed");
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Exception in BucherThread", ex);
        } finally {
        	System.out.println("Disconnected! Client IP : " + clientAddress);

//            try {
//                // clientSock.close();
//            } catch (IOException e) {}
        }
    }
}
 