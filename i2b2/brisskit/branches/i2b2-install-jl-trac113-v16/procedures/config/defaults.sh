#!/bin/bash
#
# Default settings used by scripts within the bin directory
# 
#-------------------------------------------------------------------

# Log file name:
JOB_LOG_NAME=job.log

# Database: either oracle or sqlserver
DB_TYPE=sqlserver

# Name of directory to hold archives of source, 
# demo data and others acquired from elsewhere
ACQUISITIONS_DIRECTORY=acquisitions

# Name of directory to hold official i2b2 source 
# (Will be downloaded here)
SOURCE_DIRECTORY=source

# Name of directory to hold data for the i2b2 hive and two demo systems
# (Will be downloaded here)
DATA_DIRECTORY=data

# Url of BRICCS site that holds i2b2 source and data archives
ACQUISITION_BASE_URL=http://data.briccs.org.uk/i2b2/server

# Name of source zip file available from $ACQUISITION_BASE_URL 
SOURCE_ZIP=i2b2core-src-155-briccs-1.0.zip

# Name of data zip file available from $ACQUISITION_BASE_URL 
DATA_ZIP=i2b2demodata-15-briccs-1.0.zip

# Name of axis2 war file used to update JBoss, available from $ACQUISITION_BASE_URL 
AXIS_WAR=axis2.war

# Location for installation of apache web server files (used for i2b2 web client)
HTML_LOCATION=/local/www/htdocs

#
# URL to check i2b2 services are up and available
# This should be a full address. Don't use localhost.
# The port number depends upon whether you have encryption set up.
# If not, the default is 8080
LIST_SERVICES_URL=https://YOUR_SERVER_HERE:8443/i2b2/services/listServices

# Location of the i2b2 file repo cell (will be created by the install)
FILE_REPO_LOCATION=/local/FRC

# Custom space for the install workspace (if required)
# If not defined, defaults to I2B2_INSTALL_HOME/work
#I2B2_INSTALL_WORKSPACE=?

#---------------------------------------------------------------------------------
# Set these as required for your environment...
#---------------------------------------------------------------------------------
JBOSS_HOME=/usr/local/jboss-4.2.3.GA-brisskit
ANT_HOME=/usr/local/ant
JAVA_HOME=/usr/local/jdk
