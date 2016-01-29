#!/bin/sh

# copy files
mkdir /usr/local/activehome
mkdir /usr/local/activehome/bower_components
chmod +rw /usr/local/activehome/bower_components
mkdir /usr/local/activehome/properties
mkdir /var/log/activehome
chmod +rw /var/log/activehome
cp main.kevs /usr/local/activehome/
cp org.kevoree.platform.standalone-5.2.5.jar /usr/local/activehome/

# set services
ln -s ws /etc/init.d/ws
chmod +x /etc/init.d/ws
ln -s activehome /etc/init.d/activehome
chmod +x /etc/init.d/activehome