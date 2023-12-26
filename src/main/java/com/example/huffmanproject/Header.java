package com.example.huffmanproject;

public class Header {
    private int sizeofHeader; //THOSE INT ARE TREATED IN 4 BYTE REPRESENTATION.
    private int sizeOfOriginalFile;


    private StringBuilder treeRepresentation;


    public Header() {

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


}
