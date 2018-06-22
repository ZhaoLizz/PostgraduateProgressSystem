package main.java.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.util.ArrayList;
import java.util.List;
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
import main.java.db.JDBCDao;
import main.java.model.Progress;

@ViewController(value = "../../resources/layout/layout_progress_grid.fxml")
public class ProgressGridController {
    @FXML
    public StackPane root;
    @FXML
    private JFXTreeTableView<ProgressMessage> treeTableView;
    @FXML
    private JFXTreeTableColumn<ProgressMessage, String> subNameColumn;  //科目名称
    @FXML
    private JFXTreeTableColumn<ProgressMessage, String> chapterNameColumn; //章节名称
    @FXML
    private JFXTreeTableColumn<ProgressMessage, Integer> chapterIndexColumn; //章节数目
    @FXML
    private JFXTreeTableColumn<ProgressMessage, String> materialColumn;    //资料名称
    @FXML
    private JFXTreeTableColumn<ProgressMessage, String> specialColumn;  //专业名称
    @FXML
    private JFXTreeTableColumn<ProgressMessage, String> schoolColumn;  //学校名称
    @FXML
    private Label treeTableViewCount;
    @FXML
    private JFXTextField searchField;

    @PostConstruct
    public void init() {

    }

    static final class ProgressMessage extends RecursiveTreeObject<ProgressMessage> {
        final StringProperty subName;
        final StringProperty chapterName;
        final SimpleIntegerProperty chapterIndex;
        final StringProperty material;
        final StringProperty special;
        final StringProperty school;

        public ProgressMessage(String subName, String chapterName, int chapterIndex, String material, String special, String school) {
            this.subName = new SimpleStringProperty(subName);
            this.chapterName = new SimpleStringProperty(chapterName);
            this.chapterIndex = new SimpleIntegerProperty(chapterIndex);
            this.material = new SimpleStringProperty(material);
            this.special = new SimpleStringProperty(special);
            this.school = new SimpleStringProperty(school);
        }

        public StringProperty subNameProperty() {
            return subName;
        }

        public StringProperty chapterNameProperty() {
            return chapterName;
        }

        public SimpleIntegerProperty getChapterIndex() {
            return chapterIndex;
        }

        public StringProperty materialProperty() {
            return material;
        }

        public StringProperty specialProperty() {
            return special;
        }

        public StringProperty schoolProperty() {
            return school;
        }

        public void setSubName(String subName) {
            this.subName.set(subName);
        }

        public void setChapterName(String chapterName) {
            this.chapterName.set(chapterName);
        }

        public void setChapterIndex(int chapterIndex) {
            this.chapterIndex.set(chapterIndex);
        }

        public void setMaterial(String material) {
            this.material.set(material);
        }

        public void setSpecial(String special) {
            this.special.set(special);
        }

        public void setSchool(String school) {
            this.school.set(school);
        }
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<ProgressMessage, T> column, Function<ProgressMessage, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<ProgressMessage, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private void setupEditableTableView() {
        /*final StringProperty subName;
        final StringProperty chapterName;
        final SimpleIntegerProperty chapterIndex;
        final StringProperty material;
        final StringProperty special;
        final StringProperty school;*/
        setupCellValueFactory(subNameColumn, ProgressMessage::subNameProperty);
        setupCellValueFactory(chapterNameColumn, ProgressMessage::chapterNameProperty);
        setupCellValueFactory(chapterIndexColumn, p -> p.chapterIndex.asObject());
        setupCellValueFactory(materialColumn, ProgressMessage::materialProperty);
        setupCellValueFactory(specialColumn, ProgressMessage::specialProperty);
        setupCellValueFactory(schoolColumn, ProgressMessage::schoolProperty);

        //设置编辑功能
        subNameColumn.setCellFactory((TreeTableColumn<ProgressMessage,String> param)->{
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        subNameColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<ProgressMessage,String> t) ->{
            t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition().getRow())
                    .getValue().subName.set(t.getNewValue());
        });


    }

    private List<ProgressMessage> fetchProgressMessage() {
        List<ProgressMessage> list = new ArrayList<>();
        Progress progress = new Progress();
        progress.setChapter_name("*");
        progress.query(Progress.class, new JDBCDao.QueryListener<Progress>() {
            @Override
            public void onSucceed(List<Progress> result) {
                System.out.println(result.size());
                System.out.println(result.get(0));
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
        return null;
    }

    public static void main(String[] args) {
        ProgressGridController controller = new ProgressGridController();
        controller.fetchProgressMessage();
    }


}
