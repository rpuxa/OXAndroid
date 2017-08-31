package MultiPlayer;

import java.io.Serializable;

public class Player implements Serializable {
    private long id;
    private String name;
    private int rating;

    public Player(long id, String name, int rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Player))
            return false;
        Player pl = (Player) obj;
        return pl.getId() == this.getId();
    }
}
