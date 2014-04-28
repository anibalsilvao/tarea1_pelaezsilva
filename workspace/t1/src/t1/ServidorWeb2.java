package t1;

import java.net.*;

public final class ServidorWeb {
	public static void main(String argv[]) throws Exception {
		int port = 6789;
		ServerSocket WebSocket = new ServerSocket(port);
		while (true) {
			Socket connectionSocket = WebSocket.accept();
			PeticionHttp request = new PeticionHttp(connectionSocket);
			Thread thread = new Thread(request);
			thread.start();
		}
	}
}
