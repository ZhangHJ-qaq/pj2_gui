package sample;

import core.metro.MetroTransferProblem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Scanner;

public class Main extends Application {

    private Parent root;

    private Button stationTextButton;
    private Button gapTextButton;
    private Text stationText;
    private Text gapText;
    private TextArea inputTextArea;
    private Button inputFeedButton;
    private TextArea outputTextArea;
    private Button go;
    private Button exportButton;


    File stationFile = new File("data/station.txt");
    File gapFile = new File("data/gap.txt");


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        this.root = root;
        primaryStage.setTitle("PJ2 XiaoWei's Problem 19302010021 Haojie");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        getAllNecessaryNodes();

        //设置选择station.txt和的行为
        stationTextButton.setOnMouseClicked(event -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("station.txt", "*.txt")
            );
            try {
                this.stationFile = fileChooser.showOpenDialog(primaryStage);
                this.stationText.setText(stationFile.getAbsolutePath());
            } catch (Exception ignored) {
            }


        });

        //设置选择gap.txt的行为
        gapTextButton.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("gap.txt", "*.txt")
            );

            try {
                this.gapFile = fileChooser.showOpenDialog(primaryStage);
                this.gapText.setText(gapFile.getAbsolutePath());
            } catch (Exception ignored) {
            }

        });

        //设置从外部文件导入输入的行为
        inputFeedButton.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text file", "*.txt")
            );

            try {
                File inputFile = fileChooser.showOpenDialog(primaryStage);

                //获取输入流
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));

                //获取scanner
                Scanner scanner = new Scanner(in);

                StringBuilder builder = new StringBuilder();

                while (scanner.hasNext()) {
                    builder.append(scanner.nextLine());
                    builder.append("\n");
                }

                inputTextArea.setText(builder.toString());

            } catch (Exception ignored) {
            }
        });

        //设置用户点击开始计算按钮后的行为
        go.setOnMouseClicked(event -> {
            try {
                long a = System.currentTimeMillis();
                if (this.gapFile == null ) {
                    throw new Exception("gap.txt没有选择");
                }
                if (this.stationText == null ) {
                    throw new Exception("station.txt没有选择");
                }


                MetroTransferProblem transferProblem = new MetroTransferProblem(
                        stationFile, gapFile);
                StringBuilder finalResultBuilder = new StringBuilder();

                Scanner scanner = new Scanner(inputTextArea.getText());

                int numberOfLines = 0;
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    String[] stations = line.split("[\t]");
                    finalResultBuilder.append(transferProblem.shortestPath(stations));
                    finalResultBuilder.append("\n");
                    numberOfLines++;
                }

                outputTextArea.setText(finalResultBuilder.toString());
                long b = System.currentTimeMillis();

                String info = String.format("计算完成，共计算了%d条路径，用时%.2f秒", numberOfLines, (b - a) / 1000.0);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText("计算完成");
                alert.setContentText(info);
                alert.show();

            } catch (Exception e) {
                outputTextArea.setText(e.getMessage());
            }

        });

        this.exportButton.setOnMouseClicked(event -> {

            try {
                String s = outputTextArea.getText();

                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName("metro_paths.txt");

                File file = fileChooser.showSaveDialog(primaryStage);


                if (file != null) {
                    PrintWriter printWriter = new PrintWriter(file);
                    printWriter.print(s);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("提示");
                    alert.setHeaderText("导出成功");
                    alert.setContentText("已经导出到" + file.getAbsolutePath());
                    alert.show();

                }

            } catch (Exception ignored) {
            }


        });


    }


    private void getAllNecessaryNodes() {
        this.stationTextButton = (Button) root.lookup("#stationTextButton");
        this.gapTextButton = (Button) root.lookup("#gapTextButton");
        this.stationText = (Text) root.lookup("#stationText");
        this.gapText = (Text) root.lookup("#gapText");
        this.inputFeedButton = (Button) root.lookup("#inputFeedButton");
        this.inputTextArea = (TextArea) root.lookup("#inputTextArea");
        this.outputTextArea = (TextArea) root.lookup("#outputTextArea");
        this.go = (Button) root.lookup("#go");
        this.exportButton = (Button) root.lookup("#exportButton");

    }


    public static void main(String[] args) {
        launch(args);
    }
}
