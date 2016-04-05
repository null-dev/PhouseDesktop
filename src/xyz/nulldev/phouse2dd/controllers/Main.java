package xyz.nulldev.phouse2dd.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Project: Phouse2DD
 * Created: 21/10/15
 * Author: nulldev
 */
public class Main implements Initializable {

    @FXML
    Label iptext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setIpText(String ip) {
        if(iptext != null) {
            iptext.setText(ip);
        }
    }

    public String getIpText() {
        return iptext != null ? iptext.getText() : null;
    }
}
