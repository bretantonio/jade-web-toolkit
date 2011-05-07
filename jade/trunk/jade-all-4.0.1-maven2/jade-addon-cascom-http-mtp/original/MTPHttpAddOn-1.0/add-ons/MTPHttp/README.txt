FIPA HTTP MTP and CASCOM Messaging Gateway for JADE and CASCOM Agent Platforms

Version: 1.0 


INTRODUCTION

This package contains a Java implementation of FIPA HTTP Transport protocol
for Jade agent platforms. This is modified version of original jade.mtp.http 
- implementation, which was made MIDP - compatible to be used with 
CASCOM Agent Platform. The J2SE version can be used with JADE and JADE-LEAP 
J2SE platforms. Because the structure of the MTP was modified and secure
connections are not yet provided the package name is jade.mtp.http2.

In addition, this package contains additional sources for CASCOM Messaging Gateway,
which makes possible to communicate with CASCOM Agent platform even if it is
behind firewall. That is usually case when mobile phone is in UMTS network.


For changes, see CHANGE.txt.


LICENSES

See GNU LESSER GENERAL PUBLIC LICENSE (lgpl.txt) file for license information.

The library files (lib-directory):

CASCOM HTTP-MTP and Gateway jar files

JUnit (junit-4.1.jar) -  distributed under Common Public License Version 1.0 see 
http://www.junit.org for source and license

Antenna (antenna-bin-0.9.14.jar) - distributed under GNU LESSER GENERAL PUBLIC LICENSE 2.1,
please see lgpl.txt and the homepage http://antenna.sourceforge.net/ for source code.



SOFTWARE VERSIONS
This version of the package was tested using the following software:
  - JDK 1.5 
  - MIDP 2.0/CLDC1.1
  - JADE 3.5 
  - Apache Ant 1.6.5


REQUIREMENTS

Java SDK 1.5 (1.6 is not recommended if you compile the LEAP)

These must be installed on your system:
1. Apache ant 1.6.5 (http://ant.apache.org)
2. JADE 3.5 (http://jade.tilab.com/)  plus the BEFipaMessage add-on and the BEFipaEnvelope add-on

Optional requirements to compile for J2ME:
1. Java Sun Wireless Toolkit 2.2 or later (http://java.sun.com/products/sjwtoolkit/)
2. LEAP add-on for JADE 3.5, installed and configured to JADE home directory and compiled at least into MIDP
   

INSTALLING 

Unzip the MTTPHttp.zip to the home directory of JADE. 
You should now have the following directory structure:
- jade
  - add-ons
     - MTPHttp
     - (possibly some other add-ons of JADE)
  - ...
  - leap
  - ...

PACKAGE CONTENTS
- source code for MTPHttp and CASCOM Messaging Gateway.
- jar files for MTPHttp and CASCOM Messaging Gateway.
- API documentation
- ant script
- licence and README-file


GETTING STARTED

type: 
    ant lib         - for J2SE-version of MTPHttp (including client classes for contacting CASCOM Messaging Gateway)
		      Also generates cascomgw.jar and cascomgw-standalone.jar

    ant test        - to run tests in J2SE
    ant doc 	    - to generate JavaDoc to doc/api
    ant run	    - to run your Jade with the MTP

    ant lib-midp    - for MIDP-version of MTPHttp

    The builded jar-files will appear in the subdirectory lib.

To start your jade with the MTP via commandline, use following parameters:

    -mtp jade.mtp.http2.MessageTransportProtocol
    -aclcodecs cascom.fipa.acl.BitEffACLCodec

Remember to include at least the following jar-files in classpath:
    - lib/MtpHttp.jar
    - lib/BEFipaEnvelope.jar
    - lib/BEFipaMessage.jar



Please see the MTPHttp.html file for further instructions on how to use MTTPHttp and CASCOM Messaging Gateway.


--
Heikki Helin <firstname.j.lastname@teliasonera.com>
Ahti Syreeni <firstname.lastname@teliasonera.com>

