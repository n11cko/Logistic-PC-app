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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static utils.DbUtils.*;

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
    public ComboBox<Manager> respManagerOfDestField;
    //public ComboBox<TemporaryManagerType> respManagerOfDestField;
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
    public ComboBox<Truck> truckForDestination;
    @FXML
    public ComboBox<Cargo> cargoForDestination;
    @FXML
    private User loggedUser;
    ObservableList<Driver> listDriver;
    ObservableList<Manager> listManager;
    ObservableList<Cargo> listCargo;
    ObservableList<Truck> listTruck;
    int index = -1;
    int id;

    public void setInfo(User user) {
        this.loggedUser = user;
    }

    public void createTruck() {
        if (makeField.getText().isEmpty() || modelField.getText().isEmpty() || yearField.getText().isEmpty() || odometerField.getText().isEmpty() || tankCapacityField.getText().isEmpty()) {
           AlertDialog.throwAlert("Creating truck error", "You cannot leave field empty");
         return;
       }
        // Create a new Cargo object from the input fields
        TyreType tyreType = tyreTypeField.getValue();
        Truck truck = new Truck(
                makeField.getText(),
                modelField.getText(),
                Integer.parseInt(yearField.getText()),
                Double.parseDouble(odometerField.getText()),
                Double.parseDouble(tankCapacityField.getText()),
                tyreType
        );

        // Insert the cargo data into the database
        String query = "INSERT INTO truck (make, model, year, odometer, fuelTankCapacity, tyreType) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectToBd();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set the parameter values for the prepared statement
            stmt.setString(1, truck.getMake());
            stmt.setString(2, truck.getModel());
            stmt.setInt(3, truck.getYear());
            stmt.setDouble(4, truck.getOdometer());
            stmt.setDouble(5, truck.getFuelTankCapacity());
            stmt.setString(6, truck.getTyreType().getValue());

            // Execute the query
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                // If the insert was successful, add the cargo object to the list view
                truckListField.getItems().add(truck);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ObservableList<Truck> truckList = DbUtils.getTruckData();
            truckForDestination.setItems(truckList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Initialize cargo ComboBox with data from database
        try {
            ObservableList<Cargo> cargoList = DbUtils.getCargo();
            cargoForDestination.setItems(cargoList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removeTruck(MouseEvent mouseEvent) {
        Truck selectedTruck = truckListField.getSelectionModel().getSelectedItem();
        try {
            PreparedStatement pst = connectToBd().prepareStatement("delete from truck where id = ?");
            pst.setInt(1, selectedTruck.getId());
            pst.executeUpdate();
            listTruck = DbUtils.getTruckData();
            truckListField.setItems(listTruck);
            AlertDialog.throwAlert("Deleting truck info", "Deleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTruck() {
        Connection conn = DbUtils.connectToBd();
        Truck selectedTruck = truckListField.getSelectionModel().getSelectedItem();
        if (selectedTruck != null) {
            TextField makeField = new TextField(selectedTruck.getMake());
            TextField modelField = new TextField(selectedTruck.getModel());
            TextField yearField = new TextField(Integer.toString(selectedTruck.getYear()));
            TextField odometerField = new TextField(Double.toString(selectedTruck.getOdometer()));
            TextField tankCapacityField = new TextField(Double.toString(selectedTruck.getFuelTankCapacity()));
            ComboBox<TyreType> tyreTypeField = new ComboBox<>();
            tyreTypeField.getItems().addAll(TyreType.values());
            tyreTypeField.setValue(selectedTruck.getTyreType());

            VBox vbox = new VBox(
                    new Label("Make:"), makeField,
                    new Label("Model:"), modelField,
                    new Label("Year:"), yearField,
                    new Label("Odometer:"), odometerField,
                    new Label("Tank Capacity:"), tankCapacityField,
                    new Label("Tyre Type:"), tyreTypeField
            );

            AlertDialog.updateItem(truckListField,
                    selectedTruck,
                    "Update Truck Information",
                    vbox,
                    () -> {
                        PreparedStatement ps = null;
                        try {
                            ps = conn.prepareStatement("UPDATE truck SET make=?, model=?, year=?, odometer=?, fuelTankCapacity=?, tyreType=? WHERE id = ?");
                            ps.setString(1, makeField.getText());
                            ps.setString(2, modelField.getText());
                            ps.setInt(3, Integer.parseInt(yearField.getText()));
                            ps.setDouble(4, Double.parseDouble(odometerField.getText()));
                            ps.setDouble(5, Double.parseDouble(tankCapacityField.getText()));
                            ps.setString(6, tyreTypeField.getValue().toString());
                            ps.setInt(7, selectedTruck.getId());
                            int rowsAffected = ps.executeUpdate();
                            if (rowsAffected > 0) {
                                selectedTruck.setMake(makeField.getText());
                                selectedTruck.setModel(modelField.getText());
                                selectedTruck.setYear(Integer.parseInt(yearField.getText()));
                                selectedTruck.setOdometer(Double.parseDouble(odometerField.getText()));
                                selectedTruck.setFuelTankCapacity(Double.parseDouble(tankCapacityField.getText()));
                                selectedTruck.setTyreType(tyreTypeField.getValue());
                                truckListField.refresh();
                                Alert success = new Alert(Alert.AlertType.INFORMATION, "Truck updated successfully.");
                                success.showAndWait();
                            }
                        } catch (SQLException e
                        ) {
                            e.printStackTrace();
                        } finally {
                            DbUtils.disconnect(conn, ps);
                        }
                    });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a truck to update.");
            alert.showAndWait();
        }
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
        //respManagerOfDestField.setItems(FXCollections.observableArrayList(TemporaryManagerType.values()));
        //respManagerOfDestField.getSelectionModel().select(0);
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

        //cargo

        try {
            ObservableList<Truck> truckList1 = DbUtils.getTruckData();
            ObservableList<Cargo> cargoList1 = DbUtils.getCargo();
            ObservableList<Manager> managerList = DbUtils.getDataManagers();
            ObservableList<Destination> truckDestination = DbUtils.getDestinationData();
            ObservableList<Cargo> cargoData = getCargo();
            ObservableList<Truck> truckData = getTruckData();
            ObservableList<Destination> destinationList = DbUtils.getDestinationData();
            truckForDestination.setItems(truckList1);
            cargoForDestination.setItems(cargoList1);
            respManagerOfDestField.setItems(managerList);
            destinationListField.setItems(truckDestination);
            cargoListField.setItems(cargoData);
            truckListField.setItems(truckData);
            destinationListField.setItems(destinationList);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }



    public void createCargo() {
        if (cargoTitleField.getText().isEmpty() || cargoWeightField.getText().isEmpty() || cargoDescriptionField.getText().isEmpty() || cargoCustomerField.getText().isEmpty()) {
            AlertDialog.throwAlert("Creating cargo error", "You cannot leave field empty");
            return;
        }

        // Create a new Cargo object from the input fields
        CargoType cargoType = cargoTypeField.getValue();
        Cargo cargo = new Cargo(
                cargoTitleField.getText(),
                Double.parseDouble(cargoWeightField.getText()),
                cargoType,
                cargoDescriptionField.getText(),
                cargoCustomerField.getText()
        );

        // Insert the cargo data into the database
        String query = "INSERT INTO cargo (title, dateCreated, dateUpdated, weight, cargoType, description, customer) " +
                "VALUES (?, CURDATE(), CURDATE(), ?, ?, ?, ?)";

        try (Connection conn = connectToBd();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set the parameter values for the prepared statement
            stmt.setString(1, cargo.getTitle());
            stmt.setDouble(2, cargo.getWeight());
            stmt.setString(3, cargo.getCargoType().getValue());
            stmt.setString(4, cargo.getDescription());
            stmt.setString(5, cargo.getCustomer());

            // Execute the query
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                // If the insert was successful, add the cargo object to the list view
                cargoListField.getItems().add(cargo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void removeCargo() {
        Cargo selectedCargo = cargoListField.getSelectionModel().getSelectedItem();
        System.out.println(index);
            try {
                PreparedStatement pst = connectToBd().prepareStatement("delete from cargo where id = ?");
                pst.setInt(1, selectedCargo.getId());
                pst.executeUpdate();
                listCargo = DbUtils.getCargo();
                cargoListField.setItems(listCargo);
                AlertDialog.throwAlert("Deleting cargo info", "Deleted");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }






    public void updateCargo() {
        Connection conn = DbUtils.connectToBd();
        Cargo selectedCargo = cargoListField.getSelectionModel().getSelectedItem();
        if (selectedCargo != null) {
            TextField titleField = new TextField(selectedCargo.getTitle());
            TextField weightField = new TextField(Double.toString(selectedCargo.getWeight()));
            ComboBox<CargoType> typeField = new ComboBox<>();
            typeField.getItems().addAll(CargoType.values());
            typeField.setValue(selectedCargo.getCargoType());
            TextArea descriptionField = new TextArea(selectedCargo.getDescription());
            TextField customerField = new TextField(selectedCargo.getCustomer());

            VBox vbox = new VBox(
                    new Label("Title:"), titleField,
                    new Label("Weight:"), weightField,
                    new Label("Type:"), typeField,
                    new Label("Description:"), descriptionField,
                    new Label("Customer:"), customerField
            );

            AlertDialog.updateItem(cargoListField,
                    selectedCargo,
                    "Update Cargo Information",
                    vbox,
                    () -> {
                        PreparedStatement ps = null;
                        try {
                            ps = conn.prepareStatement("UPDATE cargo SET title=?, weight=?, cargoType=?, description=?, customer=? WHERE id = ?");
                            ps.setString(1, titleField.getText());
                            ps.setDouble(2, Double.parseDouble(weightField.getText()));
                            ps.setString(3, typeField.getValue().toString());
                            ps.setString(4, descriptionField.getText());
                            ps.setString(5, customerField.getText());
                            ps.setInt(6, selectedCargo.getId());
                            int rowsAffected = ps.executeUpdate();
                            if (rowsAffected > 0) {
                                selectedCargo.setTitle(titleField.getText());
                                selectedCargo.setWeight(Double.parseDouble(weightField.getText()));
                                selectedCargo.setCargoType(typeField.getValue());
                                selectedCargo.setDescription(descriptionField.getText());
                                selectedCargo.setCustomer(customerField.getText());
                                cargoListField.refresh();
                                Alert success = new Alert(Alert.AlertType.INFORMATION, "Cargo updated successfully.");
                                success.showAndWait();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            AlertDialog.throwAlert("Error updating cargo", "Cannot update cargo");
                        } finally {
                            DbUtils.disconnect(conn, ps);
                        }
                    });
        } else {
            AlertDialog.throwAlert("Error updating cargo", "Please select cargo to update");
        }
    }


        public void goToForum() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("../view/forum-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Forum page");
        stage.setScene(scene);
        stage.show();
    }



    public void createRoute() throws SQLException {
        // Retrieve values from the UI
        String stCity = stCityField.getText();
        Long stLn = Long.parseLong(stLnField.getText());
        Long stLt = Long.parseLong(stLtField.getText());
        String endCity = endCityField.getText();
        Long endLn = Long.parseLong(endLnField.getText());
        Long endLt = Long.parseLong(endLtField.getText());
        LocalDate dateCreated = LocalDate.now();
        LocalDate dateUpdated = LocalDate.now();
        Manager manager = respManagerOfDestField.getValue();
        Truck truck = truckForDestination.getValue();
        Cargo cargo= cargoForDestination.getValue();
        // TemporaryManagerType temporaryManager = respManagerOfDestField.getValue();
        List<Destination> destinations = destinationListField.getItems();
        truck=DbUtils.getTruckById(truck.getId());
        manager=DbUtils.getManagerById(manager.getId());
        cargo=DbUtils.getCargoById(cargo.getId());
        // Create a new Route instance with the retrieved values
        Destination route = new Destination();
        route.setStartCity(stCity);
        route.setStartLn(stLn);
        route.setStartLat(stLt);
        route.setEndCity(endCity);
        route.setEndLn(endLn);
        route.setEndLat(endLt);
        route.setDateCreated(dateCreated);
        route.setDateUpdated(dateUpdated);
        route.setResponsibleManagers(manager);
        route.setCargo(cargo);
        route.setTruck(truck);


        // route.setTemporaryManager(temporaryManager);

        // Insert the new Route into the database using DbUtils
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DbUtils.connectToBd();
            String query = "INSERT INTO destination (startCity, startLn, startLat, endCity, endLn, endLat, dateCreated, dateUpdated, truck_id, manager_id, cargo_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, route.getStartCity());
            stmt.setLong(2, route.getStartLn());
            stmt.setLong(3, route.getStartLat());
            stmt.setString(4, route.getEndCity());
            stmt.setLong(5, route.getEndLn());
            stmt.setLong(6, route.getEndLat());
            stmt.setDate(7, Date.valueOf(route.getDateCreated()));
            stmt.setDate(8, Date.valueOf(route.getDateUpdated()));
            stmt.setInt(9, route.getTruck().getId());
            stmt.setInt(10, route.getResponsibleManagers().getId());
            stmt.setInt(11, route.getCargo().getId());
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                // If the insert was successful, add the cargo object to the list view
                destinationListField.getItems().add(route);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DbUtils.disconnect(conn, stmt);
        }
    }

    // Method to add destination to the database
    private int addDestination(Destination destination) {
        int newId = -1;
        try {
            Connection conn = connectToBd(); // implement this method to get a database connection
            PreparedStatement statement = conn.prepareStatement("INSERT INTO destination (startCity, startLn, startLat, endCity, endLn, endLat, dateCreated, dateUpdated, truck_id, cargo_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, destination.getStartCity());
            statement.setLong(2, destination.getStartLn());
            statement.setLong(3, destination.getStartLat());
            statement.setString(4, destination.getEndCity());
            statement.setLong(5, destination.getEndLn());
            statement.setLong(6, destination.getEndLat());
            statement.setObject(7, destination.getDateCreated());
            statement.setObject(8, destination.getDateUpdated());
            statement.setInt(9, destination.getTruck().getId());
            statement.setInt(10, destination.getCargo().getId());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                newId = generatedKeys.getInt(1);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newId;
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
        Connection conn = DbUtils.connectToBd();
        Driver selectedDriver = driverTable.getSelectionModel().getSelectedItem();
        if (selectedDriver != null) {
            TextField loginField = new TextField(selectedDriver.getLogin());
            TextField nameField = new TextField(selectedDriver.getName());
            TextField surnameField = new TextField(selectedDriver.getSurname());
            TextField med_numField = new TextField(selectedDriver.getMedCertificateNumber());
            TextField driver_licenceField = new TextField(selectedDriver.getDriverLicense());
            TextField phone_numberField = new TextField(selectedDriver.getPhoneNumber());

            VBox vbox = new VBox(
                    new Label("Name:"), nameField,
                    new Label("Surname:"), surnameField,
                    new Label("Phone number"), phone_numberField,
                    new Label("Medical licence number"), med_numField,
                    new Label("Driver Licence"), driver_licenceField
            );
            AlertDialog.updateItem(driverTable,
                    selectedDriver,
                    "Update Driver Information",
                    vbox,
                    () -> {
                        PreparedStatement ps = null;
                        try {
                            ps = conn.prepareStatement("UPDATE drivers SET name=?, surname=?, med_num=? ,driver_license=?, phone_num = ? WHERE login = ?");
                            ps.setString(1, nameField.getText());
                            ps.setString(2, surnameField.getText());
                            ps.setString(3, med_numField.getText());
                            ps.setString(4, driver_licenceField.getText());
                            ps.setString(5, phone_numberField.getText());
                            ps.setString(6, loginField.getText());
                            ps.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        AlertDialog.throwAlert("Updating driver info", "Updated");
                    });
            listDriver = DbUtils.getDataDrivers();
            driverTable.setItems(listDriver);
        } else {
            Manager selectedManager = managerTable.getSelectionModel().getSelectedItem();
            TextField loginField = new TextField(selectedManager.getLogin());
            TextField nameField = new TextField(selectedManager.getName());
            TextField surnameField = new TextField(selectedManager.getSurname());
            TextField emailField = new TextField(selectedManager.getEmail());
            TextField phone_numberField = new TextField(selectedManager.getPhoneNumber());

            VBox vbox = new VBox(
                    new Label("Name:"), nameField,
                    new Label("Surname:"), surnameField,
                    new Label("Phone number"), phone_numberField,
                    new Label("Email"), emailField
            );
            AlertDialog.updateItem(managerTable,
                    selectedManager,
                    "Update manager information",
                    vbox,
                    () -> {
                        PreparedStatement ps = null;
                        try {
                            ps = conn.prepareStatement("UPDATE managers SET name=?, surname=?, phone_num = ?, email=? WHERE login = ?");
                            ps.setString(1, nameField.getText());
                            ps.setString(2, surnameField.getText());
                            ps.setString(3, phone_numberField.getText());
                            ps.setString(4, emailField.getText());
                            ps.setString(5, loginField.getText());
                            ps.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        } finally {
                            DbUtils.disconnect(conn, ps);
                        }
                        AlertDialog.throwAlert("Updating driver info", "Updated");
                    });
            listManager = DbUtils.getDataManagers();
            managerTable.setItems(listManager);
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
