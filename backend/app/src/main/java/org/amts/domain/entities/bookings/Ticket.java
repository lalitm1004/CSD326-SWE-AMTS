package org.amts.domain.entities.bookings;

import java.util.UUID;

public class Ticket {
  private UUID id;
  private UUID showId;
  private UUID seatId;
  private UUID bookingId;
  private boolean isRefunded;

  public Ticket(
      UUID id,
      UUID showId,
      UUID seatId,
      UUID bookingId) {

    this.id = id;
    this.showId = showId;
    this.seatId = seatId;
    this.bookingId = bookingId;

    this.isRefunded = false;
  }

  public void refund() {
    if (this.isRefunded) {
      throw new IllegalStateException("Ticket already refunded");
    }
    this.isRefunded = true;
  }

  // getters
  public UUID getId() {
    return id;
  }

  public UUID getShowId() {
    return showId;
  }

  public UUID getSeatId() {
    return seatId;
  }

  public UUID getBookingId() {
    return bookingId;
  }

  public boolean isRefunded() {
    return isRefunded;
  }
}
