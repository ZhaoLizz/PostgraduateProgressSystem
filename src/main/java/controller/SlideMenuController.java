package main.java.controller;

import com.jfoenix.controls.JFXListView;

import java.util.Objects;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import main.java.model.CurUser;

@ViewController(value = "../../resources/layout/layout_slide_menu.fxml")
public class SlideMenuController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    @ActionTrigger("labelOne")
    private Label labelOne;
    @FXML
    @ActionTrigger("labelTwo")
    private Label labelTwo;
    @FXML
    @ActionTrigger("labelThree")
    private Label labelThree;
    @FXML
    @ActionTrigger("labelFour")
    private Label labelFour;
    @FXML
    @ActionTrigger("labelFive")
    private Label labelFive;

    @FXML
    private JFXListView<Label> sideList;

    private static String[] sideContentTitle;
    private static Class[] sideContentController;

    @PostConstruct
    public void init() {
        // is manager
        if (CurUser.getInstance().isStudent_is_manager()) {
            sideContentTitle = new String[]{"考研进度"};
            sideContentController = new Class[]{ProgressGridController.class};

        } else {
            //TODO 学生端侧边栏
//            sideContentTitle = new String[]{"考研进度"};
//            sideContentController = new Class[]{ProgressGridController.class};
        }
        labelOne.setText(sideContentTitle[0]);
//        labelTwo.setText(sideContentTitle[1]);
//        labelThree.setText(sideContentTitle[2]);
//        labelFour.setText(sideContentTitle[3]);
//        labelFive.setText(sideContentTitle[4]);
        Objects.requireNonNull(context, "context");
        FlowHandler contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
        sideList.propagateMouseEventsToParent();
        sideList.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            new Thread(() -> {
                Platform.runLater(() -> {
                    if (newVal != null) {
                        try {
                            contentFlowHandler.handle(newVal.getId());
                        } catch (VetoException exc) {
                            exc.printStackTrace();
                        } catch (FlowException exc) {
                            exc.printStackTrace();
                        }
                    }
                });
            }).start();
        });

        Flow contentFlow = (Flow) context.getRegisteredObject("ContentFlow");
        bindNodeToController(labelOne, sideContentController[0], contentFlow, contentFlowHandler);
//        bindNodeToController(checkbox, CheckboxController.class, contentFlow, contentFlowHandler);
//        bindNodeToController(combobox, ComboBoxController.class, contentFlow, contentFlowHandler);
//        bindNodeToController(dialogs, DialogController.class, contentFlow, contentFlowHandler);
//        bindNodeToController(labelFive, sideContentController[1], contentFlow, contentFlowHandler);
    }


    private void bindNodeToController(Node node, Class<?> controllerClass, Flow flow, FlowHandler flowHandler) {
        flow.withGlobalLink(node.getId(), controllerClass);
    }
}
