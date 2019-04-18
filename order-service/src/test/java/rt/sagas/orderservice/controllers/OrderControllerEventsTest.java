package rt.sagas.orderservice.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.orderservice.JmsReceiver;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.events.OrderCreated;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerEventsTest extends AbstractOrderControllerTest {

    @Autowired
    private JmsReceiver jmsReceiver;

    @Test
    public void testOrderCreatedEventSentOnOrderCreation() throws Exception {

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(
                        new Order(17L, "1234567890123456"))))
                .andExpect(status().is(HttpStatus.CREATED.value()));

        OrderCreated orderCreated = (OrderCreated) jmsReceiver.pollEvent();
        assertThat(orderCreated, is(notNullValue()));
        assertThat(orderCreated.getUserId(), is(17L));
        assertThat(orderCreated.getCartNumber(), is("1234567890123456"));
        assertThat(orderCreated.getOrderId(), is(notNullValue()));

    }

}

