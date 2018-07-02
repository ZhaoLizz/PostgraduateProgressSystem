package main.java.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.IntegerTextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;
import main.java.db.JDBCDao;
import main.java.db.JDBCHelper;
import main.java.model.Chapter;
import main.java.model.Progress;
import main.java.model.Student;
import main.java.model.Subject;
import main.java.utils.Toast;

@ViewController(value = "../../resources/layout/layout_progress_grid.fxml")
public class StuProgressGridController {
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
    private JFXButton treeTableViewAdd;
    @FXML
    private JFXButton treeTableViewRemove;
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
        StringProperty student_no;

        public ProgressMessage(String subName, String chapterName, int chapterIndex, String material, String special, String school, String student_no) {
            this.subName = new SimpleStringProperty(subName);
            this.chapterName = new SimpleStringProperty(chapterName);
            this.chapterIndex = new SimpleIntegerProperty(chapterIndex);
            this.material = new SimpleStringProperty(material);
            this.special = new SimpleStringProperty(special);
            this.school = new SimpleStringProperty(school);
            this.student_no = new SimpleStringProperty(student_no);
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
            return "StudentFX{" +
                    "student_name=" + subName +
                    ", student_pw=" + chapterName +
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


        //添加数据,设置table属性
        final ObservableList<ProgressMessage> progressMessagesList = fetchProgressMessage();
        treeTableView.setRoot(new RecursiveTreeItem<>(progressMessagesList, RecursiveTreeObject::getChildren));
        treeTableView.setShowRoot(false);
        treeTableView.setEditable(true);
        treeTableViewCount.textProperty()
                .bind(Bindings.createStringBinding(() ->
                                PREFIX + treeTableView.getCurrentItemsCount() + POSTFIX,
                        treeTableView.currentItemsCountProperty()));

        //设置搜索框
        searchField.textProperty().addListener(setupSerachField(treeTableView));

        //TODO 学生进度信息增删,有待商榷
        //设置增删按钮
        treeTableViewAdd.setVisible(false);
        treeTableViewAdd.setOnMouseClicked(e -> {
            Toast.logger("可以添加自己的进度信息");
        });
        //删除(自己的信息)
        treeTableViewRemove.setVisible(false);
        /*treeTableViewRemove.disableProperty()
                .bind(Bindings.equal(-1, treeTableView.getSelectionModel().selectedIndexProperty()));
        treeTableViewRemove.setOnMouseClicked(e->{
            ProgressMessage removedItem = treeTableView.getSelectionModel().selectedItemProperty().get().getValue();
            progressMessagesList.remove(removedItem);
            final IntegerProperty currCountProp = treeTableView.currentItemsCountProperty();
            currCountProp.set(currCountProp.get() - 1);

            Progress progress = new Progress();
            progress.setStudent_no(removedItem.student_no.getValue());
            progress.setSubject_name(removedItem.subName.getValue());
            progress.setChapter_name(removedItem.chapterName.getValue());
            progress.delete(new JDBCDao.DeleteListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("delete progress succeed");
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });*/
    }




    private ChangeListener<String> setupSerachField(final JFXTreeTableView<ProgressMessage> tableView) {
        return (observable, oldValue, newValue) -> tableView.setPredicate(progressMessageTreeItem -> {
            final ProgressMessage progressMessage = progressMessageTreeItem.getValue();
            return progressMessage.subName.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || progressMessage.material.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || progressMessage.chapterName.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || progressMessage.school.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || progressMessage.special.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || Integer.toString(progressMessage.chapterIndex.getValue()).contains(newValue);
        });
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
            ProgressMessage progressMessage = new ProgressMessage(subject_name, chapter_name, chapter_index, subject_refer_material, student_special[0], student_target[0], student_no);
            list.add(progressMessage);
        }

        return list;
    }

}
