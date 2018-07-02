package main.java.app;

import java.io.IOException;

import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.applet.Main;


public class MainApp extends Application {
    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    //主布局(舞台)
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Font.loadFont(MainApp.class.getResource("../../resources/roboto/FontAwesome.ttf").
                toExternalForm(), 12);

        //加载布局
        Parent root = FXMLLoader.load(getClass().getResource("../../resources/layout/layout_login.fxml"));
        //取消系统默认装饰
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("考研进度管理系统");

        Scene mainScene = new Scene(root, 350, 420);
        mainScene.setRoot(root);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(MainApp.class.getResource("../../resources/css/jfoenix-design.css").toExternalForm(),
                MainApp.class.getResource("../../resources/css/jfoenix-main-demo.css").toExternalForm());

        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        primaryStage.show();
        //退出
        primaryStage.setOnCloseRequest(event -> Platform.exit());
    }

    public static void showLoginStage() {
        try {
            //加载布局
            Parent root = FXMLLoader.load(MainApp.class.getResource("../../resources/layout/layout_login.fxml"));
            //取消系统默认装饰
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle("考研进度管理系统");

            Scene mainScene = new Scene(root, 350, 420);
            mainScene.setRoot(root);
            final ObservableList<String> stylesheets = mainScene.getStylesheets();
            stylesheets.addAll(MainApp.class.getResource("../../resources/css/jfoenix-design.css").toExternalForm(),
                    MainApp.class.getResource("../../resources/css/jfoenix-main-demo.css").toExternalForm());

            primaryStage.setResizable(false);
            primaryStage.setScene(mainScene);
            primaryStage.show();
            //退出
            primaryStage.setOnCloseRequest(event -> Platform.exit());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    //向外提供获取主舞台方法
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
