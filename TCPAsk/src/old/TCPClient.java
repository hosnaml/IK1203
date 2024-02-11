import java.net.*;
import java.io.IOException;
public class TCPClient {
    private static int BUFFERSIZE = 1024;

    public byte[] askServer(String hostname, int port, byte[] bytesToServer) throws IOException {
        while()
    }
    public byte[] askServer(String hostname, int port) throws IOException {

    }
    public static void main(String argv[]) throws Exception
    {
    // Pre-allocate byte buffers for reading/receiving
        byte[] fromUserBuffer = new byte[BUFFERSIZE];
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        //Create a socket of a given type.
        Socket clientSocket = new Socket("hostname", 6789);

        //Open connection on the socket to a port on a host. block (wait) until connection is accepted or denied.
        int fromUserLength = System.in.read(fromUserBuffer); // User input

        clientSocket.getOutputStream().write(fromUserBuffer, 0, fromUserLength);

        //Receive data on the connection. Block until data is available.
        int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);

        System.out.print("FROM SERVER: ");
        // Use print method since it is a string
        System.out.write(fromServerBuffer, 0, fromServerLength);
        //Close connection and terminate socket.
        clientSocket.close();
    }




}
