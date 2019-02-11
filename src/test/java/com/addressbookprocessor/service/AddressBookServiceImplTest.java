package com.addressbookprocessor.service;

import com.addressbookprocessor.domain.Gender;
import com.addressbookprocessor.domain.Person;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.addressbookprocessor.domain.Gender.Female;
import static java.time.LocalDate.of;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;
import static org.junit.rules.ExpectedException.none;

public class AddressBookServiceImplTest {

    private static final List<Person> persons;
    private static final AddressBookService service;
    static {

        persons = unmodifiableList(new ArrayList(asList(
                null,
                null,
                new Person("Jill Jungle", Female, of(1982, 04, 04)),
                new Person("Guybrush Island", Gender.Male, of(1917, 01, 01)),
                new Person("Jade BeyondGood", Female, of(1987, 02, 02)),
                new Person("Lara Raider", Female, of(1997, 03, 03)),
                null,
                null
        )));
        service = new AddressBookServiceImpl(persons);
    }

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void shouldCountFemalePersons() {

        assertThat(service.countPersonsOfGender(Female), equalTo(3L));
    }

    @Test
    public void shouldGetOldestPerson() {

        Optional<Person> personOptional = service.getOldestPerson();
        assertTrue(personOptional.isPresent());
        assertThat(personOptional.get(), hasProperty("name", equalTo("Guybrush Island")));
    }

    @Test
    public void shouldGetDaysPersonAIsOlderPersonB() {

        assertThat(service.getDaysPersonAIsOlderPersonB("Jill", "Jade"), equalTo(1765L));
    }

    @Test
    public void shouldGetDaysPersonAIsOlderPersonBMinus() {

        assertThat(service.getDaysPersonAIsOlderPersonB("Jade", "Jill"), equalTo(-1765L));
    }

    @Test
    public void shouldThrowExceptionWhenInitialisedWithNull() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Persons list must be set");

        new AddressBookServiceImpl(null);
    }

    @Test
    public void shouldThrowExceptionWhenPersonListLeftNullOnCountPersonOfGender() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Persons list must be set");

        AddressBookServiceImpl localService = new AddressBookServiceImpl();
        localService.countPersonsOfGender(Female);
    }

    @Test
    public void shouldThrowExceptionWhenPersonListLeftNullOnGetOldestPerson() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Persons list must be set");

        AddressBookServiceImpl localService = new AddressBookServiceImpl();
        localService.getOldestPerson();
    }

    @Test
    public void shouldThrowExceptionWhenPersonListLeftNullOnGetDayDifferenceOfBirth() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Persons list must be set");

        AddressBookServiceImpl localService = new AddressBookServiceImpl();
        localService.getDaysPersonAIsOlderPersonB("", "");
    }

    @Test
    public void shouldThrowExceptionWhenCountPersonsOfGenderParamIsNull() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Gender must be given");

        service.countPersonsOfGender(null);
    }

    public void shouldThrowExceptionWhenGetDaysPersonAIsOlderPersonBNameIsNull() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("PersonA and B names must be given");

        service.getDaysPersonAIsOlderPersonB(null, "Jade");
    }

    public void shouldThrowExceptionWhenGetDaysPersonAIsOlderPersonBNameIsBlank() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("PersonA and B names must be given");

        service.getDaysPersonAIsOlderPersonB("Jade", "   ");
    }

    @Test
    public void shouldCountFemalePersonsOnEmptyList() {

        AddressBookService localService = new AddressBookServiceImpl(new ArrayList<>());

        assertThat(localService.countPersonsOfGender(Female), equalTo(0L));
    }

    @Test
    public void shouldGetEmptyOnEmptyList() {

        AddressBookService localService = new AddressBookServiceImpl(new ArrayList<>());

        assertFalse(localService.getOldestPerson().isPresent());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetDaysPersonAIsOlderPersonBOnEmptyList() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Less than or more than 2 people found");

        AddressBookService localService = new AddressBookServiceImpl(new ArrayList<>());
        localService.getDaysPersonAIsOlderPersonB("Jill", "Jade");
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenOneOfTwoNamesIsFoundTwice() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Person A or B not found");

        ArrayList<Person> newPersons = new ArrayList<>(persons);
        newPersons.add(new Person("Jill SecondPersonWithName", Female, of(1999, 01, 01)));

        AddressBookService localService = new AddressBookServiceImpl(newPersons);
        localService.getDaysPersonAIsOlderPersonB("Jill", "Garrett");
    }
}