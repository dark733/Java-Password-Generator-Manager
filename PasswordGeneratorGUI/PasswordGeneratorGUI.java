import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.Base64;

public class PasswordGeneratorGUI extends JFrame {
    private JTextField emailField;
    private JTextField lengthField;
    private JTextArea outputTextArea;

    public PasswordGeneratorGUI() {
        setTitle("Password Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(400, 400);

        JLabel emailLabel = new JLabel("Email/ID:");
        emailLabel.setBounds(20, 20, 80, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(120, 20, 200, 25);
        add(emailField);

        JLabel lengthLabel = new JLabel("Password Length:");
        lengthLabel.setBounds(20, 60, 120, 25);
        add(lengthLabel);

        lengthField = new JTextField();
        lengthField.setBounds(150, 60, 100, 25);
        add(lengthField);

        JButton generateButton = new JButton("Generate Password");
        generateButton.setBounds(120, 100, 160, 30);
        add(generateButton);

        outputTextArea = new JTextArea();
        outputTextArea.setBounds(20, 150, 350, 150);
        outputTextArea.setEditable(false);
        add(outputTextArea);

        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generatePassword();
            }
        });
    }

    private void generatePassword() {
        String email = emailField.getText();
        String lengthStr = lengthField.getText();

        try {
            int length = Integer.parseInt(lengthStr);
            if (length < 8 && length > 0) {
                throw new tooShortLengthException("The Password length is too short and is not secure");
            } else if (length <= 0) {
                throw new invalidLengthException("The Password length cannot be less than or equal to zero");
            } else {
                outputTextArea.setText("Length verified ok.\n");
                String password = generateRandomPassword(length);
                outputTextArea.append("Generated Password: " + password + "\n");
                String base64EncodedPassword = convertToBase64(password);
                outputTextArea.append("Base64 Encoded Password: " + base64EncodedPassword + "\n");
                storePassword(password, email);
            }
        } catch (NumberFormatException ex) {
            outputTextArea.setText("Please enter a valid number for password length.\n");
        } catch (tooShortLengthException ex) {
            outputTextArea.setText(ex.getMessage());
        } catch (invalidLengthException ex) {
            outputTextArea.setText(ex.getMessage());
        }
    }

    private String generateRandomPassword(int length) {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String combinedChars = uppercase + lowercase + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = uppercase.charAt(random.nextInt(uppercase.length()));
        password[1] = lowercase.charAt(random.nextInt(lowercase.length()));
        password[2] = numbers.charAt(random.nextInt(numbers.length()));

        for (int i = 3; i < length; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(length);
            char temp = password[randomIndex];
            password[randomIndex] = password[i];
            password[i] = temp;
        }

        return new String(password);
    }

    private String convertToBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    private void storePassword(String password, String email) {
        try {
            File file = new File("passwords_gen.txt");
            FileWriter writer = new FileWriter(file, true);

            if (!file.exists()) {
                file.createNewFile();
            }

            Scanner scanner = new Scanner(file);
            int count = 0;
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                count++;
            }

            writer.write((count + 1) + ". Email/ID: " + email + ", Password: " + password + "\n");

            writer.close();
            scanner.close();
            outputTextArea.append("Password stored in 'passwords_gen.txt' file.\n");
        } catch (IOException e) {
            outputTextArea.append("An error occurred while storing the password: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PasswordGeneratorGUI().setVisible(true);
            }
        });
    }

    class tooShortLengthException extends Exception {
        public tooShortLengthException(String msg) {
            super(msg);
        }
    }

    class invalidLengthException extends Exception {
        public invalidLengthException(String msg) {
            super(msg);
        }
    }
}
