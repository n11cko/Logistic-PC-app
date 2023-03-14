package fxControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Driver;
import model.Manager;
import model.User;
import utils.AlertDialog;
import utils.DbUtils;

import java.io.IOException;
import java.sql.SQLException;

public class LoginPage {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;


    public void validate() throws IOException, SQLException {
        User user = DbUtils.validateUser(loginField.getText(), passwordField.getText());
        if (user != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/main-page.fxml"));
            Parent parent = fxmlLoader.load();
            MainPage mainPage = fxmlLoader.getController();
            mainPage.setInfo(user);

            if (user instanceof Driver) {
                mainPage.managerTable.setVisible(false);
                mainPage.btnDeleteUserTableInfo.setVisible(false);
                mainPage.btnAddUserTableInfo.setVisible(false);
            } else if (user instanceof Manager) {
                mainPage.managerTable.setVisible(true);
                mainPage.btnDeleteUserTableInfo.setVisible(true);
                mainPage.btnAddUserTableInfo.setVisible(true);
            }
            Scene scene = new Scene(parent);
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Life is to short to code on java!");
            stage.setScene(scene);
            stage.show();
        } else {
            AlertDialog.throwAlert("Login dialog", "No such user. Use another login.");
        }
    }


    public void register() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/registration-page.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setTitle("Course Work!");
        stage.setScene(scene);
        stage.show();
    }
}
