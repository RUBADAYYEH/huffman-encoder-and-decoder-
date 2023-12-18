package com.example.huffmanproject;

public class Header {
    private int sizeofHeader;
    private int width;
    private int height;


    private StringBuilder treeRepresentation;
    private int sizeOfOriginalFile;

    public Header() {

    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSizeofHeader() {
        return sizeofHeader;
    }

    public void setSizeofHeader(int sizeofHeader) {
        this.sizeofHeader = sizeofHeader;
    }

    public StringBuilder getTreeRepresentation() {
        return treeRepresentation;
    }

    public void setTreeRepresentation(StringBuilder treeRepresentation) {
        this.treeRepresentation = treeRepresentation;
    }

    public int getSizeOfOriginalFile() {
        return sizeOfOriginalFile;
    }

    public void setSizeOfOriginalFile(int sizeOfOriginalFile) {
        this.sizeOfOriginalFile = sizeOfOriginalFile;
    }

    public int encodeHeader(){
        sizeofHeader=treeRepresentation.length();
        return (sizeofHeader);
    }
}
