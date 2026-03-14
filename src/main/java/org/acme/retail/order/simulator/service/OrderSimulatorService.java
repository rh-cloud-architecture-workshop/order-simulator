package org.acme.retail.order.simulator.service;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.retail.order.simulator.dto.LineItemDto;
import org.acme.retail.order.simulator.dto.OrderDto;
import org.acme.retail.order.simulator.dto.ShippingAddressDto;
import org.acme.retail.order.simulator.model.customer.Customer;
import org.acme.retail.order.simulator.model.product.Product;
import org.acme.retail.order.simulator.rest.OrderService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderSimulatorService {

    private static final Logger log = LoggerFactory.getLogger(OrderSimulatorService.class);

    @RestClient
    OrderService orderService;

    private volatile List<Customer> customers;

    private volatile List<Product> products;

    private final Random random = new Random();

    public JsonObject simulate(String customerId, Integer count) {

        getCustomers();
        log.info("Found " + customers.size() + " customers");
        getProducts();
        log.info("Found " + products.size() + " products");
        if (count == null) {
            count = 0;
        }
        if (customerId != null) {
            Customer customer = customers.stream().filter(cust -> cust.userId.equals(customerId)).findFirst().orElse(null);
            if (customer == null) {
                return new JsonObject().put("error", "Customer " + customerId + " not found");
            }
            log.info("Generating " + count + " sales for customer " + customer.userId);
            for (int i = 0; i < count; i++) {
                createOrder(customer);
            }
            log.info("Complete!");
            return new JsonObject().put("result", "Generated " + count + " orders for customer " + customer.userId);
        } else {
            log.info("Generating " + count + " sales for random customers");
            for (int i = 0; i < count; i++) {
                createOrder(randomCustomer());
            }
            log.info("Complete!");
            return new JsonObject().put("result", "Generated " + count + " orders for random customers");
        }
    }

    private void createOrder(Customer customer) {
        int numProducts = random.nextInt(5) + 1;
        Set<Product> productSet = new HashSet<>();
        while (productSet.size() < numProducts) {
            Product p = products.get(random.nextInt(products.size() - 1));
            productSet.add(p);
        }
        List<LineItemDto> lineItems = productSet.stream().map(p -> LineItemDto.builder()
                .withProduct(p.productId)
                .withQuantity(random.nextInt(3) + 1)
                .withPrice(p.price)
                .build()).collect(Collectors.toList());
        ShippingAddressDto shippingAddress = ShippingAddressDto.builder()
                .withName(customer.firstName + " " + customer.lastName)
                .withPhone(customer.phone)
                .withAddress1(customer.address.address1)
                .withAddress2(customer.address.address2)
                .withCity(customer.address.city)
                .withZip(customer.address.zipCode)
                .withState(customer.address.state)
                .withCountry(customer.address.country)
                .build();
        OrderDto order = OrderDto.builder()
                .withCustomer(customer.userId)
                .withTimestamp(Instant.ofEpochMilli(System.currentTimeMillis()))
                .withShippingAddress(shippingAddress)
                .withOrderLineItems(lineItems)
                .build();
        orderService.placeOrder(order);
    }

    private Customer randomCustomer() {
        return customers.get(random.nextInt(customers.size() - 1));
    }

    private List<Customer> getCustomers() {
        List<Customer> result = customers;
        if (result == null) {
            synchronized (this) {
                if (customers == null) {
                    customers = result = Customer.listAll();
                }
            }
        }
        return result;
    }

    private List<Product> getProducts() {
        List<Product> result = products;
        if (result == null) {
            synchronized (this) {
                if (products == null) {
                    products = result = Product.listAll();
                }
            }
        }
        return result;
    }

}
