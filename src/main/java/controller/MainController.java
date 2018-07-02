package main.java.controller;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.validation.ValidationFacade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import main.java.app.MainApp;
import main.java.datafx.ExtendedAnimatedFlowContainer;
import main.java.db.JDBCDao;
import main.java.model.Chapter;
import main.java.model.CurUser;
import main.java.model.Progress;
import main.java.model.Subject;
import main.java.utils.Toast;

@ViewController(value = "../../resources/layout/layout_main.fxml")
public class MainController {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private static StackPane root;
    @FXML
    private StackPane titleBurgerContainer;
    @FXML
    private JFXHamburger titleBurger;
    @FXML
    private StackPane optionsBurger;
    @FXML
    private JFXRippler optionsRippler;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private Label currentUserType;

    private JFXPopup toolbarPopup;
    public static JFXComboBox<Label> subjectNameComboBox = new JFXComboBox<>();
    public static JFXComboBox<Label> chapterNameComboBox = new JFXComboBox<>();
    public static JFXComboBox<Label> materialComboBox = new JFXComboBox<>();
    public static JFXSlider chapterIndexSlider = new JFXSlider();

    public static final int TYPE_INSERT = 0;
    public static final int TYPE_UPDATE = 1;


    @PostConstruct
    public void init() throws Exception {
        drawer.setOnDrawerOpening(event -> {
            System.out.println("drawer.setOnDrawerOpening");
            final Transition animation = titleBurger.getAnimation();
            animation.setRate(1);
            animation.play();
        });
        drawer.setOnDrawerClosing(e -> {
            System.out.println("drawer.setOnDrawerClosing");
            final Transition animation = titleBurger.getAnimation();
            animation.setRate(-1);
            animation.play();
        });
        titleBurgerContainer.setOnMouseClicked(event -> {
            if (drawer.isClosed() || drawer.isClosing()) {
                drawer.open();
            } else {
                drawer.close();
            }
        });

        context = new ViewFlowContext();
        Flow innerFlow = null;
        boolean isManager = CurUser.getInstance().isStudent_is_manager();

        try {
            //右上角
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/layout/popup/MainPopup.fxml"));
            loader.setController(new InputController());
            toolbarPopup = new JFXPopup(loader.load());
            optionsBurger.setOnMouseClicked(e -> toolbarPopup.show(optionsBurger,
                    JFXPopup.PopupVPosition.TOP,
                    JFXPopup.PopupHPosition.RIGHT,
                    -12,
                    15));


        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置默认显示的布局
        if (isManager) {
            currentUserType.setText("管理端");
            innerFlow = new Flow(ProgressGridController.class);
        } else {
            currentUserType.setText("学生端");
            //TODO 学生端列表界面
            innerFlow = new Flow(StuProgressGridController.class);
        }

        try {
            final FlowHandler flowHandler = innerFlow.createHandler(context);
            context.register("ContentFlow", innerFlow);
            context.register("ContentFlowHandler", flowHandler);

            final Duration containerAnimationDuration = Duration.millis(320);
            drawer.setContent(flowHandler.start(new ExtendedAnimatedFlowContainer(containerAnimationDuration, ContainerAnimations.SWIPE_LEFT)));
            context.register("ContentPane", drawer.getContent().get(0));
            //侧边栏滑动
            Flow sideMenuFlow = new Flow(SlideMenuController.class);
            final FlowHandler sideMenuFlowHandler = sideMenuFlow.createHandler(context);
            drawer.setSidePane(sideMenuFlowHandler.start(new ExtendedAnimatedFlowContainer(containerAnimationDuration, ContainerAnimations.SWIPE_LEFT)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static final class InputController {
        @FXML
        private JFXListView<?> toolbarPopupList;

        // close application
        @FXML
        private void submit() {
            if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 1) {
                Platform.exit();
                MainApp.showLoginStage();
            } else {
                showAddProgressDialog(TYPE_INSERT, null);
            }
        }
    }

    public static void showAddProgressDialog(int databaseType,Progress condition) {
        //添加学习进度
        JFXAlert alert = new JFXAlert((Stage) root.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        Label label = null;
        if (databaseType == TYPE_INSERT) {
            label = new Label("添加学习进度");
        } else {
            label = new Label("修改学习进度");
        }
        label.setFont(Font.font(Font.getFamilies().get(3)));
        layout.setHeading(label);
        setupSubjectNameComboBox(subjectNameComboBox, subjectNameConverter);
        chapterNameComboBox.setPromptText("章节名称");
        chapterNameComboBox.setEditable(true);

        Text text = new Text("章节序号");
        chapterIndexSlider.getStyleClass().add("jfx-slider-style");
        chapterIndexSlider.setMax(10);
        chapterIndexSlider.setMaxWidth(180);
        chapterIndexSlider.setOnMouseReleased(e -> {
            int curIndex = (int) chapterIndexSlider.getValue();
            if (curIndex != 0) {
                String subjectName = subjectNameComboBox.getSelectionModel().getSelectedItem().getText();
                Chapter chapter = new Chapter();
                chapter.setSubject_name(subjectName);
                chapter.setChapter_index(curIndex);
                chapter.query(Chapter.class, new JDBCDao.QueryListener<Chapter>() {
                    @Override
                    public void onSucceed(List<Chapter> result) {
                        String chapterName = result.get(0).getChapter_name();
                        chapterNameComboBox.setValue(new Label(chapterName));
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
            } else {
                chapterNameComboBox.setValue(new Label(""));
            }
        });
        HBox chaperIndexBox = new HBox(text, chapterIndexSlider);

        materialComboBox.setPromptText("参考资料");
        materialComboBox.setEditable(true);

        VBox vBox = new VBox(subjectNameComboBox, materialComboBox, chapterNameComboBox, chaperIndexBox);
        vBox.setSpacing(20);
        JFXButton commitBtn = new JFXButton("确定");
        commitBtn.getStyleClass().add("dialog-accept");
        JFXButton cancelBtn = new JFXButton("取消");
        cancelBtn.getStyleClass().add("dialog-accept");
        cancelBtn.setOnAction(event1 -> alert.hideWithAnimation());
        commitBtn.setOnMouseReleased(e -> {
            String subjectName = subjectNameComboBox.getSelectionModel().getSelectedItem().getText();
            String chapterName = chapterNameComboBox.getSelectionModel().getSelectedItem().getText();
            String studentNo = CurUser.getInstance().getStudent_no();
            Progress progress = new Progress();
            progress.setChapter_name(chapterName);
            progress.setSubject_name(subjectName);
            progress.setStudent_no(studentNo);
            if (databaseType == TYPE_INSERT) {
                progress.save(new JDBCDao.SaveListerner() {
                    @Override
                    public void onSucceed() {
                        Toast.logger("save progress succeed");
                        alert.close();
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
            } else {
                System.out.println(chapterName + "  " + subjectName + " " + studentNo);
                System.out.println("chapter name : " +  MainController.chapterNameComboBox.getSelectionModel().getSelectedItem().getText());
                if (condition != null) {
                    progress.update(condition, new JDBCDao.UpdateListener() {
                        @Override
                        public void onSucceed() {
                            System.out.println("update succeed");
                        }

                        @Override
                        public void onFailed(Exception e) {
                            e.printStackTrace();
                            System.out.println("update progress failed");
                        }
                    });
                }
            }
            alert.close();
        });

        layout.setActions(commitBtn, cancelBtn);
        layout.setBody(vBox);
        layout.setMaxHeight(200);
        layout.setMaxWidth(300);
        alert.setContent(layout);
        alert.show();
    }


    public static void setupSubjectNameComboBox(JFXComboBox<Label> jfxEditableComboBox, StringConverter<Label> stringConverter) {
        //setup comboBox
        subjectNameComboBox.getItems().clear();
        materialComboBox.getItems().clear();
        chapterIndexSlider.setValue(0);
        Set<String> chapterNameSet = new HashSet<>();
        new Subject().query(Subject.class, new JDBCDao.QueryListener<Subject>() {
            @Override
            public void onSucceed(List<Subject> result) {
                for (Subject p : result) {
                    chapterNameSet.add(p.getSubject_name());
                }
                for (String s : chapterNameSet) {
                    subjectNameComboBox.getItems().add(new Label(s));
                }
            }


            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

        jfxEditableComboBox.setConverter(stringConverter);
        jfxEditableComboBox.setEditable(true);
        jfxEditableComboBox.setPromptText("科目名称");
    }

    public static StringConverter<Label> subjectNameConverter = new StringConverter<Label>() {
        //把label转换为String显示
        @Override
        public String toString(Label object) {
            if (null != object) {
                chapterIndexSlider.setValue(0);
                String subjectName = object.getText();
                setupChapterNameComboBox(chapterNameComboBox, subjectName);
                setupSubjectMaterialComboBox(materialComboBox, subjectName);
            }
            return object == null ? "" : object.getText();
        }

        //从输入框中获取的信息回调
        @Override
        public Label fromString(String subjectName) {
            chapterIndexSlider.setValue(0);
            setupChapterNameComboBox(chapterNameComboBox, subjectName);
            setupSubjectMaterialComboBox(materialComboBox, subjectName);
            return subjectName == null || subjectName.isEmpty() ? null : new Label(subjectName);
        }
    };

    /**
     * 1. 首先根据subjectName查到科目包含的章节,设置到ComboBox里面
     * 2. 设置ComboBox相关属性和监听
     *
     * @param jfxEditableComboBox
     * @param subjectName
     */
    public static void setupChapterNameComboBox(JFXComboBox<Label> jfxEditableComboBox, String subjectName) {
        //给章节名称ComboBox添加数据
        chapterNameComboBox.getItems().clear();
        Chapter chapter = new Chapter();
        chapter.setSubject_name(subjectName);
        chapter.query(Chapter.class, new JDBCDao.QueryListener<Chapter>() {
            @Override
            public void onSucceed(List<Chapter> result) {
                for (Chapter c : result) {
                    String chapterName = c.getChapter_name();
                    chapterNameComboBox.getItems().add(new Label(chapterName));
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        //输入框监听
        jfxEditableComboBox.setConverter(new StringConverter<Label>() {
            //把label转换为String显示
            @Override
            public String toString(Label object) {
                if (null != object) {
                    String chapterName = object.getText();
                    String subjectName = subjectNameComboBox.getSelectionModel().getSelectedItem().getText();
                    setupChapterIndexSlider(chapterIndexSlider, subjectName, chapterName);
                }
                return object == null ? "" : object.getText();
            }

            //从输入框中获取的信息回调
            @Override
            public Label fromString(String chapterName) {
                String subjectName = subjectNameComboBox.getSelectionModel().getSelectedItem().getText();
                setupChapterIndexSlider(chapterIndexSlider, subjectName, chapterName);
                return chapterName == null || chapterName.isEmpty() ? null : new Label(chapterName);
            }
        });
//        jfxEditableComboBox.setEditable(true);
//        jfxEditableComboBox.setPromptText("章节名称");
    }

    public static void setupSubjectMaterialComboBox(JFXComboBox<Label> jfxEditableComboBox, String subjectName) {
        materialComboBox.getItems().clear();
        Subject subject = new Subject();
        subject.setSubject_name(subjectName);
        subject.query(Subject.class, new JDBCDao.QueryListener<Subject>() {
            @Override
            public void onSucceed(List<Subject> result) {
                for (Subject s : result) {
                    String material = s.getSubject_refer_material();
                    materialComboBox.getItems().add(new Label(material));
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        jfxEditableComboBox.setConverter(new StringConverter<Label>() {
            //把label转换为String显示
            @Override
            public String toString(Label object) {
                if (null != object) {
                    String subjectName = object.getText();

                }
                return object == null ? "" : object.getText();
            }

            //从输入框中获取的信息回调
            @Override
            public Label fromString(String string) {
                System.out.println(string);
                return string == null || string.isEmpty() ? null : new Label(string);
            }
        });
    }

    public static void setupChapterIndexSlider(JFXSlider chapterIndexSlider, String subjectName, String chapterName) {
        Subject subject = new Subject();
        subject.setSubject_name(subjectName);
        subject.query(Subject.class, new JDBCDao.QueryListener<Subject>() {
            @Override
            public void onSucceed(List<Subject> result) {
                if (result != null && result.size() == 1) {
                    chapterIndexSlider.setMax(result.get(0).getSubject_chapter_num());
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        Chapter chapter = new Chapter();
        chapter.setSubject_name(subjectName);
        chapter.setChapter_name(chapterName);
        chapter.query(Chapter.class, new JDBCDao.QueryListener<Chapter>() {
            @Override
            public void onSucceed(List<Chapter> result) {
                if (result != null && result.size() == 1) {
                    chapterIndexSlider.setValue(result.get(0).getChapter_index());
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

    }


}
