package webCalendarSpring;

import webCalendarSpring.entities.BookEntity;

import java.util.Arrays;
import java.util.Objects;

/**
 * DTO for {@link BookEntity}
 */
public class Book {

    int id;
    String name;
    byte [] pic;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) && Arrays.equals(pic, book.pic);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(pic);
        return result;
    }

    public Book(int id, String name, byte[] pic) {
        this.id = id;
        this.name = name;
        this.pic = pic;
    }

    public Book() {
    }

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
}
