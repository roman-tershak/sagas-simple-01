package rt.sagas.order.controllers;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.order.AbstractOrderTest;
import rt.sagas.order.JmsOrderCreatedEventReceiver;
import rt.sagas.order.entities.Order;
import rt.sagas.order.repositories.OrderRepository;
import rt.sagas.order.services.OrderEventsSender;
import rt.sagas.testutils.TestRuntimeException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerEventsFailureTest extends AbstractOrderTest {

    @Autowired
    private JmsOrderCreatedEventReceiver jmsOrderCreatedEventReceiver;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderEventsSender orderEventsSenderSpy;

    @After
    public void tearDown() throws Exception {
        doAnswer(invocationOnMock -> {
            return invocationOnMock.callRealMethod();
        }).when(orderEventsSenderSpy).sendOrderCreatedEvent(any(Order.class));

        orderRepository.deleteAll();
    }

    @Test
    public void testOrderCreatedEventIsNotSentWhenTransactionIsRolledBack() throws Exception {

        doAnswer(invocationOnMock -> {
            invocationOnMock.callRealMethod();
            throw new TestRuntimeException("Intended exception - ignore");
        }).when(orderEventsSenderSpy).sendOrderCreatedEvent(any(Order.class));

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(
                        new Order(11L, "123212321232123212321"))))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        assertThat(jmsOrderCreatedEventReceiver.pollEvent(
                e-> e.getCartNumber().equals("123212321232123212321"),
                10000L), is(nullValue()));
        assertThat(orderRepository.count(), is(0L));
    }

    @Test
    public void testOrderCreatedEventIsSentWhenTransactionIsNotRolledBack() throws Exception {

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(
                        new Order(11L, "123456784444444444"))))
                .andExpect(status().is(HttpStatus.CREATED.value()));

        OrderCreatedEvent orderCreatedEvent = jmsOrderCreatedEventReceiver.pollEvent(
                e -> e.getCartNumber().equals("123456784444444444"), 20000L);
        assertThat(orderCreatedEvent, is(notNullValue()));
        assertThat(orderCreatedEvent.getUserId(), is(11L));

        assertThat(orderRepository.count(), is(1L));
        Order orderCreated = orderRepository.findAll().iterator().next();
        assertThat(orderCreated.getCartNumber(),
                is("123456784444444444"));
    }

}

