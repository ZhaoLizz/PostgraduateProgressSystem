package main.java.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;
import main.java.db.JDBCDao;
import main.java.db.JDBCHelper;
import main.java.model.Chapter;
import main.java.model.Progress;
import main.java.model.Student;
import main.java.model.Subject;

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

    private static final String PREFIX = "( ";
    private static final String POSTFIX = " )";

    @PostConstruct
    public void init() {
        setupEditableTableView();
    }

    static final class ProgressMessage extends RecursiveTreeObject<ProgressMessage> {
        StringProperty subName;
        StringProperty chapterName;
        SimpleIntegerProperty chapterIndex;
        StringProperty material;
        StringProperty special;
        StringProperty school;

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

        @Override
        public String toString() {
            return "ProgressMessage{" +
                    "subName=" + subName +
                    ", chapterName=" + chapterName +
                    ", chapterIndex=" + chapterIndex +
                    ", material=" + material +
                    ", special=" + special +
                    ", school=" + school +
                    '}';
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
        setupCellValueFactory(subNameColumn, ProgressMessage::subNameProperty);
        setupCellValueFactory(chapterNameColumn, ProgressMessage::chapterNameProperty);
        setupCellValueFactory(chapterIndexColumn, p -> p.chapterIndex.asObject());
        setupCellValueFactory(materialColumn, ProgressMessage::materialProperty);
        setupCellValueFactory(specialColumn, ProgressMessage::specialProperty);
        setupCellValueFactory(schoolColumn, ProgressMessage::schoolProperty);

        //设置编辑功能
        //科目名称
        subNameColumn.setCellFactory((TreeTableColumn<ProgressMessage, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        subNameColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<ProgressMessage, String> t) -> {
            t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition().getRow())
                    .getValue().subName.set(t.getNewValue());

            String value = t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition().getRow())
                    .getValue().subName.toString();
            System.out.println(value);
        });
        //章节名称
        chapterNameColumn.setCellFactory((TreeTableColumn<ProgressMessage, String> param )-> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        chapterNameColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<ProgressMessage,String> t) ->{
            t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition().getRow())
                    .getValue().chapterName.set(t.getNewValue());
        });
        //TODO 剩余的几行事件处理

        final ObservableList<ProgressMessage> progressMessagesList = fetchProgressMessage();
        treeTableView.setRoot(new RecursiveTreeItem<>(progressMessagesList, RecursiveTreeObject::getChildren));
        treeTableView.setShowRoot(false);
        treeTableView.setEditable(true);
        treeTableViewCount.textProperty()
                .bind(Bindings.createStringBinding(() ->
                                PREFIX + treeTableView.getCurrentItemsCount() + POSTFIX,
                        treeTableView.currentItemsCountProperty()));

        //TODO 搜索框处理
//        searchField.textProperty().addListener();
    }

    private ObservableList<ProgressMessage> fetchProgressMessage() {
        final ObservableList<ProgressMessage> list = FXCollections.observableArrayList();

        //连接查询
        JDBCHelper helper = JDBCHelper.getInstance();
        List<Map<String, Object>> queryResult = null;
        String sql = "select Progress.subject_name,Progress.chapter_name,Chapter.chapter_index,\n" +
                "  Progress.student_no,Subject.subject_refer_material from Subject,Chapter,Progress\n" +
                "where  Subject.subject_name = Chapter.subject_name and Chapter.chapter_name = Progress.chapter_name\n" +
                "and Chapter.subject_name = Progress.subject_name";
        try {
            queryResult = helper.findMlutiResult(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //根据查询结果构造ProgressMessage列表
        for (Map<String, Object> map : queryResult) {

            String subject_name = (String) map.get("subject_name");
            String chapter_name = (String) map.get("chapter_name");
            int chapter_index = (int) map.get("chapter_index");
            String subject_refer_material = (String) map.get("subject_refer_material");
            final String[] student_target = {null};
            final String[] student_special = {null};

            String student_no = (String) map.get("student_no");
            Student student = new Student();
            student.setStudent_no(student_no);
            student.query(Student.class, new JDBCDao.QueryListener<Student>() {
                @Override
                public void onSucceed(List<Student> result) {
                    student_target[0] = result.get(0).getStudent_target();
                    student_special[0] = result.get(0).getStudent_special();
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
            ProgressMessage progressMessage = new ProgressMessage(subject_name, chapter_name, chapter_index, subject_refer_material, student_target[0], student_special[0]);
            list.add(progressMessage);
        }

        return list;
    }

    public static void main(String[] args) {
        ProgressGridController controller = new ProgressGridController();
        controller.fetchProgressMessage();
    }


}
