# Airport Database Using SQL

This project was done as part of the CSE 132A - Database System Principles course at University of California, San Diego.

## Project Details

The idea of this project is that we are initially given a table of airports. Each record in the table has three attributes: airline, origin and destination. 'Airline' refers to the name of the airline, while 'origin' and 'destination' indicate the two airports that are connected by that airline without any layover in between. The combination of these records can be visualized in a big directed graph where the nodes represent the airports and the edges represent the airline.

The goal of this program is to use this data and for each airport and airline, identify all the other airports that are reachable using multiple layovers. This is achieved using a recursive query.
