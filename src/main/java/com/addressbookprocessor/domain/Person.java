package com.addressbookprocessor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@ToString
public class Person {

    private String name;

    private Gender gender;

    private LocalDate birthDate;
}
