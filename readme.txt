Readme.txt


Computing Environment:
1. Application will be built using SQLite.
	The programming language will be Java. Please make sure JDK is correctly installed
2. Use JDBC to connect to the database.
3. A UNIX environment is recommended.

Objective:
Write a Java program which, given a database with the above schema, outputs a table 
Connected with columns Airline, Origin, Destination (of type char(32)) and Stops
(of type int). 

Algorithm:
Adapt the computation of transitive closure using semi‐naïve evaluation, by additionally
taking into account the airline and keeping track of the number of stops.