import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LibraryRunner {

    //Constructor
    public LibraryRunner(){}


    //Main
    public static void main(String[] args){

        HashMap<String, List<String>> borrowedBooks = new HashMap<>();

        //Create library
        Library library = new Library("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\inventory.txt");

        //Read borrowedBooks
        borrowedBooks = readFile("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\borrowed_books.txt");

        //Add existing students

        Set<String> keys = borrowedBooks.keySet();
        for (String key : keys) {
            Student student = new Student("-", key);
            library.registerStudent(student);
        }


        Boolean Quit = false;
        while (!Quit) {

            //User prompt
            System.out.println("\nWelcome to the library! How can I help?");
            System.out.print("1: Register\n" +
                    "2: Sort Books\n" +
                    "3: Search Books\n" +
                    "4: Borrow Book\n" +
                    "5: Return Book\n" +
                    "6: Show Inventory Stats\n" +
                    "> ");

            //Create scanner object
            Scanner in = new Scanner(System.in);

            try { //Exception handling
                int input = in.nextInt();

                //method call
                if (input == 1) { //Register

                    //Prompt for Student name
                    System.out.print("\nStudent Name: ");
                    String newName = in.next();
                    //creates student and gives them random number
                    Random random = new Random();
                    int randomNumber = random.nextInt(1001); // generates a random number between 0 and 1000 (inclusive)
                    Student newStudent = new Student(newName, Integer.toString(library.getStudents().size() + randomNumber));
                    library.registerStudent(newStudent);
                    System.out.println("You registration number: " + newStudent.getRegistrationNumber());
                    // Register student into borrowedBooks
                    borrowedBooks.put(newStudent.getRegistrationNumber(), new ArrayList<>());
                    System.out.println("Student registered");

                } else if (input == 2) { //Sort Books
                    System.out.print("Sort by:\n1: ISBN\n2: Quantity\n> ");
                    int answer = in.nextInt();
                    if (answer == 1 || answer == 2) {
                        ArrayList<Book> sortedBooks = library.sort(answer);
                        for(Book element : sortedBooks){
                            if(element != null)
                                System.out.println(element.toString() + "\n");
                        }
                        if(answer == 1)
                            System.out.println("Books have been sorted by isbn.");
                        else
                            System.out.println("Books have been sorted by quantity.");
                    }
                    else
                        System.out.print("Invalid Input");

                } else if (input == 3) { // Search Books

                    //prompt for isbn
                    System.out.print("\nISBN: ");
                    String isbnSearch = in.next();

                    if(library.search((isbnSearch)) == null){
                        System.out.println("Sorry, no book was found with that isbn number.");
                    }
                    else{
                        System.out.println("Book Found: " + library.search(isbnSearch).name);
                    }

                } else if (input == 4) { //Borrow Book

                    //prompt for registration number
                    System.out.print("\nStudent registration number: ");
                    String regNumber = in.next();
                    try{
                        for(int i = 0; i < library.getStudents().size()+1; i++) {
                            if (regNumber.equals(library.getStudents().get(i).getRegistrationNumber())) {
                                //prompt for isbn
                                System.out.print("\nBook ISBN: ");
                                String isbn = in.next();
                                library.lend(isbn);
                                borrowedBooks.get(regNumber).add(isbn);
                                writeFile(hashToString(borrowedBooks));
                                break;
                            }
                        }
                    }catch(IndexOutOfBoundsException e){
                        System.out.println("That is not a valid student registration number");
                    }

                } else if (input == 5) { //Return Book

                    //prompt for registration number
                    System.out.print("\nStudent registration number: ");
                    String regNumber = in.next();

                    try{
                        for(int i = 0; i < library.getStudents().size()+1; i++) {
                            if (regNumber.equals(library.getStudents().get(i).getRegistrationNumber())) {
                                System.out.println("----------------------------------------------------");
                                System.out.println("Books that are borrowed by you:");
                                for(int j = 0; j < borrowedBooks.get(regNumber).size(); j++){

                                    System.out.println("Title: " + library.search(borrowedBooks.get(regNumber).get(j)).name + "\tISBN: " + borrowedBooks.get(regNumber).get(j));
                                }
                                System.out.println("----------------------------------------------------");
                                //prompt for isbn
                                System.out.print("\nBook ISBN you would like to return: ");
                                String isbn = in.next();
                                try{
                                    for(int k = 0; k < borrowedBooks.get(regNumber).size()+1; k++) {
                                        if (isbn.equals(borrowedBooks.get(regNumber).get(k))) {
                                            library.putBack(isbn);
                                            borrowedBooks.get(regNumber).remove(k);
                                            System.out.println("You have returned " + library.search(isbn).name);
                                            writeFile(hashToString(borrowedBooks));
                                            break;
                                        }
                                    }
                                }catch(IndexOutOfBoundsException e){
                                    System.out.println("That is not a valid isbn number, you do not have that book.");
                                }
                                break;
                            }
                        }
                    }catch(IndexOutOfBoundsException e){
                        System.out.println("That is not a valid student registration number");
                    }

                } else if (input == 6) { //Show inventory stats

                    //Display graph
                    InventoryChart chart = new InventoryChart(library.availableBooks(), "Fiction vs Nonfiction");
                    chart.displayGraph();

                }
            }
            catch(InputMismatchException | IllegalArgumentException e){
                System.out.println("Invalid input. Please enter a valid number from the list");
                in.next();
            }

            //Ask if user wants to exit
            System.out.print("\nQ to quit, C to continue\n> ");
            String input = in.next();
            while (!input.equalsIgnoreCase("q") && !input.equalsIgnoreCase("c")){
                System.out.print("> ");
                input = in.next();
            }
            if (input.equalsIgnoreCase("q"))
                Quit = true;
        }
    }

    //Methods
    private static HashMap<String, List<String>> readFile(String filePath){
        HashMap<String, List<String>> hash = new HashMap<>();
        try{
            // Create File and Scanner
            File file = new File(filePath);
            Scanner in = new Scanner(file);
            // Iterate through File
            while(in.hasNextLine())
            {
                //Split the lines
                String line = in.nextLine();
                String[] values = line.split(",");

                //Declare variables
                String regNum = values[0];
                String isbn = values[1];
                if(hash.containsKey(regNum)){
                    hash.get(regNum).add(isbn);
                }
                else{
                    hash.put(regNum, new ArrayList<String>());
                    hash.get(regNum).add(isbn);
                }
            }
            in.close();
            return hash;
        } catch(FileNotFoundException e){
            System.out.println("File was not found");
        }
        return null;
    }

    private static void writeFile(String contents){
        try {
            // Write file contents
            FileWriter writer = new FileWriter("C:\\C212 Java\\Assignments\\Final Project New\\Final Project\\borrowed_books.txt");
            writer.write(contents);
            writer.close();
        }catch (FileNotFoundException e){
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }


    private static String hashToString(HashMap<String, List<String>> hashMap){
        String result = "";
        //convert hashmap into a string
        for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();
            for (String value : valueList) {
                result += key + "," + value + "\n";
            }
        }
        return result;
    }

}
