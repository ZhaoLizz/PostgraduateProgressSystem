package main.java.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRippler;


import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.java.datafx.ExtendedAnimatedFlowContainer;
import main.java.model.CurUser;

@ViewController(value = "../../resources/layout/layout_main.fxml")
public class MainController {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;
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

    @PostConstruct
    public void init() throws  Exception {
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

        //设置默认显示的布局
        if (isManager) {
            currentUserType.setText("管理端");
            innerFlow = new Flow(ProgressGridController.class);
        } else {
            currentUserType.setText("学生端");
            //TODO 学生端列表界面
//            innerFlow = new Flow(ProgressGridController.class);
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

}
