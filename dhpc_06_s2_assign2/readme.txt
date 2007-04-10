#Author: Bo CHEN Student ID: 1139520
#10th, Nov. 2006

Note:
1.Without setting the AXISCLASSPATH as CLASSPATH,the command for running Client will be:
java Client -cp .:$AXISCLASSPATH http://localhost:8080/axis/services/datamining /users/teaching/subjects/honours/dhpc/Pi000.txt
2.Please do not change the locations of original files and the directory "datamining", otherwise compiling process won't be done correctly


Steps needed to run the program:
1.Running the tcsh script
in the command line, input ./TcshScript.sh

After running the script, the services datamining will have been deployed and the Tomcat server will have been started.
You can now execute the Standalone or Client program.

2.Running the program
input: java Standalone Pi000.txt
or input: java Client http://localhost:8080/axis/services/datamining /users/teaching/subjects/honours/dhpc/Pi000.txt
(with AXISCLASSPATH being set as CLASSPATH)
