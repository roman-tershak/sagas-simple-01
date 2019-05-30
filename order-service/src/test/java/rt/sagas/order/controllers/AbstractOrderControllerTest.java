package rt.sagas.order.controllers;

import com.atomikos.jdbc.AbstractDataSourceBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

@AutoConfigureMockMvc
public class AbstractOrderControllerTest {

    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private AbstractDataSourceBean dataSource;

    @Before
    public void setUp() throws Exception {
        dataSource.init();
    }

    @After
    public void tearDown() throws Exception {
//        AbstractDataSourceBean dataSource = (AbstractDataSourceBean) ctx.getBean("dataSource");
        dataSource.close();
//        XATransactionalResource jmsConnectionFactory = (XATransactionalResource) ctx.getBean("jmsConnectionFactory");
//        jmsConnectionFactory.close();
    }

    @Autowired
    protected MockMvc mvc;

    protected static String convertToJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    protected static <T> T convertToObject(String json, Class<T> clazz) throws IOException {
        return new ObjectMapper().readValue(json, clazz);
    }
}
