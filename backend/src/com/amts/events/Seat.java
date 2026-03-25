com.amts.events;

import java.util.UUID;

public class Seat {

    public enum SeatType {
        ORDINARY,
        BALCONY
    }

    private UUID id;
    private String number;
    private SeatType seatType;

    public Seat(UUID id, String number, SeatType seatType) {
        this.id = id;
        this.number = number;
        this.seatType = seatType;
    }

    // getters
    public UUID getId() { return id; }
    public String getNumber() { return number; }
    public SeatType getSeatType() { return seatType; }

    // setters
    public void setNumber(String number) { this.number = number; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }
}
