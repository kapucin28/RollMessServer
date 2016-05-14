package rollMessContent;

import alerts.EmptyAlert;
import alerts.ExitAlert;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Created by TIMBULI REMUS K@puc!n on 07-May-16.
 */
public class RollUI extends Pane {

    // Menu variables---------------------------------------------------------------------------------------------------
    private final MenuBar menuBar = new MenuBar();
    private final Menu fileMenu = new Menu("_File");
    private final Menu editMenu = new Menu("_Edit");
    private final MenuItem save = new MenuItem("Save As...");
    private final MenuItem load = new MenuItem("Load...");
    private final MenuItem exit = new MenuItem("Exit");
    private final MenuItem clear = new MenuItem("Clear Text");
    private final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
    //------------------------------------------------------------------------------------------------------------------

    // Chat fields variables--------------------------------------------------------------------------------------------
    private TextField textField = new TextField();
    private TextArea textArea = new TextArea();
    //------------------------------------------------------------------------------------------------------------------

    // Chat connexion variables-----------------------------------------------------------------------------------------
    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;
    private Object message;
    private ServerSocket chatServer;
    private final int chatPort = 9000;
    private Socket chatSocket;
    //------------------------------------------------------------------------------------------------------------------

    // File Chooser-----------------------------------------------------------------------------------------------------
    private Stage fileStage = new Stage();
    private FileChooser chooser;
    private File file;
    private Object fileMessages;
    private ObjectOutputStream toFile;
    private ObjectInputStream fromFile;
    //------------------------------------------------------------------------------------------------------------------

    // Root pane variables----------------------------------------------------------------------------------------------
    private Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    private final GridPane root = new GridPane();
    //------------------------------------------------------------------------------------------------------------------

    // Constructor------------------------------------------------------------------------------------------------------
    public RollUI() {
        layoutSetup();
        chatThread();
        userActions();
    }
    //------------------------------------------------------------------------------------------------------------------

    // RollUI layout setup----------------------------------------------------------------------------------------------
    private void layoutSetup() {

        // MenuBar setup------------------------------------------------------------------------------------------------
        editMenu.getItems().addAll(clear);
        fileMenu.getItems().addAll(save, load, separatorMenuItem, exit);
        menuBar.getMenus().addAll(fileMenu, editMenu);
        //--------------------------------------------------------------------------------------------------------------

        // Chat area setup----------------------------------------------------------------------------------------------
        textArea.setPrefHeight(bounds.getHeight() / 3 + 75);
        textArea.setPrefWidth(bounds.getWidth() / 4);
        textField.setPrefWidth(bounds.getWidth() / 3);
        textArea.setPromptText("Messages received");
        textField.setPromptText("Write message");
        //--------------------------------------------------------------------------------------------------------------

        // Root setup---------------------------------------------------------------------------------------------------
        root.add(menuBar, 0, 0);
        root.add(textField, 0, 1);
        root.add(textArea, 0, 2);
        root.setPrefWidth(bounds.getWidth() / 3);
        root.setPrefHeight(bounds.getHeight() / 2);
        getChildren().add(root);
        //--------------------------------------------------------------------------------------------------------------
    }
    //------------------------------------------------------------------------------------------------------------------

    // Chat Thread method-----------------------------------------------------------------------------------------------
    private void chatThread() {
        new Thread(() -> {
            try {
                chatServer = new ServerSocket(chatPort);
                textArea.appendText("Server started at: " + "\n" + new Date() + "\n");
                chatSocket = chatServer.accept();
                while (true) {
                    fromClient = new ObjectInputStream(chatSocket.getInputStream());
                    message = fromClient.readObject();
                    textArea.appendText("Received from client: " + chatSocket.getPort() + "\n" + message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }
    //------------------------------------------------------------------------------------------------------------------

    // User actions method----------------------------------------------------------------------------------------------
    private void userActions() {
        sendMessage();
        saveMessages();
        clearTable();
        exitSetup();
    }
    //------------------------------------------------------------------------------------------------------------------

    // Sending message method-------------------------------------------------------------------------------------------
    private void sendMessage() {
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                try {
                    toClient = new ObjectOutputStream(chatSocket.getOutputStream());
                    toClient.writeObject(textField.getText());
                    toClient.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                textArea.appendText("Sent: " + textField.getText() + "\n");
                textField.clear();
            }
        });
    }
    //------------------------------------------------------------------------------------------------------------------

    // Save messages to file--------------------------------------------------------------------------------------------
    private void saveMessages() {
        save.setOnAction(e -> {
            if (textArea.getText().isEmpty()) {
                new EmptyAlert();
            } else {
                fileStage = new Stage();
                chooser = new FileChooser();
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
                file = chooser.showSaveDialog(fileStage);
                try {
                    toFile = new ObjectOutputStream(new FileOutputStream(file));
                    fileMessages = textArea.getText();
                    toFile.writeObject(fileMessages);
                    toFile.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
    //------------------------------------------------------------------------------------------------------------------

    // Clear table method-----------------------------------------------------------------------------------------------
    private void clearTable() {
        clear.setOnAction(e -> textArea.clear());
    }
    //------------------------------------------------------------------------------------------------------------------

    // Exit method -----------------------------------------------------------------------------------------------------
    private void exitSetup() {
        exit.setOnAction(e -> {
            e.consume();
            new ExitAlert();
        });
    }
    //------------------------------------------------------------------------------------------------------------------
}