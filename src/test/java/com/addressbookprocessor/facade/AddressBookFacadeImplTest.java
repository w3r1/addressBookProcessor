package com.addressbookprocessor.facade;

import com.addressbookprocessor.domain.Person;
import com.addressbookprocessor.reader.AddressBookCsvReader;
import com.addressbookprocessor.service.AddressBookService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.addressbookprocessor.domain.Gender.Male;
import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddressBookFacadeImplTest {

    private static final String SOME_CSV_PATH = "/some/path/to/csv";

    @Rule
    public ExpectedException expectedException = none();

    @Mock
    private AddressBookCsvReader addressBookCsvReader;

    @Mock
    private AddressBookService addressBookService;

    @InjectMocks
    private AddressBookFacadeImpl addressBookFacade;

    @Test
    public void shouldProcessAddressBook() {

        given(addressBookService.countPersonsOfGender(any())).willReturn(123L);
        given(addressBookService.getOldestPerson())
                .willReturn(Optional.of(Person.builder().name("Jackl").build()));
        given(addressBookService.getDaysPersonAIsOlderPersonB(eq("Bill"), eq("Paul")))
                .willReturn(987L);

        String screenOutput = addressBookFacade.processAddressBook(SOME_CSV_PATH);

        verify(addressBookCsvReader).readPersonsFromCsv(eq(SOME_CSV_PATH));
        verify(addressBookService).countPersonsOfGender(eq(Male));
        verify(addressBookService).getOldestPerson();
        verify(addressBookService).getDaysPersonAIsOlderPersonB(eq("Bill"), eq("Paul"));

        assertTrue(screenOutput.contains("Count of people with gener men: 123"));
        assertTrue(screenOutput.contains("Oldest person is: Jackl"));
        assertTrue(screenOutput.contains("Bill is older Paul: 987 days"));
    }

    @Test
    public void shouldProcessAddressBookWithEmptyValues() {

        given(addressBookService.countPersonsOfGender(any())).willReturn(0L);
        given(addressBookService.getOldestPerson())
                .willReturn(Optional.empty());
        willAnswer((var) -> { throw new IllegalArgumentException("Abc"); })
                .given(addressBookService).getDaysPersonAIsOlderPersonB(eq("Bill"), eq("Paul"));

        String screenOutput = addressBookFacade.processAddressBook(SOME_CSV_PATH);

        verify(addressBookCsvReader).readPersonsFromCsv(eq(SOME_CSV_PATH));
        verify(addressBookService).countPersonsOfGender(eq(Male));
        verify(addressBookService).getOldestPerson();
        verify(addressBookService).getDaysPersonAIsOlderPersonB(eq("Bill"), eq("Paul"));

        assertTrue(screenOutput.contains("Count of people with gener men: 0"));
        assertTrue(screenOutput.contains("Oldest person is: <none>"));
        assertTrue(screenOutput.contains("Bill is older Paul: <none> days"));
    }

    @Test
    public void shouldThrowExceptionWhenParamNull() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Given addressBookFilePath is blank");

        addressBookFacade.processAddressBook(null);
    }

    @Test
    public void shouldThrowExceptionWhenParamBlank() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Given addressBookFilePath is blank");

        addressBookFacade.processAddressBook("   ");
    }
}