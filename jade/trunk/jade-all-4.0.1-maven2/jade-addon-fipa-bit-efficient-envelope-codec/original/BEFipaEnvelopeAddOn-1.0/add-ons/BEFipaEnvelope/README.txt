FIPA Bit-efficient Message Envelope Codec
Version 1.0

INTRODUCTION
This package contains a Java implementation of FIPA Message Envelope
Representation in Bit-Efficient Encoding. Currently, the codec implements 
cascom.fipa.envelope.EnvelopeCodec -interface and this is used only by 
jade.mtp.http2 -implementation of CASCOM Agent Platform.


LICENSE

See GNU LESSER GENERAL PUBLIC LICENSE (lgpl.txt) file for license information.

The library files (lib-directory):

BEFipaEnvelope jar files for J2SE and MIDP

JUnit (junit-4.1.jar) -  distributed under Common Public License Version 1.0 see 
http://www.junit.org for source and license

Antenna (antenna-bin-0.9.14.jar) - distributed under GNU LESSER GENERAL PUBLIC LICENSE 2.1,
please see lgpl.txt and the homepage http://antenna.sourceforge.net/ for source code.


SOFTWARE VERSIONS
This version of the package was tested using the following software:
  - JDK 1.5.0  (1.5.0_12-b04)
  - Apache Ant 1.6.5
  - Sun Wireless Toolkit 2.2 (MIDP 2.0/CLDC1.1)
  - JADE 3.5

For compiling to J2ME MIDP
  - Leap add-on (source distribution) 3.5 installed to Jade home directory


REQUIREMENTS

Java SDK 1.5 (1.6 is not recommended for now because it may cause problems when compiling LEAP).

These must be installed on your system:
1. Apache ant 1.6.5 (http://ant.apache.org)
2. JADE 3.5 (http://jade.tilab.com/) plus the BEFipaMessage add-on

Optional requirements to compile for J2ME MIDP:
1. Java Sun Wireless Toolkit 2.2 or 2.3 (http://java.sun.com/products/sjwtoolkit/)
2. LEAP add-on (source distribution) for JADE 3.5, installed, configured and compiled for J2SE and MIDP. 
Note: LEAP 3.5 must be installed to home directory of JADE 3.5, not add-ons -directory and it
really must be compiled (with java 1.5, not 1.6)


INSTALLING 

Unzip the BEFipaEnvelope.zip to the home directory of JADE. 
You should now have the following directory structure:
- jade
  - add-ons
     - BEFipaEnvelope
     - (possibly some other add-ons of JADE)
  - ...
  

COMPILING

If you haven't compiled JADE, compile it first. 

If you want to compile for J2ME MIDP, make sure that LEAP add-on is compiled for J2SE and MIDP (in case
BEFipaEnvelope will be compiled to MIDP). Please note 
that if you haven't configured LEAP add-on before, you may have to edit the
file add-ons/leap/buildLEAP.properties first and you really must compile 
the LEAP project to MIDP and J2SE, only
unzipping LEAP is not enought. 

Commands to compile and use this project: 
    ant lib         - for J2SE-version of BEFipaEnvelope
    ant doc         - generate JavaDoc to doc/api
    ant test        - to run tests in J2SE

    ant lib-midp    - generate MIDP-version of BEFipaEnvelope
    ant test-midp   - run tests in emulator in MIDP (will take some time)

You will find compiled jar-files in lib-directory.


PACKAGE CONTENTS
- source code for BEFipaEnvelope
- jar files for BEFipaEnvelope
- API documentation
- ant script
- licence and README-file



--
Heikki Helin  (firstname.j.lastname@teliasonera.com)
Ahti Syreeni (firstname.lastname@teliasonera.com)