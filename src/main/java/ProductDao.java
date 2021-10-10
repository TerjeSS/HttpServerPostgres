import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;

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
}
