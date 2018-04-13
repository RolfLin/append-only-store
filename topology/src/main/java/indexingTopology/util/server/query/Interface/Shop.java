package indexingTopology.util.server.query.Interface;

/**
 * Created by robert on 29/12/16.
 */
public class Shop {
    public int id;
    public double x;
    public double y;
    public double rating;
    public String name;
    public String image;

    public Shop(int id, double x, double y, double rating, String name, String image) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.rating = rating;
        this.name = name;
        this.image = image;
    }

    public String toString() {
        return String.format("id: %d, x: %f, y: %f, rating: %f, name: %s, image: %s", id, x, y, rating, name, image);
    }
}
