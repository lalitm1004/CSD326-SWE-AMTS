# Pending SRS Implementation Tasks

## 1. Core Functionality Tracking
- [x] **FR-CL1: Auditorium Layout Initialization** - Need a text-based format parser & startup loader to declaratively inject seat limits/layouts into the system.
- [ ] **FR-SCS5: Allocate Complimentary and VIP Seats** - Extend the domain configuration to designate specific seats as Complimentary or VIP, ensuring they are excluded from ordinary/balcony ticket sale pools.
- [ ] **FR-TS5 & FR-TS6: Ticket Cancellation State Management** - Ticket cancellation currently only logs a refund but fails to return the underlying seat to an "available" state in the system. It also needs to explicitly invalidate the associated sales agent's commission (FR-TS6).
- [ ] **FR-ET4: Generate Yearly Balance Sheet** - `ExpenseTrackingController.getBalanceSheetsByYear` returns a list of individual balance sheets; the SRS requires logic to auto-generate a single *consolidated* yearly balance sheet document.