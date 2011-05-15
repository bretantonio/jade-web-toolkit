#!/bin/sh

export JADE_HOME=$HOME/jade/3.4b/leap/j2se
export CLASSPATH=$JADE_HOME/lib/JadeLeap.jar:$JADE_HOME/lib/crimson.jar:$HOME/JSA/classes

java -cp $CLASSPATH jade.Boot -name test -gui -nomtp isbnholder:jsademos.booktrading.IsbnHolderAgent\(isbn.txt\)\;bookseller1:jsademos.booktrading.BookSellerAgent\(seller1.txt\,isbnholder\)\;bookseller2:jsademos.booktrading.BookSellerAgent\(seller2.txt\,isbnholder\)\;bookbuyer:jsademos.booktrading.BookBuyerAgent\(isbnholder\,bookseller1\,bookseller2\)
