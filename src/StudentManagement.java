import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Scanner;

class Student {
    private String studentId;
    private String name;
    private double marks;

    public Student(String studentId, String name, double marks) {
        this.studentId = studentId;
        this.name = name;
        this.marks = marks;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "ID: " + studentId + ", Name: " + name + ", Marks: " + marks;
    }
}

public class StudentManagement {

    private static int nextId = 1;

    //private static ArrayList<Student> students = new ArrayList<>();

    private static Stack<String> undoStack = new Stack<>();

    private static LinkedList<Student> students = new LinkedList<>();

    private static int studentCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nStudent Management System");
            System.out.println("1. Add Student");
            System.out.println("2. Edit Student");
            System.out.println("3. Delete Student");
            System.out.println("4. Search Student by ID");
            System.out.println("5. Search Student by Name");
            System.out.println("6. Sort Students by Name (Bubble Sort)");
            System.out.println("7. Sort Students by Marks (Insertion Sort)");
            System.out.println("8. Display All Students");
            System.out.println("9. Undo Last Action");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    addStudent(scanner);
                    break;
                case 2:
                    editStudent(scanner);
                    break;
                case 3:
                    deleteStudent(scanner);
                    break;
                case 4:
                    searchById(scanner);
                    break;
                case 5:
                    searchByName(scanner);
                    break;
                case 6:
                    sortByNameBubble();
                    break;
                case 7:
                    sortByMarksInsertion();
                    break;
                case 8:
                    displayStudents();
                    break;
                case 9:
                    undoLastAction();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    // Thêm sinh viên
    public static void addStudent(Scanner scanner) {
        if (studentCount >= 100) {
            System.out.println("Cannot add more students. Student array is full.");
            return;
        }

        // Validate the student name
        String name = "";
        while (name.trim().isEmpty()) {
            System.out.print("Enter student name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty. Please enter a valid name.");
            } else if (name.matches(".*\\d.*")) {
                System.out.println("Name cannot contain numbers. Please enter a valid name.");
                name = ""; // Reset the name if invalid
            }
        }

        // Validate the student marks
        double marks = 0;
        boolean validMarks = false;
        while (!validMarks) {
            try {
                System.out.print("Enter student marks (0-10): ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Marks cannot be empty. Please enter valid marks.");
                    continue;
                }

                marks = Double.parseDouble(input);
                if (marks < 0 || marks > 10) {
                    System.out.println("Marks must be between 0 and 10. Please enter again.");
                } else {
                    validMarks = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Marks must be a number.");
            }
        }

        Student newStudent = new Student("S" + nextId++, name, marks);
        students.add(newStudent);
        studentCount++;
        System.out.println("Student added successfully.");
        undoStack.push("add");
    }


    // Sửa thông tin sinh viên
    public static void editStudent(Scanner scanner) {
        System.out.print("Enter student ID to edit: ");
        String id = scanner.nextLine();

        Student student = findStudentById(id);
        if (student != null) {
            // Validate the new student name
            String newName = "";
            while (newName.trim().isEmpty()) {
                System.out.print("Enter new name: ");
                newName = scanner.nextLine().trim();
                if (newName.isEmpty()) {
                    System.out.println("Name cannot be empty. Please enter a valid name.");
                } else if (newName.matches(".*\\d.*")) {
                    System.out.println("Name cannot contain numbers. Please enter a valid name.");
                    newName = ""; // Reset if invalid
                }
            }

            // Validate the new student marks
            double newMarks = 0;
            boolean validMarks = false;
            while (!validMarks) {
                try {
                    System.out.print("Enter new marks (0-10): ");
                    String input = scanner.nextLine().trim();

                    if (input.isEmpty()) {
                        System.out.println("Marks cannot be empty. Please enter valid marks.");
                        continue;
                    }

                    newMarks = Double.parseDouble(input);
                    if (newMarks < 0 || newMarks > 10) {
                        System.out.println("Marks must be between 0 and 10. Please enter again.");
                    } else {
                        validMarks = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Marks must be a number.");
                }
            }

            // Update the student's name and marks
            student.setName(newName);
            student.setMarks(newMarks);
            System.out.println("Student information updated successfully.");
            undoStack.push("edit");
        } else {
            System.out.println("Student with ID " + id + " not found.");
        }
    }


    // Xóa sinh viên
    public static void deleteStudent(Scanner scanner) {
        try {
            System.out.print("Enter student ID to delete: ");
            String id = scanner.nextLine();

            Student student = findStudentById(id);
            if (student != null) {
                students.remove(student);  // Remove student from the list
                studentCount--;  // Decrease the count of students
                System.out.println("Student deleted successfully.");
                undoStack.push("delete");  // Push the delete action to undoStack
            } else {
                System.out.println("Student with ID " + id + " not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting the student: " + e.getMessage());
        }
    }

    // Tìm kiếm sinh viên theo ID
    public static void searchById(Scanner scanner) {
        try {
            System.out.print("Enter student ID to search: ");
            String id = scanner.nextLine();

            // Perform a linear search through the list of students
            boolean found = false;
            for (Student student : students) {
                if (student.getStudentId().equals(id)) {
                    System.out.println("Found student: " + student);
                    found = true;
                    break;  // Exit the loop once the student is found
                }
            }

            if (!found) {
                System.out.println("Student with ID " + id + " not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while searching for the student: " + e.getMessage());
        }
    }


    // Tìm kiếm sinh viên theo tên
    public static void searchByName(Scanner scanner) {
        try {
            System.out.print("Enter student name to search: ");
            String name = scanner.nextLine().toLowerCase();
            students.sort((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
            int left = 0, right = students.size() - 1;
            boolean found = false;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                Student midStudent = students.get(mid);
                int comparison = midStudent.getName().toLowerCase().compareTo(name);

                if (comparison == 0) {
                    System.out.println("Found student: " + midStudent);
                    found = true;
                    break;
                } else if (comparison < 0) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            if (!found) {
                System.out.println("No student found with name " + name);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while searching for the student: " + e.getMessage());
        }
    }


    // Tìm sinh viên theo ID
    private static Student findStudentById(String id) {
        for (Student student : students) {
            if (student.getStudentId().equals(id)) {
                return student;
            }
        }
        return null;
    }

    // Sắp xếp sinh viên theo tên (Bubble Sort)
    public static void sortByNameBubble() {
        try {
            for (int i = 0; i < students.size() - 1; i++) {
                for (int j = 0; j < students.size() - i - 1; j++) {
                    if (students.get(j).getName().compareToIgnoreCase(students.get(j + 1).getName()) > 0) {
                        Student temp = students.get(j);
                        students.set(j, students.get(j + 1));
                        students.set(j + 1, temp);
                    }
                }
            }
            System.out.println("Students sorted by name (Bubble Sort):");
            displayStudents();
        } catch (Exception e) {
            System.out.println("An error occurred while sorting students: " + e.getMessage());
        }
    }


    // Sắp xếp sinh viên theo điểm (Insertion Sort)
    public static void sortByMarksInsertion() {
        try {
            for (int i = 1; i < students.size(); i++) {
                Student key = students.get(i);
                int j = i - 1;

                // Move elements of students[0..i-1], that are less than key.getMarks(), to one position ahead
                while (j >= 0 && students.get(j).getMarks() < key.getMarks()) {
                    students.set(j + 1, students.get(j));
                    j--;
                }
                students.set(j + 1, key);
            }

            System.out.println("Students sorted by marks (Insertion Sort):");
            displayStudents();
        } catch (Exception e) {
            System.out.println("An error occurred while sorting students by marks: " + e.getMessage());
        }
    }


    // Hiển thị tất cả sinh viên
    public static void displayStudents() {
        try {
            if (students.isEmpty()) {
                System.out.println("No students available.");
                return;
            }

            for (Student student : students) {
                if (student != null) {  // Check if the student object is not null before printing
                    System.out.println(student);
                } else {
                    System.out.println("Encountered a null student object.");
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while displaying students: " + e.getMessage());
        }
    }


    // Thực hiện undo (hủy thao tác cuối cùng)
    public static void undoLastAction() {
        if (undoStack.isEmpty()) {
            System.out.println("No actions to undo.");
            return;
        }

        String lastAction = undoStack.pop();
        switch (lastAction) {
            case "add":
                if (studentCount > 0) {
                    students.remove(studentCount - 1);
                    studentCount--;
                    System.out.println("Last add action undone.");
                } else {
                    System.out.println("No students to undo.");
                }
                break;
            case "edit":
                System.out.println("Edit operation cannot be undone.");
                break;
            case "delete":
                System.out.println("Delete operation cannot be undone.");
                break;
            default:
                System.out.println("Unknown action.");
        }
    }
}
