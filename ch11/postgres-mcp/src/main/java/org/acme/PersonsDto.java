package org.acme;

import java.util.List;

public record PersonsDto(List<PersonDto> persons) {
    public record PersonDto(String name, String email, String address, String phone) {
    }
}