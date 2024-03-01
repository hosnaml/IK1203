import java.net.*;

public class ConcHTTPAsk {
    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(args[0]);
        // Create a ServerSocket object to listen for client requests
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Launching...");

        while(true){
        
            // When a new client contacts the server, the server creates a new thread.
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");
            // Step 2: Create a MyRunnable object.
            MyRunnable myRunnable = new MyRunnable(clientSocket, port);
            // Step 3: Create a Thread object, passing the MyRunnable object to the constructor.
            Thread thread = new Thread(myRunnable);
            // Step 4: Start the thread.
            thread.start();
        
        }
    }


}

