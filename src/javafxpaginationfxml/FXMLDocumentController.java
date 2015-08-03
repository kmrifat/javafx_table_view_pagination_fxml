/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxpaginationfxml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author rifat
 */
public class FXMLDocumentController implements Initializable {
    
    private Label label;
    StackPane tablePane = new StackPane();
    @FXML
    private TableView<Sample> tableView;
    @FXML
    private TableColumn<Sample, Integer> clm1;
    @FXML
    private TableColumn<Sample, String> clm2;
    @FXML
    private TableColumn<Sample, String> clm3;
    
    ProgressIndicator progressIndicator = new ProgressIndicator();

    
    private final static int dataSize = 499;
    private final static int rowsPerPage = 100;

    private final ObservableList<Sample> dataList = FXCollections.observableArrayList(new ArrayList<Sample>(rowsPerPage));
    
    @FXML
    private AnchorPane acMain;
    @FXML
    private StackPane stackpane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableView.setItems(dataList);
        clm1.setCellValueFactory(new PropertyValueFactory<>("id"));
        clm2.setCellValueFactory(new PropertyValueFactory<>("foo"));
        clm3.setCellValueFactory(new PropertyValueFactory<>("bar"));
        
        
        Pagination pagination = new Pagination((dataSize / rowsPerPage + 1), 0);
        progressIndicator.setMaxSize(200, 200);

        // wrap table and progress indicator into a stackpane, progress indicator is on top of table
        tablePane.getChildren().add(tableView);
        tablePane.getChildren().add(progressIndicator);

        pagination.setPageFactory((final Integer pageIndex) -> {
            progressIndicator.setVisible(true);
            
            dataList.clear();
            
            // long running background task
            new Thread() {
                public void run() {
                    try {
                        int fromIndex = pageIndex * rowsPerPage;
                        int toIndex = Math.min(fromIndex + rowsPerPage, dataSize);
                        
                        List<Sample> loadedList = loadData(fromIndex, toIndex);
                        
                        Platform.runLater(() -> dataList.setAll( loadedList));
                        
                    } finally {
                        
                        Platform.runLater(() -> progressIndicator.setVisible(false));
                        
                    }
                }
            }.start();
            
            return tablePane;
        });
        
        BorderPane borderPane = new BorderPane(pagination);
        stackpane.getChildren().add(borderPane);
        
    }    
    
    private List<Sample> loadData(int fromIndex, int toIndex) {
        List<Sample> list = new ArrayList<>();
        try {
            for (int i = fromIndex; i < toIndex; i++) {
                list.add(new Sample(i, "foo " + i, "bar " + i));
            }
            Thread.sleep(500);
        } catch( Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @FXML
    private void onTableClick(MouseEvent event) {
        System.out.println("Click");
        
        Sample selectedId = tableView.getSelectionModel().getSelectedItem();
        System.out.println(selectedId.getId());
        System.out.println(selectedId.getFoo());
        System.out.println(selectedId.getBar());
    }
    
}
