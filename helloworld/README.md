HelloWorld Servlet example
=====================================

###### Download and configure Wildfly 22
Download Wildfly 22, and configure as follows: 
URL : http://localhost:8080/helloworld/
JRE : Java SDK 11
![image](https://user-images.githubusercontent.com/12558275/223874594-11d70ff8-af12-436b-bd45-fdcc6c802c6f.png)

In the tab Deployment, do not forget to use + button to add the war that will be deployed at the server startup
![image](https://user-images.githubusercontent.com/12558275/223874824-5bc10c82-7838-4534-937d-3096be4211ce.png)

###### Database path configuration
For the database, you need to change the variable PATH_DB in the class UploadServlet.java, as illustrated in the image below :

![image](https://user-images.githubusercontent.com/12558275/223875743-a34f7cc8-95aa-4cc4-9cc2-e948f594bb89.png)

###### Deploy
Once finishing all previous steps, you can execute the following cmd, which generates the war file that will be used for the server deployment
```shell
mvn clean install
```
As a result, a war file (helloworld.war) will be generated inside of your "{pathToYourProject}/target" folder 

###### Deploy server Wildfly and test
If you use IDE IntelliJ, just click on one of the green buttons as shown in the image to start your server
![image](https://user-images.githubusercontent.com/12558275/223875256-e4e7268b-d680-4d19-93b5-372c5ba55958.png)

By clicking the following URL : 
```shell
[http://localhost:8080/helloworld/hello](http://localhost:8080/helloworld/upload.jsp)
```
You should see a page that looks like this

![image](https://user-images.githubusercontent.com/12558275/223875402-1ad7a30c-45e6-4903-9650-9e134bf4064e.png)

You are invited to upload your millenium json file and empire json file

Then, you can click on the button "Compute odd", that will give you the result of odd, as follows 
![image](https://user-images.githubusercontent.com/12558275/223876299-474caf97-616c-4531-bbc4-6793aff14756.png)
