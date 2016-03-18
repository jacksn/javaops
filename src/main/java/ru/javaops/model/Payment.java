package ru.javaops.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * GKislin
 * 02.09.2015.
 */
@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {
    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @Column(name = "sum")
    private int sum;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "comment")
    private String comment;

    @Override
    public String toString() {
        return "Payment(" + sum + currency + ':' + comment + ')';
    }

    public Payment() {
    }

    public Payment(int sum, Currency currency, String comment) {
        this.sum = sum;
        this.currency = currency;
        this.comment = comment;
    }
}
