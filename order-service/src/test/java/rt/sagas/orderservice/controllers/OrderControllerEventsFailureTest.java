package rt.sagas.orderservice.controllers;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.orderservice.JmsReceiver;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.repositories.OrderRepository;
import rt.sagas.orderservice.services.OrderEventsSender;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerEventsFailureTest extends AbstractOrderControllerTest {

    @Autowired
    private JmsReceiver jmsReceiver;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderEventsSender orderEventsSenderSpy;

    @After
    public void tearDown() {
        doAnswer(invocationOnMock -> {
            return invocationOnMock.callRealMethod();
        }).when(orderEventsSenderSpy).sendOrderCreatedEvent(any(Order.class));
    }

    @Test
    public void testOrderCreatedEventIsNotSentWhenTransactionIsRolledBack() throws Exception {

        doAnswer(invocationOnMock -> {
            invocationOnMock.callRealMethod();
            throw new RuntimeException();
        }).when(orderEventsSenderSpy).sendOrderCreatedEvent(any(Order.class));

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(
                        new Order(17L, "1234567890123456"))))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        assertThat(jmsReceiver.pollEvent(5000L), is(nullValue()));
        assertThat(orderRepository.count(), is(0L));
    }

    @Test
    public void testOrderCreatedEventIsSentWhenTransactionIsNotRolledBack() throws Exception {

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(
                        new Order(17L, "1234567890123456"))))
                .andExpect(status().is(HttpStatus.CREATED.value()));

        OrderCreatedEvent orderCreated = jmsReceiver.pollEvent();
        assertThat(orderCreated, is(notNullValue()));
        assertThat(orderCreated.getUserId(), is(17L));

        assertThat(orderRepository.count(), is(1L));
        assertThat(orderRepository.findById(orderCreated.getOrderId()).get().getCartNumber(),
                is("1234567890123456"));
    }

}

