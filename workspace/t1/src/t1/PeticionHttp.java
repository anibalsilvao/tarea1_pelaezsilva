package t1;

import java.io.*;
import java.net.*;
import java.util.*;

public final class PeticionHttp implements Runnable { 
	final static String CRLF = "\r\n"; // Mayor orden en escritura y ruteo
	Socket socket;
	// Constructor
	public PeticionHttp(Socket socket) throws Exception {
		this.socket = socket;
	}
	// Implementaci�n del m�todo run()
	public void run() {
		try	{
			processRequest();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	private void processRequest() throws Exception {
		InputStream instream = socket.getInputStream();  
		DataOutputStream outstream = new DataOutputStream(socket.getOutputStream());  	 
		BufferedReader lecbuffer = new BufferedReader(new InputStreamReader(instream));
		// A continuaci�n con propositos de ruteo, se imprimen las lineas que arroja la solicitud
		String lineareq = lecbuffer.readLine();   
		System.out.println();
		System.out.println(lineareq); 
		
		InetAddress direccion = socket.getInetAddress();
		String ipstr= direccion.getHostAddress();
		System.out.println("Direcci�n IP: " + ipstr);

		StringTokenizer tokens = new StringTokenizer(lineareq);
		String metodo = tokens.nextToken();
		String nomarch = tokens.nextToken();
		// Se trabaja cono el metodo get
		if(nomarch.equals("/ver.html") && metodo.equals("GET")) {
			 FileWriter filewriter = null;
			 PrintWriter printw = null;
			 filewriter = new FileWriter("ver.html"); // Declarar el archivo
		     printw = new PrintWriter(filewriter); // Declarar un impresor

		     printw.println("<!DOCTYPE html>");
		     printw.println("<html>");
		     printw.println("<head>");
		     printw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		     printw.println("<title>Contactos</title>");
		     printw.println("</head>");   
		     printw.println("<body> <center>"); 
		     printw.println("<h2> <b> VER CONTACTOS </b> </h2>"); 
		     printw.println("<table style=\"text-align:center\" width=\"50%\" border=\"1\" bordercolor=\"#000000\">");
		     printw.println("<tr> <th> Nombre </th> <th> IP </th> <th> Puerto </th> </tr>");

		     // Leer desde archivo servidor. 
		     // Abrimos el archivo
	         FileInputStream leer = new FileInputStream("Contactos.txt");
	         // Creamos el objeto de entrada
	         DataInputStream entrada = new DataInputStream(leer);
	         // Creamos el Buffer de Lectura
	         BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));

	         String datos;

	         // Leer el archivo linea por linea
	         while ((datos = buffer.readLine()) != null) {
	        	 StringTokenizer div = new StringTokenizer(datos, " "); 
	        	 while (div.hasMoreTokens()){
	        		 String nombre = div.nextToken();
	        		 String dip = div.nextToken();
	        		 String puerto = div.nextToken();

	                // Imprimimos la l�nea por pantalla
	        	 	printw.println("<tr>");
	            	printw.println("<td>" + nombre + "</td>");
	            	printw.println("<td>" + dip + "</td>");
	            	printw.println("<td>" + puerto + "</td>");
	            	printw.println("</tr>");
	            }
	         }
	         // Cerramos el archivo
	         entrada.close();

	         printw.println("</table>");
	         printw.println("</br>");
	         printw.println("<a href=\"index.html\" style='text-decoration:none;color:black'> <b> Volver </b> </a>");
		     printw.println("</center> </body>");  
		     printw.println("</html>");
		     printw.close();
		}
		// Se agrega el "." al nombre del archivo para conservar el directorio
		nomarch = "." + nomarch;
		//Se trabaja con el metodo Post
		if(metodo.equals("POST")) {
			FileWriter fichero = null;
			PrintWriter pw = null;
			String headerLine = null;
			int i=0;
			String nombre = null;
			String ip = null;
			String port = null;

			try {
				while(true){		   
					headerLine = lecbuffer.readLine();
					System.out.println(headerLine);
					if(headerLine.length() == 0){
						i++;
						if(i==2){
							nombre = lecbuffer.readLine();
						}
						if(i==3){
							ip = lecbuffer.readLine();
						}
						if(i==4){
							port = lecbuffer.readLine();
							break;
						}
					}		   		   
				}
			}
			catch(Exception e3){
				System.out.println(lecbuffer.readLine());
			}
			try{   	   
				fichero = new FileWriter("Contactos.txt",true);
				pw = new PrintWriter(fichero); 
				pw.println(nombre + " " + ip + " " + port); 
			}        
			catch(Exception e){
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
   
	// Se abre el archivo solicitado para realizar la respuesta
	FileInputStream fis = null;
	boolean existencia = true;
	try{
		fis = new FileInputStream(nomarch);
	}
	catch(FileNotFoundException e){
		existencia = false;
	}   

   // Se crea la respuesta a la peticion
   String status = null;
   String tipocontenido = null;
   String cuerpo = null;
   if(existencia){
	   status = "HTTP/1.1 200 OK: ";
	   tipocontenido = "Contenido: " +
	   contentType(nomarch) + CRLF;
	}else{
	   status = "HTTP/1.1 404 ERROR: ";
	   tipocontenido = "Contenido: text/html" + CRLF;
	   cuerpo = "<html>" + "<head><title>No se encuentra la dirrecion</title></head>" + "<body>No se encuentra la dirrecion</body></html>";
	}
	// Se termina la construcci�n de la respuesta
	// Se env�a la linea de estado
	outstream.writeBytes(status);
	// Contenido
	outstream.writeBytes(tipocontenido);
	// Linea en blanco para el orden
	outstream.writeBytes(CRLF);  
	// El cuerpo de la llamada
	if(existencia){
		sendBytes(fis, outstream);
		fis.close();
	}else{
		outstream.writeBytes(cuerpo);
	}
   
	outstream.close(); // Se cierran las variables
	lecbuffer.close();
	socket.close();
	}

	// Necesario para la funci�n de escritura
	private static void sendBytes(FileInputStream fis, OutputStream outstream)
	throws Exception{
		// Se construye un buffer para el socket
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Se copia el archivo requerido en el stream de salida
		while((bytes = fis.read(buffer)) != -1 ) {
			outstream.write(buffer, 0, bytes);
		}
	}
	// De acuerdo a lo investigado lo �ptimo es mantener varios tipos de archivos para su reconocimiento, aunque en la tarea se usen solo .html
	private static String contentType(String fileName){
		if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
		return "text/html";
		if(fileName.endsWith(".jpg"))
		return "text/jpg";
		if(fileName.endsWith(".gif"))
		return "text/gif";
		return "application/octet-stream";
	}
}
