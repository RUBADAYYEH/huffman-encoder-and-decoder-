package com.example.huffmanproject;

import javafx.scene.control.Label;


import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

public class DecompressController {
    String[][] codesTable;
    private static String filePath;
    static BufferedOutputStream bufferedSteam;
    BufferedInputStream inputFile;


    public String getFilePath() {
        return filePath;
    }

    public  void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void initialize() throws IOException {
        System.out.println(filePath+" de ");

      if (filePath.contains("png")||filePath.contains("jpg")){
          decompressImage();
       }
       else{

          DecompressText();
       }

    }


    public static void DecompressText(){
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
             FileChannel inChannel = file.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(4);

            // Read the size of the original file in hexadecimal
            inChannel.read(buffer);
            buffer.flip();
            String hexSizeOfHeader= Integer.toHexString(buffer.getInt());
            int sizeOfHeader = Integer.parseInt(hexSizeOfHeader, 16);

            buffer.clear();

            // Read the size of the header in hexadecimal
            inChannel.read(buffer);
            buffer.flip();
            String hexSizeOfFile= Integer.toHexString(buffer.getInt());
            int sizeOfFile = Integer.parseInt(hexSizeOfFile, 16);
            System.out.println("sof"+sizeOfFile);

            buffer.clear();

            // Now you can read the actual header string
            ByteBuffer stringBuffer = ByteBuffer.allocate(sizeOfHeader);
            inChannel.read(stringBuffer);
            stringBuffer.flip();


            String headerString = new String(stringBuffer.array(), StandardCharsets.UTF_8);
            System.out.println("Header String: " + (headerString));



            Node root=buildTree(headerString);

            // System.out.println(headerString);
            stringBuffer.clear();

            // Now, you have m, n, and the header string

            // Continue reading the rest of the file
            ByteBuffer textBuffer = ByteBuffer.allocate(8);
            StringBuilder actualString = new StringBuilder();
            Node currentNode = root; // Assuming you have the root of the Huffman tree
            int count=0;
           // System.out.println("size of file"+sizeOfFile);

            while (count<=sizeOfFile && inChannel.read(textBuffer) != -1) {
                textBuffer.flip();

                while (textBuffer.hasRemaining()) {
                    byte byteRead = textBuffer.get();

                    // Process each bit in the byte
                    for (int i = 7; i >= 0; i--) {
                        int bit = (byteRead >> i) & 1; // Get the i-th bit

                        // Traverse the Huffman tree based on the bit
                        if (bit == 0) {
                            currentNode = currentNode.getLeft();
                        } else {
                            currentNode = currentNode.getRight();
                        }

                        // Check if it's a leaf node
                        if (currentNode.getLeft() == null && currentNode.getRight() == null) {
                            // Append the character to the result
                            actualString.append(currentNode.getValue());
                            // Reset to the root for the next iteration
                            currentNode = root;
                        }
                    }
                    count++;

                }


                textBuffer.clear();
            }

          // System.out.println("actual String "+actualString);
            System.out.println("size after decompreseeion "+actualString.length());
            writeToFile("output1.txt",stringToBinaryString(actualString.toString()));

        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }

    }
    public static void decompressImage(){

            try (FileInputStream fileInputStream = new FileInputStream(filePath);
                 DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {

              int sizeOfHeader=dataInputStream.readInt();
              int sizeOfFile=dataInputStream.readInt();
              StringBuilder header= new StringBuilder();

              for (int i=0;i<sizeOfHeader;i++){
                  header.append((char) (dataInputStream.read()));
              }
              int count=0;
              StringBuilder actualString=new StringBuilder();


            System.out.println("Header String: " + (header));



            Node root=buildTree(header.toString());

            Node currentNode = root; // Assuming you have the root of the Huffman tree

            // System.out.println("size of file"+sizeOfFile);

            while (count<sizeOfFile ) {



                    int  byteRead = dataInputStream.readUnsignedByte() ;

                    // Process each bit in the byte
                    for (int i = 7; i >= 0; i--) {
                        int bit = (byteRead >> i) & 1; // Get the i-th bit

                        // Traverse the Huffman tree based on the bit
                        if (bit == 0) {
                            currentNode = currentNode.getLeft();
                        } else {
                            currentNode = currentNode.getRight();
                        }

                        // Check if it's a leaf node
                        if (currentNode.getLeft() == null && currentNode.getRight() == null) {
                            // Append the character to the result
                            actualString.append(currentNode.getValue());
                            count++;
                            // Reset to the root for the next iteration
                            currentNode = root;
                        }
                    }


                }
                System.out.println("size after decompreseeion "+actualString.length());



        // Save the image to a PNG file
            String filePaths[]=filePath.split("\\.");
            writeToFile(filePaths[0]+"-decompressed.png",stringToBinaryString(actualString.toString()));




        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }


    }
    private static void writeToFile(String outputFile,String binaryString) throws IOException {

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(outputFile );

            // Convert the binary string to bytes and write to the output stream
            byte[] byteArray = binaryStringToByteArray(binaryString);
            out.write(byteArray);
        } finally {
            if (out != null) {
                out.close();
            }
            System.out.println("Read and Write complete to "+outputFile);
        }


    }


    public static byte[] binaryStringToByteArray(String binaryString) {
        int length = binaryString.length();

        // Ensure that the length of the binary string is a multiple of 8
        if (length % 8 != 0) {
            throw new IllegalArgumentException("Binary string length must be a multiple of 8");
        }

        // Create a byte array to hold the converted bytes
        byte[] byteArray = new byte[length / 8];

        // Convert each group of 8 bits to a byte
        for (int i = 0; i < length; i += 8) {
            String eightBits = binaryString.substring(i, i + 8);
            byteArray[i / 8] = (byte) ((byte) Integer.parseInt(eightBits, 2) & 0xFF);
        }

        return byteArray;
    }
    public static Node buildTree(String postOrderTraversal) {
        if (postOrderTraversal == null || postOrderTraversal.length() == 0) {
            return null;
        }

        Stack<Node> stack = new Stack<>();

        for (int i = 0; i < postOrderTraversal.length()-1; i++) {
            char currentChar = postOrderTraversal.charAt(i);

            if (currentChar == '0') {
                if (!stack.empty()){
                // Non-leaf node
                Node right = stack.pop();
                Node left = stack.pop();
                Node nonLeafNode = new Node(left, right);
                stack.push(nonLeafNode);}
            } else if (currentChar == '1') {
                // Leaf node
                i++; // Move to the next character which represents the ASCII character
                char leafValue = postOrderTraversal.charAt(i);
                Node leafNode = new Node(leafValue);
                stack.push(leafNode);
            }
        }

        // The stack now contains the root of the reconstructed tree
        return stack.pop();
    }



    public static String stringToBinaryString(String input) {
      //  System.out.println("input is "+input);
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
      //  System.out.println("Binary String: " + binaryString.toString());
        return binaryString.toString();
    }


}