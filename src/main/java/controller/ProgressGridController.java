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

    @PostConstruct
    public void init() {

    }

    static final class ProgressMessage extends RecursiveTreeObject<ProgressMessage> {
         StringProperty subName;
         StringProperty chapterName;
         SimpleIntegerProperty chapterIndex;
         StringProperty material;
         StringProperty special;
         StringProperty school;

        public ProgressMessage() {
        }

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
        subNameColumn.setCellFactory((TreeTableColumn<ProgressMessage, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        subNameColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<ProgressMessage, String> t) -> {
            t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition().getRow())
                    .getValue().subName.set(t.getNewValue());
        });


    }

    private List<ProgressMessage> fetchProgressMessage() {
        List<ProgressMessage> list = new ArrayList<>();
        ProgressMessage progressMessage = new ProgressMessage();
        //查询全部的Progress表数据
        Progress progress = new Progress();
        progress.query(Progress.class, new JDBCDao.QueryListener<Progress>() {
            @Override
            public void onSucceed(List<Progress> result) {
                for (Progress p : result) {

                    //遍历每条查询到的Progress结果,在这个结果的基础上派生出学生, 章节->科目条目
                    progressMessage.setSubName(p.getSubject_name());
                    progressMessage.setChapterName(p.getChapter_name());

                    Chapter chapter = new Chapter();
                    chapter.setChapter_name(p.getChapter_name());
                    chapter.setSubject_name(p.getSubject_name());
                    chapter.query(Chapter.class, new JDBCDao.QueryListener<Chapter>() {
                        @Override
                        public void onSucceed(List<Chapter> result) {
                            System.out.println(result.size());
                            Chapter chapterData = result.get(0);
                            progressMessage.setChapterIndex(chapterData.getChatper_index());

                            //由章节派生出科目
                            Subject subject = new Subject();
                            subject.setSubject_name(chapterData.getSubject_name());
                            subject.query(Subject.class, new JDBCDao.QueryListener<Subject>() {
                                @Override
                                public void onSucceed(List<Subject> result) {
                                    System.out.println(result.get(0));

                                    progressMessage.setMaterial(result.get(0).getSubject_refer_material());
                                }

                                @Override
                                public void onFailed(Exception e) {
                                }
                            });
                        }

                        @Override
                        public void onFailed(Exception e) {
e.printStackTrace();
                        }
                    });

                    //根据Progress表的学生用户名字段查询专业,学校
                    Student student = new Student();
                    student.setStudent_no(p.getStudent_no());
                    student.query(Student.class, new JDBCDao.QueryListener<Student>() {
                        @Override
                        public void onSucceed(List<Student> result) {
                            System.out.println(result.get(0));
                            progressMessage.setSchool(result.get(0).getStudent_no());
                            progressMessage.setSpecial(result.get(0).getStudent_special());
                        }
                        @Override
                        public void onFailed(Exception e) {
                            e.printStackTrace();

                        }
                    });

                    System.out.println("here");
                    System.out.println(progressMessage);
                }

            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    public static void main(String[] args) {
        ProgressGridController controller = new ProgressGridController();
        controller.fetchProgressMessage();
    }


}
