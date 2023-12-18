package com.example.huffmanproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.PriorityQueue;

public class HuffmanTreeBuildStart extends Application {
    public static String filePath;
    @Override
    public void start(Stage stage) throws IOException {
      FXMLLoader fxmlLoader = new FXMLLoader(HuffmanTreeBuildStart.class.getResource("hello-view.fxml"));
       Scene scene = new Scene(fxmlLoader.load(), 800, 700);
       stage.setTitle("Hello!");
       stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        // counting array . range of 1 byte unsigned .

         launch();







    }


    }
