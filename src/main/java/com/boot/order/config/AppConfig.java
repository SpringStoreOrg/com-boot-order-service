package com.boot.order.config;

import com.boot.order.client.RetrieveMessageErrorDecoder;
import com.boot.order.dto.*;
import com.boot.order.model.*;
import feign.codec.ErrorDecoder;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.modelmapper.*;
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

        modelMapper.typeMap(OrderDTO.class, Order.class).setPostConverter(context -> {
            OrderDTO s = context.getSource();
            Order d = context.getDestination();
            if(s.getShippingAddress() instanceof PersonAddressDTO){
                d.setShippingAddress(modelMapper.map(s.getShippingAddress(), OrderPersonAddress.class));
            }
            if(s.getShippingAddress() instanceof CompanyAddressDTO){
                d.setShippingAddress(modelMapper.map(s.getShippingAddress(), OrderCompanyAddress.class));
            }
            if(s.getReceiptAddress() instanceof PersonAddressDTO){
                d.setReceiptAddress(modelMapper.map(s.getReceiptAddress(), OrderPersonAddress.class));
            }
            if(s.getReceiptAddress() instanceof CompanyAddressDTO){
                d.setReceiptAddress(modelMapper.map(s.getReceiptAddress(), OrderCompanyAddress.class));
            }
            return d;
        });

        modelMapper.typeMap(Order.class, OrderGetDetailsDTO.class).setPostConverter(context -> {
            Order s = context.getSource();
            OrderGetDetailsDTO d = context.getDestination();
            if(s.getShippingAddress() instanceof OrderPersonAddress){
                d.setShippingAddress(modelMapper.map(s.getShippingAddress(), PersonAddressDTO.class));
            }
            if(s.getShippingAddress() instanceof OrderCompanyAddress){
                d.setShippingAddress(modelMapper.map(s.getShippingAddress(), CompanyAddressDTO.class));
            }
            if(s.getReceiptAddress() instanceof OrderPersonAddress){
                d.setReceiptAddress(modelMapper.map(s.getReceiptAddress(), PersonAddressDTO.class));
            }
            if(s.getReceiptAddress() instanceof OrderCompanyAddress){
                d.setReceiptAddress(modelMapper.map(s.getReceiptAddress(), CompanyAddressDTO.class));
            }
            return d;
        });

        modelMapper.typeMap(Order.class, AdminOrderGetDTO.class)
                .addMapping(s->s.getShippingAddress().getRecipient(), AdminOrderGetDTO::setRecipient);

        modelMapper.typeMap(OrderEntry.class, OrderEntryDTO.class)
                .addMapping(OrderEntry::getProductSlug, OrderEntryDTO::setSlug)
                .addMapping(OrderEntry::getProductName, OrderEntryDTO::setName);
        modelMapper.typeMap(OrderEntryDTO.class, OrderEntry.class)
                .addMapping(OrderEntryDTO::getSlug, OrderEntry::setProductSlug)
                .addMapping(OrderEntryDTO::getName, OrderEntry::setProductName);
        modelMapper.typeMap(OrderEntryDTO.class, StockDTO.class)
                .addMapping(OrderEntryDTO::getSlug, StockDTO::setProductSlug);

        modelMapper.typeMap(CompanyAddressDTO.class, OrderCompanyAddress.class);
        modelMapper.typeMap(PersonAddressDTO.class, OrderPersonAddress.class);

        return modelMapper;
    }
}
