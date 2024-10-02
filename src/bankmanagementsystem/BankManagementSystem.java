package bankmanagementsystem;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.sql.*;
import java.text.SimpleDateFormat;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

// MAIN CLASS

public class BankManagementSystem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and show the login page
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });

    }

}

// LOGIN PAGE

class LoginPage extends JFrame implements ActionListener {

    private JTextField accountNumberField;
    private JPasswordField pinField;
    private JButton signInButton;
    private JButton signUpButton;
    private JTextField cardTextField;
    private JPasswordField pinTextField;
    private JButton login;
    private JButton clear;
    private JButton signup;
    DBConnection dbConnection;

    public LoginPage() {
        setTitle("BANKING SYSTEM");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 480);
        setLocationRelativeTo(null);
        dbConnection = new DBConnection();

        // Create a content pane with custom background color
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#C7ECFC")); // Off-white color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(null); // Using absolute positioning
        contentPane.setBackground(new Color(240, 240, 240)); // Set off-white background color

        // Load and scale the image
        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icons/bank.png"));
        Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledIcon = new ImageIcon(image);

        // Create a panel for logo and welcome text
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(Color.decode("#C7ECFC")); // Set off-white background color
        contentPane.add(logoPanel);
        logoPanel.setBounds(0, 0, 800, 100); // Positioning

        // Create and configure the JLabel for the logo
        JLabel logoLabel = new JLabel(scaledIcon);
        logoPanel.add(logoLabel);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to ATM");
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        logoPanel.add(welcomeLabel);

        // Card Number field
        JLabel cardno = new JLabel("Card No.:");
        cardno.setFont(new Font("Times New Roman", Font.BOLD, 28));
        cardno.setBounds(120, 150, 150, 40);
        contentPane.add(cardno);

        cardTextField = new JTextField();
        cardTextField.setBounds(300, 150, 220, 30);
        cardTextField.setFont(new Font("Times New Roman", Font.BOLD, 14));
        contentPane.add(cardTextField);

        // PIN field
        JLabel pin = new JLabel("PIN:");
        pin.setFont(new Font("Times New Roman", Font.BOLD, 28));
        pin.setBounds(120, 220, 250, 40);
        contentPane.add(pin);

        pinTextField = new JPasswordField();
        pinTextField.setBounds(300, 220, 220, 30);
        pinTextField.setFont(new Font("Times New Roman", Font.BOLD, 14));
        contentPane.add(pinTextField);

        // Sign In button
        login = new JButton("Sign in");
        login.setBounds(300, 300, 100, 30);
        login.setBackground(Color.decode("#7FB2F0")); // Set background color
        login.setForeground(Color.WHITE);
        login.addActionListener(this);
        contentPane.add(login);

        // Clear button
        clear = new JButton("Clear");
        clear.setBounds(430, 300, 100, 30);
        clear.setBackground(Color.decode("#7FB2F0")); // Set background color
        clear.setForeground(Color.WHITE);
        clear.addActionListener(this);
        contentPane.add(clear);

        // Sign Up button
        signup = new JButton("Sign Up");
        signup.setBounds(300, 350, 230, 30);
        signup.setBackground(Color.decode("#7FB2F0")); // Set background color
        signup.setForeground(Color.WHITE);
        signup.addActionListener(this);
        contentPane.add(signup);

        setContentPane(contentPane);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == clear) {
            cardTextField.setText("");
            pinTextField.setText("");
        } else if (ae.getSource() == login) {
            // Retrieve the card number and PIN entered by the user
            String cardNumber = cardTextField.getText();
            String pin = new String(pinTextField.getPassword());

            // Query the database to verify the credentials
            boolean isAuthenticated = authenticateUser(cardNumber, pin);

            if (isAuthenticated) {
                // Fetch user's name from the database
                String name = getNameFromDatabase(cardNumber);

                //Open the ATM Interface
                ATMInterface atmInterface = new ATMInterface();
                atmInterface.setVisible(true);
                try {
                    String query = "INSERT INTO login (cardNumber, pin) VALUES (?, ?)";
                    PreparedStatement ps = dbConnection.prepareStatement(query);
                    ps.setString(1, cardNumber);
                    ps.setString(2, pin);

                    ps.executeUpdate();
                    ps.close();

                } catch (SQLException e) {
                    System.out.println(e);
                    setVisible(false);

                } finally {
                    // Closing DBConnection
                    dbConnection.close();
                }

                dispose(); // Close the login interface
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Card Number or PIN. Please try again.");
            }
        } else if (ae.getSource() == signup) {
            setVisible(false); // Hide the login interface
            SignupPageOne signupPageOne = new SignupPageOne();
            signupPageOne.setVisible(true); // Show the signup page one interface
        }
    }

    private boolean authenticateUser(String cardNumber, String pin) {
        boolean isAuthenticated = false;
        Connection dbConnection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            dbConnection = DriverManager.getConnection("jdbc:mysql:///banking_system", "root", "Payal@123");
            // Prepare the SQL statement to check for the user's credentials
            String query = "SELECT * FROM signupThree WHERE cardNumber = ? AND pin = ?";
            statement = dbConnection.prepareStatement(query);
            statement.setString(1, cardNumber);
            statement.setString(2, pin);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check if any record matches the provided credentials
            if (resultSet.next()) {
                // User authentication successful
                isAuthenticated = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            // Close the database resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        }

        return isAuthenticated;
    }

    private String getNameFromDatabase(String cardNumber) {
        String name = null;
        Connection dbConnection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            dbConnection = DriverManager.getConnection("jdbc:mysql:///banking_system", "root", "Payal@123");
            // Prepare the SQL statement to retrieve the user's name based on card number
            String query = "SELECT name FROM users WHERE cardNumber = ?";
            statement = dbConnection.prepareStatement(query);
            statement.setString(1, cardNumber);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check if any record matches the provided card number
            if (resultSet.next()) {
                // Retrieve the user's name
                name = resultSet.getString("name");
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            // Close the database resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        }

        return name;
    }

}

class SignupPageOne extends JFrame implements ActionListener {

    JTextField fullNameField;
    JTextField emailField;
    JTextField fatherNameField;
    JTextField addressField;
    JTextField cityField;
    JTextField stateField;
    JTextField pinCodeField;
    JTextField contactField;
    JRadioButton maleRadioButton;
    JRadioButton femaleRadioButton;
    JRadioButton otherGenderRadioButton;
    JRadioButton marriedRadioButton;
    JRadioButton unmarriedRadioButton;
    JRadioButton otherMaritalStatusRadioButton;
    JButton nextButton;
    JDateChooser dobChooser;
    DBConnection dbConnection;

    public SignupPageOne() {

        // Initialize DBConnection
        dbConnection = new DBConnection();

        setTitle("BANKING SYSTEM / SIGNUP 1 ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create a content pane with custom background color
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#C7ECFC")); // Set white background color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(null); // Using absolute positioning
        contentPane.setBackground(Color.WHITE); // Set white background color

        // Generate random 4-digit application form number
        int formNumber = (int) generateRandomFormNumber();

        // Application Form heading with random number
        JLabel headingLabel = new JLabel("Application Form No : " + formNumber);
        headingLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        // Set bounds to center horizontally and position at the top
        headingLabel.setBounds(0, 30, 850, 40);
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center horizontally
        contentPane.add(headingLabel);

        // Subheading for Personal Details
        JLabel subheadingLabel = new JLabel("Personal Details - Page 1");
        subheadingLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        subheadingLabel.setBounds(0, 80, 850, 30);
        subheadingLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center horizontally
        contentPane.add(subheadingLabel);

        // Full Name field
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        fullNameLabel.setBounds(80, 150, 100, 30);
        contentPane.add(fullNameLabel);

        fullNameField = new JTextField();
        fullNameField.setBounds(250, 150, 500, 30); // Adjusted width
        fullNameField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(fullNameField);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        emailLabel.setBounds(80, 200, 100, 30);
        contentPane.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(250, 200, 500, 30); // Adjusted width
        emailField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(emailField);

        // Father's Name field
        JLabel fatherNameLabel = new JLabel("Father's Name:");
        fatherNameLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        fatherNameLabel.setBounds(80, 250, 150, 30);
        contentPane.add(fatherNameLabel);

        fatherNameField = new JTextField();
        fatherNameField.setBounds(250, 250, 500, 30); // Adjusted width
        fatherNameField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(fatherNameField);

        // Date of Birth field using JCalendar
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        dobLabel.setBounds(80, 300, 150, 30);
        contentPane.add(dobLabel);

        dobChooser = new JDateChooser(); // JCalendar component
        dobChooser.setBounds(250, 300, 200, 30);
        dobChooser.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(dobChooser);

        // Address field
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        addressLabel.setBounds(80, 350, 100, 30);
        contentPane.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(250, 350, 500, 30); // Adjusted width
        addressField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(addressField);

        // City field
        JLabel cityLabel = new JLabel("City:");
        cityLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        cityLabel.setBounds(80, 400, 100, 30);
        contentPane.add(cityLabel);

        cityField = new JTextField();
        cityField.setBounds(250, 400, 200, 30); // Adjusted width
        cityField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(cityField);

        // State field
        JLabel stateLabel = new JLabel("State:");
        stateLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        stateLabel.setBounds(500, 400, 100, 30);
        contentPane.add(stateLabel);

        stateField = new JTextField();
        stateField.setBounds(570, 400, 180, 30); // Adjusted width
        stateField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(stateField);

        // Pin Code field
        JLabel pinCodeLabel = new JLabel("Pin Code:");
        pinCodeLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        pinCodeLabel.setBounds(80, 450, 100, 30);
        contentPane.add(pinCodeLabel);

        pinCodeField = new JTextField();
        pinCodeField.setBounds(250, 450, 200, 30); // Adjusted width
        pinCodeField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(pinCodeField);

        // Contact Number field
        JLabel contactLabel = new JLabel("Contact Number:");
        contactLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        contactLabel.setBounds(80, 500, 180, 30);
        contentPane.add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(250, 500, 200, 30); // Adjusted width
        contactField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        contentPane.add(contactField);

        // Gender radio buttons
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        genderLabel.setBounds(80, 550, 100, 30);
        contentPane.add(genderLabel);

        maleRadioButton = new JRadioButton("Male");
        maleRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        maleRadioButton.setBounds(250, 550, 80, 30);
        maleRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(maleRadioButton);

        femaleRadioButton = new JRadioButton("Female");
        femaleRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        femaleRadioButton.setBounds(340, 550, 100, 30);
        femaleRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(femaleRadioButton);

        otherGenderRadioButton = new JRadioButton("Other");
        otherGenderRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        otherGenderRadioButton.setBounds(450, 550, 100, 30);
        otherGenderRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(otherGenderRadioButton);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);
        genderGroup.add(otherGenderRadioButton);

        // Marital Status radio buttons
        JLabel maritalStatusLabel = new JLabel("Marital Status:");
        maritalStatusLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        maritalStatusLabel.setBounds(80, 600, 150, 30);
        contentPane.add(maritalStatusLabel);

        marriedRadioButton = new JRadioButton("Married");
        marriedRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        marriedRadioButton.setBounds(250, 600, 100, 30);
        marriedRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(marriedRadioButton);

        unmarriedRadioButton = new JRadioButton("Unmarried");
        unmarriedRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        unmarriedRadioButton.setBounds(360, 600, 120, 30);
        unmarriedRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(unmarriedRadioButton);

        otherMaritalStatusRadioButton = new JRadioButton("Other");
        otherMaritalStatusRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        otherMaritalStatusRadioButton.setBounds(500, 600, 100, 30);
        otherMaritalStatusRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(otherMaritalStatusRadioButton);

        ButtonGroup maritalStatusGroup = new ButtonGroup();
        maritalStatusGroup.add(marriedRadioButton);
        maritalStatusGroup.add(unmarriedRadioButton);
        maritalStatusGroup.add(otherMaritalStatusRadioButton);

        // Next button
        nextButton = new JButton("Next");
        nextButton.setBounds(620, 660, 80, 30);
        nextButton.setBackground(Color.decode("#7FB2F0"));
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(this);
        contentPane.add(nextButton);

        setContentPane(contentPane);
        setSize(850, 800);
        setLocation(350, 10);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ac) {
        // Generate random form number
        String formno = "" + generateRandomFormNumber();

        // Retrieve data from GUI components
        String fullName = fullNameField.getText();
        String fatherName = fatherNameField.getText();
        String dob = ((JTextField) dobChooser.getDateEditor().getUiComponent()).getText();
        String gender = getSelectedGender();
        String email = emailField.getText();
        String maritalStatus = getSelectedMaritalStatus();
        String address = addressField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String pinCode = pinCodeField.getText();
        try {
            // Constructing SQL query to insert data into database
            String query = "INSERT INTO signup (formno, fullName, fatherName, dob, gender, email, maritalStatus, address, city, state, pinCode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ps.setString(1, formno); // Set the form number
            ps.setString(2, fullName);
            ps.setString(3, fatherName);

            // Convert the date string to MySQL date format 'YYYY-MM-DD'
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = dobChooser.getDate();
            dob = sdf.format(utilDate);

            ps.setString(4, dob);
            ps.setString(5, gender);
            ps.setString(6, email);
            ps.setString(7, maritalStatus);
            ps.setString(8, address);
            ps.setString(9, city);
            ps.setString(10, state);
            ps.setString(11, pinCode);

            // Executing SQL query
            ps.executeUpdate();

            // Closing PreparedStatement
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
            setVisible(false);
            new SignupPageTwo(formno).setVisible(true);

        } finally {
            // Closing DBConnection
            dbConnection.close();
        }
    }

    // Helper method to generate random form number
    public int generateRandomFormNumber() {
        Random random = new Random();
        return random.nextInt(9000) + 1000; // Range: 1000 to 9999
    }

    // Helper methods to get selected gender and marital status
    public String getSelectedGender() {
        if (maleRadioButton.isSelected()) {
            return "Male";
        } else if (femaleRadioButton.isSelected()) {
            return "Female";
        } else if (otherGenderRadioButton.isSelected()) {
            return "Other";
        } else {
            return "Null";
        }
    }

    public String getSelectedMaritalStatus() {
        if (marriedRadioButton.isSelected()) {
            return "Married";
        } else if (unmarriedRadioButton.isSelected()) {
            return "Unmarried";
        } else if (otherMaritalStatusRadioButton.isSelected()) {
            return "Other";
        } else {
            return "Null";
        }
    }

}

class SignupPageTwo extends JFrame implements ActionListener {

    JComboBox<String> religionComboBox;
    JComboBox<String> categoryComboBox;
    JComboBox<String> incomeComboBox;
    JComboBox<String> qualificationComboBox;
    JComboBox<String> occupationComboBox;
    JTextField panNumberField;
    JTextField adharNumberField;
    JRadioButton seniorCitizenYesRadioButton;
    JRadioButton seniorCitizenNoRadioButton;
    JRadioButton existingAccountYesRadioButton;
    JRadioButton existingAccountNoRadioButton;
    JButton nextButton;
    DBConnection dbConnection;
    String formno; // Instance variable to store formno

    public SignupPageTwo(String formno) {
        this.formno = formno;

        // Initialize DBConnection
        dbConnection = new DBConnection();

        setTitle("BANKING SYSTEM / SIGNUP 2");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create a content pane with custom background color
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#C7ECFC")); // Set white background color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(null); // Using absolute positioning
        contentPane.setBackground(Color.decode("#C7ECFC")); // Set white background color

        // Subheading for Account Details
        JLabel subheadingLabel = new JLabel("Additional Details - Page 2");
        subheadingLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        subheadingLabel.setBounds(0, 30, 850, 30);
        subheadingLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center horizontally
        contentPane.add(subheadingLabel);

        // Religion Combo Box
        JLabel religionLabel = new JLabel("Religion:");
        religionLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        religionLabel.setBounds(100, 140, 200, 30);
        contentPane.add(religionLabel);

        String[] religions = {"Hindu", "Muslim", "Christian", "Others"};
        religionComboBox = new JComboBox<>(religions);
        religionComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        religionComboBox.setBackground(Color.WHITE);
        religionComboBox.setBounds(300, 140, 400, 30);
        contentPane.add(religionComboBox);

        // Category Combo Box
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        categoryLabel.setBounds(100, 190, 200, 30);
        contentPane.add(categoryLabel);

        String[] categories = {"General", "OBC", "ST", "NT", "SC", "Others"};
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        categoryComboBox.setBackground(Color.WHITE);
        categoryComboBox.setBounds(300, 190, 400, 30);
        contentPane.add(categoryComboBox);

        // Income Combo Box
        JLabel incomeLabel = new JLabel("Income:");
        incomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        incomeLabel.setBounds(100, 240, 200, 30);
        contentPane.add(incomeLabel);

        String[] incomes = {"Null", "< 1,50,000", "< 2,50,000", "< 5,00,000", "Upto 10,00,000"};
        incomeComboBox = new JComboBox<>(incomes);
        incomeComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        incomeComboBox.setBackground(Color.WHITE);
        incomeComboBox.setBounds(300, 240, 400, 30);
        contentPane.add(incomeComboBox);

        // Educational Qualification Combo Box
        JLabel qualificationLabel = new JLabel("Educational Qualification:");
        qualificationLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        qualificationLabel.setBounds(100, 290, 300, 30);
        contentPane.add(qualificationLabel);

        String[] qualifications = {"Non Graduation", "Graduation", "Post Graduation", "Doctorate", "Others"};
        qualificationComboBox = new JComboBox<>(qualifications);
        qualificationComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        qualificationComboBox.setBackground(Color.WHITE);
        qualificationComboBox.setBounds(450, 290, 200, 30);
        contentPane.add(qualificationComboBox);

        // Occupation Combo Box
        JLabel occupationLabel = new JLabel("Occupation:");
        occupationLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        occupationLabel.setBounds(100, 340, 200, 30);
        contentPane.add(occupationLabel);

        String[] occupations = {"Salaried", "Self Employed", "Business", "Student", "Retired", "Others"};
        occupationComboBox = new JComboBox<>(occupations);
        occupationComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        occupationComboBox.setBackground(Color.WHITE);
        occupationComboBox.setBounds(300, 340, 400, 30);
        contentPane.add(occupationComboBox);

        // PAN Number Field
        JLabel panNumberLabel = new JLabel("PAN Number:");
        panNumberLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        panNumberLabel.setBounds(100, 390, 200, 30);
        contentPane.add(panNumberLabel);

        panNumberField = new JTextField();
        panNumberField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        panNumberField.setBounds(300, 390, 400, 30);
        contentPane.add(panNumberField);

        // Aadhaar Number Field
        JLabel adharNumberLabel = new JLabel("Aadhaar Number:");
        adharNumberLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        adharNumberLabel.setBounds(100, 440, 200, 30);
        contentPane.add(adharNumberLabel);

        adharNumberField = new JTextField();
        adharNumberField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        adharNumberField.setBounds(300, 440, 400, 30);
        contentPane.add(adharNumberField);

        // Senior Citizen Radio Buttons
        JLabel seniorCitizenLabel = new JLabel("Senior Citizen:");
        seniorCitizenLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        seniorCitizenLabel.setBounds(100, 490, 200, 30);
        contentPane.add(seniorCitizenLabel);

        seniorCitizenYesRadioButton = new JRadioButton("Yes");
        seniorCitizenYesRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        seniorCitizenYesRadioButton.setBounds(300, 490, 100, 30);
        seniorCitizenYesRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(seniorCitizenYesRadioButton);

        seniorCitizenNoRadioButton = new JRadioButton("No");
        seniorCitizenNoRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        seniorCitizenNoRadioButton.setBounds(450, 490, 100, 30);
        seniorCitizenNoRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(seniorCitizenNoRadioButton);

        ButtonGroup seniorCitizenGroup = new ButtonGroup();
        seniorCitizenGroup.add(seniorCitizenYesRadioButton);
        seniorCitizenGroup.add(seniorCitizenNoRadioButton);

        // Existing Account Radio Buttons
        JLabel existingAccountLabel = new JLabel("Existing Account:");
        existingAccountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        existingAccountLabel.setBounds(100, 540, 200, 30);
        contentPane.add(existingAccountLabel);

        existingAccountYesRadioButton = new JRadioButton("Yes");
        existingAccountYesRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        existingAccountYesRadioButton.setBounds(300, 540, 100, 30);
        existingAccountYesRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(existingAccountYesRadioButton);

        existingAccountNoRadioButton = new JRadioButton("No");
        existingAccountNoRadioButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        existingAccountNoRadioButton.setBounds(450, 540, 100, 30);
        existingAccountNoRadioButton.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(existingAccountNoRadioButton);

        ButtonGroup existingAccountGroup = new ButtonGroup();
        existingAccountGroup.add(existingAccountYesRadioButton);
        existingAccountGroup.add(existingAccountNoRadioButton);

        // Next Button
        nextButton = new JButton("Next");
        nextButton.setBounds(620, 660, 80, 30);
        nextButton.setBackground(Color.decode("#7FB2F0"));
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(this);
        contentPane.add(nextButton);

        setContentPane(contentPane);
        setSize(850, 800);
        setLocation(350, 10);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == nextButton) {
            // Retrieve data from GUI components
            String religion = (String) religionComboBox.getSelectedItem();
            String category = (String) categoryComboBox.getSelectedItem();
            String income = (String) incomeComboBox.getSelectedItem();
            String qualification = (String) qualificationComboBox.getSelectedItem();
            String occupation = (String) occupationComboBox.getSelectedItem();
            String panNumber = panNumberField.getText();
            String adharNumber = adharNumberField.getText();
            String seniorCitizen = seniorCitizenYesRadioButton.isSelected() ? "Yes" : "No";
            String existingAccount = existingAccountYesRadioButton.isSelected() ? "Yes" : "No";

            // Check Aadhaar number length
            if (adharNumber.length() != 12) {
                JOptionPane.showMessageDialog(this, "Aadhaar number must be 12 digits long.", "Invalid Aadhaar Number", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method without proceeding further
            }

            // Insert data into database
            try {
                String query = "INSERT INTO signupTwo (formno, religion, category, income, qualification, occupation, panNumber, adharNumber, seniorCitizen, existingAccount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = dbConnection.prepareStatement(query);
                ps.setString(1, formno);
                ps.setString(2, religion);
                ps.setString(3, category);
                ps.setString(4, income);
                ps.setString(5, qualification);
                ps.setString(6, occupation);
                ps.setString(7, panNumber);
                ps.setString(8, adharNumber);
                ps.setString(9, seniorCitizen);
                ps.setString(10, existingAccount);

                ps.executeUpdate();
                ps.close();

            } catch (SQLException e) {
                System.out.println(e);
                setVisible(false);
                new SignupPageThree(formno).setVisible(true);

            } finally {
                // Closing DBConnection
                dbConnection.close();
            }
        }
    }

}

class SignupPageThree extends JFrame implements ActionListener {

    JLabel heading, accountType, cardNumber, pin, servicesRequired, cardNumberValueLabel, pinValueLabel;
    JCheckBox atmCard, mobileBanking, emailSmsAlerts, checkbook, eStatement;
    JRadioButton savingAccount, fixedDepositAccount, currentAccount, recurringDepositAccount;
    ButtonGroup accountTypeGroup;
    JButton submit;
    JCheckBox selfDeclaration1, selfDeclaration2, selfDeclaration;
    String formno;
    DBConnection dbConnection;

    public SignupPageThree(String formno) {
        this.formno = formno;

        // Initialize DBConnection
        dbConnection = new DBConnection();

        setTitle("BANKING SYSTEM / SIGNUP 3");
        setBounds(500, 200, 800, 700);
        getContentPane().setBackground(Color.decode("#C7ECFC"));
        setLayout(null);

        heading = new JLabel("Account Details - Page 3");
        heading.setFont(new Font("Times New Roman", Font.BOLD, 24));
        heading.setBounds(280, 40, 400, 40);
        add(heading);

        accountType = new JLabel("Account Type: ");
        accountType.setFont(new Font("Times New Roman", Font.BOLD, 20));
        accountType.setBounds(100, 100, 200, 30);
        add(accountType);

        savingAccount = new JRadioButton("Saving Account");
        savingAccount.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        savingAccount.setBounds(300, 100, 150, 30);
        savingAccount.setBackground(Color.decode("#C7ECFC"));
        add(savingAccount);

        fixedDepositAccount = new JRadioButton("Fixed Deposit Account");
        fixedDepositAccount.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        fixedDepositAccount.setBounds(500, 100, 200, 30);
        fixedDepositAccount.setBackground(Color.decode("#C7ECFC"));
        add(fixedDepositAccount);

        currentAccount = new JRadioButton("Current Account");
        currentAccount.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        currentAccount.setBounds(300, 140, 150, 30);
        currentAccount.setBackground(Color.decode("#C7ECFC"));
        add(currentAccount);

        recurringDepositAccount = new JRadioButton("Recurring Deposit Account");
        recurringDepositAccount.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        recurringDepositAccount.setBounds(500, 140, 250, 30);
        recurringDepositAccount.setBackground(Color.decode("#C7ECFC"));
        add(recurringDepositAccount);

        accountTypeGroup = new ButtonGroup();
        accountTypeGroup.add(savingAccount);
        accountTypeGroup.add(fixedDepositAccount);
        accountTypeGroup.add(currentAccount);
        accountTypeGroup.add(recurringDepositAccount);

        cardNumber = new JLabel("Card Number: ");
        cardNumber.setFont(new Font("Times New Roman", Font.BOLD, 20));
        cardNumber.setBounds(100, 200, 200, 30);
        add(cardNumber);

        String cardNumberValue = generateRandomCardNumber();
        cardNumberValueLabel = new JLabel(cardNumberValue);
        cardNumberValueLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        cardNumberValueLabel.setBounds(300, 200, 400, 30);
        add(cardNumberValueLabel);

        pin = new JLabel("PIN: ");
        pin.setFont(new Font("Times New Roman", Font.BOLD, 20));
        pin.setBounds(100, 250, 200, 30);
        add(pin);

        String pinValue = generateRandomPin();
        pinValueLabel = new JLabel(pinValue);
        pinValueLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        pinValueLabel.setBounds(300, 250, 400, 30);
        add(pinValueLabel);

        servicesRequired = new JLabel("Services Required: ");
        servicesRequired.setFont(new Font("Times New Roman", Font.BOLD, 20));
        servicesRequired.setBounds(100, 300, 200, 30);
        add(servicesRequired);

        atmCard = new JCheckBox("ATM Card");
        atmCard.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        atmCard.setBounds(300, 300, 150, 30);
        atmCard.setBackground(Color.decode("#C7ECFC"));
        add(atmCard);

        mobileBanking = new JCheckBox("Mobile Banking");
        mobileBanking.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        mobileBanking.setBounds(300, 340, 150, 30);
        mobileBanking.setBackground(Color.decode("#C7ECFC"));
        add(mobileBanking);

        emailSmsAlerts = new JCheckBox("Email and SMS Alerts");
        emailSmsAlerts.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        emailSmsAlerts.setBounds(500, 300, 200, 30);
        emailSmsAlerts.setBackground(Color.decode("#C7ECFC"));
        add(emailSmsAlerts);

        checkbook = new JCheckBox("Checkbook");
        checkbook.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        checkbook.setBounds(500, 340, 150, 30);
        checkbook.setBackground(Color.decode("#C7ECFC"));
        add(checkbook);

        eStatement = new JCheckBox("E-Statement");
        eStatement.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        eStatement.setBounds(300, 380, 150, 30);
        eStatement.setBackground(Color.decode("#C7ECFC"));
        add(eStatement);

        String selfDeclarationText1 = "I hereby declare that the information provided above is true and accurate to the best of my knowledge.";
        String selfDeclarationText2 = "I understand that any false information provided may result in the rejection of my application or the termination of services.";

        selfDeclaration = new JCheckBox(selfDeclarationText1);
        selfDeclaration.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        selfDeclaration.setBounds(50, 500, 600, 30);
        selfDeclaration.setBackground(Color.decode("#C7ECFC"));
        add(selfDeclaration);

        JCheckBox selfDeclarationPart2 = new JCheckBox(selfDeclarationText2);
        selfDeclarationPart2.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        selfDeclarationPart2.setBounds(50, 530, 750, 30); // Adjusted Y position
        selfDeclarationPart2.setBackground(Color.decode("#C7ECFC"));
        add(selfDeclarationPart2);

        submit = new JButton("Submit");
        submit.setBounds(620, 660, 80, 30);
        submit.setBackground(Color.decode("#7FB2F0"));
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        setSize(850, 800);
        setLocation(350, 10);
        setVisible(true);
    }

    public String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (i > 0 && i % 4 == 0) {
                cardNumber.append(" "); // Add a space after every four digits
            }
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    public String generateRandomPin() {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == submit) {
            // Retrieve data from GUI components
            String selectedAccountType = "";
            if (savingAccount.isSelected()) {
                selectedAccountType = "Saving Account";
            } else if (fixedDepositAccount.isSelected()) {
                selectedAccountType = "Fixed Deposit Account";
            } else if (currentAccount.isSelected()) {
                selectedAccountType = "Current Account";
            } else if (recurringDepositAccount.isSelected()) {
                selectedAccountType = "Recurring Deposit Account";
            }

            String cardNumber = cardNumberValueLabel.getText();
            String pin = pinValueLabel.getText();
            String services = "";
            if (atmCard.isSelected()) {
                services += "ATM Card, ";
            }
            if (mobileBanking.isSelected()) {
                services += "Mobile Banking, ";
            }
            if (emailSmsAlerts.isSelected()) {
                services += "Email and SMS Alerts, ";
            }
            if (checkbook.isSelected()) {
                services += "Checkbook, ";
            }
            if (eStatement.isSelected()) {
                services += "E-Statement";
            }

            // Set initial balance
            double initialBalance = 0.0; // Set initial balance here

            // Insert data into database
            try {
                String query = "INSERT INTO signupThree (formno, accountType, cardNumber, pin, services, balance) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = dbConnection.prepareStatement(query);
                ps.setString(1, formno);
                ps.setString(2, selectedAccountType);
                ps.setString(3, cardNumber);
                ps.setString(4, pin);
                ps.setString(5, services);
                ps.setDouble(6, initialBalance); // Set initial balance

                ps.executeUpdate();
                ps.close();

                // Show success message
                JOptionPane.showMessageDialog(this, "Account details submitted successfully!");

                // Proceed to the login page
                dispose(); // Close the current window
                new LoginPage().setVisible(true); // Open the login page

            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

}


class ATMInterface extends JFrame implements ActionListener {

    private JLabel welcomeLabel;
    private JLabel welcomeLabel2;
    private JButton withdrawButton;
    private JButton depositButton;
    private JButton logoutButton;
    JPanel buttonPanel;
    DBConnection dbConnection;

    public ATMInterface() {
        setTitle("BANKING SYSTEM / ATM");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1066, 800); // Adjusted width and height for 4:3 ratio
        setLocationRelativeTo(null);

        dbConnection = new DBConnection();

        // Create a content pane with custom background color
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#C7ECFC")); // Set custom background color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(null); // Using absolute positioning
        // Set the content pane
        setContentPane(contentPane);

        getContentPane().setBackground(Color.decode("#C7ECFC"));
        setContentPane(contentPane);

// Welcome message 1
        welcomeLabel = new JLabel("Welcome to ATM", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        welcomeLabel.setBounds(0, 100, 1066, 30); // Adjusted bounds
        contentPane.add(welcomeLabel);

// Welcome message 2
        welcomeLabel2 = new JLabel("Enjoy convenient, secure banking. Withdraw, check balance, and manage accounts effortlessly.", SwingConstants.CENTER);
        welcomeLabel2.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        welcomeLabel2.setBounds(0, 140, 1066, 50); // Adjusted bounds
        contentPane.add(welcomeLabel2);

        buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20)); // 3 rows, 2 columns with larger gaps
        buttonPanel.setBounds(283, 300, 500, 250); // Adjusted bounds
        buttonPanel.setBackground(Color.decode("#C7ECFC"));
        contentPane.add(buttonPanel);

// Create buttons with slightly bigger size
        withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(this);
        withdrawButton.setPreferredSize(new Dimension(200, 50));// Slightly bigger size
        withdrawButton.setBackground(Color.decode("#7FB2F0"));
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        buttonPanel.add(withdrawButton);

        depositButton = new JButton("Deposit");
        depositButton.addActionListener(this);
        depositButton.setPreferredSize(new Dimension(200, 50)); // Slightly bigger size
        depositButton.setBackground(Color.decode("#7FB2F0"));
        depositButton.setForeground(Color.WHITE);
        depositButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        buttonPanel.add(depositButton);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        logoutButton.setPreferredSize(new Dimension(200, 50)); // Slightly bigger size
        logoutButton.setBackground(Color.decode("#7FB2F0"));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        buttonPanel.add(logoutButton);

        contentPane.add(buttonPanel);

// Adjust the size of the content pane to fit all components
        contentPane.setPreferredSize(new Dimension(1066, 600));
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == withdrawButton) {
            JOptionPane.showMessageDialog(this, "Withdraw button clicked.");
            dispose(); // Close the current ATMInterface window
            new Withdrawal().setVisible(true); // Open the Withdrawal window
        } else if (ae.getSource() == depositButton) {
            JOptionPane.showMessageDialog(this, "Deposit button clicked.");
            dispose(); // Close the current ATMInterface window
            new Deposit().setVisible(true); // Open the Withdrawal window
        } else if (ae.getSource() == logoutButton) {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Logged out successfully.");
                dispose();
            }
        }
    }

}



class Deposit extends JFrame implements ActionListener {

    private JTextField depositAmountField;
    private JButton backButton;
    private JButton nextButton;
    private JLabel balanceLabel;
    private JLabel headingLabel, amountLabel; // Add a JLabel for the heading
    private double balance = 0.00; // Initial balance amount
    private DBConnection dbConnection;

    public Deposit() {
        setTitle("BANKING SYSTEM / DEPOSIT");
        setSize(1066, 800);
        setLocationRelativeTo(null);
        dbConnection = new DBConnection();

        // Create a content pane with custom background color
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#C7ECFC")); // Set custom background color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(null); // Using absolute positioning
        // Set the content pane
        setContentPane(contentPane);

        getContentPane().setBackground(Color.decode("#C7ECFC"));
        setContentPane(contentPane);

        // Initialize components
        headingLabel = new JLabel("DEPOSIT", SwingConstants.CENTER); // Set the heading text
        headingLabel.setFont(new Font("Times New Roman", Font.BOLD, 30)); // Customize font if needed
        headingLabel.setBounds(0, 100, 1066, 30); // Adjusted bounds
        contentPane.add(headingLabel);

        amountLabel = new JLabel("Enter the amount to deposit (in Rupees): ", SwingConstants.CENTER); // Label for the amount field
        amountLabel.setFont(new Font("Times New Roman", Font.BOLD, 26));
        amountLabel.setBounds(0, 190, 1066, 50); // Adjusted bounds
        contentPane.add(amountLabel);

        depositAmountField = new JTextField();
        depositAmountField.setBounds(280, 300, 500, 50); // Adjusted width
        depositAmountField.setFont(new Font("Times New Roman", Font.BOLD, 26));
        depositAmountField.setHorizontalAlignment(JTextField.CENTER); // Center align text
        contentPane.add(depositAmountField);

        backButton = new JButton("Back");
        backButton.setBounds(200, 500, 150, 50); // Adjusted bounds
        backButton.setBackground(Color.decode("#7FB2F0"));
        contentPane.add(backButton);

        nextButton = new JButton("Next");
        nextButton.setBounds(700, 500, 150, 50); // Adjusted bounds
        nextButton.setBackground(Color.decode("#7FB2F0"));
        contentPane.add(nextButton);

        balanceLabel = new JLabel("Balance: $" + balance);
        balanceLabel.setBounds(450, 650, 200, 30); // Adjusted bounds
        balanceLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        contentPane.add(balanceLabel);

        // Add action listeners
        backButton.addActionListener(this);
        nextButton.addActionListener(this);
        setVisible(true);
    }

    private String generateDepositID() {
        Random random = new Random(); // Random number generator
        int randomNumber = random.nextInt(900000) + 100000; // Random 6-digit number
        return String.format("%06d", randomNumber); // Format the random number as a 6-digit string
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backButton) {
            // Handle back button
            dispose(); // Close the current window
            new ATMInterface().setVisible(true); // Open the ATMInterface window
        } else if (ae.getSource() == nextButton) {
            // Handle next button
            double depositAmount = Double.parseDouble(depositAmountField.getText());
            balance += depositAmount; // Update balance with deposit

            // Update balance in the withdrawal table
            try {
                String query = "UPDATE withdrawal SET balance = balance + ? WHERE id = 1"; // Assuming balance row has id = 1
                PreparedStatement ps = dbConnection.prepareStatement(query);
                ps.setDouble(1, depositAmount);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }

            balanceLabel.setText("Balance: $" + balance); // Update balance label

            // Generate deposit ID
            String depositID = generateDepositID();

            // Insert deposit details into database
            try {
                String query = "INSERT INTO deposits (deposit_id, deposit_amount) VALUES (?, ?)";
                PreparedStatement ps = dbConnection.prepareStatement(query);
                ps.setString(1, depositID);
                ps.setDouble(2, depositAmount);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }

            JOptionPane.showMessageDialog(this, "Deposit successful. New balance: $" + balance);
        }
    }

}

class Withdrawal extends JFrame implements ActionListener {

    private JTextField withdrawalAmountField;
    private JButton backButton;
    private JButton nextButton;
    private JLabel balanceLabel;
    private JLabel headingLabel, amountLabel; // Add a JLabel for the heading
    private double balance = 1000.00; // Initial balance amount
    DBConnection dbConnection;

    public Withdrawal() {
        setTitle("BANKING SYSTEM / WITHDRAW");
        setSize(1066, 800);
        setLocationRelativeTo(null);
        dbConnection = new DBConnection();

        // Create a content pane with custom background color
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#C7ECFC")); // Set custom background color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(null); // Using absolute positioning
        // Set the content pane
        setContentPane(contentPane);

        getContentPane().setBackground(Color.decode("#C7ECFC"));
        setContentPane(contentPane);

        // Initialize components
        headingLabel = new JLabel("WITHDRAWAL", SwingConstants.CENTER); // Set the heading text
        headingLabel.setFont(new Font("Times New Roman", Font.BOLD, 30)); // Customize font if needed
        headingLabel.setBounds(0, 100, 1066, 30); // Adjusted bounds
        contentPane.add(headingLabel);

        amountLabel = new JLabel("Enter the amount to withdraw (in Rupees): ", SwingConstants.CENTER); // Label for the amount field
        amountLabel.setFont(new Font("Times New Roman", Font.BOLD, 26));
        amountLabel.setBounds(0, 190, 1066, 50); // Adjusted bounds
        contentPane.add(amountLabel);

        withdrawalAmountField = new JTextField();
        withdrawalAmountField.setBounds(280, 300, 500, 50); // Adjusted width
        withdrawalAmountField.setFont(new Font("Times New Roman", Font.BOLD, 26));
        withdrawalAmountField.setHorizontalAlignment(JTextField.CENTER); // Center align text
        contentPane.add(withdrawalAmountField);

        backButton = new JButton("Back");
        backButton.setBounds(200, 500, 150, 50); // Adjusted bounds
        backButton.setBackground(Color.decode("#7FB2F0"));
        contentPane.add(backButton);

        nextButton = new JButton("Next");
        nextButton.setBounds(700, 500, 150, 50); // Adjusted bounds
        nextButton.setBackground(Color.decode("#7FB2F0"));
        contentPane.add(nextButton);

        balanceLabel = new JLabel("Balance: $" + balance);
        balanceLabel.setBounds(450, 650, 200, 30); // Adjusted bounds
        balanceLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        contentPane.add(balanceLabel);

        // Add action listeners
        backButton.addActionListener(this);
        nextButton.addActionListener(this);
        setVisible(true);
    }

    private String generateWithdrawalID() {
        Random random = new Random(); // Random number generator
        int randomNumber = random.nextInt(900000) + 100000; // Random 6-digit number
        return String.format("%06d", randomNumber); // Format the random number as a 6-digit string
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backButton) {
            // Handle back button
            dispose(); // Close the current window
            new ATMInterface().setVisible(true); // Open the ATMInterface window
        } else if (ae.getSource() == nextButton) {
            // Handle next button
            double withdrawalAmount = Double.parseDouble(withdrawalAmountField.getText());
            if (balance >= withdrawalAmount) {
                // Sufficient balance, proceed with withdrawal
                balance -= withdrawalAmount;

                // Update balance in the withdrawal table
                try {
                    String query = "UPDATE withdrawals SET balance = ? WHERE id = 1"; // Assuming balance row has id = 1
                    PreparedStatement ps = dbConnection.prepareStatement(query);
                    ps.setDouble(1, balance);
                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }

                balanceLabel.setText("Balance: $" + balance); // Update balance label

                // Generate withdrawal ID
                String withdrawalID = generateWithdrawalID();

                // Insert withdrawal details into database
                try {
                    String query = "INSERT INTO withdrawals (withdrawal_id, withdrawal_amount) VALUES (?, ?)";
                    PreparedStatement ps = dbConnection.prepareStatement(query);
                    ps.setString(1, withdrawalID);
                    ps.setDouble(2, withdrawalAmount);
                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }

                JOptionPane.showMessageDialog(this, "Withdrawal successful. New balance: $" + balance);
            } else {
                // Insufficient balance
                JOptionPane.showMessageDialog(this, "Insufficient balance. Your current balance is $" + balance);
            }
        }
    }

}

class DBConnection {

    Connection dbConnection;
    PreparedStatement ps;

    public DBConnection() {
        try {
            // Establishing the database connection
            dbConnection = DriverManager.getConnection("jdbc:mysql:///banking_system", "root", "username");
        } catch (Exception e) {
            // Printing any exceptions that occur during the connection process
            System.out.println(e);
        }
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        ps = dbConnection.prepareStatement(query);
        return ps;
    }

    public Connection getConnection() {
        return dbConnection;
    }

    public void close() {
        try {
            if (ps != null) {
                ps.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
