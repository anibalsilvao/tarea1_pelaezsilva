package cliente;

import java.net.*;

public final class ServidorWeb {
	
	public static int port = 5555;
	public static void main(String argv[]) throws Exception {
		
		ServerSocket WebSocket = new ServerSocket(port);
		while (true) {
			Socket connectionSocket = WebSocket.accept();
			PeticionHttp request = new PeticionHttp(connectionSocket);
			Thread thread = new Thread(request);
			thread.start();
		}
	}	
}
