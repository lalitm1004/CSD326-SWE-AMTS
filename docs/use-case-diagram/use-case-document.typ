// ═══════════════════════════════════════════════════════════════
// Use Case Document — Auditorium Management and Ticketing System
// ═══════════════════════════════════════════════════════════════

#set page(
  paper: "a4",

  header: context {
    let page-num = counter(page).at(here()).first()

    set text(10pt)
    if page-num > 1 [
      *Use Case Document — Auditorium Management and Ticketing System*
    ]

    h(1fr)

    [*#counter(page).display("1")*]
  },

  footer: [
    #set text(10pt)
    #block(width: 100%, align(center)[
      *CSD326 - Software Engineering: Group 20*
    ])
  ],

  numbering: "1"
)

// ─── Reusable table function ────────────────────────────────
#let use-case-table(
  id: "",
  title: "",
  description: "",
  primary-actor: "",
  secondary-actor: "",
  preconditions: "",
  postconditions: "",
  dependency: "",
  generalization: "",
  main-scenario: "",
  extensions: "",
  frequency: "",
) = {
  set text(10pt)
  table(
    columns: (auto, 1fr),
    stroke: 0.5pt + luma(180),
    fill: (col, _) => if col == 0 { luma(230) } else { none },
    align: (col, _) => if col == 0 { right } else { left },

    [*ID:*],                    [#id],
    [*Title:*],                 [#title],
    [*Description:*],           [#description],
    [*Primary Actor:*],         [#primary-actor],
    [*Secondary Actor:*],       [#secondary-actor],
    [*Preconditions:*],         [#preconditions],
    [*Postconditions:*],        [#postconditions],
    [*Dependency:*],            [#dependency],
    [*Generalization:*],        [#generalization],
    [*Main Success Scenario:*], [#main-scenario],
    [*Extensions or Alternate Flow:*], [#extensions],
    [*Frequency of Use:*],      [#frequency],
  )
}

// ─── Front page ─────────────────────────────────────────────
#context [
  #set align(right)
  #v(2em)

  #text(27pt, weight: "bold")[Use Case Document]

  #text(24pt, weight: "bold")[Auditorium Management and \ Ticketing System]

  #text(24pt, weight: "bold")[Group 20]

  #line(length: 100%, stroke: 2pt)

  #text(20pt, weight: "bold")[
    Jia Khot \
    Lalit Maurya \
    Rachit Kumar \
    Sonali Verma \
  ]

  #pagebreak()
]

// ═══════════════════════════════════════════════════════════════
//  UC1 — Register Account
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC1",
  title:           "Register Account",
  description:     "An unregistered user creates a new account on the AMTS by providing an email address and a password. Upon successful registration, the system assigns the default 'Spectator' role to the new account.",
  primary-actor:   "Unregistered User",
  secondary-actor: "—",
  preconditions:   "The user does not already possess an account in the system.",
  postconditions:  "A new user account exists in the system with the default 'Spectator' role assigned. The user can now log in.",
  dependency:      "—",
  generalization:  "—",
  main-scenario: [
    1. The unregistered user navigates to the registration page. \
    2. The user enters a valid email address and a password. \
    3. The system validates the email format and password strength. \
    4. The system creates the account and assigns the default 'Spectator' role. \
    5. The system confirms successful registration.
  ],
  extensions: [
    *3a.* Email already exists: The system displays an error and prompts the user to log in or use a different email. \
    *3b.* Invalid email format or weak password: The system displays a validation error.
  ],
  frequency:  "Moderate — whenever a new user signs up.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC2 — Login
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC2",
  title:           "Login",
  description:     "A registered user authenticates with the system using their email address and password to gain access to role-specific functionality.",
  primary-actor:   "Registered User",
  secondary-actor: "—",
  preconditions:   "The user has an existing registered account in the system.",
  postconditions:  "The user is authenticated and gains access to system functionality according to their assigned roles.",
  dependency:      "UC1 — Register Account (the user must have a registered account).",
  generalization:  "—",
  main-scenario: [
    1. The user navigates to the login page. \
    2. The user enters their registered email and password. \
    3. The system validates the credentials. \
    4. The system establishes an authenticated session. \
    5. The user is redirected to the dashboard corresponding to their assigned role(s).
  ],
  extensions: [
    *3a.* Invalid email or password: The system displays an authentication error and allows the user to retry. \
    *3b.* Account locked due to too many failed attempts: The system displays a lockout message.
  ],
  frequency:  "Very High — used every time a user accesses the system.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC3 — Assign / Revoke Roles
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC3",
  title:           "Assign / Revoke Roles",
  description:     "The Auditorium Secretary assigns or revokes predefined roles (Show Manager, Sales Agent, Financial Clerk, Spectator) for registered user accounts. The Auditorium Secretary role itself cannot be assigned or revoked through this use case.",
  primary-actor:   "Auditorium Secretary",
  secondary-actor: "—",
  preconditions:   "The Auditorium Secretary is logged in (includes UC2). The target user has an existing account.",
  postconditions:  "The target user's roles are updated and enforced immediately across all system functions.",
  dependency:      "«include» UC2 — Login",
  generalization:  "—",
  main-scenario: [
    1. The Auditorium Secretary navigates to the role management page. \
    2. The Auditorium Secretary searches for the target user account. \
    3. The system displays the target user's current roles. \
    4. The Auditorium Secretary assigns or revokes the desired role(s). \
    5. The system updates the user's roles and confirms the change.
  ],
  extensions: [
    *2a.* Target user account not found: The system displays an error. \
    *4a.* Attempt to assign/revoke the Auditorium Secretary role: The system denies the operation with an authorization error. \
    *4b.* Unauthorized user attempts to modify roles: The system denies the operation.
  ],
  frequency:  "Low — performed only when organizational role changes occur.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC4 — Assign Secretary Role
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC4",
  title:           "Assign Secretary Role",
  description:     "The President assigns or revokes the Auditorium Secretary role for a registered user account. This is a privileged operation restricted exclusively to the President.",
  primary-actor:   "President",
  secondary-actor: "—",
  preconditions:   "The President is logged in (includes UC2). The target user has an existing account.",
  postconditions:  "The Auditorium Secretary role is assigned to or revoked from the target user. The change takes effect immediately.",
  dependency:      "«include» UC2 — Login",
  generalization:  "Extends UC3 — Assign / Revoke Roles (specifically for the Auditorium Secretary role).",
  main-scenario: [
    1. The President navigates to the role management page. \
    2. The President searches for the target user account. \
    3. The system displays the target user's current roles. \
    4. The President assigns or revokes the Auditorium Secretary role. \
    5. The system updates the user's roles and confirms the change.
  ],
  extensions: [
    *2a.* Target user not found: The system displays an error. \
    *4a.* Non-President user attempts this operation: The system denies the request with an authorization error.
  ],
  frequency:  "Very Low — performed only during leadership transitions.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC5 — View Show Details
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC5",
  title:           "View Show Details",
  description:     "Any user (including unregistered users) can view the details of shows scheduled in the auditorium, including show name, description, dates, timings, seat availability, and pricing.",
  primary-actor:   "Unregistered User",
  secondary-actor: "—",
  preconditions:   "At least one show has been configured and scheduled in the system.",
  postconditions:  "The user has viewed the requested show information. No system state is modified.",
  dependency:      "—",
  generalization:  "—",
  main-scenario: [
    1. The user navigates to the show listing page. \
    2. The system displays a list of scheduled shows. \
    3. The user selects a show to view its details. \
    4. The system displays the show's name, description, schedule, seat availability, and pricing information.
  ],
  extensions: [
    *2a.* No shows are currently scheduled: The system displays an appropriate message indicating no shows are available.
  ],
  frequency:  "Very High — used by all users browsing shows.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC6 — Schedule Show
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC6",
  title:           "Schedule Show",
  description:     "The Auditorium Secretary or Show Manager schedules a new show in the system. The Auditorium Secretary creates the show entry and assigns a Show Manager. The Show Manager then enters show dates, timings, and the number of shows per day.",
  primary-actor:   "Auditorium Secretary",
  secondary-actor: "Show Manager",
  preconditions:   "The Auditorium Secretary / Show Manager is logged in. The auditorium layout has been initialized.",
  postconditions:  "A new show entry exists in the system with its scheduling details configured.",
  dependency:      "—",
  generalization:  "—",
  main-scenario: [
    1. The Auditorium Secretary navigates to show management and creates a new show entry. \
    2. The Auditorium Secretary assigns an authorized Show Manager to the show. \
    3. The Show Manager enters show dates, timings, and the number of shows per day. \
    4. The system validates the scheduling details (e.g., no time conflicts). \
    5. The system stores the show schedule and confirms creation.
  ],
  extensions: [
    *2a.* No Show Manager is available: The Auditorium Secretary must first assign the Show Manager role to a user (UC3). \
    *4a.* Scheduling conflict with an existing show: The system displays the conflict and prompts for resolution.
  ],
  frequency:  "Moderate — whenever a new show is planned.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC7 — Configure Show Details
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC7",
  title:           "Configure Show Details",
  description:     "The Show Manager configures the details of an existing show, including seat pricing for balcony and ordinary categories, seat count limits per category, show name, description, and other show parameters.",
  primary-actor:   "Show Manager",
  secondary-actor: "—",
  preconditions:   "The Show Manager is logged in. A show entry has been created and assigned to this Show Manager.",
  postconditions:  "The show's configuration parameters are updated and ready for ticket sales.",
  dependency:      "UC6 — Schedule Show (a show must be scheduled first).",
  generalization:  "—",
  main-scenario: [
    1. The Show Manager navigates to the show configuration page for their assigned show. \
    2. The Show Manager enters or modifies the show name, description, and other details. \
    3. The Show Manager sets seat prices for balcony and ordinary categories. \
    4. The Show Manager defines the number of seats available for sale per category. \
    5. The system validates and saves the configuration.
  ],
  extensions: [
    *3a.* Invalid pricing values entered: The system displays a validation error. \
    *4a.* Seat limit exceeds auditorium capacity: The system rejects the configuration with an error. \
    *5a.* «extend» UC9 — Allocate VIP / Comp. Seats: Optionally, the Show Manager allocates VIP and complimentary seats.
  ],
  frequency:  "Moderate — once per show, potentially revised before sales begin.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC9 — Allocate VIP / Complimentary Seats
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC9",
  title:           "Allocate VIP / Complimentary Seats",
  description:     "The Show Manager allocates specific seats as VIP or complimentary for a show. These seats are excluded from public sale and reserved for designated guests.",
  primary-actor:   "Show Manager",
  secondary-actor: "—",
  preconditions:   "The Show Manager is logged in. The show has been created and its configuration (UC7) is in progress or complete.",
  postconditions:  "The designated seats are marked as VIP or complimentary and removed from the pool of seats available for sale.",
  dependency:      "«extend» UC7 — Configure Show Details",
  generalization:  "—",
  main-scenario: [
    1. The Show Manager opens the seat allocation interface for the assigned show. \
    2. The Show Manager selects specific seats on the auditorium layout. \
    3. The Show Manager designates the selected seats as VIP or complimentary. \
    4. The Show Manager enters guest details for the allocated seats. \
    5. The system marks the seats accordingly and excludes them from public sale.
  ],
  extensions: [
    *2a.* Selected seat is already sold: The system prevents allocation and displays an error. \
    *3a.* Seat limit for VIP/complimentary exceeded: The system warns the Show Manager.
  ],
  frequency:  "Low — once per show during configuration.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC10 — Purchase Ticket Online
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC10",
  title:           "Purchase Ticket Online",
  description:     "A spectator purchases a ticket for a show directly through the online system. The spectator selects a show, chooses seat type (balcony or ordinary), completes the online payment, and receives a booking confirmation with a generated ticket.",
  primary-actor:   "Spectator",
  secondary-actor: "—",
  preconditions:   "The spectator is logged in. A show is scheduled with seats available for sale.",
  postconditions:  "A ticket is generated and assigned to the spectator. The selected seat is marked as sold. Payment is recorded.",
  dependency:      "—",
  generalization:  "—",
  main-scenario: [
    1. The spectator browses available shows and selects one. \
    2. The spectator selects seat type and quantity. \
    3. The system displays the total price. \
    4. The spectator proceeds to the payment page and completes the online payment. \
    5. The system processes the payment, books the seat(s), and generates a booking confirmation and ticket(s).
  ],
  extensions: [
    *2a.* No seats available for the selected category: The system informs the spectator. \
    *4a.* Payment fails: The system releases the temporarily locked seats and displays a payment error. \
    *4b.* «extend» UC11 — Purchase Coupon: The spectator optionally applies a coupon for a 10% discount.
  ],
  frequency:  "Very High — primary means of ticket purchase.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC11 — Purchase Coupon
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC11",
  title:           "Purchase Coupon",
  description:     "A spectator purchases a coupon worth ₹1000 from the system. The coupon provides a 10-digit code that can be applied during online ticket checkout to receive a 10% discount on every seat purchased using the coupon.",
  primary-actor:   "Spectator",
  secondary-actor: "—",
  preconditions:   "The spectator is logged in.",
  postconditions:  "A 10-digit coupon code worth ₹1000 is generated and assigned to the spectator. Payment of ₹1000 is recorded.",
  dependency:      "«extend» UC10 — Purchase Ticket Online (coupon is used during online ticket purchase).",
  generalization:  "—",
  main-scenario: [
    1. The spectator navigates to the coupons page. \
    2. The spectator selects the option to purchase a coupon (₹1000). \
    3. The spectator completes the online payment of ₹1000. \
    4. The system generates a unique 10-digit coupon code. \
    5. The system displays the coupon code to the spectator and stores it for future use.
  ],
  extensions: [
    *3a.* Payment fails: The system does not generate a coupon and displays a payment error. \
    *5a.* Spectator applies coupon at checkout: 10% discount is applied to the per-seat price for all seats in the transaction.
  ],
  frequency:  "Moderate — used by spectators seeking discounted tickets.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC12 — Purchase via Agent
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC12",
  title:           "Purchase via Agent",
  description:     "A spectator purchases a ticket for a show through a Sales Agent. The spectator enters the Sales Agent's ID during the purchase process. The system records the sale under the Sales Agent's account and tracks a 1% commission on the total sale amount.",
  primary-actor:   "Spectator",
  secondary-actor: "Sales Agent",
  preconditions:   "The spectator is logged in. A show is scheduled with seats available. The Sales Agent has an active account with the Sales Agent role.",
  postconditions:  "A ticket is generated for the spectator. The sale is recorded under the Sales Agent's account. A 1% commission is calculated and credited to the Sales Agent.",
  dependency:      "—",
  generalization:  "—",
  main-scenario: [
    1. The spectator navigates to the ticket purchase page. \
    2. The spectator selects the "Purchase via Sales Agent" option. \
    3. The spectator enters the Sales Agent's unique ID. \
    4. The system validates the Sales Agent ID. \
    5. The spectator selects the show, seat type, and quantity. \
    6. The spectator completes the payment. \
    7. The system records the sale under the Sales Agent, calculates a 1% commission, and generates the ticket.
  ],
  extensions: [
    *4a.* Invalid Sales Agent ID: The system displays an error and prompts for a valid ID. \
    *6a.* Payment fails: The system releases the locked seats and displays a payment error.
  ],
  frequency:  "High — used for in-person ticket sales.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC13 — Cancel Ticket
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC13",
  title:           "Cancel Ticket",
  description:     "A spectator cancels a previously purchased ticket. The system applies a tiered refund policy based on how far in advance the cancellation is made relative to the show date. If the ticket was purchased through a Sales Agent, the corresponding agent commission is also reversed.",
  primary-actor:   "Spectator",
  secondary-actor: "Sales Agent (if original purchase was via agent)",
  preconditions:   "The spectator is logged in. The spectator holds a valid, uncancelled ticket for an upcoming show.",
  postconditions:  "The ticket is cancelled. The seat is released back to the available pool. A refund is issued according to the cancellation policy. If applicable, the Sales Agent's commission is reversed.",
  dependency:      "UC10 — Purchase Ticket Online or UC12 — Purchase via Agent.",
  generalization:  "—",
  main-scenario: [
    1. The spectator navigates to their tickets page. \
    2. The spectator selects the ticket to cancel. \
    3. The system calculates the applicable refund based on the cancellation policy. \
    4. The system displays the refund amount and requests confirmation. \
    5. The spectator confirms the cancellation. \
    6. The system cancels the ticket, releases the seat, and processes the refund.
  ],
  extensions: [
    *3a.* Cancellation more than 3 days before show: Refund = full amount − ₹5 per ticket. \
    *3b.* Cancellation between 24 hours and 3 days before show: Refund = full amount − ₹10 (ordinary) or ₹15 (balcony) per ticket. \
    *3c.* Cancellation within 24 hours of the show: Refund = 50% of the ticket price. \
    *6a.* Ticket was purchased via Sales Agent: The system also reverses the 1% commission credited to the agent.
  ],
  frequency:  "Low to Moderate — dependent on cancellation rate.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC14 — View Sales Records
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC14",
  title:           "View Sales Records",
  description:     "Authorized users query and view sales records for tickets sold through the auditorium. The President and Financial Clerk have full access to all sales data including online sales, coupon-based sales, agent sales, and agent commissions. Sales Agents can view only their own sales records and commissions earned.",
  primary-actor:   "President / Financial Clerk / Sales Agent",
  secondary-actor: "—",
  preconditions:   "The user is logged in with an authorized role (President, Financial Clerk, or Sales Agent).",
  postconditions:  "The requested sales records are displayed to the user. No system state is modified.",
  dependency:      "—",
  generalization:  "—",
  main-scenario: [
    1. The authorized user navigates to the sales records page. \
    2. The user optionally applies filters (by show, date range, or Sales Agent). \
    3. The system retrieves sales records scoped by the user's role. \
    4. The system displays the records, including ticket counts, revenue, seat percentages sold per category, and commissions.
  ],
  extensions: [
    *3a.* Sales Agent queries records: The system restricts results to only the agent's own sales and commission data. \
    *3b.* Unauthorized user attempts to access: The system denies the request with an authorization error. \
    *4a.* No records match the applied filters: The system displays an appropriate empty-state message.
  ],
  frequency:  "Moderate — used regularly for financial oversight.",
)

#pagebreak()

// ═══════════════════════════════════════════════════════════════
//  UC15 — Manage Expense Logs
// ═══════════════════════════════════════════════════════════════
#use-case-table(
  id:              "UC15",
  title:           "Manage Expense Logs",
  description:     "The Financial Clerk manages expense records for specific shows by creating, updating, and deleting expense entries in the show's balance sheet. The President has read-only access to expense logs. The system automatically generates a yearly balance sheet by consolidating all show-specific expense records and updates it whenever individual show balance sheets are modified.",
  primary-actor:   "Financial Clerk",
  secondary-actor: "President (read-only access)",
  preconditions:   "The Financial Clerk is logged in. At least one show exists in the system. This use case includes UC14 (View Sales Records) as part of comprehensive financial management.",
  postconditions:  "The expense records for the targeted show are updated. The show balance sheet reflects the changes. The yearly balance sheet is automatically recalculated.",
  dependency:      "«include» UC14 — View Sales Records",
  generalization:  "—",
  main-scenario: [
    1. The Financial Clerk navigates to the expense management page and selects a show. \
    2. The system displays the current balance sheet for the selected show. \
    3. The Financial Clerk creates, updates, or deletes an expense entry (e.g., artists, catering, decor). \
    4. The system validates and saves the expense record. \
    5. The system updates the show's balance sheet and automatically recalculates the yearly balance sheet.
  ],
  extensions: [
    *1a.* President views expense logs: The system displays expense records in read-only mode. \
    *3a.* Invalid expense entry data: The system displays a validation error. \
    *3b.* Unauthorized user attempts modification: The system denies the operation. \
    *5a.* Yearly balance sheet generation/update fails: The system logs the error and notifies the Financial Clerk.
  ],
  frequency:  "Moderate — used during and after each show's financial lifecycle.",
)
