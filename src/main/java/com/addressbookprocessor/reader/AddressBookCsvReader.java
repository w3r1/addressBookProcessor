package com.addressbookprocessor.reader;

import com.addressbookprocessor.domain.Gender;
import com.addressbookprocessor.domain.Person;
import com.addressbookprocessor.reader.exception.ProvidedFileHasErrorException;
import io.vavr.control.Try;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.addressbookprocessor.domain.Gender.valueOf;
import static java.time.LocalDate.now;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.util.logging.Level.SEVERE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;

public class AddressBookCsvReader {

    private static final Logger LOGGER = Logger.getLogger(AddressBookCsvReader.class.getName());

    private static final String DATE_FORMAT = "d/M/yy";

    public List<Person> readPersonsFromCsv(final String addressBookFilePath) {

        boolean isParamValid = isNotBlank(addressBookFilePath);
        if (!isParamValid) {
            LOGGER.severe("Given addressBookFilePath is blank");
            throw new IllegalArgumentException("Given addressBookFilePath is blank");
        }

        final CSVParser addressBookFileParser = intialiseCSVParser(addressBookFilePath);
        List<Person> persons = parsePersons(addressBookFileParser);

        return persons;
    }

    private List<Person> parsePersons(CSVParser addressBookFileParser) {

        List<Person> persons = new ArrayList<>();
        for (CSVRecord personEntry : addressBookFileParser) {

            Person person =
                    Try.of(() -> {

                        String fullName = personEntry.get(0);
                        Gender gender = valueOf(personEntry.get(1));
                        LocalDate birthDate = parse(personEntry.get(2), ofPattern(DATE_FORMAT));

                        boolean isBirthdateInFuture = birthDate.isAfter(now());
                        if (isBirthdateInFuture) {
                            birthDate = birthDate.minus(100, YEARS);
                        }

                        return new Person(fullName, gender, birthDate);
                    })
                    .onFailure(ex -> {

                        LOGGER.log(
                                SEVERE,
                                join("File on provided path cannot be read: CSV structure/data unexpected of: ", personEntry),
                                ex);
                        throw new ProvidedFileHasErrorException("File on provided path cannot be read: CSV structure/data unexpected");
                    })
                    .get();
            persons.add(person);
        }
        return persons;
    }

    private CSVParser intialiseCSVParser(String addressBookFilePath) {

        final Reader addressBookFileReader =
                Try.of(() -> Files.newBufferedReader(Paths.get(addressBookFilePath)))
                .onFailure(ex -> {

                    LOGGER.log(
                            SEVERE,
                            "File on provided path cannot be read",
                            ex);
                    throw new ProvidedFileHasErrorException("File on provided path cannot be read");
                })
                .get();

        return Try.of(() -> new CSVParser(addressBookFileReader, CSVFormat.DEFAULT.withTrim()))
        .onFailure(ex -> {

            LOGGER.log(
                    SEVERE,
                    "File on provided path cannot be read: CSV reader error",
                    ex);
            throw new ProvidedFileHasErrorException("File on provided path cannot be read: CSV reader error");
        })
        .get();
    }
}
