# Pending SRS Implementation Tasks

## 1. Core Functionality Tracking
- [ ] **FR-CL1: Auditorium Layout Initialization** - Need a text-based format parser & startup loader to declaratively inject seat limits/layouts into the system.
- [ ] **FR-SCS5: Complimentary and VIP Seats** - Implement specific blocking flags ensuring certain seats are excluded from general sales pools.
- [ ] **FR-TS2: Coupon Purchasing Charge** - Restructure the `/coupon` logic so it charges spectators a flat 1000 INR upon purchasing the 10% discount coupon instead of generating it freely.

## 2. Non-Functional / Safety Rules
- [ ] **NFR-SAFE-1: Temporary Seat Locking** - Implement a Redis or local cache 5-minute timeout lock for seats that have been clicked by a spectator, effectively preventing duplicate cart selections globally.
- [ ] **NFR-DOC-1: Standardized API Docs** - Draft comprehensive Swagger or explicit Markdown API documentation specifying paths, schemas, and usage examples.
- [ ] **Testing:** Create comprehensive unit, functional, & integration test layouts!
