export type FaqItem = {
    question: string;
    answer: string[];
    srsRefs?: string[];
};

export type FaqCategory = {
    title: string;
    description: string;
    items: FaqItem[];
};

export const homeFaq: FaqCategory[] = [
    {
        title: 'About AMTS',
        description: 'What the system is designed to manage across the auditorium lifecycle.',
        items: [
            {
                question: 'What is AMTS?',
                answer: [
                    'AMTS is the Auditorium Management and Ticketing System for the college auditorium. It is designed to manage show scheduling, ticket sales, seat allocation, coupon-based discounts, financial tracking, and operational reporting for an 800-seat venue.',
                    'The SRS positions it as a shared system for student-society stakeholders, administrative roles, sales agents, financial staff, and spectators.'
                ],
                srsRefs: ['FR-CL1']
            },
            {
                question: 'What does AMTS manage besides ticket booking?',
                answer: [
                    'AMTS covers much more than spectator booking. The system also supports event and show setup, seat pricing, complimentary and VIP allocation, sales-agent commission tracking, show expense records, and balance-sheet style reporting.',
                    'In other words, the product spans both the public ticketing side and the internal operational side of running auditorium events.'
                ],
                srsRefs: ['FR-SCS1', 'FR-TS1', 'FR-SR1']
            }
        ]
    },
    {
        title: 'Accounts and Roles',
        description: 'Who uses the system and what access boundaries exist.',
        items: [
            {
                question: 'Do I need an account to use AMTS?',
                answer: [
                    'You can browse public event information without signing in, but booking-related and account-specific actions require authentication. The SRS defines authenticated access with role-based permissions, and the current app also keeps seat maps, bookings, coupons, profile access, and dashboard actions behind sign-in.',
                    'New accounts default to the spectator role.'
                ],
                srsRefs: ['FR-CVA1', 'FR-CVA2', 'NFR-SEC-1']
            },
            {
                question: 'What user roles exist in AMTS?',
                answer: [
                    'The SRS defines Spectator, Sales Agent, Show Manager, Auditorium Secretary, President, and Financial Clerk roles. The current codebase also includes a technical ROOT role for elevated administration.',
                    'These roles determine who can schedule shows, manage expenses, view reports, sell tickets offline, or manage their own bookings.'
                ],
                srsRefs: ['FR-CVA2']
            },
            {
                question: 'What can logged-out users see today?',
                answer: [
                    'Public visitors can browse the homepage, event listings, event pages, and public show details such as title, schedule, and pricing. Actions tied to booking state or user-owned data still require sign-in.',
                    'That means seat availability, checkout, coupon purchase, booking history, profile access, and dashboard tools remain account-gated.'
                ]
            }
        ]
    },
    {
        title: 'Events, Shows, and Seating',
        description: 'How the auditorium catalog and seating model are organized.',
        items: [
            {
                question: 'What is the difference between an event and a show?',
                answer: [
                    'An event is the broader umbrella entry that users browse first. A show is a specific scheduled performance instance under that event, with its own start time, seat pricing, and seat-sale configuration.',
                    'The app reflects that structure through event pages that list one or more shows.'
                ],
                srsRefs: ['FR-SCS1', 'FR-SCS2']
            },
            {
                question: 'How are seats categorized?',
                answer: [
                    'AMTS distinguishes ordinary and balcony seats for pricing and seat-limit purposes. The SRS also allows specific seats to be marked VIP or complimentary so they stay out of normal public sale.',
                    'In the current system, VIP and complimentary seats are part of show configuration rather than spectator checkout.'
                ],
                srsRefs: ['FR-SCS3', 'FR-SCS4', 'FR-SCS5']
            },
            {
                question: 'Can the auditorium layout be changed during normal use?',
                answer: [
                    'No. The SRS specifies a predefined auditorium layout loaded from configuration during initialization, and it is not meant to be edited during ordinary system operation.',
                    'That layout is the foundation for seat visualization, allocation, and booking.'
                ],
                srsRefs: ['FR-CL1']
            }
        ]
    },
    {
        title: 'Show Management',
        description: 'How authorized staff configure the shows that spectators later browse and book.',
        items: [
            {
                question: 'Who can create or change shows?',
                answer: [
                    'The SRS assigns show scheduling and configuration responsibilities to the Auditorium Secretary and Show Managers. Those roles are responsible for creating shows and updating operational details before and during sales as permitted.',
                    'The current dashboard mirrors that split by exposing event/show management only to authorized administrative roles.'
                ],
                srsRefs: ['FR-SCS1', 'FR-SCS6']
            },
            {
                question: 'What can be configured for a show?',
                answer: [
                    'AMTS supports show names, descriptions, schedule details, thumbnail imagery, ordinary and balcony seat pricing, sellable seat counts, and reserved seat designations such as VIP or complimentary.',
                    'These settings determine what spectators see and what inventory can actually be sold.'
                ],
                srsRefs: ['FR-SCS2', 'FR-SCS3', 'FR-SCS4', 'FR-SCS5']
            },
            {
                question: 'Does AMTS support individual show deletion?',
                answer: [
                    'Yes. The current system supports deleting a specific show entry independently rather than only deleting its parent event. That is part of ongoing show-management maintenance in the dashboard.',
                    'This is an implementation capability in the current codebase, even though the SRS frames the broader requirement as managing and modifying shows.'
                ],
                srsRefs: ['FR-SCS6']
            }
        ]
    },
    {
        title: 'Booking, Coupons, and Tickets',
        description: 'How spectators purchase seats and retrieve their booking information.',
        items: [
            {
                question: 'How does online ticket booking work?',
                answer: [
                    'Spectators choose a show, select seats, and complete checkout online. After successful purchase, the system generates tickets tied to a booking record that can later be viewed from the tickets area.',
                    'The SRS treats online purchase as a core spectator capability.'
                ],
                srsRefs: ['FR-TS1']
            },
            {
                question: 'Can users choose their own seats?',
                answer: [
                    'Yes. The current booking flow is seat-based rather than quantity-only. Spectators select specific seats from the auditorium grid, and AMTS calculates pricing from the seat type and show configuration.',
                    'VIP and complimentary seats are excluded from the normal purchase flow.'
                ],
                srsRefs: ['FR-TS1', 'FR-SCS5']
            },
            {
                question: 'What is a coupon in AMTS?',
                answer: [
                    'A coupon is a purchased code worth Rs. 1000 that gives a 10% discount on seat prices during online booking. The SRS defines coupon purchase as a separate spectator flow from direct seat purchase.',
                    'In the current app, authenticated spectators can buy a coupon for a selected show and then enter the code during checkout.'
                ],
                srsRefs: ['FR-TS2', 'FR-TS3']
            },
            {
                question: 'How do I find my tickets after booking?',
                answer: [
                    'AMTS keeps booking history in the My Tickets area for signed-in spectators. Individual bookings can also be looked up by booking ID, and each booking view shows the tickets associated with that booking.',
                    'This matches the system’s model of generating tickets as artifacts of successful purchases.'
                ],
                srsRefs: ['FR-TS1']
            }
        ]
    },
    {
        title: 'Refunds and Cancellations',
        description: 'How AMTS handles ticket cancellation and refund amounts.',
        items: [
            {
                question: 'Can tickets be cancelled in AMTS?',
                answer: [
                    'Yes. The SRS explicitly supports cancellation for tickets purchased online and via sales agents. Refund handling depends on how close the cancellation is to the show time.',
                    'The current system also exposes spectator ticket cancellation from the tickets flow.'
                ],
                srsRefs: ['FR-TS5', 'FR-TS6']
            },
            {
                question: 'What refund policy does the SRS define?',
                answer: [
                    'According to the SRS, cancellations made more than 3 days before the show refund the paid amount minus Rs. 5 per ticket. Cancellations made at least 24 hours before the show deduct Rs. 10 per ordinary ticket or Rs. 15 per balcony ticket.',
                    'If cancellation happens within 24 hours of the show, the refund is 50% of the amount paid per ticket.'
                ],
                srsRefs: ['FR-TS5']
            },
            {
                question: 'What happens when a sales-agent booking is cancelled?',
                answer: [
                    'The SRS says the same refund policy applies, but the related sales-agent commission must also be invalidated. That keeps commissions tied to completed sales rather than cancelled ones.',
                    'This matters for both operational reporting and payout calculations.'
                ],
                srsRefs: ['FR-TS6', 'FR-SR2']
            }
        ]
    },
    {
        title: 'Sales, Commissions, and Reports',
        description: 'What internal business reporting the system supports.',
        items: [
            {
                question: 'How do sales agents fit into the system?',
                answer: [
                    'Sales Agents are authorized to sell tickets on the spectator’s behalf for shows that have not yet occurred. Their sales are tracked separately from direct online bookings so commission calculations remain attributable.',
                    'The system also supports a dedicated agent purchase flow in the ticketing API.'
                ],
                srsRefs: ['FR-TS4', 'FR-SR2']
            },
            {
                question: 'How are commissions handled?',
                answer: [
                    'The SRS states that sales agents earn a 1% commission on completed ticket sales they make. Presidents and Financial Clerks can review broader sales and commission data, while Sales Agents are limited to their own commission-related information.',
                    'The current dashboard includes commission views aligned to those responsibilities.'
                ],
                srsRefs: ['FR-SR1', 'FR-SR2']
            },
            {
                question: 'What financial and reporting features exist?',
                answer: [
                    'AMTS includes show-level expense tracking, show balance sheets, event-level expense queries across associated shows, and yearly balance-sheet consolidation. These features are primarily for Financial Clerks and Presidents.',
                    'The reporting side of the product covers revenue, commissions, and expense visibility rather than just seat sales.'
                ],
                srsRefs: ['FR-SR1']
            }
        ]
    },
    {
        title: 'Administration and Governance',
        description: 'Which roles manage system-wide configuration and oversight.',
        items: [
            {
                question: 'What does the Auditorium Secretary do in AMTS?',
                answer: [
                    'The Auditorium Secretary has broad administrative control over auditorium operations in the SRS. That includes authority around show setup and role management, short of powers explicitly reserved to the President.',
                    'In the current app, this role has dashboard access for core event and show administration.'
                ],
                srsRefs: ['FR-SCS1']
            },
            {
                question: 'What does the President do?',
                answer: [
                    'The President is positioned as a high-level oversight role with access to reporting, commission visibility, and balance-sheet review. The SRS also reserves authority over assignment and removal of the Auditorium Secretary role to the President.',
                    'That makes the President the top policy-level business role in AMTS.'
                ],
                srsRefs: ['FR-SR1']
            },
            {
                question: 'What does the Financial Clerk do?',
                answer: [
                    'The Financial Clerk manages show-related expense logs and balance sheets. The SRS gives this role write access for show expense records, while the President keeps read-only oversight for that area.',
                    'Yearly balance sheets are also generated from these show-level financial entries.'
                ],
                srsRefs: ['FR-SR1']
            }
        ]
    },
    {
        title: 'System Rules and Reliability',
        description: 'Operational expectations defined at the system level.',
        items: [
            {
                question: 'How does AMTS prevent double booking?',
                answer: [
                    'The SRS requires booking and payment to behave atomically so the system does not leave seats in an inconsistent state. It also defines a temporary seat lock of 5 minutes when a spectator selects a seat.',
                    'These requirements describe the intended protection against duplicate claims on the same inventory.'
                ],
                srsRefs: ['NFR-REL-2', 'NFR-SAFE-1']
            },
            {
                question: 'What security expectations does the system follow?',
                answer: [
                    'The SRS requires secure email-and-password authentication and role-based authorization across system functions. It also requires validation on both client and server sides to reduce risks such as SQL injection, XSS, and CSRF.',
                    'In practice, AMTS separates public browsing from authenticated booking, dashboard, and account-specific actions.'
                ],
                srsRefs: ['NFR-SEC-1', 'NFR-SEC-5']
            },
            {
                question: 'What performance expectations are documented?',
                answer: [
                    'The SRS targets standard user actions such as login, show listing, and seat selection within roughly 2 seconds under normal load. It also expects seat-booking transactions to complete within 10 seconds and support heavy concurrent booking demand.',
                    'These are stated product requirements rather than promises tied to a single homepage session.'
                ],
                srsRefs: ['NFR-PERF-1', 'NFR-PERF-2', 'NFR-PERF-3']
            }
        ]
    }
];
