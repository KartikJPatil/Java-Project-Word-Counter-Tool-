package com.example.wordcounter;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WordCountToolWithStatsFX extends Application {
    private TextField usernameField;
    private PasswordField passwordField;
    private TextArea textArea;
    private Label charCountLabel;
    private Label spaceCountLabel;
    private Label letterCountLabel;
    private Label specialCharCountLabel;
    private Label paragraphCountLabel;
    private Scene signupScene;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/miniproject";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Pranav@2004";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Word Count Tool with Stats");

        // Login Page
        GridPane loginLayout = new GridPane();
        loginLayout.setPadding(new Insets(10));
        loginLayout.setHgap(10);
        loginLayout.setVgap(5);

        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        Hyperlink newUserLink = new Hyperlink("New User?");
        newUserLink.setOnAction(event -> {
            primaryStage.setScene(signupScene);
        });

        loginLayout.add(usernameLabel, 0, 0);
        loginLayout.add(usernameField, 1, 0);
        loginLayout.add(passwordLabel, 0, 1);
        loginLayout.add(passwordField, 1, 1);
        loginLayout.add(loginButton, 1, 2);
        loginLayout.add(newUserLink, 1, 3);

        Scene loginScene = new Scene(loginLayout, 400, 200);

        // Signup Page
        GridPane signupLayout = new GridPane();
        signupLayout.setPadding(new Insets(10));
        signupLayout.setHgap(10);
        signupLayout.setVgap(5);

        Label signupLabel = new Label("Sign Up:");
        TextField signupUsernameField = new TextField();
        PasswordField signupPasswordField = new PasswordField();
        Button signupButton = new Button("Sign Up");

        signupLayout.add(signupLabel, 0, 0);
        signupLayout.add(signupUsernameField, 1, 0);
        signupLayout.add(signupPasswordField, 1, 1);
        signupLayout.add(signupButton, 1, 2);

        signupScene = new Scene(signupLayout, 400, 200);

        // Main Page
        BorderPane mainLayout = new BorderPane();


        VBox textAndStatsBox = new VBox(10);
        textAndStatsBox.setPadding(new Insets(20));
        textAndStatsBox.setAlignment(Pos.CENTER);

        textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefHeight(300);

        Button countButton = new Button("Count Words");
        countButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        countButton.setMinWidth(150);

        VBox statisticsBox = new VBox(10);
        charCountLabel = new Label("Character Count: 0");
        spaceCountLabel = new Label("Space Count: 0");
        letterCountLabel = new Label("Letter Count: 0");
        specialCharCountLabel = new Label("Special Character Count: 0");
        paragraphCountLabel = new Label("Paragraph Count: 0");

        charCountLabel.setStyle("-fx-background-color-fill: #000000;");
        spaceCountLabel.setStyle("-fx-text-fill: #000000;");
        letterCountLabel.setStyle("-fx-text-fill: #000000;");
        specialCharCountLabel.setStyle("-fx-text-fill: #000000;");
        paragraphCountLabel.setStyle("-fx-text-fill: #000000;");

        statisticsBox.getChildren().addAll(
                charCountLabel, spaceCountLabel, letterCountLabel, specialCharCountLabel, paragraphCountLabel);

        textAndStatsBox.getChildren().addAll(textArea, countButton, statisticsBox);

        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getChildren().addAll(textAndStatsBox);

        mainLayout.setCenter(buttonPanel);

        Scene mainScene = new Scene(mainLayout, 800, 600);


        primaryStage.setScene(loginScene);

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (performLogin(username, password)) {
                primaryStage.setScene(mainScene);
            } else {
                showAlert("Invalid credentials");
            }
        });

        signupButton.setOnAction(event -> {
            String signupUsername = signupUsernameField.getText();
            String signupPassword = signupPasswordField.getText();
            if (performSignup(signupUsername, signupPassword)) {
                showAlert("User registered: " + signupUsername);
                primaryStage.setScene(loginScene);
            } else {
                showAlert("Invalid signup information");
            }
        });

        countButton.setOnAction(event -> {
            String text = textArea.getText();
            updateStatistics(text);
            int wordCount = countWords(text);
            showAlert("Word Count: " + wordCount);
        });

        textArea.setOnKeyReleased(event -> {
            String text = textArea.getText();
            updateStatistics(text);
        });

        primaryStage.show();
    }

    private Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private boolean performLogin(String username, String password) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM login WHERE user = ? AND pass = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean performSignup(String username, String password) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO login (user, pass) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int countWords(String text) {
        String[] words = text.split("\\s+");
        return words.length;
    }

    private void updateStatistics(String text) {
        int charCount = text.length();
        int spaceCount = text.replaceAll("[^ ]", "").length();
        int letterCount = text.replaceAll("[^a-zA-Z]", "").length();
        int specialCharCount = charCount - spaceCount - letterCount;
        int paragraphCount = text.isEmpty() ? 0 : text.split("\n").length;

        charCountLabel.setText("Character Count: " + charCount);
        spaceCountLabel.setText("Space Count: " + spaceCount);
        letterCountLabel.setText("Letter Count: " + letterCount);
        specialCharCountLabel.setText("Special Character Count: " + specialCharCount);
        paragraphCountLabel.setText("Paragraph Count: " + paragraphCount);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
