import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpPostClient {
    Socket socket;
    int port;
    HttpMessage httpMessage;


    public HttpPostClient(String host,int port, String requestTarget, String messageBody) throws IOException {
        socket = new Socket(host, port);
        httpMessage = executeRequest(socket, host, requestTarget, messageBody);

    }

    private HttpMessage executeRequest(Socket socket, String host,String requestTarget, String messageBody) throws IOException {
        String httpRequest =
                "POST " + requestTarget + " HTTP/1.1\r\n" +
                        "Host: " + host+"\r\n" +
                        "Connection: close\r\n" +
                        "Content-Length" + messageBody.getBytes().length + "\r\n" +
                        "\r\n" +
                        messageBody;

        socket.getOutputStream().write(httpRequest.getBytes(StandardCharsets.UTF_8));


        return new HttpMessage(socket);
    }


    public int getPort() {
        return socket.getLocalPort();
    }
}
