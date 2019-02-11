package com.addressbookprocessor;

import com.addressbookprocessor.facade.AddressBookFacadeImpl;
import com.addressbookprocessor.reader.AddressBookCsvReader;
import com.addressbookprocessor.service.AddressBookServiceImpl;

public class AddressBookProcessorMain {

    public static final void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: cmd> [application] [addessBookCsvFile:Text]");
            return;
        }

        String csvFilePath = args[0];

        AddressBookFacadeImpl addressBookFacade =
                new AddressBookFacadeImpl(new AddressBookCsvReader(), new AddressBookServiceImpl());
        System.out.println(addressBookFacade.processAddressBook(csvFilePath));
    }
}
