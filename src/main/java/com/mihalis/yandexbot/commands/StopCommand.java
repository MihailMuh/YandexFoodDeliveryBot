package com.mihalis.yandexbot.commands;

import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.telegram.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StopCommand extends BaseCommand {
    private final YandexFoodService yandexFoodService;

    private final AddressRepository addressRepository;

    public StopCommand(YandexFoodService yandexFoodService, AddressRepository addressRepository) {
        super("stop");
        this.yandexFoodService = yandexFoodService;
        this.addressRepository = addressRepository;
    }

    @Override
    public void answer(Bot bot, Message message) {
        yandexFoodService.deleteAddress(message.getChatId());
        addressRepository.deleteAddress(message.getChatId());

        bot.executeAsync("Операция отменена", message);
    }
}
