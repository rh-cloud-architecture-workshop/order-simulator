package org.acme.retail.order.simulator.model.customer;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity(name = "Address")
@Table(name = "address")
public class Address extends PanacheEntityBase {

    @Id
    @Column(name = "cust_id")
    public long id;

    @Column(name = "address1")
    public String address1;

    @Column(name = "address2")
    public String address2;

    @Column(name = "city")
    public String city;

    @Column(name = "zip")
    public String zipCode;

    @Column(name = "state")
    public String state;

    @Column(name = "country")
    public String country;

    @OneToOne
    @MapsId
    @JoinColumn(name = "cust_id")
    public Customer customer;
}
