package rt.sagas.order.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.order.JmsOrderCreatedEventReceiver;
import rt.sagas.order.entities.Order;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerEventsTest extends AbstractOrderControllerTest {

    @Autowired
    private JmsOrderCreatedEventReceiver jmsOrderCreatedEventReceiver;

    @Test
    public void testOrderCreatedEventSentOnOrderCreation() throws Exception {

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(
                        new Order(17L, "1234567890123456"))))
                .andExpect(status().is(HttpStatus.CREATED.value()));

        OrderCreatedEvent orderCreatedEvent = jmsOrderCreatedEventReceiver.pollEvent();
        assertThat(orderCreatedEvent, is(notNullValue()));
        assertThat(orderCreatedEvent.getUserId(), is(17L));
        assertThat(orderCreatedEvent.getCartNumber(), is("1234567890123456"));
        assertThat(orderCreatedEvent.getOrderId(), is(notNullValue()));

    }

}

