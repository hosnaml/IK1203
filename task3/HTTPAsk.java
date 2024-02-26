import java.net.*;
import java.nio.Buffer;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import tcpclient.TCPClient;

import java.io.*;

public class HTTPAsk {

    String version = null;
    String url = null;
    String method = null;
    String hostname = null;
    boolean shutdown = false;
    String string = null;
    Integer timeout = null;
    Integer limit = null;
    int port = 0;
    byte[] toServerBytes = new byte[0];
    String httpResponseMethod = "HTTP/1.1 200 OK";
    String httpResponseBody = "";

    public static byte[] maketoServerBytes(String... args) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String arg : args) {
            if (first) {
                first = false;
            } else {
                builder.append(" ");
            }
            builder.append(arg);
        }
        builder.append("\n");
        return builder.toString().getBytes();
    }

    public void parseHTTP(String request, int port){
        // TODO: Parse the HTTP request to extract the parameters
            // You can use the String.split() method or the URL class to do this
            // Split by space to get the URL part, remove GET and HTTP/1.1
            String [] parts = request.split(" ");
            //method is the first one.
            method = parts[0];
            //version is the last one
            version = parts[2];
            // The URL part is the second element
            url = parts[1];
            // Split the URL by "?" to get the parameters
            parts = url.split("?");
            // The first part is the path
            String path = parts[0];
            // The second part is the parameters
            String parameters = parts[1];
            // Split the parameters by "&" to get the key-value pairs
            String [] params = parameters.split("&");
            // Now loop thrpoigh the key-value pairs and split them by "="
            
            if(!method.toUpperCase().equals("GET")){
                System.out.println("HTTP/1.1 400 Bad Request");
            
            }
            if(!version.equals("HTTP/1.1")){
                System.out.println("HTTP/1.1 505 HTTP Version Not Supported");
            
            }
            
            for (String param : params) {
                String [] keyValue = param.split("=");
                // The first part is the key
                String key = keyValue[0];
                // The second part is the value
                String value = keyValue[1];
            }

            // TODO: Validate the request (check that all required parameters are present)
            

            for (String param : params) {
                String [] keyValue = param.split("=");
                String key = keyValue[0];
                String value = keyValue[1];
                if (key.equals("hostname")) {
                    hostname = value;
                } else if (key.equals("port")) {
                    port = Integer.parseInt(value);
                }
                else if (key.equals("limit")) {
                    limit = Integer.parseInt(value);
                }
                else if (key.equals("shutdown")) {
                    shutdown = Boolean.parseBoolean(value);
                }
                else if (key.equals("string")) {
                    string = value;
                    toServerBytes = maketoServerBytes(string);
                }
                else if (key.equals("timeout")) {
                    timeout = Integer.parseInt(value);
                }
        
            }
        }
           
    

    public void run(int port) throws Exception {
        // Create a ServerSocket object to listen for client requests
        ServerSocket serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();

        // Create input and output streams
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
       
         
        // Read the HTTP request from the client using readLine() provided by BufferedReader.
        String request = input.readLine();
    
        
        while (true) {
           if(request.endsWith("\r\n\r\n"))
                break;
            request += input.readLine();
        }


    clientSocket.shutdownInput(); 
    
    try{
        parseHTTP(request, port);
        TCPClient client = new TCPClient(shutdown, timeout,limit);
        this.httpResponseMethod = "HTTP/1.1 200 OK";
    } 
    catch (Exception e) {
        if (e instanceof SocketTimeoutException) {
            System.out.println("Encountered SocketTimeoutException");
            this.httpResponseMethod = "HTTP/1.1 408 Request Timeout";
        } else if (e instanceof UnknownHostException) {
            System.out.println("Encountered UnknownHostException");
            this.httpResponseMethod = "HTTP/1.1 404 Not Found";
        } else {
            System.out.println("Encountered in unexpected error with message: " + e.getLocalizedMessage());
            this.httpResponseMethod = "HTTP/1.1 400 Bad Request";
        }
    }
    
    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    PrintWriter writer = new PrintWriter(output, true);
     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O");
        String httpDate = formatter.format(ZonedDateTime.now(ZoneOffset.UTC));

        String httpResponseMessage = String.format(
"""
%s
Server: Ginga Ginga Pinga Pinga
Content-Type: text/plain; charset=utf-8;
Date: %s
Keep-Alive: timeout=5, max=1000
Connection: Keep-Alive

%s
""", httpResponseMethod, httpDate, httpResponseBody);

        writer.println(httpResponseMessage);
        clientSocket.shutdownOutput();
        serverSocket.close();
    
    }      // If the request is invalid, return a 400 Bad Request response with a suitable error message
    public static void main(String[] args) throws Exception {
        
        int port = Integer.valueOf(args[0]);
        HTTPAsk server = new HTTPAsk();
        server.run(port);
            
    }
    
}

