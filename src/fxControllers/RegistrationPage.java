package fxControllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Driver;
import model.Manager;
import utils.AlertDialog;
import utils.DbUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegistrationPage implements Initializable {
    public TextField loginField;
    public TextField nameField;
    public TextField surnameField;
    public PasswordField pswField;
    public PasswordField repPswField;
    public DatePicker bDateField;
    public TextField managerEmailField;
    public TextField phoneNumField;
    public RadioButton radioD;
    public RadioButton radioM;
    public CheckBox isAdminChk;
    public DatePicker medCertField;
    public TextField medCertNum;
    public TextField driverLicenseField;
    private final String EXISTING_USER_QUERY = "(SELECT * from managers where login = ?) UNION (SELECT * from drivers where login = ?);";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        radioM.setSelected(true);
        isAdminChk.setVisible(false);
        disableFields();
    }

    private boolean checkUserExistance(Connection connection) throws SQLException {
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(EXISTING_USER_QUERY);
        preparedStatement.setString(1, loginField.getText());
        preparedStatement.setString(2, loginField.getText());
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            AlertDialog.throwAlert("Create user error", "User already exists");
            return true;
        }
        return false;
    }

    public void createNewUser() throws IOException, SQLException {
        Connection connection = DbUtils.connectToBd();
        if (checkUserExistance(connection)) return;
        String insertDriver = "INSERT INTO drivers(`login`,`password`, `name`, `surname`, `birth_date`, `med_date`, `med_num`,`driver_license`, `phone_num`) VALUES (?,?,?,?,?,?,?,?,?)";
        String insertManager = "INSERT INTO managers(`login`,`password`, `name`, `surname`, `birth_date`, `phone_num`, `email`, `employment_date`, `is_admin`) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement;


        if (radioD.isSelected()) {
            preparedStatement = connection.prepareStatement(insertDriver);
            Driver driver = new Driver(loginField.getText(), pswField.getText(), nameField.getText(), surnameField.getText(), LocalDate.parse(bDateField.getValue().toString()), phoneNumField.getText(), LocalDate.parse(medCertField.getValue().toString()), medCertNum.getText(), driverLicenseField.getText());
            preparedStatement.setString(1, driver.getLogin());
            preparedStatement.setString(2, driver.getPassword());
            preparedStatement.setString(3, driver.getName());
            preparedStatement.setString(4, driver.getSurname());
            preparedStatement.setDate(5, Date.valueOf(driver.getBirthDate()));
            preparedStatement.setDate(6, Date.valueOf(driver.getMedCertificateDate()));
            preparedStatement.setString(7, driver.getMedCertificateNumber());
            preparedStatement.setString(8, driver.getDriverLicense());
            preparedStatement.setString(9, driver.getPhoneNumber());
            preparedStatement.execute();
        } else {
            preparedStatement = connection.prepareStatement(insertManager);
            Manager manager = new Manager(loginField.getText(), pswField.getText(), nameField.getText(), surnameField.getText(), LocalDate.parse(bDateField.getValue().toString()), phoneNumField.getText(), managerEmailField.getText());
            preparedStatement.setString(1, manager.getLogin());
            preparedStatement.setString(2, manager.getPassword());
            preparedStatement.setString(3, manager.getName());
            preparedStatement.setString(4, manager.getSurname());
            preparedStatement.setDate(5, Date.valueOf(manager.getBirthDate()));
            preparedStatement.setString(6, manager.getPhoneNumber());
            preparedStatement.setString(7, manager.getEmail());
            preparedStatement.setDate(8, Date.valueOf(manager.getEmploymentDate()));
            preparedStatement.setBoolean(9, manager.isAdmin());
            preparedStatement.execute();
        }
        DbUtils.disconnect(connection, preparedStatement);
        returnToPrevious();
    }

    public void returnToPrevious() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/login-page.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setTitle("Course Work!");
        stage.setScene(scene);
        stage.show();
    }

    public void disableFields() {
        if (radioD.isSelected()) {
            medCertField.setDisable(false);
            medCertNum.setDisable(false);
            driverLicenseField.setDisable(false);
            managerEmailField.setDisable(true);
        } else {
            medCertField.setDisable(true);
            medCertNum.setDisable(true);
            driverLicenseField.setDisable(true);
            managerEmailField.setDisable(false);
        }
    }
}
