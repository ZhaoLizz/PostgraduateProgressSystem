package main.java.controller;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.util.function.Function;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;


@ViewController(value = "../../resources/layout/layout_student_grid.fxml")
public class StudentGridController {
    @FXML
    public StackPane root;
    @FXML
    private JFXTreeTableView<StudentFX> treeTableView;
    @FXML
    private JFXTreeTableColumn<StudentFX, String> noColumn;
    @FXML
    private JFXTreeTableColumn<StudentFX, String> nameColumn;
    @FXML
    private JFXTreeTableColumn<StudentFX, String> pwColumn;
    @FXML
    private JFXTreeTableColumn<StudentFX, String> targetColumn;
    @FXML
    private JFXTreeTableColumn<StudentFX, String> specialColumn;
    @FXML
    private JFXTreeTableColumn<StudentFX, Integer> isManagerColumn;


    @FXML
    private JFXButton treeTableViewRemove;
    @FXML
    private Label treeTableViewCount;
    @FXML
    private JFXTextField searchField;

    private static final String PREFIX = "( ";
    private static final String POSTFIX = " )";

    static final class StudentFX extends RecursiveTreeObject<StudentFX> {
        StringProperty student_no;
        StringProperty student_name;
        StringProperty student_pw;
        StringProperty student_target;
        StringProperty student_special;
        SimpleIntegerProperty student_is_manager;

        public StudentFX(String no, String name, String pw, String target, String special, Integer isManager) {
            student_no = new SimpleStringProperty(no);
            student_name = new SimpleStringProperty(name);
            student_pw = new SimpleStringProperty(pw);
            student_target = new SimpleStringProperty(target);
            student_special = new SimpleStringProperty(special);
            student_is_manager = new SimpleIntegerProperty(isManager);
        }

        public String getStudent_no() {
            return student_no.get();
        }

        public StringProperty student_noProperty() {
            return student_no;
        }

        public String getStudent_name() {
            return student_name.get();
        }

        public StringProperty student_nameProperty() {
            return student_name;
        }

        public String getStudent_pw() {
            return student_pw.get();
        }

        public StringProperty student_pwProperty() {
            return student_pw;
        }

        public String getStudent_target() {
            return student_target.get();
        }

        public StringProperty student_targetProperty() {
            return student_target;
        }

        public String getStudent_special() {
            return student_special.get();
        }

        public StringProperty student_specialProperty() {
            return student_special;
        }

        public int getStudent_is_manager() {
            return student_is_manager.get();
        }

        public SimpleIntegerProperty student_is_managerProperty() {
            return student_is_manager;
        }

        @Override
        public String toString() {
            return "StudentFX{" +
                    "student_no=" + student_no +
                    ", student_name=" + student_name +
                    ", student_pw=" + student_pw +
                    ", student_target=" + student_target +
                    ", student_special=" + student_special +
                    ", student_is_manager=" + student_is_manager +
                    '}';
        }
    }

    @PostConstruct
    public void init() {
        setupEditableTableView();
    }


    private void setupEditableTableView() {
        setupCellValueFactory(noColumn, StudentFX::student_noProperty);
        setupCellValueFactory(nameColumn,StudentFX::student_nameProperty);
        setupCellValueFactory(pwColumn, StudentFX::student_pwProperty);
        setupCellValueFactory(targetColumn, StudentFX::student_targetProperty);
        setupCellValueFactory(specialColumn, StudentFX::student_specialProperty);
        setupCellValueFactory(isManagerColumn, p -> p.student_is_manager.asObject());

        noColumn.setCellFactory((TreeTableColumn<StudentFX, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        nameColumn.setCellFactory((TreeTableColumn<StudentFX, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        pwColumn.setCellFactory((TreeTableColumn<StudentFX, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        targetColumn.setCellFactory((TreeTableColumn<StudentFX, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        specialColumn.setCellFactory((TreeTableColumn<StudentFX, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        isManagerColumn.setCellFactory((TreeTableColumn<StudentFX, Integer> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });


    }



    private <T> void setupCellValueFactory(JFXTreeTableColumn<StudentFX, T> column, Function<StudentFX, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<StudentFX, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

}
