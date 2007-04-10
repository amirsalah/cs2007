#!/bin/tcsh
#tcsh script for the DHPC assignment 2: Web services
#Author: Bo CHEN Student ID: 1139520
#10th, Nov. 2006
#Run the script: ./TcshScript.sh

#Set local variables for compiling
set CATALINA_HOME=$CATALINA_HOME
set AXISCLASSPATH=$AXISCLASSPATH


echo '**********Shutdown Tomcat server**********'
$CATALINA_HOME/bin/startup.sh
$CATALINA_HOME/bin/shutdown.sh

echo '**********Add activation.jar, ect. just in case ^_^**********'
mv activation.jar $CATALINA_HOME/webapps/axis/WEB-INF/lib
mv mail.jar $CATALINA_HOME/webapps/axis/WEB-INF/lib
mv xmlsec-1.4.Beta1.jar $CATALINA_HOME/webapps/axis/WEB-INF/lib

#Generate datamining interface and its implementation
echo '**********Compile the datamining Interface and its implementation**********'
javac -cp .:$AXISCLASSPATH datamining/*.java

echo '**********Generate wsdl file**********'
java -cp .:$AXISCLASSPATH org.apache.axis.wsdl.Java2WSDL -o dm.wsdl -l"http://localhost:8080/axis/services/datamining" -n urn:datamining -p"datamining" urn:datamining datamining.Datamining

echo '**********Generate Serverside wrapper code & stubs**********'
java -cp .:$AXISCLASSPATH org.apache.axis.wsdl.WSDL2Java -o . -d Session -s -p datamining.ws dm.wsdl

echo '**********Fillin wrapper: overwriting**********'
mv DataminingSoapBindingImpl.java ./datamining/ws/DataminingSoapBindingImpl.java

echo '**********Compile service code**********'
javac -cp .:$AXISCLASSPATH datamining/ws/*.java

echo '**********Package and copy dm.jar to lib**********'
jar cvf dm.jar datamining/*.class datamining/ws/*.class
cp -f dm.jar $CATALINA_HOME/webapps/axis/WEB-INF/lib

echo '**********Deploy the service**********'
cd ./datamining/ws
java -cp .:$AXISCLASSPATH org.apache.axis.client.AdminClient deploy.wsdd

echo '**********Startup Tomcat server**********'
$CATALINA_HOME/bin/startup.sh

echo '**********Compile Client code**********'
cd ..
cd ..
javac -cp .:$AXISCLASSPATH Client.java

echo '**********Compile Standalone code**********'
javac -cp .:$AXISCLASSPATH Standalone.java

exit 2
