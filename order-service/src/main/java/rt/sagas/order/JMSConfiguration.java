package rt.sagas.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JMSConfiguration {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Bean
    @DependsOn({ "transactionManager" })
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setMessageConverter(messageConverter());
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setSessionAcknowledgeMode(0);
        return jmsTemplate;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(atomikosJmsConnectionFactory());
        cachingConnectionFactory.setSessionCacheSize(50);
        return cachingConnectionFactory;
    }

    @Bean/*(initMethod = "init", destroyMethod = "close")*/
    public AtomikosConnectionFactoryBean atomikosJmsConnectionFactory() {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(10000L);
        redeliveryPolicy.setRedeliveryDelay(10000L);
        redeliveryPolicy.setUseExponentialBackOff(false);
        redeliveryPolicy.setMaximumRedeliveries(50);

        ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
        activeMQXAConnectionFactory.setBrokerURL( brokerUrl );
        activeMQXAConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);

        AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
        atomikosConnectionFactoryBean.setUniqueResourceName("activeMQBroker");
        atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory);
        atomikosConnectionFactoryBean.setLocalTransactionMode(false);
        return atomikosConnectionFactoryBean;
    }

    @Bean("jmsListenerContainerFactory")
    @DependsOn({ "transactionManager" })
    public JmsListenerContainerFactory queueListenerFactory(
            @Autowired PlatformTransactionManager transactionManager) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory( atomikosJmsConnectionFactory() );
        factory.setTransactionManager( transactionManager );
        factory.setMessageConverter( messageConverter() );
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(0);
        return factory;
    }

//    @Bean("jmsListenerContainerFactory")
//    public JmsListenerContainerFactory queueListenerFactory() {
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        factory.setMessageConverter(messageConverter());
//        factory.setConnectionFactory(cachingConnectionFactory());
//        return factory;
//    }
//

    protected MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    protected ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

}
