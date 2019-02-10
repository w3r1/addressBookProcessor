package com.addressbookprocessor;

public class AddressBookProcessorMain {

    public static final void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: cmd> [application] [addessBookCsvFile:Text]");
            return;
        }

        String filePath = args[0];
    }
}
