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
This SRS describes the software functional and nonfunctional requirements for the Auditorium Management and Ticketing System. This document is intended to be used by members of the project team that will implement and verify the system.

== 1.2 Project Scope and Product Features

=== 1.2.1 Project Overview
The Student Society requires an Auditorium Management and Ticketing System (AMTS) to streamline the scheduling, sales, and accounting of various social and cultural events. Currently, managing an 800-seat venue involving manual seat allocations, complex cancellation policies, and commission tracking for sales agents is labor-intensive and prone to error.

The AMTS will provide a centralized platform to manage seat categories (Balcony and Ordinary), handle diverse booking methods and automate financial reporting. 
