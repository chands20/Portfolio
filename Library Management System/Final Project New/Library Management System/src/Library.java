import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Library implements LibraryManagementSystem {


    //Instance Variables
    private ArrayList<FictionBook> fictionBooks;
    private HashMap<String, Integer> inventory;
    private ArrayList<NonfictionBook> nonfictionBooks;
    private ArrayList<Student> students;

    //Extra instance variables
    private String file;
    private int totalFiction = 0;
    private int totalNonFiction = 0;


    //Constructor
    public Library(String filePath) {

        //Initialize instance variables
        fictionBooks = new ArrayList<>();
        inventory = new HashMap<>();
        nonfictionBooks = new ArrayList<>();
        students = new ArrayList<>();
        file = filePath;


        //Method call to parse file
        inventory(filePath);
    }


    //Getters and Setters
    public ArrayList<FictionBook> getFictionBooks() {
        return fictionBooks;
    }


    public void setFictionBooks(ArrayList<FictionBook> fictionBooks) {
        this.fictionBooks = fictionBooks;
    }


    public HashMap<String, Integer> getInventory() {
        return inventory;
    }


    public void setInventory(HashMap<String, Integer> inventory) {
        this.inventory = inventory;
    }


    public ArrayList<NonfictionBook> getNonfictionBooks() {
        return nonfictionBooks;
    }


    public void setNonfictionBooks(ArrayList<NonfictionBook> nonfictionBooks) {
        this.nonfictionBooks = nonfictionBooks;
    }


    public ArrayList<Student> getStudents() {
        return students;
    }




    //Inherited Methods
    @Override
    public void inventory(String filePath) {


        //Read file
        try {
            //Create file and Scanner for file
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            //Process each line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");
                //Declare variables
                String isbn = values[0];
                String name = values[1];
                String author = values[2];
                String pages = values[3];
                String quantity = values[4];
                String type = values[5];
                //Add to HashMap
                inventory.put(isbn, Integer.parseInt(quantity));
                //Create books and add to list
                if (type.equalsIgnoreCase("fiction")) {
                    FictionBook newBook = new FictionBook(author, isbn, name, pages);
                    fictionBooks.add(newBook);
                    totalFiction += Integer.parseInt(quantity);
                } else if (type.equalsIgnoreCase("nonfiction")) {
                    NonfictionBook newBook = new NonfictionBook(author, isbn, name, pages);
                    nonfictionBooks.add(newBook);
                    totalNonFiction += Integer.parseInt(quantity);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) { //Exception handling
            System.out.println("File not found.");
        }
    }


    @Override
    public void lend(String isbn) {
        //Check if book is in inventory
        if (inventory.containsKey(isbn)) {
            int currentValue = inventory.get(isbn);
            // Check if the current value is greater than 0 before decreasing it
            if (currentValue > 0) {
                // Update the value by decrementing it by 1
                int updatedValue = currentValue - 1;
                // Put the updated value back into the HashMap
                inventory.put(isbn, updatedValue);
                writeFile(BookstoString());
                System.out.println("You have borrowed " + search(isbn).name);
            } else {
                System.out.println("That book is not available, all books are being lent out.");
            }
        } else {
            System.out.println("We do not carry that book");
        }
    }




    @Override
    public void putBack(String isbn) {


        //Check if book is in inventory
        if (inventory.containsKey(isbn)) {
            int currentValue = inventory.get(isbn);
            int updatedValue = currentValue + 1;
            inventory.put(isbn, updatedValue);
            writeFile(BookstoString());
        } else {
            System.out.println("We do not carry that book");
        }
    }


    @Override
    public void registerStudent(Student student) {
        try {
            for (Student element : students) {
                if (element.getName().equals(student.getName())) {
                    throw new IllegalArgumentException("Student is already in list");
                }
            }
            students.add(student);

        } catch (IllegalArgumentException e) {
            System.out.println("Student is registered, try again");
        }
    }


    @Override
    public Book search(String s) {
        for (int i = 0; i < fictionBooks.size(); i++) {
            if (fictionBooks.get(i).isbn.equals(s)) {
                return fictionBooks.get(i);
            }
        }
        for (int i = 0; i < nonfictionBooks.size(); i++) {
            if (nonfictionBooks.get(i).isbn.equals(s)) {
                return nonfictionBooks.get(i);
            }
        }
        return null;
    }


    @Override
    public ArrayList<Book> sort(int n) {
        {
            //Create ArrayList
            ArrayList<Book> sortedLists = new ArrayList<>(); //Will return
            ArrayList<String> isbnList = new ArrayList<>(inventory.keySet());//Will contain the isbn

            //Sort by isbn
            if (n == 1)
                Collections.sort(isbnList);
                //sort by quantity
            else if (n == 2)
                Collections.sort(isbnList, (s1, s2) -> inventory.get(s1).compareTo(inventory.get(s2)));
            //Iterate through bookLists and find each isbn, adding them to the ArrayList in order
            for (String isbn : isbnList) {
                sortedLists.add(search(isbn));
            }
            //Update inventory file based off sorted List
            writeFile(formatListOfBooks(sortedLists));
            //Return
            return sortedLists;
        }
    }

    public int[] availableBooks() {
        int[] availableBooks = {totalFiction, totalNonFiction};
        return availableBooks;
    }

    private void writeFile(String contents) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(contents);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }


    private String BookstoString() {

        //Create ArrayList containing all books in order of list
        ArrayList<Book> allBooks = new ArrayList<>();
        for (String key : inventory.keySet()) {

            // Check if the key is present in the fictionBooks list
            for (Book book : fictionBooks) {
                if (book.getIsbn().equals(key)) {
                    allBooks.add(book);
                    break;
                }
            }
            // Check if the key is present in the nonfictionBooks list
            for (Book book : nonfictionBooks) {
                if (book.getIsbn().equals(key)) {
                    allBooks.add(book);
                    break;
                }
            }
        }
        return (formatListOfBooks(allBooks));
    }


    private String formatListOfBooks(List<Book> books){
        //Convert each element of the list to string format and add to result
        String result = "";
        for (Book book : books) {
            String currentString = book.getIsbn() +
                    "," + book.getName() +
                    "," + book.getAuthor() +
                    "," + book.getPages() +
                    "," + inventory.get(book.getIsbn()) +
                    ",";
            if (book instanceof FictionBook)
                currentString += "fiction\n";
            else if (book instanceof NonfictionBook)
                currentString += "nonfiction\n";
            result += currentString;
        }
        return result;
    }

}
