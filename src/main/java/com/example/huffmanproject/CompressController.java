package com.example.huffmanproject;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

//THIS CONTROLLER IS INITIALIZED FROM HUFFMANTABLECONTROLLER CLASS .
public class CompressController {
    //codesTable are passed from HuffmanTableController class which represents the table for ascii and their
    //character representation and their path (huffman code).

    private String[][] codesTable;
    private static String filePath;
    private static StringBuilder encoded;
    private static Header header;
    private String deliveredNotification;

    public String getDeliveredNotification() {
        return deliveredNotification;
    }

    public void setDeliveredNotification(String deliveredNotification) {
        this.deliveredNotification = deliveredNotification;
    }

    private static StringBuilder treeTraversal;
   static  int fileSize;


    public StringBuilder getTreeTraversal() {
        return treeTraversal;
    }

    public void setTreeTraversal(StringBuilder treeTraversal) {
        this.treeTraversal = treeTraversal;
    }

    public void setCodesTable(String[][] codesTable) {
        this.codesTable = codesTable;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void initialize() throws IOException {
         fileSize = 0;


        encoded = new StringBuilder();
        //System.out.println("size of table is : "+codesTable.length);
       /* for (int i=0;i<codesTable.length;i++){
            for (int j=0;j<codesTable[i].length;j++){
                System.out.print(codesTable[i][j]+" ");
            }
            System.out.println();
        }*/

        System.out.println(" this file is about to get compressed "+getFilePath());
        RandomAccessFile file = new RandomAccessFile(filePath, "r");



        FileChannel inChannel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(8);

        int byteRead;
        while (inChannel.read(buffer) != -1) {
            buffer.flip();
            for (int i = 0; i < buffer.limit(); i++) {
                byteRead =  (buffer.get() & 0xFF);  // Convert signed byte to unsigned int
                fileSize++;
                //System.out.println(byteRead);
                encoded.append(searchForCode(byteRead));
            }

            buffer.clear(); // Prepare for writing new data
        }

        if (buffer.position() > 0) {
            while (buffer.hasRemaining()) {
                byteRead =  (buffer.get() & 0xFF);
                fileSize++;
                encoded.append(searchForCode(byteRead).trim());
            }
        }
        inChannel.close();
        file.close();

        String[] fileSpec = filePath.split("\\.");

        //if (fileSpec[1].equals("jpg") || fileSpec[1].equals("png")) {
            processImageCompression();
            System.out.println(fileSpec[1]+" File written Successfully");
       // } else {
      //      processTextCompression(file);
       //     System.out.println(fileSpec[1]+"File written Successfully");
       // }





            //FileOutputStream Ofile=new FileOutputStream("output.bin","w");
            //  FileChannel outputChannel=Ofile.getChannel();
            // ByteBuffer buffer=ByteBuffer.allocate(8);

        }

    private void processImageCompression() throws IOException {

        header = new Header();
        header.setSizeOfOriginalFile(fileSize);
        System.out.println("file size"+fileSize);
        header.setTreeRepresentation(treeTraversal);
        // System.out.println(" Header is : "+treeTraversal);
        header.setSizeofHeader(header.getTreeRepresentation().length());


        String pathOfFiles[]=filePath.split("\\.");
        String extension =pathOfFiles[1];
        convertAndWriteToFile(header,encoded.toString(), pathOfFiles[0]+"-"+extension+".huff");
        HuffmanTableController.OriginalSize=header.getSizeOfOriginalFile();
        HuffmanTableController.afterCompSize=encoded.length()/8;
       // double ratio=(encoded.length()/(sb.length()/8))*100;
        //deliveredNotification=("Orginal file size :"+encoded.length()+"\n"+"after compression :"+sb.length()/8+"\n"+"compression rate :"+ratio+"%");

    }







    public String searchForCode(int byteRead) {

        for (int i = 1; i < codesTable.length; i++) {
            for (int j = 0; j < codesTable[i].length; j++) {

                if (byteRead == Integer.parseInt(codesTable[i][1])) {


                    //System.out.println(codesTable[i][2]+" for "+codesTable[i][0]);
                    return codesTable[i][2];}

                else{
                  //  System.out.println("byte Read "+byteRead+" does not match "+codesTable[i][]);
                }
            }
        }
        return "";
    }

    public static void convertAndWriteToFile(Header header ,String binaryString, String filePath) throws IOException {
        // Ensure that the length of the binary string is a multiple of 8

        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
            // Write integers as 4-byte values
            dataOutputStream.writeInt(header.getSizeofHeader());
            dataOutputStream.writeInt(header.getSizeOfOriginalFile());

            writeBinaryStringToBytes(dataOutputStream, stringToBinaryString(header.getTreeRepresentation().toString()));

            writeBinaryStringToBytes( dataOutputStream, binaryString);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void writeBinaryStringToBytes(DataOutputStream dataOutputStream, String binaryString) throws IOException {
        // Ensure that the length of the binary string is a multiple of 8
        int remainder = binaryString.length() % 8;
        if (remainder != 0) {
            // Append zeros to make the length a multiple of 8
            int zerosToAdd = 8 - remainder;
            binaryString = binaryString + "0".repeat(zerosToAdd);
        }
        int [] buffer =new int[8];
        int track=0;

        // Convert binary string to bytes and write to the stream
        for (int i = 0; i < binaryString.length(); i += 8) {
            String byteString = binaryString.substring(i, i + 8);
            int byteValue = Integer.parseInt(byteString, 2) ;
            buffer[track++]=byteValue;
            if (track==8){
                for (int t=0;t<8;t++){
                    dataOutputStream.write(buffer[t]);
                }
                track=0;

            }
            //dataOutputStream.write(byteValue);
        }
        if (track!=0){
            for (int i=0;i<track;i++){
                dataOutputStream.write(buffer[i]);
            }
        }
    }


            // Create a byte array to hold the converted bytes






    public static String stringToBinaryString(String input) {
        // System.out.println("input is "+input);
        StringBuilder binaryString = new StringBuilder();

        // Iterate through each character in the input string
        for (char c : input.toCharArray()) {
            // Convert each character to its ASCII value and then to binary
            String charBinary = Integer.toBinaryString(c);

            // Ensure each binary representation is 8 bits long by padding with leading zeros if needed
            int padding = 8 - charBinary.length();
            if (padding > 0) {
                binaryString.append("0".repeat(Math.max(0, padding)));
            }

            binaryString.append(charBinary);
        }
        // System.out.println("Binary String: " + binaryString.toString());
        return binaryString.toString();
    }
    }







