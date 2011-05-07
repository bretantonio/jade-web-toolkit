FIPA Message Representation in Bit-Efficient Encoding package.
Version 0.1.9

INTRODUCTION
This package contains a Java implementation of FIPA ACL Message
Representation in Bit-Efficient Encoding. 

This version is also MIDP2.0/CLDC1.1 compatible.


LICENSES
See GNU LESSER GENERAL PUBLIC LICENSE (lgpl.txt) file for license information.

The library files (lib-directory):

BEFipaMessage jar files for J2SE and MIDP

JUnit (junit-4.1.jar) -  distributed under Common Public License Version 1.0 see 
http://www.junit.org for source and license

Antenna (antenna-bin-0.9.14.jar) - distributed under GNU LESSER GENERAL PUBLIC LICENSE 2.1,
please see lgpl.txt and the homepage http://antenna.sourceforge.net/ for source code.


SOFTWARE VERSIONS
This version of the package was tested using the following software:
  - J2SE 1.5 (1.5.0_12-b04)
  - MIDP 2.0/CLDC1.1
  - JADE-LEAP 3.5

REQUIREMENTS

Java SDK 1.5 (Java 1.6 is not recommended for now as it may cause problems when compiling LEAP)

These must be installed on your system:
1. Apache ant (http://ant.apache.org)
2. JADE 3.5 or later (http://jade.tilab.com/)

Optional requirements in order to compile for J2ME:
1. Java Sun Wireless Toolkit 2.2 or later (http://java.sun.com/products/sjwtoolkit/)
2. JADE 3.5 and LEAP add-on (source distribution) for JADE 3.5, installed and configured to home directory of JADE   

 
INSTALLING

Unzip the BEFipaMessageAddOn.zip to the home directory of JADE. 
You should now have the following directory structure:
- jade
  - add-ons
     - BEFipaMessage
     - (possibly some other add-ons of JADE)
  - ...
  - leap
  - ...


COMPILING

If you haven't compiled JADE, compile it first. 

If you want to compile for J2ME MIDP, also compile LEAP-add-on MIDP version first. In this case you 
really MUST compile the LEAP project (source distribution) first, not just unzip it.


Commands to compile and use this project: 
    ant compile	      - compile J2SE files
    ant lib             - create J2SE .jar-files 
    ant doc		- generate javadoc to doc/api
    ant dist	        - create distribution package to your jade/add-ons directory
    ant test		- run JUnit tests (may take some time)	

    ant lib-midp        - create MIDP .jar-files 
    ant run-midp        - run a MIDP example in emulator.

You will find compiled jar-files in lib-directory.


CONFIGURATION AND USAGE

In order to use BE-ACL codec, the BEFipaMessage.jar must be added to the
classpath when starting( either by including it into the $CLASSPATH environment
variable - %CLASSPATH% under windows or by specifying it on the command
line ). 

Here is an example of how you would start the platform assuming you
are in the root of the Jade directory:
java -classpath ./lib/jade.jar:./lib/jadeTools.jar:./lib/iiop.jar:./add-ons/BEFipaMessage/lib/BEFipaMessage.jar
jade.Boot -aclcodec cascom.fipa.acl.BitEffACLCodec (for Unix)

or
java -classpath ./lib/jade.jar;./lib/jadeTools.jar;./lib/iiop.jar;./add-ons/BEFipaMessage/lib/BEFipaMessage.jar
jade.Boot -aclcodec cascom.fipa.acl.BitEffACLCodec (for Windows)

More aclcodecs can be indicated separated by a ';'


PACKAGE CONTENTS
Enclosed in this package is the source code and binaries (jar file(s)) 
for the bit-efficient FIPA message representation, and some 
documentation. 

GETTING STARTED
examples/*.java :-)


See also BEFipaMessage.html file for how to use bit-efficient encoding 
when programming agents.



--
Heikki Helin  (firstname.j.lastname@teliasonera.com)
Ahti Syreeni (firstname.lastname@teliasonera.com)
