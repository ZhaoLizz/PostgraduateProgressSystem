package main.java.controller;

import com.jfoenix.controls.JFXListView;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@ViewController(value = "../../resources/layout/layout/layout_slide_menu.fxml")
public class SideMenuController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    @ActionTrigger("labelOne")
    private Label labelOne;


    @FXML
    private JFXListView<Label> sideList;

    private static String[] slideContentTitle;
    private static Class[] slideContentController;

    @PostConstruct
    public void init() {

    }
}
