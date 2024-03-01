import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.Buffer;
import java.io.*;

public class MyRunnable implements Runnable{

    private Socket clientSocket;
    private int port;
    String version = null;
    String url = null;
    String method = null;
    String hostname = null;
    boolean shutdown = false;
    String string = null;
    Integer timeout = null;
    Integer limit = null;
    int extPort = 0;
    byte[] toServerBytes = new byte[0];
    String httpResponseMethod = "HTTP/1.1 200 OK";
    String httpResponseBody = "";

    public MyRunnable(Socket clientSocket, int port){
        this.clientSocket = clientSocket;
        this.port = port;
    }

    public static byte[] maketoServerBytes(String ...args) {
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
    
    

    public void parseHTTP(String request, int port) throws Exception {
        // TODO: Parse the HTTP request to extract the parameters
        // You can use the String.split() method or the URL class to do this
        // Split by space to get the URL part, remove GET and HTTP/1.1
        String line = request.split("\r\n")[0];
        String[] parts = line.split(" ");
        // method is the first one.
        method = parts[0].trim();
        // version is the last one
        version = parts[2];
        // The URL part is the second element
        url = parts[1];
        // Split the URL by "?" to get the parameters
        parts = url.split("\\?");
        // The first part is the path
        String path = parts[0];
        // The second part is the parameters
        String parameters = parts[1];
        // Split the parameters by "&" to get the key-value pairs
        String[] params = parameters.split("&");
        // Now loop thrpoigh the key-value pairs and split them by "="

        if(!request.toLowerCase().contains("ask")){
            System.out.println("System resource not found");
            throw new BadRequestException();
        }
        if (!method.toUpperCase().equals("GET")) {
            System.out.println("Received " + method + " which was not a GET request");
            //throw new IllegalArgumentException("HTTP/1.1 400 Bad Request");
            throw new IncorrectRequestMethod();
        }
        if (!version.equals("HTTP/1.1")) {
            System.out.println("Received " + version + " which does not match required HTTP/1.1 version");
            //throw new IllegalArgumentException("HTTP/1.1 505 HTTP Version Not Supported");
            throw new BadRequestException();
        }
        for (String param : params) {
            String[] keyValue = param.split("=");
            // The first part is the key
            String key = keyValue[0];
            // The second part is the value
            String value = keyValue[1];
        }

        // TODO: Validate the request (check that all required parameters are present)

        for (String param : params) {
            String[] keyValue = param.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            if (key.equals("hostname")) {
                hostname = value;
            } else if (key.equals("port")) {
                extPort = Integer.parseInt(value);
            } else if (key.equals("limit")) {
                limit = Integer.parseInt(value);
            } else if (key.equals("shutdown")) {
                shutdown = Boolean.parseBoolean(value);
            } else if (key.equals("string")) {
                string = value;
                toServerBytes = maketoServerBytes(string);
            } else if (key.equals("timeout")) {
                timeout = Integer.parseInt(value);
            }

        }

    }

    public void run() throws RuntimeException{

        try {
            // Create input stream
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //System.out.println("Marko!");

            // Read the HTTP request from the client using readLine() provided by
            // BufferedReader.
            StringBuilder requestBuilder = new StringBuilder();

            String inputLine;
            while (!(inputLine = input.readLine()).isEmpty()) {
                requestBuilder.append(inputLine);
                requestBuilder.append("\r\n"); // Add the newline character back in
            }
            String request = requestBuilder.toString();

            //System.out.println("Polo!");

            clientSocket.shutdownInput();

        
            parseHTTP(request, port);
            TCPClient client = new TCPClient(shutdown, timeout, limit);

            this.httpResponseBody = new String(client.askServer(hostname, extPort, toServerBytes),
                    StandardCharsets.UTF_8);

            this.httpResponseMethod = "HTTP/1.1 200 OK";
        } catch (Exception e) {

            if (e instanceof SocketTimeoutException) { 
                System.out.println("Socket timeout occurred, sending 408");
                this.httpResponseMethod = "HTTP/1.1 408 Request Timeout";
            } else if (e instanceof UnknownHostException) {
                System.out.println("Encountered UnknownHostException");
                this.httpResponseMethod = "HTTP/1.1 404 Not Found";
            } else if (e instanceof BadRequestException || e instanceof IncorrectRequestMethod) {
                System.out.println("Either the HTTP protocol was wrong or GET was not provided");
                this.httpResponseMethod = "HTTP/1.1 400 Bad Request";
            } else {
                System.out.println("Encountered an exception: " + e.getLocalizedMessage());
                this.httpResponseMethod = "HTTP/1.1 400 Bad Request";
            }
        }

        try{BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            PrintWriter writer = new PrintWriter(output, true);
    
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O");
            String httpDate = formatter.format(ZonedDateTime.now(ZoneOffset.UTC));
    
            String httpResponseMessage = String.format(
                    """
                            %s
                            Server: Prolog prolog prolog
                            Content-Type: text/plain; charset=utf-8;
                            Date: %s
                            Keep-Alive: timeout=5, max=1000
                            Connection: Keep-Alive
    
                            %s
                            """, httpResponseMethod, httpDate, httpResponseBody);
    
            writer.println(httpResponseMessage);
            clientSocket.shutdownOutput();}
        catch(Exception e){
            e.printStackTrace();
        }
        //serverSocket.close();

}

    
}