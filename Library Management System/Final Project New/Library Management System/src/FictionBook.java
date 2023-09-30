public class FictionBook extends Book{

    //Constructor
    public FictionBook(String author, String isbn, String name, String pages) {
        super(author, isbn, name, pages);
    }

    //Methods
    @Override
    public String toString() {
        return ("Fiction"
                + "\nAuthor: " + author
                + "\nISBN: " + isbn
                + "\nName: " + name
                + "\nPages: " + pages);
    }
}
