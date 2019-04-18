package rt.sagas.orderservice.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.orderservice.Application;
import rt.sagas.orderservice.entities.Order;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static rt.sagas.orderservice.entities.OrderStatus.NEW;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrderControllerTest extends AbstractOrderControllerTest {

    @Test
    public void testOrderCreation() throws Exception {
        long userId = 17L;
        String cartNumber = "1234567890123456";
        Order order = new Order(userId, cartNumber);

        String responseString = mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(order)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        Order orderCreated = convertToObject(responseString, Order.class);
        assertThat(orderCreated.getId(), is(notNullValue()));
        assertThat(orderCreated.getUserId(), is(userId));
        assertThat(orderCreated.getCartNumber(), is(cartNumber));
        assertThat(orderCreated.getStatus(), is(NEW));
    }

    @Test
    public void testOrderCreationFailsIfUserIdIsInvalid() throws Exception {

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(new Order(null, "1234567890123456"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", equalTo("userId is mandatory")));

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(new Order(-1L, "1234567890123456"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", equalTo("userId must be positive")));

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(new Order(0L, "1234567890123456"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", equalTo("userId must be positive")));
    }

    @Test
    public void testOrderCreationFailsIfCartNumberIsInvalid() throws Exception {

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(new Order(7L, null))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", equalTo("cartNumber must not be blank")));

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(new Order(8L, " "))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", equalTo("cartNumber must not be blank")));

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertToJson(new Order(8L, ""))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", equalTo("cartNumber must not be blank")));
    }

}
