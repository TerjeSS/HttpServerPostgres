import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpServer {
    private final ProductDao dao = new ProductDao();
    private Socket clientSocket;
    private final ServerSocket serversocket;
    private Path root;
    String responseBody;
    List<String> categoriesList = new ArrayList<>();
    List<Product> existingProducts = new ArrayList<>();
    private Map<String, String> queryMap;

    public HttpServer(int port) throws IOException {
        serversocket = new ServerSocket(port);
        handleClients();
    }
    private void handleClients() throws IOException {
          new Thread(() -> {
              try {
                  while(true) {
                      handleClient();
                  }
              } catch (IOException | SQLException e) {
                  e.printStackTrace();
              }
          }).start();
        }

    private void handleClient() throws IOException, SQLException {
        clientSocket = serversocket.accept();
        HttpMessage httpMessage = new HttpMessage(clientSocket);
        String[] requestLineArray = httpMessage.statusLine;
        String requestTarget = requestLineArray[1];
        Map<String, String> headerFields = httpMessage.headerFields;
        int questionPos = requestTarget.indexOf("?");
        String messageBody = httpMessage.messageBody;
        String fileTarget;


        if(questionPos != -1) {
            fileTarget = requestTarget.substring(0, questionPos);
        }else {
            fileTarget = requestTarget;
        }

        if (checkForFile(fileTarget)){
            responseBody = Files.readString(root.resolve(requestTarget.substring(1)));
            write200Response(responseBody);
        }
         if(fileTarget.equals("/api/searchProduct")){
             responseBody = "";
            responseBody = "TEst test";
            String queryParameter = requestTarget.substring(questionPos+1);
            int equalsPos = queryParameter.indexOf("=");
            String productName = queryParameter.substring(equalsPos+1);
            List<Product> productList = dao.searchProduct(productName);
            if(productList.size() != 0){
                for (Product product : productList) {
                    responseBody += "<li>Product: " + product.getName() + ". Category: " + product.getCategory() + "</li>";
                }
            }else {
                responseBody = "No products found :(";
            }
            write200Response(responseBody);

        }
        else if(fileTarget.equals("/api/newProduct")){
            queryMap = new HashMap<>();
            addToDatabase(messageBody);
            write200Response(responseBody);
        }
        else if(fileTarget.equals("/api/categoryOptions")){
            int value = 1;
            for (String s : categoriesList) {
                responseBody+= "<option>" + s + "</option>";
            }
            write200Response(responseBody);
        }
        else if(fileTarget.equals("/api/products")){
            responseBody = "";
            listAllProducts();
            write200Response(responseBody);
        }
        else{
            write404Response(fileTarget);
        }
    }

    private void listAllProducts() throws SQLException, IOException {
        List<Product> productList = dao.retrieveProducts();
        if(productList.size() == 0){
            responseBody = "The products database is currently empty. Please add products";
            write200Response(responseBody);
        }
        for (Product product : productList) {
            responseBody += "<li>Product: " + product.getName() + ". Category: " + product.getCategory() + "</li>";
        }
    }

    private void addToDatabase(String messageBody) throws SQLException {
        String[] queryParameters = messageBody.split("&");
        for (String queryParameter : queryParameters) {
            int index = queryParameter.indexOf("=");
            queryMap.put(queryParameter.substring(0,index), queryParameter.substring(index+1));
        }
        Product product = new Product(queryMap.get("productName"),queryMap.get("category"));
        dao.addToDatabase(product);
        responseBody = product.getName() +" added";
    }

    private void write404Response(String fileTarget) throws IOException {
        String notFound = "The requested file: " + fileTarget + ", was not found";
        String response =
                "HTTP/1.1 404 NOT FOUND\r\n" +
                        "Connection: Close\r\n" +
                        "Content type: text/html\r\n" +
                        "Content-Length: " + notFound.getBytes().length + "\r\n" +
                        "\r\n" +
                        notFound;
        clientSocket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
    }

    private void write200Response(String responseBody) throws IOException {
        String response =
                "HTTP/1.1 200 OK\r\n" +
                        "Connection: Close\r\n" +
                        "Content type: text/html\r\n" +
                        "Content-Length: " + responseBody.getBytes().length + "\r\n" +
                        "\r\n" +
                        responseBody;
        clientSocket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
    }

    private boolean checkForFile(String possibleFileTarget) {
        if(Files.exists(root.resolve(possibleFileTarget.substring(1)))){
            return true;
        }else{
            return false;
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        HttpServer server = new HttpServer(5555);
        server.setRoot(Paths.get("./src/main/resources"));
        server.setCategories(List.of("Candy","Fruit","Pastry"));
        /*Product product = new Product("Japp", "Candy");
        ProductDao productDao = new ProductDao();
        productDao.addToDatabase(product);*/
    }
    //Only for testing
    private void setProducts(List<Product> products) {
        existingProducts = products;
    }


    private void setRoot(Path path) {
        this.root = path;
    }
    private void setCategories(List<String> categories){
        this.categoriesList = categories;
    }
}
