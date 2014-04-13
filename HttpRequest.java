package t1;

import java.io.*;
import java.net.*;
import java.util.*;

public final class HttpRequest implements Runnable {
 
	final static String CRLF = "\r\n";//For convenience
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception {
		
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run() {
		
		try	{
			
			processRequest();
		}
		
		catch (Exception e) {
			
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {
		
		InputStream is = socket.getInputStream();
  
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
  	
		// Set up input stream filters.
  
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
  
		String requestLine = br.readLine();
   
		System.out.println();  //Echoes request line out to screen
		System.out.println(requestLine);
   
		//The following obtains the IP address of the incoming connection.
 
		InetAddress incomingAddress = socket.getInetAddress();
		String ipString= incomingAddress.getHostAddress();
		System.out.println("The incoming address is:   " + ipString);
					
		//String Tokenizer is used to extract file name from this class.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		String metodo = tokens.nextToken();  // skip over the method, which should be “GET”
		String fileName = tokens.nextToken();
		/////////////////
    
		if(fileName.equals("/ver.html") && metodo.equals("GET")) {
						 
			 FileWriter filewriter = null;
			 PrintWriter printw = null;
			 filewriter = new FileWriter("ver.html");//declarar el archivo
		     printw = new PrintWriter(filewriter);//declarar un impresor
		            
		     printw.println("<!DOCTYPE html>");
		     printw.println("<html>");
		     printw.println("<head>");
		     printw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		     printw.println("<title>Contactos</title>");
		     printw.println("</head>");   
		     printw.println("<body>");  
		     printw.println("<ul>");
		     
		     //Leer desde archivo servidor. 
		     // Abrimos el archivo
	         FileInputStream leer = new FileInputStream("Contactos.txt");
	         // Creamos el objeto de entrada
	         DataInputStream entrada = new DataInputStream(leer);
	         // Creamos el Buffer de Lectura
	         BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
	         String datos;
	         // Leer el archivo linea por linea
	         while ((datos = buffer.readLine()) != null) {
	        	 
	                // Imprimimos la línea por pantalla
	        	 	printw.println("<li>");
	        	    //System.out.println(datos);
	            	printw.println(datos);  
	            	printw.println("</li>");
	          }
	          // Cerramos el archivo
	          entrada.close();
		     
	         printw.println("</ul>");
	         printw.println("<a href=\"index.html\">Volver</a></li>");
		     printw.println("</body>");  
		     printw.println("</html>");
		     
		     printw.close();
			 
			
			//os.writeBytes("<html>coco \n</html>");
		}
    
		// Prepend a “.” so that file request is within the current directory.
		fileName = "." + fileName;
		
		if(metodo.equals("POST")) {
			
			FileWriter fichero = null;
			PrintWriter pw = null;
			String headerLine = null;
			int i=0;
			String nombre = null;
			String ip = null;
			String port = null;
			
			try {
	  
				/*while ((headerLine = br.readLine()).length() != 0) { //While the header still has text, print it
		    	System.out.println(headerLine);
		   		}*/
				while(true) {		   
					
					headerLine = br.readLine();
					System.out.println(headerLine);
					
					if(headerLine.length() == 0) {
						
						i++;
						
						if(i==2) {
							
							nombre = br.readLine();
						}
						
						if(i==3) {
							
							ip = br.readLine();
						}
						
						if(i==4) {
							
							port = br.readLine();
							break;
						}
					  }		   		   
				   }
				}
			
			catch(Exception e3) {
				
				System.out.println("sdjs" + br.readLine());
			}
			
       try {
    	   
           fichero = new FileWriter("Contactos.txt",true);
           pw = new PrintWriter(fichero); 
           pw.println(nombre + " " + ip + " " + port); 
       } 
       
       catch (Exception e) {
           e.printStackTrace();
       } 
       
       finally {
    	   
          try {
        	  
          // Nuevamente aprovechamos el finally para 
          // asegurarnos que se cierra el fichero.
          if (null != fichero)
             fichero.close();
          
          } 
          
          catch (Exception e2) {
        	  
             e2.printStackTrace();
          }
       }
		}
   
   // Open the requested file.
   FileInputStream fis = null;
   boolean fileExists = true;
   try {
    fis = new FileInputStream(fileName);
   } catch (FileNotFoundException e) {
    fileExists = false;
   }   

   //Construct the response message
   String statusLine = null; //Set initial values to null
   String contentTypeLine = null;
   String entityBody = null;
   if (fileExists) {
	   statusLine = "HTTP/1.1 200 OK: ";
	   contentTypeLine = "Content-Type: " +
	    contentType(fileName) + CRLF;
   } else {
	   statusLine = "HTTP/1.1 404 Not Found: ";
	   contentTypeLine = "Content-Type: text/html" + CRLF;
	   entityBody = "<html>" + "<head><title>Not Found</title></head>" + "<body>Not Found</body></html>";
   }
   //End of response message construction
 
   // Send the status line.
   os.writeBytes(statusLine);

   // Send the content type line.
   os.writeBytes(contentTypeLine);

   // Send a blank line to indicate the end of the header lines.
   os.writeBytes(CRLF);
   
   // Send the entity body.
   if (fileExists) {
    sendBytes(fis, os);
    fis.close();
   } else {
    os.writeBytes(entityBody);
   }
   
   os.close(); //Close streams and socket.
   br.close();
   socket.close();
 
  }

//Need this one for sendBytes function called in processRequest
private static void sendBytes(FileInputStream fis, OutputStream os)
throws Exception
{
   // Construct a 1K buffer to hold bytes on their way to the socket.
   byte[] buffer = new byte[1024];
   int bytes = 0;

   // Copy requested file into the socket’s output stream.
   while((bytes = fis.read(buffer)) != -1 ) {
      os.write(buffer, 0, bytes);
   }
}
private static String contentType(String fileName)
 {
 if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
  return "text/html";
 if(fileName.endsWith(".jpg"))
  return "text/jpg";
 if(fileName.endsWith(".gif"))
  return "text/gif";
 return "application/octet-stream";
 }
}