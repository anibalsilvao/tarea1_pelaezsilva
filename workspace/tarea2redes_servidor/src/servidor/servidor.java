package servidor;

import java.io.*; 
import java.net.*; 
import java.util.StringTokenizer;

class TCPServer {    
	
	public static void main(String argv[]) throws Exception {    
		
		String clientSentence;            
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(6666);      
		
		while(true) {             
			
			Socket connectionSocket = welcomeSocket.accept();             
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));             
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());     
			
			clientSentence = inFromClient.readLine();    
			
			//System.out.println(clientSentence);

			StringTokenizer tokens = new StringTokenizer(clientSentence);
			String accion = tokens.nextToken();
			
			if(accion.equals("guardar")) {
				
				System.out.println("guardar");   
				
				String emisor = tokens.nextToken();
				String receptor = tokens.nextToken();
				String mensaje = tokens.nextToken();
			
				FileWriter fichero = null;
				PrintWriter pw = null;
			
				try {   	
				
					fichero = new FileWriter("Mensajes.txt",true);
					pw = new PrintWriter(fichero); 
					pw.println(emisor + " " + receptor + " " + mensaje + '\n'); 
				}       
			
				catch(Exception e) {
				
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
			
			if(accion.equals("ver")) {
				
				//System.out.println("ver");  
				
				int i = 0;
				String receptor = tokens.nextToken();
				
				FileInputStream leer = new FileInputStream("Mensajes.txt");
		        DataInputStream entrada = new DataInputStream(leer);
		        BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));

		        String datos;
		        String retornar = "";

		        // Leer el archivo linea por linea
		        while ((datos = buffer.readLine()) != null) {
		        			        
		        	StringTokenizer div = new StringTokenizer(datos, " "); 
		        	
		        	 while (div.hasMoreTokens()){
		        		 
		        		 String em = div.nextToken();
		        		 String re = div.nextToken();
		        		 String me = div.nextToken();
		        	 
		        		 if(receptor.equals(re)) {
		        		 
		        			 if(i == 0){
		        				 
		        				 i++;
		        				 retornar = em + " " + me + " ";
		        			 }	
		        		 
		        			 else {
		        				 
		        			 	 retornar = retornar + em + " " + me + " ";
		        			 }
		        	 
		        		 }
		        	 }	 
		        }
		        
		        //System.out.println(retornar);
		        outToClient.writeBytes(retornar + '\n');
		        
		        /*try {   	   

					Socket socketCliente = new Socket("localhost", 4444);
					DataOutputStream outToServer = new DataOutputStream(socketCliente.getOutputStream());
					outToServer.writeBytes(retornar);
					socketCliente.close();
				}   
				
				catch(Exception e) {
					e.printStackTrace();
				}    */
			}
		}
	}
}