package fxControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import model.Driver;
import utils.AlertDialog;
import utils.DbUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static utils.DbUtils.connectToBd;

public class MainPage implements Initializable {
    @FXML
    public TextField makeField;
    @FXML
    public TextField modelField;
    @FXML
    public TextField yearField;
    @FXML
    public TextField odometerField;
    @FXML
    public TextField tankCapacityField;
    @FXML
    public ComboBox<TyreType> tyreTypeField;
    @FXML
    public ListView<Truck> truckListField;
    //CARGO
    @FXML
    public TextField cargoWeightField;
    @FXML
    public ComboBox<CargoType> cargoTypeField;
    @FXML
    public TextField cargoDescriptionField;
    @FXML
    public TextField cargoTitleField;
    @FXML
    public TextField cargoCustomerField;
    @FXML
    public ListView<Cargo> cargoListField;
    @FXML
    public Button forumButton;
    @FXML
    public TextField stCityField;
    @FXML
    public TextField stLnField;
    @FXML
    public TextField stLtField;
    @FXML
    public TextField endCityField;
    @FXML
    public TextField endLnField;
    @FXML
    public TextField endLtField;
    @FXML
    public TextField checkpointField;
    @FXML
    public RadioButton radioShortStop;
    @FXML
    public ToggleGroup typeOfCheckpoint;
    @FXML
    public RadioButton radioLongStop;
    @FXML
    //ComboBox<Manager> respManagerOfDestField
    public ComboBox<TemporaryManagerType> respManagerOfDestField;
    @FXML
    public ListView<Destination> destinationListField;
    @FXML
    public ListView<Checkpoint> checkpointListField;
    @FXML
    public TableView<Driver> driverTable;
    @FXML
    public TableColumn<Driver, Integer> driverId;
    @FXML
    public TableColumn<Driver, String> driverLogin;
    @FXML
    public TableColumn<Driver, String> driverName;
    @FXML
    public TableColumn<Driver, String> driverSurname;
    @FXML
    public TableColumn<Driver, String> driverPhoneNum;
    @FXML
    public TableColumn<Driver, LocalDate> driverMedDate;
    @FXML
    public TableColumn<Driver, String> driverMedNum;
    @FXML
    public TableColumn<Driver, String> driverLicense;
    @FXML
    public TableColumn<Driver, LocalDate> driverBirthDate;
    @FXML
    public TableView<Manager> managerTable;
    @FXML
    public TableColumn<Manager, Integer> managerId;
    @FXML
    public TableColumn<Manager, String> managerLogin;
    @FXML
    public TableColumn<Manager, String> managerName;
    @FXML
    public TableColumn<Manager, String> managerSurname;
    @FXML
    public TableColumn<Manager, LocalDate> managerBirthDate;
    @FXML
    public TableColumn<Manager, String> managerEmail;
    @FXML
    public TableColumn<Manager, LocalDate> managerEmploymentDate;
    @FXML
    public TableColumn<Manager, String> managerPhoneNumber;
    @FXML
    public TextField changePhoneNumberTextField;
    @FXML
    public TextField changeSurnameTextField;
    @FXML
    public Button btnUpdateUserTableInfo;
    @FXML
    public Button btnDeleteUserTableInfo;
    @FXML
    public Button btnAddUserTableInfo;
    @FXML
    public DatePicker checkpointDate;
    @FXML
    public DatePicker dateCreated;
    @FXML
    public DatePicker dateUpdated;
    @FXML
    public DatePicker cargoCreationDate;
    @FXML
    public DatePicker cargoUpdateDate;
    @FXML
    private User loggedUser;
    ObservableList<Driver> listDriver;
    ObservableList<Manager> listManager;
    int index = -1;
    int id;

    public void setInfo(User user) {
        this.loggedUser = user;
    }

    public void createTruck() {
        System.out.println(loggedUser);
        if (makeField.getText().isEmpty() || modelField.getText().isEmpty() || yearField.getText().isEmpty() || odometerField.getText().isEmpty() || tankCapacityField.getText().isEmpty()) {
            AlertDialog.throwAlert("Creating truck error", "You cannot leave field empty");
            return;
        }
        try{
            int year = Integer.parseInt(yearField.getText());
            double odometer = Double.parseDouble(odometerField.getText());
            double tankCapacity = Double.parseDouble(tankCapacityField.getText());
            Truck truck = new Truck(makeField.getText(), modelField.getText(), year, odometer, tankCapacity, tyreTypeField.getValue());
            System.out.println(truckListField.getItems().add(truck));
        } catch (NumberFormatException e) {
            AlertDialog.throwAlert("Invalid year/odometer/tank capacity input", "Please enter a valid weight value.");
        }
    }


    public void removeTruck(MouseEvent mouseEvent) {
        int selectedTruck = truckListField.getSelectionModel().getSelectedIndex();
        truckListField.getItems().remove(selectedTruck);
    }

    public void updateTruck(ActionEvent actionEvent) {
        Truck selectedTruck = truckListField.getSelectionModel().getSelectedItem();
        TextField makeField = new TextField(selectedTruck.getMake());
        TextField modelField = new TextField(selectedTruck.getModel());
        TextField yearField = new TextField(String.valueOf(selectedTruck.getYear()));
        TextField odometerField = new TextField(String.valueOf(selectedTruck.getOdometer()));
        TextField tankCapacityField = new TextField(String.valueOf(selectedTruck.getFuelTankCapacity()));
        VBox vbox = new VBox(new Label("Make:"), makeField, new Label("Model:"), modelField, new Label("Year:"), yearField, new Label("Odometer:"), odometerField, new Label("Tank Capacity:"), tankCapacityField);
        AlertDialog.updateItem(truckListField, selectedTruck, "Update Truck Information", vbox, () -> {
            selectedTruck.setMake(makeField.getText());
            selectedTruck.setModel(modelField.getText());
            selectedTruck.setYear(Integer.parseInt(yearField.getText()));
            selectedTruck.setOdometer(Double.parseDouble(odometerField.getText()));
            selectedTruck.setFuelTankCapacity(Double.parseDouble(tankCapacityField.getText()));
            AlertDialog.throwAlert("Updating truck info", "Updated");
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DbUtils.connectToBd();
        User loggedUser = null;
        try {
            loggedUser = DbUtils.validateUser("a", "a");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        radioLongStop.setSelected(true);
        //Initializing Truck
        tyreTypeField.setItems(FXCollections.observableArrayList(TyreType.values()));
        tyreTypeField.getSelectionModel().select(0);
        //Initializing Cargo
        cargoTypeField.setItems(FXCollections.observableArrayList(CargoType.values()));
        cargoTypeField.getSelectionModel().select(0);
        respManagerOfDestField.setItems(FXCollections.observableArrayList(TemporaryManagerType.values()));
        respManagerOfDestField.getSelectionModel().select(0);
        //Initializing driver info in driver management
        driverId.setCellValueFactory(new PropertyValueFactory<Driver, Integer>("id"));
        driverLogin.setCellValueFactory(new PropertyValueFactory<Driver, String>("login"));
        driverName.setCellValueFactory(new PropertyValueFactory<Driver, String>("name"));
        driverSurname.setCellValueFactory(new PropertyValueFactory<Driver, String>("surname"));
        driverBirthDate.setCellValueFactory(new PropertyValueFactory<Driver, LocalDate>("birthDate"));
        driverMedDate.setCellValueFactory(new PropertyValueFactory<Driver, LocalDate>("medCertificateDate"));
        driverMedNum.setCellValueFactory(new PropertyValueFactory<Driver, String>("medCertificateNumber"));
        driverLicense.setCellValueFactory(new PropertyValueFactory<Driver, String>("driverLicense"));
        driverPhoneNum.setCellValueFactory(new PropertyValueFactory<Driver, String>("phoneNumber"));
        listDriver = DbUtils.getDataDrivers();
        driverTable.setItems(listDriver);
        //Initializing manager info in driver management
        managerId.setCellValueFactory(new PropertyValueFactory<Manager, Integer>("id"));
        managerLogin.setCellValueFactory(new PropertyValueFactory<Manager, String>("login"));
        managerName.setCellValueFactory(new PropertyValueFactory<Manager, String>("name"));
        managerSurname.setCellValueFactory(new PropertyValueFactory<Manager, String>("surname"));
        managerBirthDate.setCellValueFactory(new PropertyValueFactory<Manager, LocalDate>("birthDate"));
        managerEmail.setCellValueFactory(new PropertyValueFactory<Manager, String>("email"));
        managerEmploymentDate.setCellValueFactory(new PropertyValueFactory<Manager, LocalDate>("employmentDate"));
        managerPhoneNumber.setCellValueFactory(new PropertyValueFactory<Manager, String>("phoneNumber"));
        listManager = DbUtils.getDataManagers();
        managerTable.setItems(listManager);

        //Check the user role and show the corresponding table
        if (loggedUser instanceof Driver) {
            managerTable.setVisible(false);
        }

    }


    public void createCargo() {
        if (cargoTitleField.getText().isEmpty() || cargoWeightField.getText().isEmpty() || cargoDescriptionField.getText().isEmpty() || cargoCustomerField.getText().isEmpty() || cargoCreationDate.getValue() == null || cargoUpdateDate.getValue() == null) {
            AlertDialog.throwAlert("Creating cargo error", "You cannot leave field empty");
            return;
        }
        try {
            double weight = Double.parseDouble(cargoWeightField.getText());
            Cargo cargo = new Cargo(cargoTitleField.getText(), weight, cargoTypeField.getValue(), cargoDescriptionField.getText(), cargoCustomerField.getText(), cargoCreationDate.getValue(), cargoUpdateDate.getValue());
            System.out.println(cargoListField.getItems().add(cargo));
        } catch (NumberFormatException e) {
            AlertDialog.throwAlert("Invalid weight input", "Please enter a valid weight value.");
        }
    }

    public void removeCargo() {
        int selectedCargo = cargoListField.getSelectionModel().getSelectedIndex();
        cargoListField.getItems().remove(selectedCargo);
    }

    public void updateCargo() {
        Cargo selectedCargo = cargoListField.getSelectionModel().getSelectedItem();
        TextField titleField = new TextField(selectedCargo.getTitle());
        TextField weightField = new TextField(String.valueOf(selectedCargo.getWeight()));
        ChoiceBox<CargoType> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll(CargoType.values());
        typeChoiceBox.setValue(selectedCargo.getCargoType());
        TextArea descriptionArea = new TextArea(selectedCargo.getDescription());
        TextField customerField = new TextField(selectedCargo.getCustomer());
        VBox vbox = new VBox(new Label("Title:"), titleField, new Label("Weight:"), weightField, new Label("Type:"), typeChoiceBox, new Label("Description:"), descriptionArea, new Label("Customer:"), customerField);
        AlertDialog.updateItem(cargoListField, selectedCargo, "Update Cargo Information", vbox, () -> {
            selectedCargo.setTitle(titleField.getText());
            selectedCargo.setWeight(Double.parseDouble(weightField.getText()));
            selectedCargo.setCargoType(typeChoiceBox.getValue());
            selectedCargo.setDescription(descriptionArea.getText());
            selectedCargo.setCustomer(customerField.getText());
            AlertDialog.throwAlert("Updating cargo info", "Updated");
        });
    }


    public void goToForum() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/forum-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Forum page");
        stage.setScene(scene);
        stage.show();
    }

    public void createRoute() {
        if (stCityField.getText().isEmpty() || stLnField.getText().isEmpty() || stLtField.getText().isEmpty() || endCityField.getText().isEmpty() || endLnField.getText().isEmpty() || endLtField.getText().isEmpty() || dateCreated.getValue()==null || dateUpdated.getValue()==null){
            AlertDialog.throwAlert("Creating route error", "You cannot leave field empty");
            return;
        }
        try{
            long stLn = Long.parseLong(stLnField.getText());
            long stLt = Long.parseLong(stLtField.getText());
            long endLn = Long.parseLong(endLnField.getText());
            long endLt = Long.parseLong(endLtField.getText());
            Destination destination = new Destination(stCityField.getText(), stLn, stLt, endCityField.getText(), endLn, endLt, dateCreated.getValue(), dateUpdated.getValue(), checkpointField.getText(), TemporaryManagerType.MANAGER_1);
            System.out.println(destinationListField.getItems().add(destination));
        } catch (NumberFormatException e) {
            AlertDialog.throwAlert("Invalid longitude/latitude input", "Please enter a valid weight value.");
        }

    }

    public void deleteRoute() {
        int selectedRoute = destinationListField.getSelectionModel().getSelectedIndex();
        destinationListField.getItems().remove(selectedRoute);
    }

    public void updateRoute() {
        Destination selectedDestination = destinationListField.getSelectionModel().getSelectedItem();
        TextField stCityField = new TextField(selectedDestination.getStartCity());
        TextField stLnField = new TextField(String.valueOf(selectedDestination.getStartLn()));
        TextField stLatField = new TextField(String.valueOf(selectedDestination.getStartLat()));
        TextField endCity = new TextField(selectedDestination.getEndCity());
        TextField endLnField = new TextField(String.valueOf(selectedDestination.getEndLn()));
        TextField endLatField = new TextField(String.valueOf(selectedDestination.getEndLat()));
        VBox vbox = new VBox(new Label("Your start city:"), stCityField, new Label("Your start longitude:"), stLnField, new Label("Your start latitude:"), stLatField, new Label("Your end city:"), endCity, new Label("Your end longitude:"), endLnField, new Label("Your end latitude:"), endLatField);
        AlertDialog.updateItem(destinationListField, selectedDestination, "Update Your destination Information", vbox, () -> {
            selectedDestination.setStartCity(stCityField.getText());
            selectedDestination.setStartLn(Long.parseLong(stLnField.getText()));
            selectedDestination.setStartLat(Long.parseLong(stLatField.getText()));
            selectedDestination.setEndCity(endCity.getText());
            selectedDestination.setEndLn(Long.parseLong(endLnField.getText()));
            selectedDestination.setEndLat(Long.parseLong(endLatField.getText()));
            AlertDialog.throwAlert("Updating Destination info", "Updated");
        });
    }

    public void addCheckpoint() {
        if (checkpointField.getText().isEmpty() || checkpointDate.getValue()==null){
            AlertDialog.throwAlert("Creating route error", "You cannot leave field empty");
            return;
        }
        ObservableList<Destination> currentTableData = destinationListField.getSelectionModel().getSelectedItems();
        checkpointField.getText();
        Checkpoint checkpoint = new Checkpoint(checkpointField.getText(), radioLongStop.isSelected(), checkpointDate.getValue(), respManagerOfDestField.getValue());
        System.out.println(checkpointListField.getItems().add(checkpoint));
        checkpoint.setLinkedDestinations(currentTableData);
    }

    public void deleteCheckpoint() {
        int selectedCheckpoint = checkpointListField.getSelectionModel().getSelectedIndex();
        checkpointListField.getItems().remove(selectedCheckpoint);
    }

    public void updateCheckpoint() {
        Checkpoint selectedCheckpoint = checkpointListField.getSelectionModel().getSelectedItem();
        TextField checkpointField = new TextField(selectedCheckpoint.getTitle());
        RadioButton radioShortStop = new RadioButton("Short Stop");
        RadioButton radioLongStop = new RadioButton("Long Stop");
        ToggleGroup typeOfCheckpoint = new ToggleGroup();
        radioShortStop.setToggleGroup(typeOfCheckpoint);
        radioLongStop.setToggleGroup(typeOfCheckpoint);
        if (selectedCheckpoint.isLongStop()) {
            radioShortStop.setSelected(true);
        } else {
            radioLongStop.setSelected(true);
        }
        VBox vbox = new VBox(new Label("Checkpoint name:"), checkpointField, new Label("Type of checkpoint:"), radioShortStop, radioLongStop, new Label("Responsible Manager:"), respManagerOfDestField);
        AlertDialog.updateItem(checkpointListField, selectedCheckpoint, "Update Checkpoint Information", vbox, () -> {
            selectedCheckpoint.setTitle(checkpointField.getText());
            selectedCheckpoint.setLongStop(radioLongStop.isSelected());
            AlertDialog.throwAlert("Updating checkpoint info", "Updated");
        });
    }

    //write update table also for managers
    public void UpdateTableViewForUser() {
        String phNumber, surname;
        index = driverTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            id = Integer.parseInt(String.valueOf(driverTable.getItems().get(index).getId()));
            phNumber = changePhoneNumberTextField.getText();
            surname = changeSurnameTextField.getText();
            try {
                PreparedStatement pst = connectToBd().prepareStatement("update drivers set surname = ?,phone_num = ? where id = ? ");
                pst.setString(1, surname);
                pst.setString(2, phNumber);
                pst.setInt(3, id);
                pst.executeUpdate();
                listDriver = DbUtils.getDataDrivers();
                driverTable.setItems(listDriver);
                AlertDialog.throwAlert("Updating driver info", "Updated");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        index = managerTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            id = Integer.parseInt(String.valueOf(managerTable.getItems().get(index).getId()));
            phNumber = changePhoneNumberTextField.getText();
            surname = changeSurnameTextField.getText();
            try {
                PreparedStatement pst = connectToBd().prepareStatement("update managers set surname = ?, phone_num = ? where id = ?");
                pst.setString(1, surname);
                pst.setString(2, phNumber);
                pst.setInt(3, id);
                pst.executeUpdate();
                listManager = DbUtils.getDataManagers();
                managerTable.setItems(listManager);
                AlertDialog.throwAlert("Updating manager info", "Updated");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void deleteTableViewForUser() {
        index = driverTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            int driverId = Integer.parseInt(String.valueOf(driverTable.getItems().get(index).getId()));
            try {
                PreparedStatement pst = connectToBd().prepareStatement("delete from drivers where id = ?");
                pst.setInt(1, driverId);
                pst.executeUpdate();
                listDriver = DbUtils.getDataDrivers();
                driverTable.setItems(listDriver);
                AlertDialog.throwAlert("Deleting driver info", "Deleted");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        index = managerTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            int managerId = Integer.parseInt(String.valueOf(managerTable.getItems().get(index).getId()));
            try {
                PreparedStatement pst = connectToBd().prepareStatement("delete from managers where id = ?");
                pst.setInt(1, managerId);
                pst.executeUpdate();
                listManager = DbUtils.getDataManagers();
                managerTable.setItems(listManager);
                AlertDialog.throwAlert("Deleting manager info", "Deleted");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void AddUserToTableViewForUser() {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);

        TextField login = new TextField();
        login.setPromptText("Login");
        TextField name = new TextField();
        name.setPromptText("Name");
        TextField surname = new TextField();
        surname.setPromptText("Surname");
        TextField medNumber = new TextField();
        medNumber.setPromptText("Medical Number");
        TextField drLicense = new TextField();
        drLicense.setPromptText("Driver License");
        TextField phNumber = new TextField();
        phNumber.setPromptText("Phone Number");
        DatePicker birthDate = new DatePicker();
        birthDate.setPromptText("Birth Date");
        DatePicker medCertificate = new DatePicker();
        medCertificate.setPromptText("Medical Certificate Date");

        Button addButton = new Button("Add User");
        addButton.setOnAction(event -> {
            String loginText = login.getText();
            String nameText = name.getText();
            String surnameText = surname.getText();
            String medNumberText = medNumber.getText();
            String drLicenseText = drLicense.getText();
            String phNumberText = phNumber.getText();
            LocalDate birthDateValue = birthDate.getValue();
            LocalDate medCertificateValue = medCertificate.getValue();
            if (loginText.isEmpty() || nameText.isEmpty() || surnameText.isEmpty() || medNumberText.isEmpty() || drLicenseText.isEmpty() || phNumberText.isEmpty() || birthDateValue == null || medCertificateValue == null) {
                AlertDialog.throwAlert("Error", "Please fill all fields.");
            } else {
                try {
                    PreparedStatement pst = connectToBd().prepareStatement("insert into drivers (login, name, surname, birth_date, med_date,  med_num, driver_license, phone_num) values (?, ?, ?, ?,?,?,?,?)");
                    String DB_URL = "jdbc:mysql://localhost/transport_system";
                    String USER = "root";
                    String PASS = "";
                    Connection conn = null;
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("ALTER TABLE drivers ALTER COLUMN password SET DEFAULT 'default_password'");
                    pst.setString(1, loginText);
                    pst.setString(2, nameText);
                    pst.setString(3, surnameText);
                    pst.setDate(4, Date.valueOf(birthDateValue));
                    pst.setDate(5, Date.valueOf(medCertificateValue));
                    pst.setString(6, medNumberText);
                    pst.setString(7, drLicenseText);
                    pst.setString(8, phNumberText);
                    pst.executeUpdate();
                    listDriver = DbUtils.getDataDrivers();
                    driverTable.setItems(listDriver);
                    AlertDialog.throwAlert("Adding new driver", "Added");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        vbox.getChildren().addAll(login, name, surname, birthDate, medCertificate, medNumber, drLicense, phNumber, addButton);

        Scene scene = new Scene(vbox, 400, 500);
        Stage stage = new Stage();
        stage.setTitle("Add User");
        stage.setScene(scene);
        stage.show();
    }
}
