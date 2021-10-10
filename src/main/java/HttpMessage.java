import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {

    private  Socket socket;
    Map<String, String> headerFields = new HashMap<>();
    int contentLength;
    String messageBody;
    String[] statusLine;
    public HttpMessage(Socket socket) throws IOException {
        this.socket = socket;
        statusLine = readLine(socket).split(" ");
        readHeaders();

        if(headerFields.containsKey("Content-Length")){
        contentLength = Integer.parseInt(headerFields.get("Content-Length"));
        }
        messageBody = readBody();
    }


    private String readBody() throws IOException {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < getContentLength(); i++) {
            builder.append((char) socket.getInputStream().read());
        }
        return builder.toString();
    }

         void readHeaders() throws IOException {
        String headerLine;
        while (!(headerLine = readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(":");
            headerFields.put(headerLine.substring(0,colonPos), headerLine.substring(colonPos+1).trim());
        }
    }

    static String readLine(Socket socket) throws IOException {
        StringBuilder builder = new StringBuilder();
        int c;
        while ( (c = socket.getInputStream().read()) != '\r'){
            builder.append( (char) c);
        }
            socket.getInputStream().read();
            return builder.toString();
    }

    public int getContentLength() {
        return contentLength;
    }
}
