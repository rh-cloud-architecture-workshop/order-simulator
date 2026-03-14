package org.acme.retail.order.simulator.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.retail.order.simulator.dto.OrderDto;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/services/order")
@RegisterRestClient(configKey="order-service")
public interface OrderService {

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response placeOrder(OrderDto order);

}
