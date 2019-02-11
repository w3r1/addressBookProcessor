package com.addressbookprocessor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class Person {

    private String name;

    private Gender gender;

    private LocalDate birthDate;
}
