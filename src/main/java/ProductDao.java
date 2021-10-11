import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    PGSimpleDataSource dataSource;

    public ProductDao() {
        dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/product_database");
        dataSource.setUser("product_user");
        dataSource.setPassword("productusersittpassord");


        }

    public void addToDatabase(Product product) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO products (productName, productCategory) values(?,?)", PreparedStatement.RETURN_GENERATED_KEYS)){
                statement.setString(1, product.getName());
                statement.setString(2,product.getCategory());
                statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            if(rs.next()){
            product.setId(rs.getLong(1));
            }
            }
        }
    }
    public List<Product> searchProduct(String name) throws SQLException {
        List<Product> productList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE productName = ? ") ){
                statement.setString(1,name);
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    productList.add(new Product(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3)));                }
            }
        }
        return productList;
    }

    public List<Product> retrieveProducts() throws SQLException {
        List<Product> productList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM products")){
                ResultSet rs = statement.executeQuery();
                while (rs.next()){
                    productList.add(new Product(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3)));
                }
            }
        }
        return productList;
    }
}
