package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.service.YandexFoodService;
import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;

@Component
public class StopCommand extends Command {
    private final YandexFoodService yandexFoodService;

    private final AddressRepository addressRepository;

    public StopCommand(YandexFoodService yandexFoodService, AddressRepository addressRepository) {
        super("stop");
        this.yandexFoodService = yandexFoodService;
        this.addressRepository = addressRepository;
    }

    @Override
    public void answer(Parcel parcel) {
        yandexFoodService.deleteAddress(parcel.getUserId());
        addressRepository.deleteAddress(parcel.getUserId());

        parcel.answerAsync("Операция отменена");
    }
}
