package com.addressbookprocessor;

import com.addressbookprocessor.reader.AddressBookCsvReaderTest;
import org.junit.Test;

public class AddressBookProcessorMainTest {

    private static final String BASE_PATH = AddressBookCsvReaderTest.class.getResource("../../..").getPath();
    private static final String CORRECT_CSV = BASE_PATH + "/AddressBookCorrect";

    @Test
    public void shouldExecuteMainSuccessfully() {

        AddressBookProcessorMain.main(new String[] {CORRECT_CSV});
    }
}