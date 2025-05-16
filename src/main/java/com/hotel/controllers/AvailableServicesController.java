package com.hotel.controllers;

import com.hotel.dao.ServiceDAO;
import com.hotel.dao.impl.ServiceDAOImpl;
import com.hotel.model.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AvailableServicesController implements Initializable {
    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, Integer> serviceIdColumn;
    @FXML private TableColumn<Service, String> serviceNameColumn;
    @FXML private TableColumn<Service, String> descriptionColumn;
    @FXML private TableColumn<Service, Number> priceColumn;
    @FXML private Button addServiceBtn;
    @FXML private Button editServiceBtn;
    @FXML private Button deleteServiceBtn;
    @FXML private Button clearBtn;
    @FXML private TextField serviceNameField;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionArea;
    @FXML private MenuItem editMenuItem;
    @FXML private MenuItem deleteMenuItem;

    private ServiceDAO serviceDAO = new ServiceDAOImpl();
    private ObservableList<Service> servicesData;
    private Service selectedService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        servicesData = FXCollections.observableArrayList();
        servicesTable.setItems(servicesData);
        loadServices();
        
        // Set up selection model
        servicesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedService = newSelection;
            editServiceBtn.setDisable(newSelection == null);
            deleteServiceBtn.setDisable(newSelection == null);
            editMenuItem.setDisable(newSelection == null);
            deleteMenuItem.setDisable(newSelection == null);
            
            if (newSelection != null) {
                populateFields(newSelection);
            } else {
                clearFields();
            }
        });
        
        // Set up context menu actions
        editMenuItem.setOnAction(e -> handleEditService());
        deleteMenuItem.setOnAction(e -> handleDeleteService());
        
        // Set up button actions
        addServiceBtn.setOnAction(e -> handleAddService());
        editServiceBtn.setOnAction(e -> handleEditService());
        deleteServiceBtn.setOnAction(e -> handleDeleteService());
        clearBtn.setOnAction(e -> clearFields());
    }

    private void loadServices() {
        try {
            List<Service> services = serviceDAO.getAllServices();
            servicesData.clear();
            servicesData.addAll(services);
        } catch (SQLException e) {
            showDatabaseError("Could not load services", e);
        }
    }

    @FXML
    private void handleAddService() {
        if (isInputValid()) {
            try {
                Service service = new Service();
                service.setServiceName(serviceNameField.getText());
                service.setPrice(new BigDecimal(priceField.getText()));
                service.setDescription(descriptionArea.getText());
                
                serviceDAO.addService(service);
                clearFields();
                loadServices();
            } catch (SQLException e) {
                showDatabaseError("Could not add service", e);
            }
        }
    }

    @FXML
    private void handleEditService() {
        if (selectedService != null && isInputValid()) {
            try {
                selectedService.setServiceName(serviceNameField.getText());
                selectedService.setPrice(new BigDecimal(priceField.getText()));
                selectedService.setDescription(descriptionArea.getText());
                
                serviceDAO.updateService(selectedService);
                clearFields();
                loadServices();
            } catch (SQLException e) {
                showDatabaseError("Could not edit service", e);
            }
        }
    }

    @FXML
    private void handleDeleteService() {
        if (selectedService != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Service");
            alert.setHeaderText("Are you sure you want to delete this service?");
            alert.setContentText("Service: " + selectedService.getServiceName());

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    serviceDAO.deleteService(selectedService.getServiceId());
                    clearFields();
                    loadServices();
                } catch (SQLException e) {
                    showDatabaseError("Could not delete service", e);
                }
            }
        }
    }

    private void populateFields(Service service) {
        serviceNameField.setText(service.getServiceName());
        priceField.setText(service.getPrice().toString());
        descriptionArea.setText(service.getDescription());
    }

    private void clearFields() {
        serviceNameField.clear();
        priceField.clear();
        descriptionArea.clear();
        selectedService = null;
    }

    private boolean isInputValid() {
        String errorMessage = "";
        
        if (serviceNameField.getText() == null || serviceNameField.getText().trim().isEmpty()) {
            errorMessage += "Service Name is required\n";
        }
        
        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
            errorMessage += "Price is required\n";
        } else {
            try {
                new BigDecimal(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Price must be a valid number\n";
            }
        }

        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void showDatabaseError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
