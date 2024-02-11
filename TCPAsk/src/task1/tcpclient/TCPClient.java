package tcpclient;
import javax.print.attribute.SetOfIntegerSyntax;
import java.net.*;
import java.io.*;

public class TCPClient {

    private static int BUFFERSIZE = 1024;
    
    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port) throws IOException {

        // Pre-allocate byte buffers for receiving
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        // TODO: Error handling

        Socket clientSocket = new Socket(hostname, port);

        int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);

        //System.out.write(fromServerBuffer, 0 , fromServerLength);

        clientSocket.close();

        return fromServerBuffer;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        if(toServerBytes.length == 0){
            return(askServer(hostname,port));
        }
        // Pre-allocate byte buffers for receiving
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        // TODO: Error handling

        Socket clientSocket = new Socket(hostname, port);

        clientSocket.getOutputStream().write(toServerBytes, 0, toServerBytes.length);

        int fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);

        //System.out.write(fromServerBuffer, 0 , fromServerLength);

        clientSocket.close();
        return fromServerBuffer;
    }
}
