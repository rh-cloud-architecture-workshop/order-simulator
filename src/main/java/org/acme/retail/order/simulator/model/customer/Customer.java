package org.acme.retail.order.simulator.model.customer;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity(name = "Customer")
@Table(name = "customer")
public class Customer extends PanacheEntityBase {

    @Id
    @Column(name = "id")
    public long id;

    @Column(name = "user_id")
    public String userId;

    @Column(name = "first_name")
    public String firstName;

    @Column(name = "last_name")
    public String lastName;

    @Column(name = "email")
    public String email;

    @Column(name = "phone")
    public String phone;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public Address address;
}
