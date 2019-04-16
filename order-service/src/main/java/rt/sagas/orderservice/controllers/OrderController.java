package rt.sagas.orderservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.services.OrderService;

import javax.validation.Valid;

@RestController("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Order create(@RequestBody @Valid Order order) {
        return orderService.createOrder(order);
    }

}
