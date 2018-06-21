package main.java.controller;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.app.MainApp;
import main.java.db.JDBCDao;
import main.java.model.Student;
import main.java.model.User;
import main.java.utils.TextUtils;
import main.java.utils.Toast;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

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
        //todo 验证登录
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
                        User user = User.getInstance();
                        user.setStudent_is_manager(database.getStudent_is_manager() == 1);
                        user.setStudent_no(database.getStudent_no());

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

    private void changeStage(boolean isManager) {
        if (isManager) {
            //TODO manager stage
        } else {
            Stage primaryStage = MainApp.getPrimaryStage();
            primaryStage.close();
            //新建窗口
            Stage stage = new Stage();
            stage.setTitle("考研进度管理系统");
            DefaultFlowContainer container = new DefaultFlowContainer();
            flowContext = new ViewFlowContext();
            flowContext.register("Stage", stage);
            JFXDecorator decorator = new JFXDecorator(stage, container.getView());

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
        layout.setMaxHeight(Control.USE_PREF_SIZE);
        layout.setMaxWidth(Control.USE_PREF_SIZE);


        JFXButton closeButton = new JFXButton("确定");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> alert.hideWithAnimation());
        layout.setActions(closeButton); //dialoglayout加入确认按钮
        alert.setContent(layout);
        alert.show();
    }

}
