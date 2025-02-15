package org.acme.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


@Entity
public class Ride {

    @Id
    @GeneratedValue
    private Long id;

    public String name;
    public double rating;
}
