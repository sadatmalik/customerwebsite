package com.sadatmalik.customerwebsite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Account {
    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private Long creditCardNum;
    private String creditCardType;
    private String balance;
    private Date updatedAt;
}