package com.sadatmalik.customerwebsite.model;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cars")
@Builder
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String make;
    private String model;

    @OneToOne(
            mappedBy = "car",
            optional = true
    )
    private Customer customer;

    public void validate() throws IllegalStateException {
        if (make == null || make.equals("")) {
            throw new IllegalStateException("You must enter a make");
        }

        if (model == null || model.equals("")) {
            throw new IllegalStateException("You must enter a model");
        }
    }

}
