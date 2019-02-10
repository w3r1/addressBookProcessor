package com.addressbookprocessor.reader;

import com.addressbookprocessor.domain.AddressEntry;
import com.addressbookprocessor.domain.Gender;
import com.addressbookprocessor.reader.exception.ProvidedFileHasErrorException;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.util.List;

import static com.addressbookprocessor.domain.Gender.Male;
import static java.time.LocalDate.of;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

public class AddressBookCsvReaderTest {

    private static final String BASE_PATH = AddressBookCsvReaderTest.class.getResource("../../..").getPath();
    private static final String CORRECT_CSV = BASE_PATH + "/AddressBookCorrect";
    private static final String MALFORMED_CSV_GENDER = BASE_PATH + "/AddressBookWrongGender";
    private static final String MALFORMED_CSV_DATE = BASE_PATH + "/AddressBookWrongDateFormat";
    private static final String MALFORMED_CSV_TWO_COLUMNS = BASE_PATH + "/AddressBookWrongTwoColumns";
    private static final String NOTEXISTENT_CSV = BASE_PATH + "/NotExistentCsv";

    @Rule
    public ExpectedException expectedException = none();

    private AddressBookCsvReader csvReader = new AddressBookCsvReader();

    @Test
    public void shouldReadAddressEntriesFromCorrectFormattedCSV() {

        String testFirstEntryPersonName = "Bill McKnight";
        Gender testFirstEntryPersonGender = Male;
        LocalDate testFirstEntryPersonBirth = of(1977, 03, 16);

        List<AddressEntry> addressEntries = csvReader.readAddressEntriesFromCsv(CORRECT_CSV);

        assertThat(addressEntries, Matchers.hasSize(5));
        assertThat(addressEntries.get(0), hasProperty("name", Matchers.equalTo(testFirstEntryPersonName)));
        assertThat(addressEntries.get(0), hasProperty("gender", Matchers.equalTo(testFirstEntryPersonGender)));
        assertThat(addressEntries.get(0), hasProperty("birthDate", Matchers.equalTo(testFirstEntryPersonBirth)));
    }

    @Test
    public void shouldThrowExceptionWhenCsvFileIsNull() {

        String testPath = null;

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Given addressBookFilePath is blank");

        csvReader.readAddressEntriesFromCsv(testPath);
    }

    @Test
    public void shouldThrowExceptionWhenCsvFileIsEmpty() {

        String testPath = "   ";

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Given addressBookFilePath is blank");

        csvReader.readAddressEntriesFromCsv(testPath);
    }

    @Test
    public void shouldThrowExceptionWhenCsvIsNotExistent() {

        expectedException.expect(ProvidedFileHasErrorException.class);
        expectedException.expectMessage("File on provided path cannot be read");

        csvReader.readAddressEntriesFromCsv(NOTEXISTENT_CSV);
    }

    @Test
    public void shouldThrowExceptionWhenCsvHasInvalidGenderType() {

        expectedException.expect(ProvidedFileHasErrorException.class);
        expectedException.expectMessage("File on provided path cannot be read: CSV structure/data unexpected");

        csvReader.readAddressEntriesFromCsv(MALFORMED_CSV_GENDER);
    }

    @Test
    public void shouldThrowExceptionWhenCsvHasInvalidBirthDate() {

        expectedException.expect(ProvidedFileHasErrorException.class);
        expectedException.expectMessage("File on provided path cannot be read: CSV structure/data unexpected");

        csvReader.readAddressEntriesFromCsv(MALFORMED_CSV_DATE);
    }

    @Test
    public void shouldThrowExceptionWhenCsvHasLessColumnsThenExpected() {

        expectedException.expect(ProvidedFileHasErrorException.class);
        expectedException.expectMessage("File on provided path cannot be read: CSV structure/data unexpected");

        csvReader.readAddressEntriesFromCsv(MALFORMED_CSV_TWO_COLUMNS);
    }
}