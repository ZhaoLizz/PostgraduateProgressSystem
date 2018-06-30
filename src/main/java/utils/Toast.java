package main.java.utils;

import com.jfoenix.controls.JFXSnackbar;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.layout.Pane;

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
}