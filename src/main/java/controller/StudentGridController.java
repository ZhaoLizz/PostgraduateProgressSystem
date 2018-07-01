package main.java.controller;


import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.db.JDBCDao;
import main.java.model.CurUser;
import main.java.model.Student;
import main.java.utils.TextUtils;
import main.java.utils.Toast;


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
    private JFXTreeTableColumn<StudentFX, String> isManagerColumn;

    @FXML
    private JFXButton treeTableViewAdd;
    @FXML
    private JFXButton treeTableViewRemove;
    @FXML
    private Label treeTableViewCount;
    @FXML
    private JFXTextField searchField;

    /*//右下角弹出按钮
    @FXML
    private JFXNodesList nodesList;
    @FXML
    private JFXButton newButton;
    @FXML
    private JFXButton deleteButton;
    @FXML
    private JFXButton addButton;*/


    private static final String PREFIX = "( ";
    private static final String POSTFIX = " )";

    static final class StudentFX extends RecursiveTreeObject<StudentFX> {
        StringProperty student_no;
        StringProperty student_name;
        StringProperty student_pw;
        StringProperty student_target;
        StringProperty student_special;
        StringProperty student_is_manager;

        public StudentFX(String no, String name, String pw, String target, String special, Boolean isManager) {
            student_no = new SimpleStringProperty(no);
            student_name = new SimpleStringProperty(name);
            student_pw = new SimpleStringProperty(pw);
            student_target = new SimpleStringProperty(target);
            student_special = new SimpleStringProperty(special);
            student_is_manager = new SimpleStringProperty(String.valueOf(isManager));
        }

        public StudentFX(Student student) {
            student_no = new SimpleStringProperty(student.getStudent_no());
            student_name = new SimpleStringProperty(student.getStudent_name());
            student_pw = new SimpleStringProperty(student.getStudent_pw());
            student_target = new SimpleStringProperty(student.getStudent_target());
            student_special = new SimpleStringProperty(student.getStudent_special());
            student_is_manager = new SimpleStringProperty(String.valueOf(student.getStudent_is_manager() > 0));
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

        public String getStudent_is_manager() {
            return student_is_manager.get();
        }

        public StringProperty student_is_managerProperty() {
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
        setupCellValueFactory(nameColumn, StudentFX::student_nameProperty);
        setupCellValueFactory(pwColumn, StudentFX::student_pwProperty);
        setupCellValueFactory(targetColumn, StudentFX::student_targetProperty);
        setupCellValueFactory(specialColumn, StudentFX::student_specialProperty);
        setupCellValueFactory(isManagerColumn, StudentFX::student_is_managerProperty);
//        setupCellValueFactory(isManagerColumn, p -> p.student_is_manager.asObject());

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

        isManagerColumn.setOnEditStart((TreeTableColumn.CellEditEvent<StudentFX, String> t) -> {
            JFXAlert alert = new JFXAlert((Stage) treeTableView.getScene().getWindow());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(true);
            JFXDialogLayout layout = new JFXDialogLayout();
            JFXToggleButton toggleButton = new JFXToggleButton();
            toggleButton.setText("管理员权限");
            toggleButton.setOnMouseReleased(v->{
                boolean isSelect = toggleButton.isSelected();
                t.getTreeTableView()
                        .getTreeItem(t.getTreeTablePosition().getRow())
                        .getValue().student_is_manager.set(String.valueOf(isSelect));

                Student condition = getStudentCondition(t);
                Student newValue = new Student();
                newValue.setStudent_is_manager(isSelect ? 1 : 0);
                newValue.update(condition, new JDBCDao.UpdateListener() {
                    @Override
                    public void onSucceed() {
                        Toast.logger("update ismanager succeed");
                    }

                    @Override
                    public void onFailed(Exception e) {
                        e.printStackTrace();
                    }
                });
            });
            layout.setBody(toggleButton);
            layout.setMaxHeight(200);
            layout.setMaxWidth(200);
            alert.setContent(layout);
            alert.show();


        });

        noColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<StudentFX, String> t) -> {
            Student condition = getStudentCondition(t);
            Student newValue = new Student();
            newValue.setStudent_no(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("update student_no succeed!");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().student_no.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        nameColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<StudentFX, String> t) -> {
            Student condition = getStudentCondition(t);
            Student newValue = new Student();
            newValue.setStudent_name(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("update student_name succeed!");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().student_name.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        pwColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<StudentFX, String> t) -> {
            Student condition = getStudentCondition(t);
            Student newValue = new Student();
            newValue.setStudent_pw(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("update student_pw succeed!");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().student_pw.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        targetColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<StudentFX, String> t) -> {
            Student condition = getStudentCondition(t);
            Student newValue = new Student();
            newValue.setStudent_target(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("update student_target succeed!");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().student_target.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        specialColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<StudentFX, String> t) -> {
            Student condition = getStudentCondition(t);
            Student newValue = new Student();
            newValue.setStudent_special(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("update student_special succeed!");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().student_special.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });


        //添加数据,设置属性
        final ObservableList<StudentFX> studentFXObservableList = fetchStudentFX();
        treeTableView.setRoot(new RecursiveTreeItem<>(studentFXObservableList, RecursiveTreeObject::getChildren));
        treeTableView.setShowRoot(false);
        treeTableView.setEditable(true);
        treeTableViewCount.textProperty()
                .bind(Bindings.createStringBinding(() ->
                                PREFIX + treeTableView.getCurrentItemsCount() + POSTFIX,
                        treeTableView.currentItemsCountProperty()));

        searchField.textProperty().addListener(setupSerachField(treeTableView));

        //TODO 添加删除
        treeTableViewAdd.setOnMouseClicked(e -> {
            addStudent((Stage) treeTableView.getScene().getWindow(), studentFXObservableList);
        });

        treeTableViewRemove.disableProperty()
                .bind(Bindings.equal(-1, treeTableView.getSelectionModel().selectedIndexProperty()));
        treeTableViewRemove.setOnMouseClicked(e->{
            StudentFX removedItem = treeTableView.getSelectionModel().selectedItemProperty().get().getValue();
            studentFXObservableList.remove(removedItem);
            final IntegerProperty currCountProp = treeTableView.currentItemsCountProperty();
            currCountProp.set(currCountProp.get() - 1);
            //database
            Student student = new Student();
            student.setStudent_no(removedItem.getStudent_no());
            student.delete(new JDBCDao.DeleteListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("delete student succeed");
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

    }

    private void addStudent(Stage stage,ObservableList<StudentFX> studentFXObservableList) {
        //创建注册的dialog
        JFXAlert alert = new JFXAlert(stage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        Label label = new Label("添加学生信息");
        label.setFont(Font.font(Font.getFamilies().get(3)));
        layout.setHeading(label);

        JFXTextField usernameTextField = new JFXTextField();
        usernameTextField.setPromptText("用户名");
        JFXPasswordField passwordTextField = new JFXPasswordField();
        passwordTextField.setPromptText("密码");
        JFXTextField studentnameTextField = new JFXTextField();
        studentnameTextField.setPromptText("姓名");
        JFXTextField targetTextField = new JFXTextField();
        targetTextField.setPromptText("目标院校");
        JFXTextField specialTextField = new JFXTextField();
        specialTextField.setPromptText("考研专业");
        JFXToggleButton toggleButton = new JFXToggleButton();
        toggleButton.setText("管理员权限");


        VBox vBox = new VBox(usernameTextField, passwordTextField, studentnameTextField, targetTextField, specialTextField,toggleButton);
        vBox.setSpacing(20);

        JFXButton commitBtn = new JFXButton("确定");
        commitBtn.getStyleClass().add("dialog-accept");
        JFXButton cancelBtn = new JFXButton("取消");
        cancelBtn.getStyleClass().add("dialog-accept");
        cancelBtn.setOnAction(event1 -> alert.hideWithAnimation());
        //注册逻辑
        commitBtn.setOnAction(event -> {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();
            String studentName = studentnameTextField.getText();
            String target = targetTextField.getText();
            String special = specialTextField.getText();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.showDialog("提示", "用户名或密码不能为空!", (Stage) treeTableView.getScene().getWindow());
                return;
            } else {
                //先仅仅设置用户名,查询是否唯一
                Student student = new Student();
                student.setStudent_no(username);
                student.query(Student.class, new JDBCDao.QueryListener<Student>() {
                    @Override
                    public void onSucceed(List<Student> result) {
                        if (result == null || result.size() == 0) {
                            System.out.println("用户名合法,执行save()");
                            student.setStudent_pw(password);
                            student.setStudent_name(studentName);
                            student.setStudent_target(target);
                            student.setStudent_special(special);
                            student.setStudent_is_manager(toggleButton.isSelected() ? 1 : 0);
                            student.save(new JDBCDao.SaveListerner() {
                                @Override
                                public void onSucceed() {
                                    studentFXObservableList.add(new StudentFX(student));
                                    final IntegerProperty currCountProp = treeTableView.currentItemsCountProperty();
                                    currCountProp.set(currCountProp.get() + 1);
                                    alert.close();
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        } else {
                            Toast.showDialog("提示", "用户名已存在", stage);
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        layout.setActions(commitBtn, cancelBtn);
        layout.setBody(vBox);
        layout.setMaxHeight(200);
        layout.setMaxWidth(300);
        alert.setContent(layout);
        alert.show();
    }

    private ChangeListener<String> setupSerachField(final JFXTreeTableView<StudentFX> tableView) {
        return (observable, oldValue, newValue) -> tableView.setPredicate(progressMessageTreeItem -> {
            final StudentFX studentFX = progressMessageTreeItem.getValue();
            return studentFX.student_no.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || studentFX.student_name.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || studentFX.student_pw.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || studentFX.student_target.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || studentFX.student_special.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || studentFX.student_is_manager.getValue().toLowerCase().contains(newValue.toLowerCase());
        });
    }

    private ObservableList<StudentFX> fetchStudentFX() {
        final ObservableList<StudentFX> list = FXCollections.observableArrayList();

        Student student = new Student();
        student.query(Student.class, new JDBCDao.QueryListener<Student>() {
            @Override
            public void onSucceed(List<Student> result) {
                for (Student s : result) {
                    StudentFX studentFX = new StudentFX(s);
                    list.add(studentFX);
                }
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

        return list;
    }

    private Student getStudentCondition(TreeTableColumn.CellEditEvent<StudentFX, String> t) {
        StudentFX studentFX = t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition().getRow())
                .getValue();

        Student condition = new Student();
        condition.setStudent_no(studentFX.getStudent_no());
        return condition;
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
