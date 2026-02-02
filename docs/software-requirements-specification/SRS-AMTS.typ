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
  
  #v(1em)
  #line(length: 100%, stroke: 2pt)
  
  #text(20pt, weight: "bold")[
    // Group 20 \
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
