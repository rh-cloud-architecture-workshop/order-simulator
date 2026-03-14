package org.acme.retail.order.simulator.model.product;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "catalog")
public class Product extends PanacheEntityBase {

    @Id
    @Column(name = "item_id")
    public String productId;

    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "category")
    public long category;

    @Column(name = "price")
    public double price;

}
