# Auditorium Management and Ticketing System (AMTS)

A college has a large (800-seat) auditorium. The college has entrusted the auditorium’s management to the students’ society. The society needs the following software to efficiently manage the various shows held in the auditorium and to keep track of accounts. The society’s functionaries responsible for the day-to-day operation of the software are the auditorium secretary and the society’s president. Various social and cultural events are held in the auditorium.

The auditorium secretary should have overall authority to schedule shows, select and authorize show managers and sales agents. There are two categories of seats: balcony and ordinary. Balcony seats are usually more expensive for any show. The show manager sets the prices for these two categories for each show, based on the show’s popularity. The show manager also determines the number of balcony and ordinary seats that can be sold. For each show, some seats are offered as complimentary gifts to important student society functionaries and VIPs, and these must be entered into the system.

The show manager also enters the show dates, the number of shows on any particular date, and the show timings. The software should support a way for the show manager to configure the various show parameters.

The auditorium secretary appoints a set of sales agents. Sales agents receive a 1% commission on the total sales they make for any show.

The system should allow spectators to query the availability of different classes of seats for a show online. For the convenience of spectators, two methods of seat booking are supported.

If a spectator pays Rs. 1000/-, a unique 10-digit ID is generated and given to him. He can use this ID to book seats for shows online using a web browser. For each seat booked for a show using the unique ID, he receives a 10% discount.

A spectator can also book a seat for a single show only through regular payment. For online booking, a spectator indicates the type of seat required, and the requested seat is booked if available. The software should support printing a ticket showing the seat numbers allocated.

A spectator should be able to cancel his booking before 3 clear days of the show. In this case, the ticket price is refunded to him after deducting Rs. 5/- as the booking charge per ticket. If a ticket is returned at least 1 day before a show, a booking charge of Rs. 10/- is deducted for ordinary tickets and Rs. 15/- for balcony tickets. When a cancellation is made on the day of the show, a 50% deduction applies.

The show manager can query the percentage of seats booked for various classes of seats and the amount collected for each class at any time. When a salesperson makes a sale, the computer should record the salesperson’s ID in the sales transaction. This information helps compute the total amount collected by each salesperson and the commission payable to each salesperson. These data can be queried by the show manager.

Anyone should also be able to view the various shows planned for the next month and the rates for various categories of seats for a show using a web browser. The show manager should be able to view the total amount collected for his show as well as the sales agent-wise collection figures.

The accounts clerk should be able to enter the various types of expenditures incurred for a show, including payments to artists and auditorium maintenance charges. The computer should prepare a balance sheet for every show and a comprehensive, up-to-date balance sheet for every year. The different types of balance sheets should be accessible to the president of the student society only.

Since the software product should be as low-cost as possible, it is proposed that the software run on a high-end PC and be built using free system software such as Linux and the Apache web server.