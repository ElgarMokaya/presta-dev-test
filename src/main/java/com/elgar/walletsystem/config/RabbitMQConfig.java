package com.elgar.walletsystem.config;

import com.elgar.walletsystem.config.properties.QueueConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;


@Configuration
@AllArgsConstructor
public class RabbitMQConfig {

    private final QueueConfiguration queueConfiguration;

    @Bean
    public Queue walletQueue(){
       return new Queue(
       queueConfiguration.getWallet().getName());
    }
    @Bean
    public TopicExchange walletExchange(){
        return new TopicExchange(queueConfiguration.getWallet().getExchange());
    }
    @Bean
    public Binding walletBinding(){
        return BindingBuilder.bind(walletQueue()).to(walletExchange()).with(queueConfiguration.getWallet().getRoutingKey());
    }
    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    AmqpTemplate template(ConnectionFactory connectionFactory){
       final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
       rabbitTemplate.setMessageConverter(messageConverter());
       return rabbitTemplate;
    }
}
