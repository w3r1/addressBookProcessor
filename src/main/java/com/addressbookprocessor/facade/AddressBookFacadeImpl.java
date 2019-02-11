package com.addressbookprocessor.facade;

import com.addressbookprocessor.domain.Gender;
import com.addressbookprocessor.domain.Person;
import com.addressbookprocessor.reader.AddressBookCsvReader;
import com.addressbookprocessor.service.AddressBookService;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static com.addressbookprocessor.domain.Gender.Male;
import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@AllArgsConstructor
public class AddressBookFacadeImpl implements AddressBookFacade {

    private static final Logger LOGGER = Logger.getLogger(AddressBookFacadeImpl.class.getName());

    private static final String OLDER_PERSON_A = "Bill";
    private static final String OLDER_PERSON_B = "Paul";
    private static final Gender GENDER_COUNT_ON = Male;

    private static final String END_LINE = System.lineSeparator();

    private AddressBookCsvReader csvReader;

    private AddressBookService addressBookService;

    @Override
    public String processAddressBook(String addressBookFilePath) {

        boolean isParamValid = isNotBlank(addressBookFilePath);
        if (!isParamValid) {
            LOGGER.severe("Given addressBookFilePath is blank");
            throw new IllegalArgumentException("Given addressBookFilePath is blank");
        }

        List<Person> personList = csvReader.readPersonsFromCsv(addressBookFilePath);
        addressBookService.setPersons(personList);

        Long countMales = addressBookService.countPersonsOfGender(GENDER_COUNT_ON);
        Optional<Person> oldestPerson = addressBookService.getOldestPerson();

        Long daysOlder = null;
        try {
            daysOlder = addressBookService.getDaysPersonAIsOlderPersonB(OLDER_PERSON_A, OLDER_PERSON_B);
        } catch (IllegalArgumentException ex) {
            LOGGER.warning(join("No result at getDaysPersonAIsOlderPersonB for ", OLDER_PERSON_A,
                    " and ", OLDER_PERSON_B));
        }

        return screenOutput(countMales, oldestPerson, daysOlder);
    }

    private String screenOutput(Long countMales, Optional<Person> oldestPerson, Long daysOlder) {

        StringBuilder builder = new StringBuilder(500);
        builder.append("Count of people with gener men: ").append(countMales).append(END_LINE);
        builder.append("Oldest person is: ")
                .append(oldestPerson.isPresent() ? oldestPerson.get().getName() : "<none>").append(END_LINE);
        builder.append(OLDER_PERSON_A).append(" is older ").append(OLDER_PERSON_B).append(": ")
                .append(daysOlder != null ? daysOlder : "<none>").append(" days").append(END_LINE);

        return builder.toString();
    }
}
