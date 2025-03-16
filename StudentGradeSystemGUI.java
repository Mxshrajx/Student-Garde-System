import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Student implements Serializable {
    String name;
    int id, english, marathi, physics, chemistry, maths;
    float percentage;
    char grade;

    public Student(String name, int id, int english, int marathi, int physics, int chemistry, int maths) {
        this.name = name;
        this.id = id;
        this.english = english;
        this.marathi = marathi;
        this.physics = physics;
        this.chemistry = chemistry;
        this.maths = maths;
        this.percentage = calculatePercentage();
        this.grade = calculateGrade();
    }

    private float calculatePercentage() {
        return (english + marathi + physics + chemistry + maths) / 5.0f;
    }

    private char calculateGrade() {
        if (percentage >= 90) return 'A';
        else if (percentage >= 80) return 'B';
        else if (percentage >= 70) return 'C';
        else if (percentage >= 60) return 'D';
        else return 'F';
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) - Percentage: %.2f%%, Grade: %c", name, id, percentage, grade);
    }
}

public class StudentGradeSystemGUI extends JFrame {
    private final DefaultListModel<String> studentListModel = new DefaultListModel<>();
    private final JList<String> studentList = new JList<>(studentListModel);
    private final java.util.List<Student> students = new java.util.ArrayList<>();
    private static final String FILE_NAME = "students.dat";

    public StudentGradeSystemGUI() {
        setTitle("Student Grade Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(8, 2));
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JTextField englishField = new JTextField();
        JTextField marathiField = new JTextField();
        JTextField physicsField = new JTextField();
        JTextField chemistryField = new JTextField();
        JTextField mathsField = new JTextField();
        JTextField searchField = new JTextField();

        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("ID:")); inputPanel.add(idField);
        inputPanel.add(new JLabel("English:")); inputPanel.add(englishField);
        inputPanel.add(new JLabel("Marathi:")); inputPanel.add(marathiField);
        inputPanel.add(new JLabel("Physics:")); inputPanel.add(physicsField);
        inputPanel.add(new JLabel("Chemistry:")); inputPanel.add(chemistryField);
        inputPanel.add(new JLabel("Maths:")); inputPanel.add(mathsField);
        inputPanel.add(new JLabel("Search:")); inputPanel.add(searchField);

        JButton addButton = new JButton("Add Student");
        JButton loadButton = new JButton("Load Students");
        JButton deleteButton = new JButton("Delete Student");
        JButton exportButton = new JButton("Export Report");
        JButton searchButton = new JButton("Search");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(searchButton);

        add(new JScrollPane(studentList), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int id = Integer.parseInt(idField.getText());
                int english = Integer.parseInt(englishField.getText());
                int marathi = Integer.parseInt(marathiField.getText());
                int physics = Integer.parseInt(physicsField.getText());
                int chemistry = Integer.parseInt(chemistryField.getText());
                int maths = Integer.parseInt(mathsField.getText());

                Student student = new Student(name, id, english, marathi, physics, chemistry, maths);
                students.add(student);
                studentListModel.addElement(student.toString());
                saveStudents();

                JOptionPane.showMessageDialog(this, "Student added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for ID and marks.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> loadStudents());

        deleteButton.addActionListener(e -> {
            int index = studentList.getSelectedIndex();
            if (index != -1) {
                students.remove(index);
                studentListModel.remove(index);
                saveStudents();
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        exportButton.addActionListener(e -> exportReport());

        searchButton.addActionListener(e -> {
            String query = searchField.getText().toLowerCase();
            studentListModel.clear();
            for (Student student : students) {
                if (student.name.toLowerCase().contains(query) || String.valueOf(student.id).contains(query)) {
                    studentListModel.addElement(student.toString());
                }
            }
        });

        loadStudents();
    }

    private void saveStudents() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadStudents() {
        students.clear();
        studentListModel.clear();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            students.addAll((java.util.List<Student>) ois.readObject());
            for (Student student : students) {
                studentListModel.addElement(student.toString());
            }
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void exportReport() {
        try (PrintWriter writer = new PrintWriter(new File("Student_Report.txt"))) {
            for (Student student : students) {
                writer.println(student);
            }
            JOptionPane.showMessageDialog(this, "Report exported successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGradeSystemGUI().setVisible(true));
    }
}
