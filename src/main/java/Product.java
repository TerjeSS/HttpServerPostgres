public class Product {
    String name;
    String category;
    long id;

    public Product(long id, String name, String category) {
        this.name = name;
        this.category = category;
    }

    public Product(String name, String category) {
        this.name = name;
        this.category = category;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
