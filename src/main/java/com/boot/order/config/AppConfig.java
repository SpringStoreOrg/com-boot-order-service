package com.boot.order.config;

import com.boot.order.client.RetrieveMessageErrorDecoder;
import com.boot.order.dto.AddressDTO;
import com.boot.order.dto.OrderDTO;
import com.boot.order.dto.OrderEntryDTO;
import com.boot.order.model.Order;
import com.boot.order.model.OrderAddress;
import com.boot.order.model.OrderEntry;
import feign.codec.ErrorDecoder;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.velocity.VelocityEngineFactory;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class AppConfig {
    @Bean
    public VelocityEngine getVelocityEngine() throws VelocityException, IOException {
        VelocityEngineFactory velocityEngineFactory = new VelocityEngineFactory();
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
 
        velocityEngineFactory.setVelocityProperties(props);
        return velocityEngineFactory.createVelocityEngine();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Order.class, OrderDTO.class);
        modelMapper.typeMap(OrderEntry.class, OrderEntryDTO.class);
        modelMapper.typeMap(OrderAddress.class, AddressDTO.class);

        return modelMapper;
    }
}
