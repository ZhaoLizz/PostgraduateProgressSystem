package main.java.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
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
import main.java.model.Chapter;
import main.java.model.Subject;

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


}
