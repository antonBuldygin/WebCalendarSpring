package webCalendarSpring.entities;

import jakarta.persistence.*;

import java.util.Arrays;

@Entity
@Table(name = "book_entity")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "pic")
    @Lob
    private byte[] pic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "BookEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pic=" + Arrays.toString(pic) +
                '}';
    }
}