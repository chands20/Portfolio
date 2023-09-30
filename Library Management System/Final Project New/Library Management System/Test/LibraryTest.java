import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {


    FictionBook fb1 = new FictionBook("George Orwell", "78350", "1984", "328");
    FictionBook fb2 = new FictionBook("Charles Dickens", "81759", "A Tale of Two Cities", "400");
    FictionBook fb3 = new FictionBook("Aldous Huxley", "87562", "Brave New World", "311");
    NonfictionBook nfb1 = new NonfictionBook("Stephen Hawking", "82749", "A Brief History of Time", "212");
    NonfictionBook nfb2 = new NonfictionBook("Michelle Obama", "90560", "Becoming", "448");


    //Getters and Setters
    @Test
    void getSetFictionBooks() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");


        FictionBook Fbook1 = new FictionBook("George Orwell", "78350", "1984", "328");
        ArrayList<FictionBook> fictionBooks = new ArrayList<>();
        fictionBooks.add(Fbook1);
        library.setFictionBooks(fictionBooks);


        assertEquals(library.getFictionBooks(), fictionBooks);
    }


    @Test
    void getSetInventory() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");


        HashMap<String, Integer> inventory = new HashMap<>();
        inventory.put("78350", 21);
        inventory.put("82749",13);
        inventory.put("81759",18);
        inventory.put("90560",8);
        inventory.put("87562",19);


        assertEquals(inventory, library.getInventory());


        HashMap<String, Integer> inventory2 = new HashMap<>();
        inventory2.put("78350", 21);
        inventory2.put("82749",13);
        inventory2.put("81759",18);
        library.setInventory(inventory2);


        assertEquals(inventory2, library.getInventory());
    }


    @Test
    void getSetNonfictionBooks() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");


        NonfictionBook NFbook1 = new NonfictionBook("Stephen Hawking", "82749", "A Brief History of Time", "212");
        ArrayList<NonfictionBook> nonfictionBooks= new ArrayList<>();
        nonfictionBooks.add(NFbook1);
        library.setNonfictionBooks(nonfictionBooks);


        assertEquals(library.getNonfictionBooks(), nonfictionBooks);
    }


    @Test
    void getStudents() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");


        Student student1 = new Student("Tom", "1234");
        Student student2 = new Student("Jerry", "4321");


        library.registerStudent(student1);
        library.registerStudent(student2);


        ArrayList<Student> students = new ArrayList<>();
        students.add(student1);
        students.add(student2);


        assertEquals(library.getStudents(), students);
    }

    @Test
    void Testlend() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");

        assertEquals(21, library.getInventory().get("78350"));
        library.lend("78350");
        assertEquals(20, library.getInventory().get("78350"));
        library.lend("78350");
        assertEquals(19, library.getInventory().get("78350"));

        //reset values
        library.putBack("78350");
        library.putBack("78350");
    }

    @Test
    void TestputBack() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");

        assertEquals(21, library.getInventory().get("78350"));
        library.putBack("78350");
        assertEquals(22, library.getInventory().get("78350"));
        library.putBack("78350");
        assertEquals(23, library.getInventory().get("78350"));

        //Reset file
        library.lend("78350");
        library.lend("78350");
    }

    @Test
    void TestregisterStudent() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");
        Student s1 = new Student("Connor", "123");
        Student s2 = new Student("Luke", "321");
        ArrayList<Student> students = new ArrayList<>();

        assertEquals(students, library.getStudents());

        students.add(s1);
        library.registerStudent(s1);

        assertEquals(students, library.getStudents());

        students.add(s2);
        library.registerStudent(s2);

        assertEquals(students, library.getStudents());



    }

    @Test
    void Testsearch() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");

        // Test search method for an existing book
        String b1String = fb1.toString();
        Book result = library.search("78350");
        assertEquals(b1String, result.toString());

        String nfb1String = nfb1.toString();
        Book nfresult = library.search("82749");
        assertEquals(nfb1String, nfresult.toString());


    }

    @Test
    void Testsort() {

        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");
        //Sort by ISBN
        ArrayList<Book> sortedBooksByISBNCheck = new ArrayList<>();
        sortedBooksByISBNCheck.add(fb1);
        sortedBooksByISBNCheck.add(fb2);
        sortedBooksByISBNCheck.add(nfb1);
        sortedBooksByISBNCheck.add(fb3);
        sortedBooksByISBNCheck.add(nfb2);
        // Check sorted
        String sortedIsbn = "";
        for(int i = 0; i < sortedBooksByISBNCheck.size(); i++){
            sortedIsbn += sortedBooksByISBNCheck.get(i);
        }
        // Sort method
        String sort1 = "";
        ArrayList<Book> sort1ArrayList = library.sort(1);
        for(int i = 0; i < sort1ArrayList.size(); i++){
            sort1 += sort1ArrayList.get(i);
        }
        assertEquals(sortedIsbn, sort1);
        //Sort by Quantity
        //Check sorted
        ArrayList<Book> sortedByQuantityCheck = new ArrayList<>();
        sortedByQuantityCheck.add(nfb2);
        sortedByQuantityCheck.add(nfb1);
        sortedByQuantityCheck.add(fb2);
        sortedByQuantityCheck.add(fb3);
        sortedByQuantityCheck.add(fb1);
        String sortedQuantity = "";
        for(int i = 0; i < sortedByQuantityCheck.size(); i++){
            sortedQuantity += sortedByQuantityCheck.get(i);
        }
        // sort method
        String sort2 = "";
        ArrayList<Book> sort2ArrayList = library.sort(2);
        for(int i = 0; i < sort2ArrayList.size(); i++){
            sort2 += sort2ArrayList.get(i);
        }
        assertEquals(sortedQuantity, sort2);

    }

    @Test
    void TestavailableBooks() {
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\bookTests.txt");
        int[] totalBooks = library.availableBooks();
        int fiction = totalBooks[0];
        int nonfiction = totalBooks[1];
        assertEquals(58, fiction);
        assertEquals(21,nonfiction);


    }
}