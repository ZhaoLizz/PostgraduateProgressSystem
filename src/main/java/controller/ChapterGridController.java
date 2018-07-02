package main.java.controller;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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
import main.java.model.Chapter;
import main.java.model.Subject;
import main.java.utils.TextUtils;
import main.java.utils.Toast;

@ViewController(value = "../../resources/layout/layout_chapter_grid.fxml")
public class ChapterGridController {
    @FXML
    public StackPane root;
    @FXML
    private JFXTreeTableView<ChapterMessage> treeTableView;
    @FXML
    private JFXTreeTableColumn<ChapterMessage, String> subjectNameColumn; //科目名称
    @FXML
    private JFXTreeTableColumn<ChapterMessage, String> chapterNameColumn;     //章节名称
    @FXML
    private JFXTreeTableColumn<ChapterMessage, Integer> chapterIndexColumn;   //章节序号


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

    public ChapterGridController() {
    }


    @PostConstruct
    public void init() {
        setupEditableTableView();
    }

    static final class ChapterMessage extends RecursiveTreeObject<ChapterMessage> {
        StringProperty subjectName;
        StringProperty chapterName;
        SimpleIntegerProperty chapterIndex;

        public ChapterMessage(Chapter chapter) {
            subjectName = new SimpleStringProperty(chapter.getSubject_name());
            chapterName = new SimpleStringProperty(chapter.getChapter_name());
            chapterIndex = new SimpleIntegerProperty(chapter.getChapter_index());
        }

        @Override
        public String toString() {
            return "ChapterMessage{" +
                    "subjectName=" + subjectName +
                    ", chapterName=" + chapterName +
                    ", chapterIndex=" + chapterIndex +
                    '}';
        }

        public String getSubjectName() {
            return subjectName.get();
        }

        public StringProperty subjectNameProperty() {
            return subjectName;
        }

        public String getChapterName() {
            return chapterName.get();
        }

        public StringProperty chapterNameProperty() {
            return chapterName;
        }

        public int getChapterIndex() {
            return chapterIndex.get();
        }

        public SimpleIntegerProperty chapterIndexProperty() {
            return chapterIndex;
        }
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<ChapterMessage, T> column, Function<ChapterMessage, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<ChapterMessage, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private void setupEditableTableView() {
        setupCellValueFactory(subjectNameColumn, ChapterMessage::subjectNameProperty);
        setupCellValueFactory(chapterNameColumn, ChapterMessage::chapterNameProperty);
        setupCellValueFactory(chapterIndexColumn, p -> p.chapterIndex.asObject());

        subjectNameColumn.setCellFactory((TreeTableColumn<ChapterMessage, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        chapterNameColumn.setCellFactory((TreeTableColumn<ChapterMessage, String> param) -> {
            return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
        });
        chapterIndexColumn.setCellFactory((TreeTableColumn<ChapterMessage, Integer> param) -> {
            return new GenericEditableTreeTableCell<>(new IntegerTextFieldEditorBuilder());
        });

        chapterIndexColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<ChapterMessage, Integer> t) -> {
            ChapterMessage chapterMessage = t.getTreeTableView().getTreeItem(t.getTreeTablePosition()
                    .getRow()).getValue();

            Chapter condition = new Chapter();
            condition.setSubject_name(chapterMessage.getSubjectName());
            condition.setChapter_name(chapterMessage.getChapterName());

            Chapter newValue = new Chapter();
            newValue.setChapter_index(t.getNewValue());
            newValue.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("update succeed chapter index");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().chapterIndex.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        });

        subjectNameColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<ChapterMessage, String> t) -> {
            ChapterMessage chapterMessage = t.getTreeTableView().getTreeItem(t.getTreeTablePosition()
                    .getRow()).getValue();

            Chapter condition = new Chapter();
            condition.setSubject_name(chapterMessage.getSubjectName());
            condition.setChapter_name(chapterMessage.getChapterName());

            Chapter chapter = new Chapter();
            chapter.setSubject_name(chapterMessage.getSubjectName());
            chapter.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("更新成功 subject_name");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().subjectName.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        });

        chapterNameColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<ChapterMessage, String> t) -> {
            ChapterMessage chapterMessage = t.getTreeTableView().getTreeItem(t.getTreeTablePosition()
                    .getRow()).getValue();

            Chapter condition = new Chapter();
            condition.setSubject_name(chapterMessage.getSubjectName());
            condition.setChapter_name(chapterMessage.getChapterName());

            Chapter chapter = new Chapter();
            chapter.setChapter_name(chapterMessage.getChapterName());
            chapter.update(condition, new JDBCDao.UpdateListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("更新成功 chapter_name");
                    t.getTreeTableView()
                            .getTreeItem(t.getTreeTablePosition().getRow())
                            .getValue().chapterName.set(t.getNewValue());
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        });

        final ObservableList<ChapterMessage> chapterMessageObservableList = fetchChapterMessage();
        treeTableView.setRoot(new RecursiveTreeItem<>(chapterMessageObservableList, RecursiveTreeObject::getChildren));
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
            ChapterMessage removedItem = treeTableView.getSelectionModel().selectedItemProperty().get().getValue();
            chapterMessageObservableList.remove(removedItem);
            final IntegerProperty currCountProp = treeTableView.currentItemsCountProperty();
            currCountProp.set(currCountProp.get() - 1);

            Chapter cha = new Chapter();
            cha.setSubject_name(removedItem.getSubjectName());
            cha.setChapter_name(removedItem.getChapterName());
            cha.delete(new JDBCDao.DeleteListener() {
                @Override
                public void onSucceed() {
                    Toast.logger("delete chapter succeed");
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        });

        treeTableViewAdd.setOnMouseClicked(e -> {
            addChapter((Stage) treeTableView.getScene().getWindow(), chapterMessageObservableList);
        });
    }

    private void addChapter(Stage stage, ObservableList<ChapterMessage> chapterMessageObservableList) {
        //创建注册的dialog
        JFXAlert alert = new JFXAlert(stage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        Label label = new Label("添加章节信息");
        label.setFont(Font.font(Font.getFamilies().get(3)));
        layout.setHeading(label);


        JFXTextField subjectNameTextField = new JFXTextField();
        subjectNameTextField.setPromptText("科目名称");
        JFXTextField chapterIndexTextField = new JFXTextField();
        chapterIndexTextField.setPromptText("章节序号");
        JFXTextField chapterNameTextField = new JFXTextField();
        chapterNameTextField.setPromptText("章节名称");

        VBox vBox = new VBox(subjectNameTextField, chapterIndexTextField, chapterNameTextField);
        vBox.setSpacing(20);
        JFXButton commitBtn = new JFXButton("确定");
        commitBtn.getStyleClass().add("dialog-accept");
        JFXButton cancelBtn = new JFXButton("取消");
        cancelBtn.getStyleClass().add("dialog-accept");
        cancelBtn.setOnAction(event1 -> alert.hideWithAnimation());
        //注册逻辑
        commitBtn.setOnAction(event -> {
            String subjectName = subjectNameTextField.getText();
            Integer chapterIndex = Integer.parseInt(chapterIndexTextField.getText());
            String chapterName = chapterNameTextField.getText();

            if (TextUtils.isEmpty(subjectName) || TextUtils.isEmpty(chapterIndexTextField.getText()) || TextUtils.isEmpty(chapterName)) {
                Toast.showDialog("提示", "所需信息不能为空!", (Stage) treeTableView.getScene().getWindow());
                return;
            } else {
                Chapter chapter = new Chapter();
                chapter.setSubject_name(subjectName);
                chapter.setChapter_name(chapterName);
                chapter.query(Chapter.class, new JDBCDao.QueryListener<Chapter>() {
                    @Override
                    public void onSucceed(List<Chapter> result) {
                        if (result == null || result.size() == 0) {
                            System.out.println("subject name and chapter name合法,执行save()");
                            chapter.setChapter_index(chapterIndex);
                            chapter.save(new JDBCDao.SaveListerner() {
                                @Override
                                public void onSucceed() {
                                    chapterMessageObservableList.add(new ChapterMessage(chapter));
                                    final IntegerProperty currCountProp = treeTableView.currentItemsCountProperty();
                                    currCountProp.set(currCountProp.get() + 1);
                                    alert.close();
                                }

                                @Override
                                public void onFailed(Exception e) {

                                }
                            });

                        } else {
                            Toast.showDialog("提示", "章节信息已存在", stage);
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {

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

    private ObservableList<ChapterMessage> fetchChapterMessage() {
        final ObservableList<ChapterMessage> list = FXCollections.observableArrayList();
        Chapter chapter = new Chapter();
        chapter.query(Chapter.class, new JDBCDao.QueryListener<Chapter>() {
            @Override
            public void onSucceed(List<Chapter> result) {
                for (Chapter c : result) {
                    ChapterMessage chapterMessage = new ChapterMessage(c);
                    list.add(chapterMessage);
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
        return list;
    }

    private ChangeListener<String> setupSerachField(final JFXTreeTableView<ChapterMessage> tableView) {
        return (observable, oldValue, newValue) -> tableView.setPredicate(progressMessageTreeItem -> {
            final ChapterMessage subjectMessage = progressMessageTreeItem.getValue();
            return subjectMessage.subjectName.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || subjectMessage.chapterName.getValue().toLowerCase().contains(newValue.toLowerCase())
                    || Integer.toString(subjectMessage.chapterIndex.getValue()).contains(newValue);
        });
    }
}
