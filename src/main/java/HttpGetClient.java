import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpGetClient {
        Socket socket;
        int port;
        HttpMessage httpMessage;


    public HttpGetClient(String host,int port, String requestTarget) throws IOException {
       socket = new Socket(host, port);

        httpMessage = executeRequest(socket, host, requestTarget);
        
    }

    private HttpMessage executeRequest(Socket socket, String host,String requestTarget) throws IOException {
        String httpRequest =
                "GET " + requestTarget + " HTTP/1.1\r\n" +
                        "Host: " + host+"\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        socket.getOutputStream().write(httpRequest.getBytes(StandardCharsets.UTF_8));


        return new HttpMessage(socket);
    }

    public static void main(String[] args) throws IOException {
        HttpGetClient client = new HttpGetClient("www.httpbin.org", 80, "/html");
    }

    public int getPort() {
        return socket.getLocalPort();
    }
}
