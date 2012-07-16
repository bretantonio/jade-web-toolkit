C:\Program Files\Java\jdk1.7.0\db\lib>java -classpath .;derby.jar;derbytools.jar org.apache.derby.tools.ij
-- Ignore the database not created warning if present
connect 'jdbc:derby:FREELEC;create=true' ;

-- First delete the tables if they exist. 
-- Ignore the table does not exist error if present
DROP TABLE FREELEC.flights ;

-- CREATE the products table for Bigdog's Surf Shop
CREATE TABLE FREELEC.flights (
id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
flight_number VARCHAR(10)  NOT NULL,
origin_airport VARCHAR(10)  NOT NULL,
destination_airport VARCHAR(10)  NOT NULL,
carrier VARCHAR(20) NOT NULL,
price INTEGER,
date TIMESTAMP,
flight_type VARCHAR(8),
available_seats INTEGER
) ;


-- Insert 26 rows into the products table
INSERT INTO FREELEC.flights(flight_number, origin_airport, destination_airport, carrier, price, date, flight_type, available_seats)
    VALUES 
('OZ8921', '김포', '제주', '아시아나항공', 400, TIMESTAMP('2012-09-23 12:00:00'), '국내선', 50), 
('MU512', '김포', '상해(홍차오)', '중국동방항공', 2000, TIMESTAMP('2012-09-23 12:00:00'), '국제선', 22), 
('JL92', '김포', '도쿄/하네다', '일본항공', 100, TIMESTAMP('2012-09-23 12:05:00'), '국제선', 37), 
('7C111', '김포', '제주', '제주항공', 100, TIMESTAMP('2012-09-23 12:05:00'), '국내선', 0), 
('KE1335', '김포', '여수', '대한항공', 800, TIMESTAMP('2012-09-23 12:20:00'), '국내선', 14), 
('OZ8813', '김포', '김해(부산)', '아시아나항공', 800, TIMESTAMP('2012-09-28 12:30:00'), '국내선', 4), 
('BX8813', '김포', '김해(부산)', '에어부산', 700, TIMESTAMP('2012-09-28 12:30:00'), '국내선', 97), 
('OZ8923', '김포', '제주', '아시아나항공', 700, TIMESTAMP('2012-09-30 12:30:00'), '국내선', 75), 
('CZ318', '김포', '북경', '중국남방항공', 756, TIMESTAMP('2012-09-30 12:30:00'), '국제선', 43), 
('NH1162', '김포', '도쿄/하네다', '전일본공수', 756, TIMESTAMP('2012-09-30 12:40:00'), '국제선', 28), 
('JL972', '김포', '오사카(간사이)', '일본항공', 536, TIMESTAMP('2012-09-30 12:45:00'), '국제선', 78), 
('OZ8927', '김포', '제주', '아시아나항공', 536, TIMESTAMP('2012-09-30 12:45:00'), '국내선', 21), 
('OZ8929', '김포', '제주', '아시아나항공', 700, TIMESTAMP('2012-09-30 12:50:00'), '국내선', 120), 
('KE1609', '김포', '울산', '대한항공', 700, TIMESTAMP('2012-10-06 12:50:00'), '국내선', 99), 
('TW711', '김포', '제주', '티웨이항공', 800, TIMESTAMP('2012-10-06 12:55:00'), '국내선', 43), 
('KE1113', '김포', '김해(부산)', '대한항공', 800, TIMESTAMP('2012-10-06 13:00:00'), '국내선', 95), 
('ZE215', '김포', '제주', '이스타항공', 536, TIMESTAMP('2012-10-06 13:00:00'), '국내선', 5), 
('LJ335', '김포', '제주', '진에어', 536, TIMESTAMP('2012-10-06 13:10:00'), '국내선', 5), 
('BX8815', '김포', '김해(부산)', '에어부산', 756, TIMESTAMP('2012-10-11 13:30:00'), '국내선', 7), 
('OZ8815', '김포', '김해(부산)', '아시아나항공', 756, TIMESTAMP('2012-10-11 13:30:00'), '국내선', 11), 
('KE1611', '김포', '울산', '대한항공', 387, TIMESTAMP('2012-10-11 13:30:00'), '국내선', 50), 
('KE1229', '김포', '제주', '대한항공', 1645, TIMESTAMP('2012-10-14 13:40:00'), '국내선', 22), 
('261', '김포', '쑹산', '중화항공', 99, TIMESTAMP('2012-10-14 13:45:00'), '국제선', 7), 
('7C113', '김포', '제주', '제주항공', 99, TIMESTAMP('2012-10-14 13:45:00'), '국내선', 120), 
('OZ8931', '김포', '제주', '아시아나항공', 100, TIMESTAMP('2012-10-15 13:55:00'), '국내선', 99), 
('KE1231', '김포', '제주', '대한항공', 101, TIMESTAMP('2012-10-16 14:00:00'), '국내선', 43), 
('KE1115', '김포', '김해(부산)', '대한항공', 102, TIMESTAMP('2012-10-17 14:00:00'), '국내선', 95) ;

-- Query the database to dump the contents of the products table.
SELECT * FROM FREELEC.flights ;

exit ;
