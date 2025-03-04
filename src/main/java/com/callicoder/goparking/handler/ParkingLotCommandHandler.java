package com.callicoder.goparking.handler;

import static com.callicoder.goparking.utils.MessageConstants.*;

import com.callicoder.goparking.domain.Car;
import com.callicoder.goparking.domain.ParkingLot;
import com.callicoder.goparking.domain.ParkingSlot;
import com.callicoder.goparking.domain.Ticket;
import com.callicoder.goparking.exceptions.ParkingLotFullException;
import com.callicoder.goparking.exceptions.SlotNotFoundException;
import com.callicoder.goparking.utils.StringUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParkingLotCommandHandler {

    private ParkingLot parkingLot;

    public void createParkingLot(int numSlots) {
        if (isParkingLotCreated()) {
            System.out.println(PARKING_LOT_ALREADY_CREATED);
            return;
        }

        try {
            parkingLot = new ParkingLot(numSlots);
            System.out.println(
                String.format(PARKING_LOT_CREATED_MSG, parkingLot.getNumSlots())
            );
        } catch (IllegalArgumentException ex) {
            System.out.println("Bad input: " + ex.getMessage());
        }
    }

    public void park(String registrationNumber, String color) {
        if (!isParkingLotCreated()) {
            System.out.println(PARKING_LOT_NOT_CREATED);
            return;
        }
        boolean alreadyParked=parkingLot.isVehicleAlreadyParked(registrationNumber);
        if(alreadyParked){
            System.out.println(DUPLICATE_VEHICLE_MESSAGE);
            return ;
        }
        try {
            Car car = new Car(registrationNumber, color);
            Ticket ticket = parkingLot.reserveSlot(car);
            System.out.println(
                String.format(
                    PARKING_SLOT_ALLOCATED_MSG,
                    ticket.getSlotNumber()
                )
            );
        } catch (IllegalArgumentException ex) {
            System.out.println("Bad input: " + ex.getMessage());
        } catch (ParkingLotFullException ex) {
            System.out.println(ex.getMessage());
        }
    }


    public void leave(String slotNumber){
        if(!isParkingLotCreated()){
            System.out.println(PARKING_LOT_NOT_CREATED);
        }else{
            Integer slot=Integer.parseInt(slotNumber);
            if(slot<0){
                System.out.println("invalid slot "+slot);
                return ;
            }
            ParkingSlot parkingSlot=parkingLot.leaveSlot(slot);
            System.out.println(
                String.format(PARKING_LOT_FREE_MESSAGE, parkingSlot.getSlotNumber())
            );          
        }
    }
    
    public void slot_numbers_for_cars_with_colour(String color){
        if(!isParkingLotCreated()){
            System.out.println(PARKING_LOT_NOT_CREATED);
        }else{
            List<Integer>slotNumbers=parkingLot.getSlotNumbersByColor(color);            
            for(Integer slot:slotNumbers){
                System.out.println(SLOT_NO+slot);
            }
        }
    }
    public void registration_numbers_for_cars_with_colour(String color){
        if(!isParkingLotCreated()){
            System.out.println(PARKING_LOT_NOT_CREATED);
        }else{
            List<String>registrationNumbers=parkingLot.getRegistrationNumbersByColor(color);
            if(registrationNumbers.size()>0){
                System.out.println("Slot numbers for "+color+" are:");
                for(String number:registrationNumbers){
                    System.out.println(number);
                }    
            }else{
                System.out.println("no registration number found");
            }
        }
    }
    public void slot_number_for_registration_number(String registrationNumber){
        if(!isParkingLotCreated()){
            System.out.println(PARKING_LOT_NOT_CREATED);            
        }else{
            Optional<Integer>slotNumber=parkingLot.getSlotNumberByRegistrationNumber(registrationNumber);
            if(slotNumber.isEmpty()){
                System.out.println(SLOT_NUMBER_NOT_FOUND+" "+registrationNumber);
            }else{
                System.out.println(SLOT_NO+slotNumber.get());
            }
        }
    }
    public void status() {
        if (!isParkingLotCreated()) {
            System.out.println(PARKING_LOT_NOT_CREATED);
            return;
        }

        System.out.println(SLOT_NO + "    " + REGISTRATION_NO + "    " + Color);
        parkingLot
            .getOccupiedSlots()
            .forEach(
                parkingSlot -> {
                    System.out.println(
                        StringUtils.rightPadSpaces(
                            Integer.toString(parkingSlot.getSlotNumber()),
                            SLOT_NO.length()
                        ) +
                        "    " +
                        StringUtils.rightPadSpaces(
                            parkingSlot.getCar().getRegistrationNumber(),
                            REGISTRATION_NO.length()
                        ) +
                        "    " +
                        parkingSlot.getCar().getColor()
                    );
                }
            );
    }

    private boolean isParkingLotCreated() {
        if (parkingLot == null) {
            return false;
        }
        return true;
    }
}
