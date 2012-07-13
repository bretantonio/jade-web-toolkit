C:\Program Files\Java\jdk1.7.0\db\lib>java -classpath .;derby.jar;derbytools.jar org.apache.derby.tools.ij
-- Ignore the database not created warning if present
connect 'jdbc:derby:FREELEC;create=true' ;

-- First delete the tables if they exist. 
-- Ignore the table does not exist error if present
DROP TABLE FREELEC.flights ;

-- CREATE the products table for Bigdog's Surf Shop
CREATE TABLE FREELEC.flights (
id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
flight_number CHAR(5)  NOT NULL,
origin_airport CHAR(3)  NOT NULL,
destination_airport CHAR(3)  NOT NULL,
carrier VARCHAR(20) NOT NULL,
price INTEGER,
date TIMESTAMP,
duration VARCHAR(8),
available_seats INTEGER
) ;


-- Insert 26 rows into the products table
INSERT INTO FREELEC.flights(flight_number, origin_airport, destination_airport, carrier, price, date, duration, available_seats)
    VALUES 
('SA001', 'SFO', 'DEN', 'SpeedyAir', 400, TIMESTAMP('2012-09-23 19:00:00'), '20m', 50), 
('SA002', 'SFO', 'LHR', 'SpeedyAir', 2000, TIMESTAMP('2012-09-23 20:00:00'), '11h65m', 22), 
('SA003', 'SFO', 'LAX', 'SpeedyAir', 100, TIMESTAMP('2012-09-23 21:00:00'), '22m', 37), 
('SA004', 'LAX', 'SFO', 'SpeedyAir', 100, TIMESTAMP('2012-09-23 22:00:00'), '34m', 0), 
('PA001', 'DAL', 'FRA', 'PromptAir', 800, TIMESTAMP('2012-09-23 23:00:00'), '9h35m', 14), 
('PA002', 'FRA', 'DAL', 'PromptAir', 800, TIMESTAMP('2012-09-28 00:00:00'), '9h55m', 4), 
('PA003', 'FRA', 'BOM', 'PromptAir', 700, TIMESTAMP('2012-09-28 01:00:00'), '8h30m', 97), 
('PA004', 'BOM', 'FRA', 'PromptAir', 700, TIMESTAMP('2012-09-30 02:00:00'), '8h10m', 75), 
('PA005', 'DEN', 'ABQ', 'PromptAir', 756, TIMESTAMP('2012-09-30 03:00:00'), '1h10m', 43), 
('PA006', 'ABQ', 'DEN', 'PromptAir', 756, TIMESTAMP('2012-09-30 04:00:00'), '1h10m', 28), 
('PA007', 'DEN', 'ATL', 'PromptAir', 536, TIMESTAMP('2012-09-30 05:00:00'), '2h55m', 78), 
('PA008', 'ATL', 'DEN', 'PromptAir', 536, TIMESTAMP('2012-09-30 06:00:00'), '3h10m', 21), 
('RA981', 'FRA', 'BOM', 'RainvilleAir', 700, TIMESTAMP('2012-09-30 07:00:00'), '9h30m', 120), 
('RA982', 'BOM', 'FRA', 'RainvilleAir', 700, TIMESTAMP('2012-10-06 08:00:00'), '9h10m', 99), 
('RA983', 'DAL', 'FRA', 'RainvilleAir', 800, TIMESTAMP('2012-10-06 09:00:00'), '10h35m', 43), 
('RA984', 'FRA', 'DAL', 'RainvilleAir', 800, TIMESTAMP('2012-10-06 10:00:00'), '10h55m', 95), 
('RA985', 'DEN', 'ATL', 'RainvilleAir', 536, TIMESTAMP('2012-10-06 11:00:00'), '3h55m', 5), 
('RA986', 'ATL', 'DEN', 'RainvilleAir', 536, TIMESTAMP('2012-10-06 12:00:00'), '4h10m', 5), 
('RA987', 'DEN', 'ABQ', 'RainvilleAir', 756, TIMESTAMP('2012-10-11 13:00:00'), '2h10m', 7), 
('RA988', 'ABQ', 'DEN', 'RainvilleAir', 756, TIMESTAMP('2012-10-11 14:00:00'), '2h10m', 11), 
('BA001', 'SFO', 'DEN', 'BeethAir', 387, TIMESTAMP('2012-10-11 15:00:00'), '20m', 50), 
('BA002', 'SFO', 'LHR', 'BeethAir', 1645, TIMESTAMP('2012-10-14 16:00:00'), '11h65m', 22), 
('BA003', 'SFO', 'LAX', 'BeethAir', 99, TIMESTAMP('2012-10-14 17:00:00'), '30m', 7), 
('BA004', 'LAX', 'SFO', 'BeethAir', 99, TIMESTAMP('2012-10-14 18:00:00'), '40m', 10) ;

-- Query the database to dump the contents of the products table.
SELECT * FROM FREELEC.flights ;

exit ;
