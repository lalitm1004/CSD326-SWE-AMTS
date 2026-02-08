#set page(
  paper: "a4",
  
  header: context {
    let page-num = counter(page).at(here()).first()
    
    set text(10pt)
    if page-num > 1 [
      *SRS document for Auditorium Management and Ticketing System*
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

/// Front Page
#context [
  #set align(right)
  #v(2em)
  
  #text(27pt, weight: "bold")[Software Requirements Specification]

  
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

/// Table of contents
#context [
  #text(22pt, weight: "bold")[= Table of Contents]
  #v(1em)

  #show outline.entry: it => {
    if it.level == 1 {
      strong(it)
    } else {
      it
    }
  }

  // fill with dots
  #show outline.entry: set outline.entry(
    fill: repeat([.#h(0.1em)])
  )

  #outline(title: none)
  #pagebreak()
]


= 1. Introduction

== 1.1 Purpose
This Software Requirements Specification (SRS) document describes the functional and non-functional requirements of Version 1.0 of the Auditorium Management and Ticketing System (AMTS). The document is intended for use by the development team responsible for implementing and verifying the correct functioning of the system, as well as by the student society stakeholders who will utilize the system for managing auditorium operations.

Unless otherwise specified, all requirements outlined in this document are considered high priority and are committed for the initial release.

== 1.2 Project Scope and Product Features

=== 1.2.1 Project Overview
The college's 800-seat auditorium hosts numerous social and cultural events throughout the academic year. Management of this facility has been delegated to the students' society, which requires an efficient software solution to handle show scheduling, ticket sales, seat allocation, financial tracking, and reporting.

Currently, the society lacks an integrated system to manage these operations, resulting in manual processes that are time-consuming, error-prone, and difficult to track for accounting purposes. The proposed Auditorium Management and Ticketing System (AMTS) will provide a comprehensive web-based solution for the following:
- Configuration of auditorium seat layout, with designation of balcony and ordinary seat categories
- Authorized personnel (the auditorium secretary and the society president) to configure and manage shows
- Blocking and allocation of VIP and complimentary seats on a per-show basis
- Sales agents to sell tickets and track their commissions
- Online viewing of show information and ticket booking by spectators through multiple payment methods
- Automated financial tracking including revenue collection, expenditures, and balance sheet generation

The system will be accessible through web browsers, eliminating the need for specialized client software installation. It will be built using cost-effective, open-source technologies to minimize licensing costs while ensuring reliability and scalability.

Multiple user roles with distinct access levels and functionality will be supported to ensure proper authorization and separation of duties.


=== 1.2.2 Project Scope
The Auditorium Management and Ticketing System can be broken down into five domains: _Auditorium Configuration_, _Role Management_, _Show Configuration_, _Sales Management_, and _Financial Reporting_.

==== 1.2.2.1 Auditorium Configuration
The system shall support the configuration of auditorium layouts, including the specification and management of seat type information.

==== 1.2.2.2 Role Management
The system shall be pre-configured with a defined set of user roles and associated permissions, including President, Auditorium Secretary, Show Manager, Sales Agent, Financial Clerk and Spectator. The assignment of these roles to users shall be configurable, and access to AMTS functionality shall be enforced based on the assigned roles.

==== 1.2.2.3 Show Configuration and Scheduling
The system shall enable authorized users (Show Managers) to create, modify, and manage shows conducted in the auditorium. It shall support configuration of seat pricing for different categories, definition of seating capacity, reservation of VIP and complimentary seats, and management of show dates, timings, and recurrence. The system shall also track seat availability based on configured capacity and bookings.

==== 1.2.2.4 Ticket Sales / Commission Tracking
The system shall enable Spectators to purchase tickets for balcony and ordinary seats for a show. Spectators should be able to purchase a seat online or via a sales agent. For online bookings, spectators should be able to purchase coupon codes worth Rs. 1000 which will provide them with a 10% off every seat they purchase using the coupon code. On purchasing seats via Sales Agents, the Sales Agents earn a 1% commission for the total sales they make for any show. The President and Auditorium secretary should be able to query the sales records for any show, along with percentage of seats sold for every category, and the commissions earned by the Sales Agents. The Sales Agents should be able to query their own sales records and view the commissions earned by them.

==== 1.2.2.5 Expense Tracking / Balance Sheet
The system shall enable an expense tracking system maintained by a Financial Clerk who may enter account logs / balance sheets of show-related transactions (i.e. show artists, speakers, decor, catering, etc.) outside of seat booking-related transactions. The only user authorized to update, delete, or otherwise edit account entries is the Financial Clerk, with the Student President reserving read-only access to expense logs. Each show has a corresponding balance sheet. The system shall contain a feature that can auto-generate a yearly expense / balance sheet by consolidating the expenses of all shows over the course of the corresponding year.

= 2. Overall Description
== 2.1 User Classes and Characteristics
#table(
  columns: 2,
  fill: (col, row) => if row == 0 { luma(230) } else { none },
  [User], [Description],
  [President], [This user class has access to financial reports and may view balance sheets generated by the system.],
  [Auditorium Secretary], [This user class has overall administrative authority over the AMTS, including the ability to assign Show Managers and Sales Agents.],
  [Show Manager], [This user class is responsible for managing show-related configurations, including seating capacity, seat pricing by category, VIP seat reservations, and show details such as name, description, schedule, and recurrence.],
  [Sales Agent], [This user class is responsible for conducting on-ground ticket sales. Sales Agents may sell tickets for any show that has not yet occurred and shall receive a 1% commission on all completed sales.],
  [Financial Clerk], [This user class is responsible for managing financial information related to specific shows and has access to system-generated balance sheets for auditing purposes.],
  [Spectator], [This user class can view show details and book tickets through in-person sales agents, online without a voucher, or online using a voucher. The voucher (Rs. 1000) entitles the spectator to a 10% discount on seat prices for a selected show.]
)

== 2.2 Operating Environment
- *OE-1:* The AMTS shall be deployed on a centralized linux-based server.
- *OE-3:* PostgreSQL shall be used as the relational database management system for persistent data storage.

== 2.3 Design and Implementation Constraints
- *CO-1:* The system shall be accessible to all user classes through any modern web-browser and an internet connection
- *CO-2:* All server-side code shall be written in Java 21

= 3. System Features

== 3.1 Layout/Seat Configuration of Auditorium
=== 3.1.1 Description
The system shall provide a predefined auditorium layout that represents the physical seating arrangement of the auditorium. The layout shall be specified declaratively using a text-based configuration format and shall include seating rows, columns, empty lanes (e.g., aisles), and seat types.

The layout configuration shall be loaded during system initialization and shall not be modifiable during normal system operation. The predefined layout shall be used by the system for seat visualization, allocation, and ticketing functions.

=== 3.1.2 Stimulus/Response Sequences
- Stimulus: System is initialized.
- Response: The system loads the predefined auditorium layout from the text-based configuration and validates its structure.
\
- Stimulus: Predefined auditorium layout is valid.
- Response: The layout is stored and made available for seat visualization, allocation, and ticketing.
\
- Stimulus: Predefined auditorium layout is invalid or missing.
- Response: The system reports a configuration error and prevents normal operation. 

=== 3.1.3 Functional Requirements
#table(
  columns: 3,
  fill: (col, row) => if row == 0 { luma(230) } else { none },
  [Feature ID], [Feature Name], [Description],
  [FR-CL1], [Auditorium Layout Initialization], [The application shall load a predefined, statically configured auditorium layout and statically defined seat types during system startup and validate the layout before enabling normal operation.]
) 

== 3.2 Create and View User Accounts
=== 3.2.1 Description
The system shall allow users to create, view, and delete their own user accounts. Each account shall be associated with authentication credentials and one or more permission roles.

Permission roles shall determine which sections and functions of the application the user is authorized to access. Upon account creation, a default permission role shall be assigned to the user.

=== 3.2.2 Stimulus/Response Sequences
- Stimulus: A user creates an account using an email address and password.
- Response: The system persists the user’s authentication information and assigns the default `spectator` role.
\
- Stimulus: A user requests access to their account details.
- Response: The system returns account information in accordance with the user’s assigned permissions.

=== 3.2.3 Functional Requirements
#table(
  columns: 3,
  fill: (col, row) => if row == 0 { luma(230) } else { none },
  [Feature ID], [Feature Name], [Description],
  [FR-CVA1], [Create User Account], [The system shall allow an unauthenticated user to create a user account using an email address and password, and shall assign the default role of `spectator` upon successful creation.],
  [FR-CVA2], [View User Account], [The system shall allow an authenticated user to view their own account information, subject to the permissions associated with their assigned roles.],
)

== 3.3 Assign and De-assign Roles to User Accounts
=== 3.3.1 Description
The system shall support the assignment and removal of permission roles for existing user accounts. Permission roles shall correspond to the predefined user classes described in Section 2.1.

Users assigned the Auditorium Secretary role shall be authorized to assign and remove roles for user accounts except the Auditorium Secretary role itself.
Only users assigned the President role shall be authorized to assign or remove the Auditorium Secretary role.

All role changes shall take effect immediately and shall be enforced consistently across all system functions.

=== 3.3.2 Stimulus/Response Sequences
- Stimulus: An Auditorium Secretary requests to assign or remove a non–Auditorium Secretary role for a user account.
- Response: The system updates the target user’s roles accordingly.
\
- Stimulus: A President requests to assign or remove the Auditorium Secretary role for a user account.
- Response: The system assigns or removes the Auditorium Secretary role and updates the user’s effective permissions.
\
- Stimulus: A user without sufficient authority attempts to assign or remove a role.
- Response: The system denies the request and reports an authorization error.

=== 3.3.3 Functional Requirements
#table(
  columns: 3,
  fill: (col, row) => if row == 0 { luma(230) } else { none },
  [Feature ID], [Feature Name], [Description],
  [FR-AR1], [Assign Non-Secretary Role], [The system shall allow only users with the Auditorium Secretary role to assign predefined roles other than Auditorium Secretary to existing user accounts.],
  [FR-AR2], [De-assign Non-Secretary Role], [The system shall allow only users with the Auditorium Secretary role to remove predefined roles other than Auditorium Secretary from existing user accounts.],
  [FR-AR3], [Assign Auditorium Secretary Role], [The system shall allow only users with the President role to assign the Auditorium Secretary role to a user account.],
  [FR-AR4], [De-assign Auditorium Secretary Role], [The system shall allow only users with the President role to remove the Auditorium Secretary role from a user account.],
)

== 3.4 Show Configuration and Scheduling
=== 3.4.1 Description
Show Configuration and Scheduling enables the configuration of all show-related parameters required before ticket sales begin. The responsibility for configuring show details is shared between the Auditorium Secretary and the Show Manager. This feature allows scheduling of shows, pricing, seat limits, and allocation of complimentary and VIP seats. This functionality is of high priority, as all booking, sales, and accounting operations depend on accurate show configuration.

=== 3.4.2 Stimulus/Response Sequences
- Stimulus: Auditorium Secretary schedules a new show.
- Response: System allows creation of a show entry and assignment of an authorized Show Manager.
\
- Stimulus: Show Manager enters show dates, timings, and number of shows per day.
- Response: System stores the scheduling details for the show.
\
- Stimulus: Show Manager configures seat pricing and seat limits.
- Response: System records the prices and the number of balcony and ordinary seats available for sale.
\
- Stimulus: Show Manager enters complimentary and VIP seat details.
- Response: System marks the specified seats as complimentary and excludes them from sale.
\
- Stimulus: Show Manager modifies show parameters.
- Response: System updates the show configuration accordingly.

=== 3.4.3 Functional Requirements
#table(
  columns: 3,
  fill: (col, row) => if row == 0 { luma(230) } else { none },
  [Feature ID], [Feature Name], [Description],
  [FR-SCS1], [Schedule Show], [The system shall allow the Auditorium Secretary and Show Managers to schedule shows.],
  [FR-SCS2], [Enter Show Schedule], [The system shall allow the Show Manager to enter show dates, show timings, and the number of shows per day.],
  [FR-SCS3], [Set Seat Pricing], [The system shall allow the Show Manager to set prices for balcony and ordinary seats for each show.],
  [FR-SCS4], [Define Seat Limits], [The system shall allow the Show Manager to specify the number of balcony and ordinary seats available for sale for a show.],
  [FR-SCS5], [Allocate Complimentary and VIP Seats], [The system shall allow the Show Manager to enter details of complimentary and VIP seats, which shall not be available for booking.],
  [FR-SCS6], [Modify Show Configuration], [The system shall allow authorized users to modify existing show parameters before or during ticket sales, as permitted.]
)

== 3.4 Ticket Sales
=== 3.4.1 Description
The system allows spectators three methods to purchase tickets. The users can either buy the ticket directly from an agent, buy a coupon which gives them a 10% discount off every seat they purchase online, or directly book a single seat for the show online.

On purchasing from a sales agent, the spectators will have to enter the Sales Agent's ID into the purchase page. When tickets are purchased from a Sales Agent, the agent gets a 1% commission on the total sales they make for any show.

The spectators can buy coupons worth 1000 INR from the website, for which they will receive at 10 digit code. Upon checkout, these coupons can be used to purchase seats for a show, and spectators are given 10% off the price of individual seats for all the seats they buy using coupons.

Lastly, the spectators can directly purchase the ticket for a single seat online, upon which they'll be given the confirmation and their booking ticket.

=== 3.4.2 Stimulus/Response Sequences
- Stimulus: Spectator buys a ticket from a Sales Agent.
- Response: The system generates a ticket for the spectator, and saves the sale under the Sales Agent's record to calculate total commission to be given out to the agent.
\
- Stimulus: Spectator buys a 10 digit coupon.
- Response: The system generates a 10 digit code worth Rs. 1000, that the spectator can use to avail a 10% discount off every seat they purchase for a given show.
\
- Stimulus: Spectator books a seat directly online.
- Response: The system books the seat for the spectator, creates a confirmation and generates the booking ticket for the spectator, charging the spectator accordingly based on the type of seat they choose (balcony/ordinary).
\
- Stimulus: Spectator uses the 10 digit coupon to purchase a seat online.
- Response: The system books the seats selected by the spectator, applies a 10% discount to the price of individual seats, calculates total price and generates booking ticket after successful processing of the payment online.
\
- Stimulus: Spectator cancels a ticket purchased online more than 3 days before the show.
- Response: The system refunds the total amount the spectator was charged for the ticket, after deducting INR 5 from every ticket as the per ticket booking price.
\
- Stimulus: Spectator cancels a ticket purchased online before atleast 24 hours from the show.
- Response: The system refunds the amount the spectator was charged for the ticket after deducting INR 10 for ordinary tickets and INR 15 for balcony tickets from every ticket as the per ticket booking price.
\ 
- Stimulus: Spectator makes a cancellation for a ticket purchased online within 24 hours of the show.
- Response: The system refunds 50% of the total amount paid per ticket.
\
- Stimulus: Spectator cancels a ticket purchased from a Sales Agent.
- Response: The system applies the cancellation policy as described above, and also cancels the commission earned by the Sales Agent on the ticket sale.

=== 3.4.3 Functional Requirements
#table(
  columns: 3,
  fill: (col, row) => if row == 0 { luma(230) } else { none },
  [Feature ID], [Feature Name], [Description],
  [FR-TS1], [Online Ticket Purchase], [The system should allow spectators to purchase tickets for shows online. Spectators can choose their seat types and the quantity, and then complete the payment online. Upon successful payment, Spectators will receive the generated tickets for their seats for the show.],
  [FR-TS2], [Coupon Purchase], [The system should allow spectators to purchase a 10 digit coupon code worth 1000 INR. This coupon code can then be used while purchasing seats online to get a 10% discount off the price of individual seats by the spectators.],
  [FR-TS3], [Online Ticket Purchase with Coupons], [The system should allow users to enter their purchased coupon codes during the seat booking process. This will apply a 10% discount to the per seat price of all the seats booked by the user.],
  [FR-TS4], [Seat Purchase via Sales Agent], [The system should allow Sales Agents to input their IDs during the seat booking process. This will record all the sales made by the sales agents, and track the commissions earned by them.],
  [FR-TS5], [Online Ticket Cancellation], [The system should allow Spectators to cancel tickets they have purchased online. Upon cancellation of tickets, the Spectators will get a refund with Rs. 5 deduction per ticket if cancelled before 3 days of the show, Rs. 10 for ordinary and Rs. 15 for balcony if cancelled before 1 day of the show, and 50% of the ticket price if cancelled upon the same day as the show.],
  [FR-TS6], [Sales Agent Ticket Cancellation], [The system should allow Spectators to cancel tickets they have purchased via Sales Agent. The refund will have the same policy applied to it as mentioned above, and the system will invalidate the commission earned by the Sales Agent on the sale.]
)


== 3.5 Sales Records
=== 3.5.1 Description
The system will allow authorized users (President, Financial Clerk and Sales Agents) to view sales records for the tickets sold by the auditorium, scoped by permissions i.e. President and Financial Clerk will be able to query the data for all the seats sold by the auditorium, including the seats sold online, with coupon, and by Sales Agents along with the commission earned by them. Sales Agents will only be able to query data for the seats sold by them along with the commission they have earned.

=== 3.5.2 Stimulus/Response Sequences:
- Stimulus: A President/Financial Clerk queries the financial records.
- Response: The system returns all the sales records available, filtered by specific query filters if available.
\
- Stimulus: A Sales Agent queries the sales records for the seats sold by them.
- Response: The system returns sales records for the seats sold by the Sales Agents with restricted parameters, along with the commission earned by them.
\
- Stimulus: A Sales Agent queries sales records they are not authorized to.
- Response: The system returns an error due to lack of authorization.
\
- Stimulus: A non authorized used tries to access the sales records.
- Response; The system returns an error due to lack of authorization.

=== 3.5.3 Functional Requirements
#table(
  columns: 3,
  fill: (col, row) => if row == 0 { luma(230) } else { none },
  [Feature ID], [Feature Name], [Description],
  [FR-SR1], [Query Sales Records], [The system should return sales records for the auditorium upon request, based on authorization. President and Financial Clerks shall get full access to all the sales records, and Sales Agents shall get access to limited sales data about their own commissions.],
  [FR-SR2], [Calculate Commissions], [The system should automatically calculate and assign the commissions earned by the Sales Agents on all the ticket sales made by them. This information should be available to the President and Financial Clerk upon querying, and every Sales Agent will be able to query their own records.]
)

== 3.6 Expense Tracking
=== 3.6.1 Description
The system will allow the Financial Clerk to enter, view, and manage expense records for specific shows in the form of a balance sheet for show-related expenses. The President and Financial Clerk will have the ability to query the balance sheet, and print it, with the President retaining read-only access to the expense log. The system will also automatically generate a yearly balance sheet by consolidating show expenses for the corresponding year. The Financial Clerk and the President will maintain read-only access to the yearly sheet. The yearly sheet will automatically update based on changes to show-specific balance sheets that it may constitute of.

=== 3.6.2 Stimulus/Response Sequences:
- Stimulus: A President/Financial Clerk queries the balance sheet of a specific show.
- Response: The system returns all the expense records available for that specific show.
\
- Stimulus: A President/Financial Clerk queries the balance sheet of a specific event.
- Response: The system returns expense records within the balance sheet of all shows listed under that specific event.
\
- Stimulus: A Financial Clerk enters, updates, or deletes an expense log for a specific show.
- Response: The system updates the balance sheet of that specific show with the new log or expense change. The system also updates the corresponding year's balance sheet with the new expense log or change of the show.
\
- Stimulus: A non authorized (non Financial Clerk) user tries to access any expense logs.
- Response; The system returns an error due to lack of authorization.

=== 3.6.3 Functional Requirements
#table(
  columns: 3,
  fill: (col, row) => if row == 0 { luma(230) } else { none },
  [Feature ID], [Feature Name], [Description],

  [FR-ET1], [Query Show Balance Sheet],
  [The system shall allow the President and Financial Clerk to query and view the balance sheet containing all expense records for a specific show.],

  [FR-ET2], [Query Event Expense Records],
  [The system shall allow the President and Financial Clerk to query expense records across all shows associated with a specific event.],

  [FR-ET3], [Manage Show Expense Logs],
  [The system shall allow the Financial Clerk to create, update, and delete expense logs for a specific show, and automatically update the corresponding show balance sheet.],

  [FR-ET4], [Generate Yearly Balance Sheet],
  [The system shall automatically generate a yearly balance sheet by consolidating all show-related expenses for the corresponding year.],

  [FR-ET5], [Auto-Update Yearly Balance Sheet],
  [The system shall automatically update the yearly balance sheet whenever a show-specific balance sheet that contributes to it is modified.],

  [FR-ET6], [Role-Based Access Control for Expenses],
  [The system shall restrict expense log modification privileges to the Financial Clerk, provide read-only access to the President, and deny access to all unauthorized users.],
)

= 4. External Interface Requirements
== 4.1 User Interfaces
- *UI-1:* All users shall have the ability to access the AMTS from many platforms and locations without the need to install software locally.

== 4.2 Hardware Interfaces
No hardware interface requirements have been identified

== 4.3 Software Interfaces
No software interface requirements have been identified

== 4.4 Communication Interfaces
No communication interface requirements have been identified

= 5. Other Non-Functional Requirements
== 5.1 Documentation Requirements
- *NFR-DOC-1:* The system shall provide API documentation describing all endpoints, request methods, parameters, authentication mechanisms, and request/response formats. The documentation shall include usage examples and be updated with every major system release.
- *NFR-DOC-2:* The system shall provide a Deployment Guide specifying hardware and software prerequisites, installation steps, configuration procedures, database setup, and backup/recovery instructions. The guide shall enable a system administrator to deploy the system without developer assistance.


== 5.2 Performance Requirements
- *NFR-PERF-1:* The system shall respond to standard user actions (login, show listing, seat selection) within 2 seconds under normal load (≤ 300 concurrent users).
- *NFR-PERF-2:* The system shall support at least 800 concurrent active booking sessions during peak ticket sales without service interruption.
- *NFR-PERF-3:* Seat booking transactions shall be completed within 10 seconds, including payment confirmation.
- *NFR-PERF-4:* Balance sheet and sales report generation shall complete within 10 seconds for datasets covering up to one academic year.


== 5.3 Availability and Reliability Requirements
- *NFR-REL-1:* The system shall maintain 99% uptime during the academic year, excluding scheduled maintenance.
- *NFR-REL-2:* Ticket booking and payment processing shall be atomic operations to prevent double booking or inconsistent seat allocation.

== 5.4 Safety Requirements
- *NFR-SAFE-1:* When a spectator selects a seat, the system shall temporarily lock it for 5 minutes to prevent duplicate bookings.

== 5.5 Security Requirements
- *NFR-SEC-1:* All functionalities shall require secure authentication using email and password.
- *NFR-SEC-2:* Passwords shall be hashed using a strong hashing algorithm (e.g., bcrypt or Argon2) and shall never be stored in plain text.
- *NFR-SEC-3:* All system operations shall enforce strict role-based access control as defined in Section 2.1.
- *NFR-SEC-4:* All communication between client and server shall use HTTPS with TLS encryption.
- *NFR-SEC-5:* All user inputs shall be validated on both client and server sides to prevent SQL injection, XSS, and CSRF attacks.

== 5.6 Portability Requirements
- *NFR-PORT-1:* The system shall be deployable on any Linux-based server environment supporting Java 21.
