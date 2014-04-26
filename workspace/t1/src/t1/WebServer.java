package t1;

import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer {
	
	public static void main(String argv[]) throws Exception {
		
		int port = 6789;
		// Establish the listen socket.
		ServerSocket WebSocket = new ServerSocket(port);
		
		while (true) {
			
			// Listen for a TCP connection request.
			Socket connectionSocket = WebSocket.accept();
			//Construct object to process HTTP request message
			HttpRequest request = new HttpRequest(connectionSocket);
      
			Thread thread = new Thread(request); //Create new thread to process
      
			thread.start(); //Start the thread
          
		}

	}
}