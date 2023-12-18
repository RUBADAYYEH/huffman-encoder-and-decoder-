package com.example.huffmanproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ResourceBundle;

//THIS CONTROLLER CLASS IS INITIALIZED BY THE MAIN FXML FILE .

public class HuffmanTableController implements Initializable {

    // heap is passed from HuffmanTreeBuildStart Main Class.
    // GridPane refrences an existing GridPane in FXML file .
    // codesTable will be the output of this class. (which will be used in compressing)
    private static MyPriorityQueue heap;
    private static int[] freqs;
    private static  String filePath;

    @FXML
    GridPane dynamicGridPane;
    @FXML
    VBox vbox;
    @FXML
    Button file_btn;
    @FXML
    Button compress_btn;
    @FXML
    Button decompress_btn;
    @FXML Label resultLabel;



    String[][] codesTable;

    public MyPriorityQueue getHeap() {
        return heap;
    }


    public void setHeap(MyPriorityQueue heap) {
        HuffmanTableController.heap = heap;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InnerShadow innerShadow2 = new InnerShadow();
        innerShadow2.setOffsetX(2.0);
        innerShadow2.setOffsetY(2.0);
        innerShadow2.setColor(javafx.scene.paint.Color.color(0.9, 0.9, 0.8));
        innerShadow2.setChoke(0.5); // Set choke to make the shadow more pronounced
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setOffsetX(2.0);
        innerShadow.setOffsetY(2.0);
        innerShadow.setColor(javafx.scene.paint.Color.color(0.4, 0.4, 0.4));
        vbox.setSpacing(10);
        file_btn.setOnMouseEntered(e -> {
            // Increase button width on hover
            // file_btn.setPrefWidth(214 * 3);
            file_btn.setEffect(innerShadow2); // Remove effect on hover
        });

        file_btn.setOnMouseExited(e -> {
            // Restore button width on exit
            // file_btn.setPrefWidth(214);
            file_btn.setEffect(innerShadow); // Restore effect on exit
        });
        compress_btn.setOnMouseEntered(e -> {
            // Increase button width on hover
            // compress_btn.setPrefWidth(214 * 3);
            compress_btn.setEffect(innerShadow2); // Remove effect on hover
        });

        compress_btn.setOnMouseExited(e -> {
            // Restore button width on exit
            // compress_btn.setPrefWidth(214);
            compress_btn.setEffect(innerShadow); // Restore effect on exit
        });
        decompress_btn.setOnMouseEntered(e -> {
            // Increase button width on hover
            // decompress_btn.setPrefWidth(214 * 3);
            decompress_btn.setEffect(innerShadow2); // Remove effect on hover
        });

        decompress_btn.setOnMouseExited(e -> {
            // Restore button width on exit
            // decompress_btn.setPrefWidth(214);
            decompress_btn.setEffect(innerShadow); // Restore effect on exit
        });
        ////////////////FX//////////////////////////////////////////////////////////////////

        freqs = new int[256];

        file_btn.setOnAction(e -> openFileChooser());
        compress_btn.setOnAction(e -> {
            try {
                compressFileChooser();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        decompress_btn.setOnAction(e -> {
            try {
                decompressFileChooser();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        //TABLE AND GRIDPANE ARE READY !

        // passing Huffman table to the actuall compression initializer below .


    }
    @FXML
    private void handleFileButtonClick(ActionEvent event) {
        // Your code here
    }

    @FXML
    private void compressFileChooser() throws IOException {
        if (filePath.isEmpty()){
            resultLabel.setText("File Not Selected");
        } else if (filePath.contains(".huff")) {
            resultLabel.setText("file is already compressed");
        } else {
            dynamicGridPane.getChildren().clear();
            processFile(filePath);
            CompressController compress = new CompressController();
            compress.setCodesTable(codesTable);
            System.out.println(filePath);
            compress.setFilePath(filePath);
            compress.setTreeTraversal(heap.peek().postOrderTraversal());

            try {
                String[] pathOfFiles = filePath.split("\\.");
                compress.initialize();
                resultLabel.setText("compressed to \n" + pathOfFiles[0] + ".huff." + pathOfFiles[1]);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    private void openFileChooser() {
        resultLabel.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(null);


        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
            resultLabel.setText(filePath);

        }
    }

    private void processFile(String filePath) throws IOException {
        if (filePath!=null) {

            //for reading file in 8byte buffer size .
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            FileChannel inChannel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(8);
            byte byteRead;
            while (inChannel.read(buffer) != -1) {
                buffer.flip();
                for (int i = 0; i < buffer.limit(); i++) {
                    byteRead = buffer.get();
                    freqs[byteRead & 0xFF]++;
                }

                buffer.clear(); // Prepare for writing new data
            }

            if (buffer.position() > 0) {
                while (buffer.hasRemaining()) {
                    byteRead = buffer.get();
                    freqs[byteRead & 0xFF]++;
                }
            }
            inChannel.close();
            file.close();

            int sum = 0;
            // build in heap but with customized Node .
            heap = new MyPriorityQueue(freqs.length);

            // FIRST STEP : adding all frequencies to the heap
            for (int i = 0; i < freqs.length; i++) {
                if (freqs[i] != 0) {
                    Node node = new Node((char) i, freqs[i]);
                    sum += node.getFreqs();
                    heap.add(node);

                }
            }


            //SECOND STEP : gathering nodes to get to only one node that equals the maximum .
            while (heap.peek().getFreqs() != sum) {
                Node left = heap.remove();
                Node right = heap.remove();

                Node newNode = new Node();
                newNode.setFreqs(left.getFreqs() + right.getFreqs());
                newNode.setLeft(left);
                newNode.setRight(right);
                // System.out.println(newNode.getFreqs());
                // System.out.println("left :"+newNode.getLeft().getValue()+" right :"+newNode.getRight().getValue());

                heap.add(newNode);
            }
            // heap.printHeap();
            //heap.printHeap();
            //System.out.println(heap.getSize());
            //the heap is ready !!


            if (heap.peek() != null) {
                String path = heap.peek().getPaths();

                System.out.println(path);
                System.out.println(heap.peek().postOrderTraversal());

                String[] paths = path.split(" newRecord "); //THE SPLITTER IS DEFINED IN NODE CLASS.
                //System.out.println("paths are " + paths.length);

                codesTable = new String[paths.length][3];

                dynamicGridPane.getChildren().clear();
                dynamicGridPane.setMinSize(3, paths.length + 1);
                dynamicGridPane.setMinWidth(300);

                Label letterLabel = new Label("Character");
                Label asciiLabel = new Label("Ascii");
                Label pathLabel = new Label("Code");

                letterLabel.setMinSize(110, 50);
                letterLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                asciiLabel.setMinSize(110, 50);
                asciiLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                pathLabel.setMinSize(150, 50);
                pathLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");

                dynamicGridPane.add(letterLabel, 0, 0);
                codesTable[0][0] = letterLabel.getText();
                dynamicGridPane.add(asciiLabel, 1, 0);
                codesTable[0][1] = asciiLabel.getText();
                dynamicGridPane.add(pathLabel, 2, 0);
                codesTable[0][2] = pathLabel.getText();

                for (int i = 1; i < paths.length; i++) {
                    // System.out.println(paths[i]);

                    //we will be using this condition since we will split later by : .
                    if (paths[i].length() > 13 && paths[i].charAt(6) == ':') {
                        Label letter = new Label(Character.toString(paths[i].charAt(6)));
                        System.out.println(letter.getText());
                        int asciiValue = (int) letter.getText().charAt(0);
                        Label ascii = new Label(String.valueOf(asciiValue));
                        String[] pathArr = paths[i].split(":");
                        Label pathl = new Label(pathArr[pathArr.length - 1]);


                        letter.setMinSize(110, 50);
                        letter.setStyle("-fx-font-size: 20px;  -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                        ascii.setMinSize(110, 50);
                        ascii.setStyle("-fx-font-size: 20px;  -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                        pathl.setMinSize(150, 50);
                        pathl.setStyle("-fx-font-size: 20px; -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");

                        dynamicGridPane.add(letter, 0, i);
                        codesTable[i][0] = letter.getText();
                        dynamicGridPane.add(ascii, 1, i);
                        codesTable[i][1] = ascii.getText();
                        dynamicGridPane.add(pathl, 2, i);
                        codesTable[i][2] = pathl.getText();

                    } else if (paths[i].length() > 13 && paths[i].charAt(6) == ' ') {
                        Label letter = new Label("space");
                        //  System.out.println(letter.getText());
                        int asciiValue = 32;
                        Label ascii = new Label(String.valueOf(asciiValue));
                        String[] pathArr = paths[i].split(":");
                        Label pathl = new Label(pathArr[pathArr.length - 1]);


                        letter.setMinSize(110, 50);
                        letter.setStyle("-fx-font-size: 20px;  -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                        ascii.setMinSize(110, 50);
                        ascii.setStyle("-fx-font-size: 20px;  -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                        pathl.setMinSize(150, 50);
                        pathl.setStyle("-fx-font-size: 20px; -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");

                        dynamicGridPane.add(letter, 0, i);
                        codesTable[i][0] = letter.getText();
                        dynamicGridPane.add(ascii, 1, i);
                        codesTable[i][1] = ascii.getText();
                        dynamicGridPane.add(pathl, 2, i);
                        codesTable[i][2] = pathl.getText();

                    } else if (paths[i].charAt(6) == '\n') {
                        Label letter = new Label("newLine");
                        //  System.out.println(letter.getText());
                        int asciiValue = 10;
                        Label ascii = new Label(String.valueOf(asciiValue));
                        String[] pathArr = paths[i].split(":");
                        Label pathl = new Label(pathArr[pathArr.length - 1]);


                        letter.setMinSize(110, 50);
                        letter.setStyle("-fx-font-size: 20px;  -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                        ascii.setMinSize(110, 50);
                        ascii.setStyle("-fx-font-size: 20px;  -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                        pathl.setMinSize(150, 50);
                        pathl.setStyle("-fx-font-size: 20px; -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");

                        dynamicGridPane.add(letter, 0, i);
                        codesTable[i][0] = letter.getText();
                        dynamicGridPane.add(ascii, 1, i);
                        codesTable[i][1] = ascii.getText();
                        dynamicGridPane.add(pathl, 2, i);
                        codesTable[i][2] = pathl.getText();

                    } else {

                        String[] info = paths[i].split(":");
                        if (info.length >= 4) {

                            Label letter = new Label(info[1]);
                            // System.out.println(letter.getText());
                            int asciiValue = (int) letter.getText().charAt(0);
                            Label ascii = new Label(String.valueOf(asciiValue));
                            Label pathl = new Label(info[3]);


                            letter.setMinSize(110, 50);
                            letter.setStyle("-fx-font-size: 20px;  -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                            ascii.setMinSize(110, 50);
                            ascii.setStyle("-fx-font-size: 20px;  -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");
                            pathl.setMinSize(150, 50);
                            pathl.setStyle("-fx-font-size: 20px; -fx-alignment: CENTER;-fx-border-width: 0.5; -fx-border-color: black;");

                            dynamicGridPane.add(letter, 0, i);
                            codesTable[i][0] = letter.getText();
                            dynamicGridPane.add(ascii, 1, i);
                            codesTable[i][1] = ascii.getText();
                            dynamicGridPane.add(pathl, 2, i);
                            codesTable[i][2] = pathl.getText();
                        } else {
                            System.out.println("error at " + (int) paths[i].charAt(5));

                        }
                    }


                }


            } else {
                System.out.println("HEAP EMPTY ");
            }
        }
        else{
            resultLabel.setText("File Not Selected");
        }

    }

    @FXML
    public void decompressFileChooser() throws IOException {
        if (filePath.isEmpty()) {
            resultLabel.setText("No File Selected");
        } else if (!filePath.contains(".huff")) {
            resultLabel.setText("File Should Contain .huff Extension");
        } else {

                DecompressController decompressor = new DecompressController();
                decompressor.setFilePath(filePath);
                decompressor.initialize();

        }
    }
}