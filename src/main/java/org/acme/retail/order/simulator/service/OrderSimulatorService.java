package org.acme.retail.order.simulator.service;

import io.quarkus.logging.Log;
import io.quarkus.runtime.configuration.DurationConverter;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.retail.order.simulator.dto.LineItemDto;
import org.acme.retail.order.simulator.dto.OrderDto;
import org.acme.retail.order.simulator.dto.ShippingAddressDto;
import org.acme.retail.order.simulator.model.customer.Customer;
import org.acme.retail.order.simulator.model.product.Product;
import org.acme.retail.order.simulator.rest.OrderService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderSimulatorService {

    @RestClient
    OrderService orderService;

    @Inject
    Customers customerHolder;

    @Inject
    Products productHolder;

    private final Random random = new Random();

    public JsonObject simulate(String customerId, Integer count, String period) {
        if (count == null) {
            count = 0;
        }
        if (customerId != null) {
            Customer customer = customerHolder.customers.stream().filter(cust -> cust.userId.equals(customerId)).findFirst().orElse(null);
            if (customer == null) {
                return new JsonObject().put("error", "Customer " + customerId + " not found");
            }
            Log.infof("Generating %s orders for customer %s", count, customer.userId);
            for (int i = 0; i < count; i++) {
                createOrder(customer, period);
            }
            Log.infof("Complete!");
            return new JsonObject().put("result", "Generated " + count + " orders for customer " + customer.userId);
        } else {
            Log.infof("Generating %s orders for random customers", count);
            for (int i = 0; i < count; i++) {
                createOrder(randomCustomer(), period);
            }
            Log.info("Complete!");
            return new JsonObject().put("result", "Generated " + count + " orders for random customers");
        }
    }

    private void createOrder(Customer customer, String period) {
        Duration duration = DurationConverter.parseDuration(period);
        int numProducts = random.nextInt(5) + 1;
        Set<Product> productSet = new HashSet<>();
        while (productSet.size() < numProducts) {
            Product p = productHolder.products.get(random.nextInt(productHolder.products.size() - 1));
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
                .withTimestamp(randomInstant(duration))
                .withShippingAddress(shippingAddress)
                .withOrderLineItems(lineItems)
                .build();
        orderService.createOrder(order);
    }

    private Instant randomInstant(Duration duration) {
        Instant now = Instant.now();
        Instant start = now.minus(duration);
        long startMillis = start.toEpochMilli();
        long endMillis = now.toEpochMilli();
        long randomMillis = startMillis + (long) (random.nextDouble() * (endMillis - startMillis + 1));
        return Instant.ofEpochMilli(randomMillis);
    }

    private Customer randomCustomer() {
        return customerHolder.customers.get(random.nextInt(customerHolder.customers.size() - 1));
    }

}
