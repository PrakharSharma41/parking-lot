package com.callicoder.goparking.interaction.commands;

import com.callicoder.goparking.exceptions.InvalidParameterException;
import com.callicoder.goparking.handler.ParkingLotCommandHandler;

public class SlotNumberForRegistrationNumber implements Command{
        private ParkingLotCommandHandler parkingLotCommandHandler;

    public SlotNumberForRegistrationNumber(ParkingLotCommandHandler parkingLotCommandHandler) {
        this.parkingLotCommandHandler = parkingLotCommandHandler;
    }
    @Override
    public String helpText() {
        return "slot_number_for_registration_number <registration number>";
    }    
    @Override
    public void execute(String[] params) throws InvalidParameterException {
        if (params.length < 1) {
            throw new InvalidParameterException(
                "Expected 1 parameter <registration number>"
            );
        }

        parkingLotCommandHandler.slot_number_for_registration_number(params[0]);
    }     
}
