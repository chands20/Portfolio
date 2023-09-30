import java.util.ArrayList;

public interface LibraryManagementSystem {

    public abstract void inventory(String filePath);
    public abstract void lend(String isbn);
    public abstract void putBack(String isbn);
    public abstract void registerStudent(Student student);
    public abstract Book search(String isbn);
    public abstract ArrayList<Book> sort(int mode);

}
