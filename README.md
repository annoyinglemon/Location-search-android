# Location Search for Android

An Android application that lets user search for coordinates of an address.

## Project Overview
This app uses Google Cloud APIs behind the scenes such as [Places](https://cloud.google.com/maps-platform/places) and [Maps](https://cloud.google.com/maps-platform/maps/). That being said, Google Cloud API credential is **required** to run this project. You can either **create a credential** or **use an existing credential**.

### Create a new credential
* Go to https://cloud.google.com/maps-platform/maps/ then click **Get started**
* Check **Maps** and **Places** then hit Continue
* Sign-in with your Google Account
* **Create a new project** then click **OK**
* Create a billing account, this is risk free, then Continue
* Hit **Next**
* Copy the API key, it should start with **AIza**

### Existing credential
* Go to https://console.cloud.google.com then select your existing project on the drop-down above
* On the left side menu, hover **APIs & Services** then click **Credentials**
* Click your desired API key 
* On **Application restrictions**, ensure that **None** or **Android apps** is selected
* On **API restrictions**, ensure that **Geocoding API**, **Maps SDK for Android** and **Places API** are unrestricted
* Copy the API key then hit **Save**

## Running the project
* Clone this project
* Ensure that the **Build Variant** is on **debug**
* Open **key.properties** file then paste the key between quotations
* Run the project





