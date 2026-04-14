# Pending SRS Implementation Tasks

## 1. Core Functionality Tracking
- [x] **FR-CL1: Auditorium Layout Initialization** - Need a text-based format parser & startup loader to declaratively inject seat limits/layouts into the system.
- [x] **FR-SCS5: Allocate Complimentary and VIP Seats** - Extend the domain configuration to designate specific seats as Complimentary or VIP, ensuring they are excluded from ordinary/balcony ticket sale pools.
- [x] **FR-SCS5: Allocate Complimentary and VIP Seats (API)** - Added `updateSeatDesignation` and `getSeatsByShow` use case methods; Show Manager / Auditorium Secretary can designate seats as VIP or COMPLIMENTARY via `PATCH /api/show/seat-designation`; ticket purchase already enforces exclusion of non-ORDINARY seats.
- [x] **FR-TS5 & FR-TS6: Ticket Cancellation State Management** - On cancellation, `seat_id` is set to NULL on the ticket row (FR-TS5) and a `commission_invalidation` record is inserted for offline bookings (FR-TS6).
- [x] **FR-ET4: Generate Yearly Balance Sheet** - `ExpenseTrackingController.getBalanceSheetsByYear` returns a list of individual balance sheets; the SRS requires logic to auto-generate a single *consolidated* yearly balance sheet document.