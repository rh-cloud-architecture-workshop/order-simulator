package org.acme.retail.order.simulator.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.acme.retail.order.simulator.model.customer.Customer;
import org.acme.retail.order.simulator.model.product.Product;

@ApplicationScoped
public class CustomerProductProducer {

    @Produces
    public Customers produceCustomers() {
        Customers customers = new Customers();
        customers.customers = Customer.listAll();
        Log.infof("Found %s customers", customers.customers.size());
        return customers;
    }

    @Produces
    public Products produceProducts() {
        Products products = new Products();
        products.products = Product.listAll();
        Log.infof("Found %s products", products.products.size());
        return products;
    }

}
