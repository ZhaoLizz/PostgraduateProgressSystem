package main.java.controller;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.IntegerTextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.util.List;
import java.util.function.Function;

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
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.db.JDBCDao;
import main.java.model.CurUser;
import main.java.model.Subject;
import main.java.utils.TextUtils;
import main.java.utils.Toast;

@ViewController(value = "../../resources/layout/layout_subject_grid.fxml")
public class SubjectGridController {
    @FXML
    public StackPane root;
    @FXML
    private JFXTreeTableView<SubjectMessage> treeTableView;
    @FXML
    private JFXTreeTableColumn<SubjectMessage, String> subjectNameColumn;
    @FXML
    private JFXTreeTableColumn<SubjectMessage, String> subjectReferMaterialColumn;
    @FXML
    private JFXTreeTableColumn<SubjectMessage, Integer> subjectChapterNumColumn;


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

    static final class SubjectMessage extends RecursiveTreeObject<SubjectMessage> {
        StringProperty subjectName;
        SimpleIntegerProperty subjectChapterNum;
        StringProperty subjectReferMaterial;

        public SubjectMessage(Subject subject) {
            subjectName = new SimpleStringProperty(subject.getSubject_name());
            subjectChapterNum = new SimpleIntegerProperty(subject.getSubject_chapter_num());
            subjectReferMaterial = new SimpleStringProperty(subject.getSubject_refer_material());
        }

        @Override
        public String toString() {
            return "SubjectMessage{" +
                    "subjectName=" + subjectName +
                    ", subjectChapterNum=" + subjectChapterNum +
                    ", subjectReferMaterial=" + subjectReferMaterial +
                    '}';
        }

        public String getSubjectName() {
            return subjectName.get();
        }

        public StringProperty subjectNameProperty() {
            return subjectName;
        }

        public int getSubjectChapterNum() {
            return subjectChapterNum.get();
        }

        public SimpleIntegerProperty subjectChapterNumProperty() {
            return subjectChapterNum;
        }

        public String getSubjectReferMaterial() {
            return subjectReferMaterial.get();
        }

        public StringProperty subjectReferMaterialProperty() {
            return subjectReferMaterial;
        }
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<SubjectMessage, T> column, Function<SubjectMessage, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<SubjectMessage, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private void setupEditableTableView() {
        setupCellValueFactory(subjectNameColumn, SubjectMessage::subjectNameProperty);
        setupCellValueFactory(subjectReferMaterialColumn, SubjectMessage::subjectReferMaterialProperty);
        setupCellValueFactory(subjectChapterNumColumn, p -> p.subjectChapterNum.asObject());

        subjectNameColumn.setCellFactory((TreeTableColumn<SubjectMessage, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        subjectReferMaterialColumn.setCellFactory((TreeTableColumn<SubjectMessage, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        subjectChapterNumColumn.setCellFactory((TreeTableColumn<SubjectMessage, Integer> param) -> {
            return new GenericEditableTreeTableCell<>(new IntegerTextFieldEditorBuilder());
        });

        subjectChapterNumColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<SubjectMessage, Integer> t) -> {
            Subject condition = new Subject();
            condition.setSubject_name(t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition()
                            .getRow())
                    .getValue().subjectName.getValue());

            Subject newValue = new Subject();
            newValue.setSubject_chapter_num(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("更新成功 subject_chapter_num");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().subjectChapterNum.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        subjectNameColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<SubjectMessage, String> t) -> {
            Subject condition = new Subject();
            condition.setSubject_name(t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition()
                            .getRow())
                    .getValue().subjectName.getValue());

            Subject newValue = new Subject();
            newValue.setSubject_name(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("更新成功 subject_name");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().subjectName.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        subjectReferMaterialColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<SubjectMessage, String> t) -> {
            Subject condition = new Subject();
            condition.setSubject_name(t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition()
                            .getRow())
                    .getValue().subjectName.getValue());

            Subject newValue = new Subject();
            newValue.setSubject_refer_material(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("更新成功 subject_refer_material");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().subjectReferMaterial.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });


        //添加数据
        final ObservableList<SubjectMessage> subjectMessagelist = fetchSubjectMessage();
        treeTableView.setRoot(new RecursiveTreeItem<>(subjectMessagelist, RecursiveTreeObject::getChildren));
        treeTableView.setShowRoot(false);
        treeTableView.setEditable(true);
        treeTableViewCount.textProperty()
                .bind(Bindings.createStringBinding(() ->
                                PREFIX + treeTableView.getCurrentItemsCount() + POSTFIX,
                        treeTableView.currentItemsCountProperty()));
        searchField.textProperty().addListener(setupSerachField(treeTableView));

        //增改按钮
        treeTableViewRemove.disableProperty()
                .bind(Bindings.equal(-1, treeTableView.getSelectionModel().selectedIndexProperty()));
        treeTableViewRemove.setOnMouseClicked(e -> {
            SubjectMessage removedItem = treeTableView.getSelectionModel().selectedItemProperty().get().getValue();
            subjectMessagelist.remove(removedItem);
            final IntegerProperty currCountProp = treeTableView.currentItemsCountProperty();
            currCountProp.set(currCountProp.get() - 1);

            Subject sub = new Subject();
            sub.setSubject_name(removedItem.getSubjectName());
            sub.delete(new JDBCDao.DeleteListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("delete subject succeed");
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        treeTableViewAdd.setOnMouseClicked(e -> {
            addSubject((Stage) treeTableView.getScene().getWindow(), subjectMessagelist);
        });
    }

    private void addSubject(Stage stage, ObservableList<SubjectMessage> subjectMessagesObservableList) {
        //创建注册的dialog
        JFXAlert alert = new JFXAlert(stage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        Label label = new Label("添加科目信息");
        label.setFont(Font.font(Font.getFamilies().get(3)));
        layout.setHeading(label);

        JFXTextField subjectNameTextField = new JFXTextField();
        subjectNameTextField.setPromptText("科目名称");
        JFXTextField chapterNumTextField = new JFXTextField();
        chapterNumTextField.setPromptText("章节数目");
        JFXTextField materialTextField = new JFXTextField();
        materialTextField.setPromptText("参考资料");


        VBox vBox = new VBox(subjectNameTextField, chapterNumTextField, materialTextField);
        vBox.setSpacing(20);

        JFXButton commitBtn = new JFXButton("确定");
        commitBtn.getStyleClass().add("dialog-accept");
        JFXButton cancelBtn = new JFXButton("取消");
        cancelBtn.getStyleClass().add("dialog-accept");
        cancelBtn.setOnAction(event1 -> alert.hideWithAnimation());
        //注册逻辑
        commitBtn.setOnAction(event -> {
            String subjectName = subjectNameTextField.getText();
            Integer chapterNum = Integer.parseInt(chapterNumTextField.getText());
            String material = materialTextField.getText();

            if (TextUtils.isEmpty(subjectName) || TextUtils.isEmpty(chapterNumTextField.getText()) || TextUtils.isEmpty(material)) {
                Toast.showDialog("提示", "所需信息不能为空!", (Stage) treeTableView.getScene().getWindow());
                return;
            } else {
                //先仅仅设置主码,查询是否唯一
                Subject subject = new Subject();
                subject.setSubject_name(subjectName);
                subject.query(Subject.class, new JDBCDao.QueryListener<Subject>() {
                    @Override
                    public void onSucceed(List<Subject> result) {
                        if (result == null || result.size() == 0) {
                            System.out.println("subject name合法,执行save()");
                            subject.setSubject_refer_material(material);
                            subject.setSubject_chapter_num(chapterNum);
                            subject.save(new JDBCDao.SaveListerner() {
                                @Override
                                public void onSucceed() {
                                    subjectMessagesObservableList.add(new SubjectMessage(subject));
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
                            Toast.showDialog("提示", "科目信息已存在", stage);
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

    private ObservableList<SubjectMessage> fetchSubjectMessage() {
        final ObservableList<SubjectMessage> list = FXCollections.observableArrayList();
        Subject subject = new Subject();
        subject.query(Subject.class, new JDBCDao.QueryListener<Subject>() {
            @Override
            public void onSucceed(List<Subject> result) {
                for (Subject s : result) {
                    SubjectMessage subjectMessage = new SubjectMessage(s);
                    list.add(subjectMessage);
                }
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    private ChangeListener<String> setupSerachField(final JFXTreeTableView<SubjectMessage> tableView) {
        return (observable, oldValue, newValue) -> tableView.setPredicate(progressMessageTreeItem -> {
            final SubjectMessage subjectMessage = progressMessageTreeItem.getValue();
            return subjectMessage.subjectName.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || subjectMessage.subjectReferMaterial.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || Integer.toString(subjectMessage.subjectChapterNum.getValue()).contains(newValue);
        });
    }


}
