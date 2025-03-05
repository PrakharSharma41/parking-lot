package com.callicoder.goparking.domain;

import com.callicoder.goparking.exceptions.ParkingLotFullException;
import com.callicoder.goparking.exceptions.SlotNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class ParkingLot {

    private final int numSlots;
    private final int numFloors;
    private SortedSet<ParkingSlot> availableSlots = new TreeSet<>();
    private Set<ParkingSlot> occupiedSlots = new HashSet<>();
    private Map<Integer, ParkingSlot> slotCarMapping;
    private Map<String, Integer> registrationToSlotMapping;
    private Map<String, ArrayList<String>> colorToRegistrationMapping;
    public ParkingLot(int numSlots) {
        if (numSlots <= 0) {
            throw new IllegalArgumentException(
                "Number of slots in the Parking Lot must be greater than zero."
            );
        }

        // Assuming Single floor since only numSlots are specified in the input.
        this.numSlots = numSlots;
        this.numFloors = 1;

        for (int i = 0; i < numSlots; i++) {
            ParkingSlot parkingSlot = new ParkingSlot(i + 1, 1);
            this.availableSlots.add(parkingSlot);
        }
        this.colorToRegistrationMapping = new HashMap<String, ArrayList<String>>();
        this.slotCarMapping = new HashMap<Integer, ParkingSlot>();
        this.registrationToSlotMapping = new HashMap<String, Integer>();        
    }

    public boolean isVehicleAlreadyParked(String registrationNumber){
        if(registrationToSlotMapping.containsKey(registrationNumber))return true;
        return false;
    }
    public synchronized Ticket reserveSlot(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car must not be null");
        }

        if (this.isFull()) {
            throw new ParkingLotFullException();
        }

        ParkingSlot nearestSlot = this.availableSlots.first();

        nearestSlot.reserve(car);
        this.availableSlots.remove(nearestSlot);
        this.occupiedSlots.add(nearestSlot);

        slotCarMapping.put(nearestSlot.getSlotNumber(), nearestSlot);
        registrationToSlotMapping.put(car.getRegistrationNumber(),nearestSlot.getSlotNumber());
        
        if(colorToRegistrationMapping.containsKey(car.getColor())==false){
            colorToRegistrationMapping.put(car.getColor(),new ArrayList<String>());
        }
        ArrayList<String>registrationNumbers=colorToRegistrationMapping.get(car.getColor());
        registrationNumbers.add(car.getRegistrationNumber());
        colorToRegistrationMapping.put(car.getColor(), registrationNumbers);
        return new Ticket(
            nearestSlot.getSlotNumber(),
            car.getRegistrationNumber(),
            car.getColor()
        );
    }

    public ParkingSlot leaveSlot(int slotNumber) {
        ParkingSlot slot=slotCarMapping.get(slotNumber);
        if(slot==null){
            throw new SlotNotFoundException(slotNumber);
        }

        availableSlots.add(slot);
        occupiedSlots.remove(slot);
        slotCarMapping.remove(slotNumber);
        Car car=slot.getCar();
        slot.clear();
        String registrationNumber=car.getRegistrationNumber();                
        String color=car.getColor();
        registrationToSlotMapping.remove(registrationNumber);
        ArrayList<String>registrationList=colorToRegistrationMapping.get(color);
        
        if (registrationList.contains(registrationNumber)) {
            registrationList.remove(registrationNumber);
        }                
        return slot;
    }

    public boolean isFull() {
        return this.availableSlots.isEmpty();
    }

    public List<String> getRegistrationNumbersByColor(String color) {
        if(colorToRegistrationMapping == null){
            return null;
        }
        return colorToRegistrationMapping.get(color);
    }

    public List<Integer> getSlotNumbersByColor(String color) {
        List<Integer> slots = new ArrayList<Integer>();
        if(colorToRegistrationMapping == null){
            return null;
        }
        else if(colorToRegistrationMapping.get(color) != null){
            ArrayList<String> registrationList = colorToRegistrationMapping.get(color);
            for (int i = 0; i < registrationList.size(); i++) {
                slots.add(registrationToSlotMapping.get(registrationList.get(i)));
            }
            Collections.sort(slots);
            return slots;

        }else{
            return slots;
        }    
    }

    public Optional<Integer> getSlotNumberByRegistrationNumber(
        String registrationNumber
    ) {
        if(registrationToSlotMapping == null || registrationToSlotMapping.get(registrationNumber) == null){
            return Optional.empty();
        }
        else{
            return Optional.ofNullable(registrationToSlotMapping.get(registrationNumber));
        }        
    }

    public int getNumSlots() {
        return numSlots;
    }

    public int getNumFloors() {
        return numFloors;
    }

    public SortedSet<ParkingSlot> getAvailableSlots() {
        return availableSlots;
    }

    public Set<ParkingSlot> getOccupiedSlots() {
        return occupiedSlots;
    }
}
