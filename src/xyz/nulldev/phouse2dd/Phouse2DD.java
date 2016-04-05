package xyz.nulldev.phouse2dd;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;
import xyz.nulldev.phouse2dd.controllers.Main;
import xyz.nulldev.phouse2dd.factories.SceneFactory;
import xyz.nulldev.phouse2dd.io.WIFIServer;
import xyz.nulldev.phouse2dd.util.CrashReportHandler;
import xyz.nulldev.phouse2dd.util.OrJoiner;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Optional;

/**
 * Project: Phouse2DD
 * Created: 21/10/15
 * Author: nulldev
 */
public class Phouse2DD extends Application {

    public static Stage PRIMARY_STAGE;
    public static WIFIServer WIFI_SERVER;

    double xOffset = 0;
    double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextInputDialog textInputDialog = new TextInputDialog("0.0.0.0");
        textInputDialog.setTitle("Bind IP");
        textInputDialog.setHeaderText("Bind IP Required");
        textInputDialog
                .setContentText("Please enter the IP to bind to\n(just press OK if you don't know what this means):");
        Optional<String> result = textInputDialog.showAndWait();
        if (result.isPresent()) {
            WIFI_SERVER = new WIFIServer();
            WIFI_SERVER.start(result.get());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR: Bind IP Required");
            alert.setContentText("Bind IP is required, aborting!");
            alert.showAndWait();
            Platform.exit();
            return;
        }

        PRIMARY_STAGE = primaryStage;
        Pair<Parent, Main> loaded = new SceneFactory().getRootScene();
        primaryStage.setScene(new Scene(loaded.getLeft()));

        OrJoiner joiner = new OrJoiner();
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement();
            Enumeration<InetAddress> ee = ni.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress ia = ee.nextElement();
                if (ia instanceof Inet4Address && !ia.isLoopbackAddress()) {
                    String address = ia.getHostAddress();
                    System.out.println("Found non-loopback IP address: " + address + "!");
                    joiner.add(address);
                }
            }
        }

        loaded.getRight().setIpText(joiner.join());
        //Allow drag from anywhere
        primaryStage.getScene().setOnMousePressed(event -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
            primaryStage.getScene().setCursor(Cursor.MOVE);
        });
        primaryStage.getScene().setOnMouseReleased(event -> primaryStage.getScene().setCursor(Cursor.DEFAULT));
        primaryStage.getScene().setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });
        //Non-resizable
        primaryStage.setResizable(false);
        //Set title
        primaryStage.setTitle("CompactHID Demo");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            if (WIFI_SERVER != null) {
                WIFI_SERVER.stop();
            }
        });
    }

    public static void main(String args[]) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashReportHandler());
        Phouse2DD.launch(args);
    }
}
