package xyz.nulldev.phouse2dd.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.nulldev.phouse2dd.controllers.Main;
import xyz.nulldev.phouse2dd.util.CrashReportHandler;
import xyz.nulldev.phouse2dd.util.InitializeWithData;

import java.io.IOException;
import java.util.Arrays;

/**
 * Project: Phouse2DD
 * Created: 21/10/15
 * Author: nulldev
 */
public class SceneFactory {

    public static String MODEL_ROOT = "/fxml/";

    public Pair<Parent, Main> getRootScene() {
        Pair<Object, Object> loadedScene = loadScene("Main.fxml");
        return ImmutablePair.of((Parent) loadedScene.getLeft(), (Main) loadedScene.getRight());
    }

    public Pair<Object, Object> loadSceneWithArguments(String scene, Object... args) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MODEL_ROOT + scene));
            Object loaded = loader.load(); //Loaded
            Object controller = loader.getController(); //Controller
            if(args != null && args.length > 0 && controller != null) {
                ((InitializeWithData) controller).init(args);
            }
            return new ImmutablePair<>(loaded, controller);
        } catch (IOException e) {
            CrashReportHandler.reportCrash(Thread.currentThread(), e, "Could not load scene: " + scene + " with arguments: " + (args != null ? Arrays.toString(args) : "null"));
            return null;
        }
    }

    public Pair<Object, Object> loadScene(String scene) {
        return loadSceneWithArguments(scene);
    }
}

