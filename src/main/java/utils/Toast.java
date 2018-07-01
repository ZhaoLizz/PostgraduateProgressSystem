package main.java.utils;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Toast {
    public static void show(Pane pane, String msg) {
        JFXSnackbar toast = new JFXSnackbar(pane);
        toast.show(msg, 3000);
    }

    public static void logger(String msg, Level level) {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        StackTraceElement element = traceElements[3];
        Logger logger = Logger.getLogger("test");
        logger.setLevel(level);
        logger.info("(" + element.getFileName() + ":" + element.getLineNumber() + "):>>> " + msg);
    }

    public static void logger(String msg) {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        StackTraceElement element = traceElements[3];
        Logger logger = Logger.getLogger("test");
        logger.setLevel(Level.INFO);
        logger.info("(" + element.getFileName() + ":" + element.getLineNumber() + "):>>> " + msg);
    }

    public static void showDialog(String title, String body, Stage stage) {
        JFXAlert alert = new JFXAlert(stage);
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