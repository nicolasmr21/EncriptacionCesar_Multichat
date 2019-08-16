package model;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

  // The client socket
  private static Socket clientSocket = null;
  // The output stream
  private static PrintStream os = null;
  // The input stream
  private static DataInputStream is = null;

  private static boolean logged;

  private static BufferedReader inputLine = null;
  private static boolean closed = false;
  private static String hexaKey;
  private static int decimalKey;
  
  public static void main(String[] args) {
    // The default port.
    int portNumber = 2222;
    // The default host.
    //PLEASE PUT YOUR IPV4 ADDRESS BEFORE INITIALIZING THE SERVER OR CLIENT.
    String host = "localhost";

    if (args.length < 2) {
      System.out
          .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
              + "Now using host=" + host + ", portNumber=" + portNumber);
    } else {
      host = args[0];
      portNumber = Integer.valueOf(args[1]).intValue();
    }

    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
    try {
      clientSocket = new Socket(host, portNumber);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      os = new PrintStream(clientSocket.getOutputStream());
      is = new DataInputStream(clientSocket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host " + host);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to the host "
          + host);
    }

    /*
     * If everything has been initialized then we want to write some data to the
     * socket we have opened a connection to on the port portNumber.
     */
    if (clientSocket != null && os != null && is != null) {
      try {

        /* Create a thread to read from the server. */
        new Thread(new Client()).start();
        while (!closed) {
        	
          String input = inputLine.readLine().trim();
          
 
          if(!logged) {
        	  os.println(input); 
        	  logged = true;
        	  System.out.println("Me loguee");
          }else {
        	  System.out.println("Cesar");
              os.println(cifrarCesar(input.trim(), decimalKey));
          }
          
          
        }
        /*
         * Close the output stream, close the input stream, close the socket.
         */
        os.close();
        is.close();
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }
    }
  }

  /*
   * Create a thread to read from the server. (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    /*
     * Keep on reading from the socket till we receive "Bye" from the
     * server. Once we received that then we want to break.
     */
    String responseLine;
    try {
      while ((responseLine = is.readLine()) != null) {
    	if(responseLine.startsWith("<")) {
    		String words[] = responseLine.split(" ");
    		String user = words[0];
    		String message  = "";
    		for (int i = 1; i < words.length; i++) {
				message += " " +words[i].trim();
			}
    		responseLine = user +" " +descifrarCesar(message.trim(), decimalKey);	
    	}
    	if(responseLine.startsWith("KEY:")) {
         	hexaKey = responseLine.substring(4);
    		decimalKey = hexToDecimal(hexaKey);
    		
    	}
    		
    		System.out.println(responseLine);
    	
    	
        
        if (responseLine.indexOf("*** Bye") != -1)
          break;
      }
      closed = true;
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
  
  
  /**
	 * Metodo encargado de realizar la desencriptacion de Cesar, restando n posiciones al ASCII
	 * de cada caracter
	 * @param mensajeObtenidoCliente != Null && != ""
	 * @return
	 */
	private static String descifrarCesar(String mensajeObtenidoCliente, int key) {
		
		String respuesta = "";
		char caracter ;
		
		if(mensajeObtenidoCliente != "") {
			for (int i = 0; i < mensajeObtenidoCliente.length(); i++) {
				caracter = mensajeObtenidoCliente.charAt(i);
				caracter = (char) (caracter -key);
				respuesta += Character.toString((caracter)) + "";
			}
		}
		
		return respuesta;
		
	}
	
	

	/**
	 * Metodo encargado de realizar la encriptacion de Cesar, sumando n posiciones al ASCII
	 * de cada caracter
	 * @param mensajeObtenidoCliente != Null && != ""
	 * @return
	 */
	private static String cifrarCesar(String mensajeObtenidoCliente, int key) {
		
		String respuesta = "";
		char caracter ;
		
		if(mensajeObtenidoCliente != "") {
			for (int i = 0; i < mensajeObtenidoCliente.length(); i++) {
				caracter = mensajeObtenidoCliente.charAt(i);
				caracter = (char) (caracter +key);
				respuesta += Character.toString((caracter)) + "";
			}
		}
		
		return respuesta;
		
	}
	
	public static int hexToDecimal(String hex){  
	    String digits = "0123456789ABCDEF";  
	             hex = hex.toUpperCase();  
	             int val = 0;  
	             for (int i = 0; i < hex.length(); i++)  
	             {  
	                 char c = hex.charAt(i);  
	                 int d = digits.indexOf(c);  
	                 val = 16*val + d;  
	             }  
	             return val;  
	}
}