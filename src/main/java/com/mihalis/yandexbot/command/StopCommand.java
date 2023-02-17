package com.mihalis.yandexbot.command;

import com.mihalis.yandexbot.repository.AddressRepository;
import com.mihalis.yandexbot.telegram.Parcel;
import org.springframework.stereotype.Component;

@Component
public class StopCommand extends Command {
    private final AddressRepository addressRepository;

    public StopCommand(AddressRepository addressRepository) {
        super("stop");
        this.addressRepository = addressRepository;
    }

    @Override
    public void answer(Parcel parcel) {
        addressRepository.deleteAddress(parcel.getUserId());

        parcel.answerAsync("Операция отменена");
    }
}
