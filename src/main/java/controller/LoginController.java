package main.java.controller;

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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import main.java.app.MainApp;

import java.net.URL;
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
    private BorderPane borderPane;
    @FXML
    private ImageView adminView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ChoiceBox imagePicker;

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
        Rectangle r1 = null;

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
        /*String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.show(borderPane, "输入框不能为空");
            return;
        } else {
            switch (currentType) {
                case TYPE_TEACHER:
                    TeacherImpl teacherDB = new TeacherImpl();
                    if (!teacherDB.validateLogin(username, password, Statics.SIMPLE_TEACHER)) {
                        Toast.show(borderPane, "用户名或密码错误");
                        return;
                    } else {
                        Statics.TYPE_CURR = Statics.TYPE_TEACHRE;
                    }
                    break;
                case TYPE_ADMIN:
                    TeacherImpl teacherDB1 = new TeacherImpl();
                    if (!teacherDB1.validateLogin(username, password, Statics.ADMIN_TEACHER)) {
                        Toast.show(borderPane, "用户名或密码权限错误");
                        return;
                    } else {
                        Statics.TYPE_CURR = Statics.TYPE_ADMIN;
                    }
                    break;
            }
        }*/
        /*Statics.TYPE_CURR = Statics.TYPE_TEACHRE;
        Statics.CURR_USERNAME = "T160701";

        Stage primaryStage = MainApp.getPrimaryStage();
        primaryStage.close();
        //新建主窗口
        Stage stage = new Stage();
        stage.setTitle("教材征订与发放系统");
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
        try {
            Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
            width = bounds.getWidth() / 2.5;
            height = bounds.getHeight() / 1.35;
        } catch (Exception e) {
        }

        Scene scene = new Scene(decorator, width, height);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(MainApp.class.getResource("../../resources/css/jfoenix-design.css").toExternalForm(),
                MainApp.class.getResource("../../resources/css/jfoenix-main-demo.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
*/
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

}
