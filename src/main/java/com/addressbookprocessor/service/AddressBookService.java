package com.addressbookprocessor.service;

import com.addressbookprocessor.domain.Gender;
import com.addressbookprocessor.domain.Person;

import java.util.List;
import java.util.Optional;

public interface AddressBookService {

    Long countPersonsOfGender(Gender gender);

    Optional<Person> getOldestPerson();

    Long getDaysPersonAIsOlderPersonB(String personAName, String personBName);

    void setPersons(List<Person> persons);
}