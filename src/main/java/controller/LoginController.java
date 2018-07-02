package main.java.controller;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.svg.SVGGlyph;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.app.MainApp;
import main.java.db.JDBCDao;
import main.java.db.JDBCHelper;
import main.java.model.CurUser;
import main.java.model.Progress;
import main.java.model.Student;
import main.java.utils.TextUtils;
import main.java.utils.Toast;

public class LoginController implements Initializable {


    //登录用户类型标记
    private static final int TYPE_TEACHER = 0x00;
    private static final int TYPE_CLASS = 0x01;
    private static final int TYPE_ADMIN = 0x02;

    private int currentType;


    private Scene scene;
    @FXML
    private BorderPane borderPane;  //登录界面总布局
    @FXML
    private ImageView adminView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    private double xOffset;
    private double yOffset;

    private MainApp app;

    public void setApp(MainApp app) {
        this.app = app;
    }

    //初始化回调
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JDBCHelper.getInstance();//初始化数据库连接

        //设置窗口拖放
        borderPane.setOnMousePressed(event -> {
            xOffset = MainApp.getPrimaryStage().getX() - event.getSceneX();
            yOffset = MainApp.getPrimaryStage().getY() - event.getSceneY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            MainApp.getPrimaryStage().setX(event.getSceneX() + xOffset);
            MainApp.getPrimaryStage().setY(event.getSceneY() + yOffset);
        });

        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });

        //设置登录界面背景动画
        int numberOfSquares = 30;
        while (numberOfSquares > 0) {
            maintainAnimation();
            numberOfSquares--;
        }
    }

    /**
     * 生成背景动画
     */
    private void maintainAnimation() {
        Random random = new Random();
        int sizeOfSqaure = random.nextInt(50) + 1;
        int speedOfSqaure = random.nextInt(10) + 5;
        int startXPoint = random.nextInt(420);
        int startYPoint = random.nextInt(350);
        int direction = random.nextInt(5) + 1;

        KeyValue moveXAxis = null;
        KeyValue moveYAxis = null;
        Rectangle r1 = null;    //正方形

        switch (direction) {
            case 1:
                // MOVE LEFT TO RIGHT
                r1 = new Rectangle(0, startYPoint, sizeOfSqaure, sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 - sizeOfSqaure);
                break;
            case 2:
                // MOVE TOP TO BOTTOM
                r1 = new Rectangle(startXPoint, 0, sizeOfSqaure, sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;
            case 3:
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                r1 = new Rectangle(startXPoint, 0, sizeOfSqaure, sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 - sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;
            case 4:
                // MOVE BOTTOM TO TOP
                r1 = new Rectangle(startXPoint, 420 - sizeOfSqaure, sizeOfSqaure, sizeOfSqaure);
                moveYAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 5:
                // MOVE RIGHT TO LEFT
                r1 = new Rectangle(420 - sizeOfSqaure, startYPoint, sizeOfSqaure, sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 6:
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                r1 = new Rectangle(startXPoint, 0, sizeOfSqaure, sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 - sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;

            default:
                System.out.println("default");
        }

        r1.setFill(Color.web("#669966"));
        r1.setOpacity(0.1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(speedOfSqaure * 1000), moveXAxis, moveYAxis);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        borderPane.getChildren().add(borderPane.getChildren().size() - 1, r1);
    }


    /**
     * 登录逻辑
     *
     * @param actionEvent
     * @throws Exception
     */
    public void loginButtonAction(ActionEvent actionEvent) throws Exception {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showDialog("提示", "用户名或密码不能为空!");
            return;
        } else {
            Student student = new Student();
            student.setStudent_no(username);
            student.setStudent_pw(password);
            student.query(Student.class, new JDBCDao.QueryListener<Student>() {
                @Override
                public void onSucceed(List<Student> result) {
                    if (result != null && result.size() == 1) {
                        Student database = result.get(0);
                        System.out.println(database);
                        System.out.println("登录成功");
                        Toast.show(borderPane, "登陆成功!");
                        CurUser curUser = CurUser.getInstance();
                        curUser.setStudent_is_manager(database.getStudent_is_manager() == 1);
                        curUser.setStudent_no(database.getStudent_no());

                        changeToMainStage();
                    } else {
                        showDialog("提示", "用户名或密码不正确!");
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    public void registerButtonAction() {
        //创建注册的dialog
        JFXAlert alert = new JFXAlert((Stage) borderPane.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        Label label = new Label("注册");
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

        VBox vBox = new VBox(usernameTextField, passwordTextField, studentnameTextField, targetTextField, specialTextField);
        vBox.setSpacing(20);

        JFXButton commitBtn = new JFXButton("注册");
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
                showDialog("提示", "用户名或密码不能为空!");
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
                            student.save(new JDBCDao.SaveListerner() {
                                @Override
                                public void onSucceed() {
                                    CurUser curUser = CurUser.getInstance();
                                    curUser.setStudent_no(username);
                                    showDialog("提示","注册成功");
                                    System.out.println("注册保存成功");
                                    changeToMainStage();
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        } else {
                            showDialog("提示", "该用户名已存在!");
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

    private void changeToMainStage() {
        try {
            //关闭登录界面的初始窗口
            Stage primaryStage = MainApp.getPrimaryStage();
            primaryStage.close();
            //新建窗口
            Stage stage = new Stage();
            stage.setTitle("考研进度管理系统");
            DefaultFlowContainer container = new DefaultFlowContainer();
            flowContext = new ViewFlowContext();
            flowContext.register("Stage", stage);
            JFXDecorator decorator = new JFXDecorator(stage, container.getView());

            Flow flow = new Flow(MainController.class);
            flow.createHandler(flowContext).start(container);
            decorator.setCustomMaximize(true);
            decorator.setGraphic(new SVGGlyph(""));
            double width = 800;
            double height = 600;
            Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
            width = bounds.getWidth() / 2.5;
            height = bounds.getHeight() / 1.35;

            Scene scene = new Scene(decorator, width, height);
            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(MainApp.class.getResource("../../resources/css/jfoenix-design.css").toExternalForm(),
                    MainApp.class.getResource("../../resources/css/jfoenix-main-demo.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (FlowException e) {
            e.printStackTrace();
        }


    }

    /**
     * 最小化窗口
     *
     * @param actionEvent
     * @throws Exception
     */
    public void minimizeWindow(ActionEvent actionEvent) throws Exception {
        MainApp.getPrimaryStage().setIconified(true);

    }


    /**
     * 关闭系统
     *
     * @param actionEvent
     * @throws Exception
     */
    public void closeSystem(ActionEvent actionEvent) throws Exception {
        Platform.exit();
        System.exit(0);
    }

    public void showDialog(String title, String body) {
        JFXAlert alert = new JFXAlert((Stage) borderPane.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(body));
        layout.setMaxHeight(200);
        layout.setMaxWidth(300);


        JFXButton closeButton = new JFXButton("确定");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> alert.hideWithAnimation());
        layout.setActions(closeButton); //dialoglayout加入确认按钮
        alert.setContent(layout);
        alert.show();
    }

   


}
