public class Student {

    //Instance variables
    private String name;
    private String registrationNumber;

    //Constructor
    public Student(String name, String registrationNumber) {
        this.name = name;
        this.registrationNumber = registrationNumber;
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

}
