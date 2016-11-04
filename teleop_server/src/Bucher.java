import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Logger;
import java.net.InetAddress;

public class Bucher {
    private static final int sizeBuf = 50;
    public static void main(String[] args) throws IOException {

        int port = 8888;
		System.out.println("Start server : " + InetAddress.getLocalHost()+"("+port+")");
        ServerSocket serverSock = new ServerSocket(port);
        Logger logger = Logger.getLogger("Bucher");
		
	       System.out.println("waiting for client....");
        while (true) {
            Socket clientSock = serverSock.accept();
            SocketAddress clientAddress = clientSock.getRemoteSocketAddress();
            Thread thread = new Thread(new BucherThread(clientSock, clientAddress, logger));
            thread.start();
            if(clientSock.isConnected())
                System.out.println("Connected! Client IP : " + clientAddress);
        }
    }
}
 