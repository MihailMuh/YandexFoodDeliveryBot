package com.mihalis.yandexbot.beans;

import com.mihalis.yandexbot.model.Address;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class AddressBeans {
    @Bean(name = "address")
    @Scope(SCOPE_PROTOTYPE)
    public Address getAddress() {
        return Address.of("Челябинск, Пионерская, 3А");
    }

    @Bean(name = "emptyAddress")
    @Scope(SCOPE_PROTOTYPE)
    public Address getEmptyAddress() {
        return Address.empty();
    }
}
