public class NonfictionBook extends Book{

    //Constructor
    public NonfictionBook(String author, String isbn, String name, String pages) {
        super(author, isbn, name, pages);
    }

    //Methods
    @Override
    public String toString() {
        return ("Nonfiction"
                + "\nAuthor: " + author
                + "\nISBN: " + isbn
                + "\nName: " + name
                + "\nPages: " + pages);
    }
}
