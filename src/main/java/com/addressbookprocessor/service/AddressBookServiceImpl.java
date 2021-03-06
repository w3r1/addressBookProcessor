package com.addressbookprocessor.service;

import com.addressbookprocessor.domain.Gender;
import com.addressbookprocessor.domain.Person;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;

@Setter
@NoArgsConstructor
public class AddressBookServiceImpl implements AddressBookService {

    private static final Logger LOGGER = Logger.getLogger(AddressBookServiceImpl.class.getName());

    private List<Person> persons;

    public AddressBookServiceImpl(List<Person> persons) {

        this.persons = persons;
        validatePersons();
    }

    @Override
    public Long countPersonsOfGender(Gender gender) {

        validatePersons();

        boolean isParamValid = gender != null;
        if (!isParamValid) {
            LOGGER.warning("Gender not given");
            throw new IllegalArgumentException("Gender must be given");
        }

        return persons.stream()
                .filter(p -> p != null && gender.equals(p.getGender()))
                .count();
    }

    @Override
    public Optional<Person> getOldestPerson() {

        validatePersons();

        return persons.stream()
                .sorted((p1, p2) -> {

                    boolean isP1Valid = p1 != null && p1.getBirthDate() != null;
                    if (!isP1Valid) {
                        return 1;
                    }

                    boolean isP2Valid = p2 != null && p2.getBirthDate() != null;
                    if (!isP2Valid) {
                        return -1;
                    }

                    return p1.getBirthDate().compareTo(p2.getBirthDate());
                })
                .findFirst();
    }

    @Override
    public Long getDaysPersonAIsOlderPersonB(String personAName, String personBName) {

        validatePersons();

        boolean areParamsValid = isNotBlank(personAName) && isNotBlank(personBName);
        if (!areParamsValid) {
            LOGGER.warning("Person name are (A and/or B) not given");
            throw new IllegalArgumentException("PersonA and B names must be given");
        }

        List<Person> persons = this.persons.parallelStream()
                .filter(p -> p != null
                        && p.getName() != null
                        && (p.getName().contains(personAName) || p.getName().contains(personBName)))
                .collect(Collectors.toList());

        return calculateDaysBetweenPersons(persons, personAName, personBName);
    }

    private void validatePersons() {

        boolean isPersonsListValid = persons != null;
        if (!isPersonsListValid) {
            LOGGER.warning("Persons list not given");
            throw new IllegalArgumentException("Persons list must be set");
        }
    }

    private Long calculateDaysBetweenPersons(List<Person> persons, String personAName, String personBName) {

        boolean areTwoPersonFound = persons.size() == 2;
        if (!areTwoPersonFound) {
            LOGGER.warning(join("Inaccurate sub results: Less or more then 2 peope found for ", personAName, " and ", personBName));
            throw new IllegalArgumentException("Less than or more than 2 people found");
        }

        Person person1 = persons.get(0);
        Person person2 = persons.get(1);
        boolean isPerson1PersonA = person1.getName().contains(personAName) && person2.getName().contains(personBName);
        boolean isPerson2PersonA = person2.getName().contains(personAName) && person1.getName().contains(personBName);

        if (isPerson1PersonA) {
            return DAYS.between(person1.getBirthDate(), person2.getBirthDate());
        } else if (isPerson2PersonA) {
            return DAYS.between(person2.getBirthDate(), person1.getBirthDate());
        } else {
            LOGGER.warning(join("Inaccurate sub results: One person found more times for ", personAName, " or ", personBName));
            throw new IllegalArgumentException("Person A or B not found");
        }
    }
}
