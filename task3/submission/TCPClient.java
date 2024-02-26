import java.net.*;
import java.io.*;

public class TCPClient {

    private static int BUFFERSIZE = 1024;
    private Integer TIMEOUT;
    private boolean SHUTDOWN;
    private Integer LIMIT;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.TIMEOUT = timeout;
        this.SHUTDOWN = shutdown;
        this.LIMIT = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        //use dynamic output stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Pre-allocate byte buffers for receiving
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        // TODO: Error handling

        try(Socket clientSocket = new Socket(hostname, port)) {

            //If (!TIMEOUT.equals(null)) clientSocket.setSoTimeout(TIMEOUT);

            OutputStream output = clientSocket.getOutputStream();
            //Sends the data to the server
            output.write(toServerBytes);
            output.flush();

            //After sending data shutdown is checked and if it's not null the client wil no longer send any data but it can receive data.
            if (SHUTDOWN == true) {
                clientSocket.shutdownOutput();
            }

            InputStream input = clientSocket.getInputStream();
            int bytesRead;

            try {
                if (TIMEOUT != null) {
                    clientSocket.setSoTimeout(TIMEOUT);
                }

                //reads the input from the server's input stream and writes it to the
                //dynamic array.
                while ((bytesRead = input.read(fromServerBuffer)) != -1) {
                    byteArrayOutputStream.write(fromServerBuffer, 0, bytesRead);
                    if(LIMIT != null && byteArrayOutputStream.size() >= LIMIT)
                        break;
                }
            } catch(SocketTimeoutException e){

            } finally{
                input.close();
                output.close();
                clientSocket.close();
            }

        }

        return byteArrayOutputStream.toByteArray();
    }
}
