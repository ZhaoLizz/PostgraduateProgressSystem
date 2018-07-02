package main.java.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;

import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.datafx.controller.ViewController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.java.db.JDBCDao;
import main.java.model.CurUser;
import main.java.model.Progress;
import main.java.model.Subject;
import main.java.utils.Toast;
import sun.applet.Main;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static javafx.animation.Interpolator.EASE_BOTH;

@ViewController(value = "../../resources/layout/Masonry.fxml")
public class MyProgressController {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private JFXMasonryPane masonryPane;


    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
        List<Progress> progressList = fetchMyProgress();
        ArrayList<Node> children = new ArrayList<>();
        for (int i = 0; i < progressList.size(); i++) {
            Progress progress = progressList.get(i);
            String subject_name = progress.getSubject_name();
            String chapter_name = progress.getChapter_name();
            final String[] subject_material = {""};
            Subject subject = new Subject();
            subject.setSubject_name(subject_name);
            subject.query(Subject.class, new JDBCDao.QueryListener<Subject>() {
                @Override
                public void onSucceed(List<Subject> result) {
                    subject_material[0] = result.get(0).getSubject_refer_material();
                }

                @Override
                public void onFailed(Exception e) {

                }
            });

            StackPane child = new StackPane();
            double width = Math.random() * 100 + 100;
            child.setPrefWidth(width);
            double height = Math.random() * 100 + 100;
            child.setPrefHeight(height);
            JFXDepthManager.setDepth(child, 1);
            children.add(child);

            // create content
//            Text subjectNameText = new Text(subject_name);
//            subjectNameText.autosize();
            Label subjectNameLabel = new Label(subject_name);
            subjectNameLabel.setFont(Font.font(20));
            subjectNameLabel.setAlignment(Pos.CENTER);
            subjectNameLabel.setMinWidth(width);
            Label materialLabel = new Label(subject_material[0]);
            materialLabel.setFont(Font.font(20));
            materialLabel.setAlignment(Pos.CENTER);
            VBox upVBox = new VBox(subjectNameLabel, materialLabel);

            StackPane header = new StackPane(upVBox);
            String headerColor = getDefaultColor(i % 12);
            header.setStyle("-fx-background-radius: 5 5 0 0; -fx-background-color: " + headerColor);
            header.setAlignment(Pos.CENTER);

            VBox.setVgrow(header, Priority.ALWAYS);

            Label chapterNameLabel = new Label(chapter_name);
            chapterNameLabel.setFont(Font.font(20));
            chapterNameLabel.setAlignment(Pos.CENTER);
            StackPane body = new StackPane(chapterNameLabel);
            body.setMinHeight(Math.random() * 20 + 50);


            VBox content = new VBox();
            content.getChildren().addAll(header, body);
            body.setStyle("-fx-background-radius: 0 0 5 5; -fx-background-color: rgb(255,255,255,0.87);");


            // create button
            JFXButton button = new JFXButton("");
            button.setButtonType(ButtonType.RAISED);
            button.setStyle("-fx-background-radius: 40;-fx-background-color: " + getDefaultColor((int) ((Math.random() * 12) % 12)));
            button.setPrefSize(40, 40);
            button.setRipplerFill(Color.valueOf(headerColor));
            button.setScaleX(0);
            button.setScaleY(0);
            SVGGlyph glyph = new SVGGlyph(-1,
                    "test",
                    "M1008 6.286q18.857 13.714 15.429 36.571l-146.286 877.714q-2.857 16.571-18.286 25.714-8 4.571-17.714 4.571-6.286 "
                            + "0-13.714-2.857l-258.857-105.714-138.286 168.571q-10.286 13.143-28 13.143-7.429 "
                            + "0-12.571-2.286-10.857-4-17.429-13.429t-6.571-20.857v-199.429l493.714-605.143-610.857 "
                            + "528.571-225.714-92.571q-21.143-8-22.857-31.429-1.143-22.857 18.286-33.714l950.857-548.571q8.571-5.143 18.286-5.143"
                            + " 11.429 0 20.571 6.286z",
                    Color.WHITE);
            glyph.setSize(20, 20);
            button.setGraphic(glyph);
            button.translateYProperty().bind(Bindings.createDoubleBinding(() -> {
                return header.getBoundsInParent().getHeight() - button.getHeight() / 2;
            }, header.boundsInParentProperty(), button.heightProperty()));
            button.setOnMouseReleased(e->{
                //设置dialog框框里的初始值
                MainController.chapterNameComboBox.setValue(new Label(chapter_name));
                MainController.materialComboBox.setValue(new Label(subject_material[0]));
                MainController.subjectNameComboBox.setValue(new Label(subject_name));

                //显示dialog,更新数据库
                MainController.showAddProgressDialog(MainController.TYPE_UPDATE);
            });

            StackPane.setMargin(button, new Insets(0, 12, 0, 0));
            StackPane.setAlignment(button, Pos.TOP_RIGHT);

            Timeline animation = new Timeline(new KeyFrame(Duration.millis(240),
                    new KeyValue(button.scaleXProperty(),
                            1,
                            EASE_BOTH),
                    new KeyValue(button.scaleYProperty(),
                            1,
                            EASE_BOTH)));
            animation.setDelay(Duration.millis(100 * i + 1000));
            animation.play();
            child.getChildren().addAll(content, button);
        }
        masonryPane.getChildren().addAll(children);
        Platform.runLater(() -> scrollPane.requestLayout());

        JFXScrollPane.smoothScrolling(scrollPane);
    }

    private String getDefaultColor(int i) {
        String color = "#FFFFFF";
        switch (i) {
            case 12:
                color = "#8F3F7E";
                break;
            case 11:
                color = "#B5305F";
                break;
            case 10:
                color = "#CE584A";
                break;
            case 9:
                color = "#DB8D5C";
                break;
            case 8:
                color = "#DA854E";
                break;
            case 7:
                color = "#E9AB44";
                break;
            case 6:
                color = "#FEE435";
                break;
            case 5:
                color = "#99C286";
                break;
            case 4:
                color = "#01A05E";
                break;
            case 3:
                color = "#4A8895";
                break;
            case 2:
                color = "#16669B";
                break;
            case 1:
                color = "#2F65A5";
                break;
            case 0:
                color = "#39C5BB";
                break;
            default:
                break;
        }
        return color;
    }

    private List<Progress> fetchMyProgress() {
        String subject_name = "";
        String subject_material = "";
        String chapter_name = "";
        List<Progress> progressList = new ArrayList<>();

        CurUser curUser = CurUser.getInstance();
        String student_no = curUser.getStudent_no();

        Progress progress = new Progress();
        progress.setStudent_no(student_no);
        progress.query(Progress.class, new JDBCDao.QueryListener<Progress>() {
            @Override
            public void onSucceed(List<Progress> result) {
                for (Progress p : result) {
                    progressList.add(p);
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
        return progressList;
    }

}
