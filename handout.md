# Hands-on exercise "Extending SAP S/4HANA" – SAP Cloud Platform Customer Forum

In the following exercise, you will create an extension to SAP S/4HANA Cloud on SAP Cloud Platform.

## Task 0: Setup

### Create and configure account
1. Visit https://cloudplatform.sap.com/index.html and register for a free trial.
2. After registering, log on to https://account.hanatrial.ondemand.com/cockpit/.
3. Click on the button to start a Cloud Foundry trial.
Select Frankfurt as the region and confirm the dialog.
3. Go back to the starting page and also start a Neo trial.
4. In the Neo subaccount, go to Services and look for the tile *SAP Web IDE Full-Stack*.
Click on that tile and click Enable.
5. Wait for the enabling to finish and click *Go to Service*.
6. In SAP Web IDE, go to Preferences in the menu bar on the left (gear icon) and choose *Cloud Foundry*.
Select API Endpoint `https://api.cf.eu10.hana.ondemand.com`, enter your credentials, and click Save.

### Prepare the application
1. Return to the Home screen of SAP Web IDE and choose *Clone from Git repository*.
2. Enter URL `TODO` and click Clone. 
3. Wait for the success message that cloning was successful.
Then, go to the Development area (code icon in the left menu bar).
7. Open the folder *TODO* and, therein, open the file mta.yaml.
8. In the editor, replace each of the two occurrences of `99` (first and last line) with a unique number.
9. Save your changes (*Ctrl+S* or use *File > Save*).

### Important notes when working in SAP Web IDE
* Always remember to save changes in open editors.
You can also set *Preferences > Code Editor > Automatically save changes*.
* Always stop the application and then start to redeploy.
Otherwise, Web IDE will continue to launch the old version.

## Task 1: Deploy the application
1. Right-clik on the folder *TODO* and choose *Build > Build*.
2. Wait until the build has completed and you see a new folder *mta_archives* with one file *scp-london...mtar*.
3. Right click on that file and choose *Deploy > Deploy to SAP Cloud Platform*.
4. Verify that the endpoint and Cloud Foundry space from before are selected and click Deploy.

The deployment will take a few minutes.
You can continue with the next step.

## Task 2: Configure connection to SAP S/4HANA
We will now set up the connection to a sample SAP S/4HANA system.
This is not a real system and only used for the purpose of this demo.

1. Open https://account.hanatrial.ondemand.com/cockpit/#/region/cf-eu10/overview
2. Select your global account for the trial.
This will look like p20xxxxxxxxtrial.
3. Select the subaccount *trial*.
4. From the menu on the left, select *Connectivity > Destinations*.
5. Create a *New Destination* with the following properties and click Save.
```
Name=ErpQueryEndpoint
Type=HTTP
URL=https://my-sample-sap-s4hana-system.cfapps.eu10.hana.ondemand.com/
ProxyType=Internet
Authentication=NoAuthentication
```

## Task 3: Run application
1. Return to SAP Web IDE.
You should now see a success message for the deployment that states "... application has been created."
2. Click on the button Open next to this message.
(If you do not see this message or button, look in the console of SAP Web IDE for the application URL.
It will be similar to `https://p20xxxxxxxxtrial-trial-dev-srv.cfapps.eu10.hana.ondemand.com`)
3. On the Welcome page of your application, click on the link *Address Manager*.
4. Explore the application.
Try to find the features that are not yet working.
A few of those will be fixed in the next section.

## Task 4: Adapt application
As you can see when running the application, currently the detail screen does not show any value for the position of each business partner.
Also, it is currently not possible to create a new address.
In the following, we are going to fix these issues in the source code of the application.

### Issue 1: Retrieve the position of each business partner
In this case, the position of a business partner is stored in the *search term* property.
We need to select this property when retrieving details of a business partner from SAP S/4HANA, using the SAP Cloud SDK.
1. Expand the folder *srv > application > src > main > ... > addressmgr > commands* and open the file *GetSingleBusinessPartnerByIdCommand.java*.
2. The method responsible for retrieving details of each business partner starts in line 28.

As of now, the property *search term* is not being selected as part of the OData request.
Thanks to the SAP Cloud SDK, adding this property to the list of selected properties is easy:
1. Position the cursor behind the line `BusinessPartner.FIRST_NAME,`.
Add a new line that contains `BusinessPartner.SEARCH_TERM1,`.
Mind the commas!
2. Save the file.
3. To test-run the change, right click on the folder *srv* and choose *Run > Java Application*.
This while take a couple of minutes.
You can explore the source code of the application while you are waiting.
4. When the application has started, click on the displayed link (in the header of the Run Console shown at the bottom of SAP Web IDE).
5. Verify that the position is now correctly displayed.

### Issue 2: Creating new addresses
The code for creating a new address in the SAP S/4HANA system has not yet been implemented.
In the following, we will use the SAP Cloud SDK to create an address in SAP S/4HANA via the corresponding API.
Again, the SAP Cloud SDK makes it possible to write the desired behavior in a simple and easy-to-understand manner. 

1. Open the file *CreateAddressCommand.java* in the *commands* folder mentioned above.
2. Locate the `run` method in line 26.
Replace the current method body (`return null;`) with the following:
```java
return service.createBusinessPartnerAddress(addressToCreate).execute();
```
3. Stop the currently running test application with the corresponding icon in the Run Console.
4. When the application has stopped, click on the start icon to re-run the applicaton.
5. Wait for the application to start and verify that you are now able to create an address.

### Redeploy the application
When we are content with the fixes in our test deployment, we can redeploy the full application.
Usually, we would a continuous delivery pipeline for this. 
Here, manually execute the steps from [Task 1](#task-1-deploy-the-application) (Build & Deploy) again.

## Task 4: Include ML
There is one further feature that is already implemented in the application with the help of the SAP Cloud SDK.
As of now, it is not enabled though: a machine-learning based translation of the position of a business partner.
To enable it, all we need to do is bind our application to the SAP Leonard Machine Learning Foundation service on SAP Cloud Platform.

1. Return to your account in the SAP Cloud Platform cockpit (https://account.hanatrial.ondemand.com/cockpit/#/region/cf-eu10/overview).
2. Navigate to your subaccount *trial* and select *Spaces* from the menu on the left.
3. Click on the space *dev*.
4. Select the application *srv* from the list of applications.
5. Select *Service Bindings* from the menu.
6. Click on *Bind Service*.
7. Click Next and select the service *SAP Leonardo Machine Learning* (`ml-foundation-trial-beta`).
8. Click Next three times.
Enter *my-ml-service* as instance name on the last screen of the dialog.
9. Click Finish.
10. Go back to the Overview and click Restart.
11. After the application has restarted successfully, translate the position of any business partner by clicking on the link.