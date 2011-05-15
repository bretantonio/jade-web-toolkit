@set target=%1

@if ""%1"" == """" @set target=j2se

@echo Running the JSA demo BOOKTRADING in %target% environment

java -cp "../../../bin;../../../../../leap/%target%/lib/JadeLeap.jar" jade.Boot -name test -gui -nomtp isbnholder:jsademos.booktrading.IsbnHolderAgent(isbn.txt);bookseller1:jsademos.booktrading.BookSellerAgent(seller1.txt,isbnholder);bookseller2:jsademos.booktrading.BookSellerAgent(seller2.txt,isbnholder);bookbuyer:jsademos.booktrading.BookBuyerAgent(isbnholder,bookseller1,bookseller2)
