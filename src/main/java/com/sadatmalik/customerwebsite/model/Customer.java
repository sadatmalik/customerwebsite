package com.sadatmalik.customerwebsite.model;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
@Builder
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fullName;
    private String emailAddress;
    private Integer age;
    private String address;

    public void validate() throws IllegalStateException {
        if (fullName == null || fullName.equals("")) {
            throw new IllegalStateException("You must enter a name");
        }

        if (emailAddress == null || emailAddress.equals("")) {
            throw new IllegalStateException("You must enter an email address");
        }

        if (age == null || age < 0) {
            throw new IllegalStateException("You must enter a valid age > 0");
        }

        if (address == null || address.equals("")) {
            throw new IllegalStateException("You must enter an address");
        }
    }
}