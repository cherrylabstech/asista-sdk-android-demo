# asista-PNS demo
The app demonstrates the usage of the asista PNS Android SDK.
### Using the demo apk
- Create a project in your [Firebase console](https://console.firebase.google.com)
- Navigate to the project _settings_ page, and under the _Cloud Messaging_ tab you will find your **Server key**.
- Use the **Server key** to create an asista PNS app, from which you will get your **APP_KEY**.
- Import this demo app to your AndroidStudio and add the **APP_KEY** as string resource in your android project's **_strings.xml_** file.
```
<string name="PNS_APP_KEY">{your APP KEY}</string>
```
- Run the app(in an emulator/Android device).
- Using the **deviceToken**(you can get your deviceToken from the demo app) or a subscribed **topic**(use the **SUBSCRIBE TO TOPIC/'S** option in the demo app to subscribe to a topic) 
you can send notification to the installed demo apk.

Find full documentaion at asista Developers page.

If any issues found please feel free to create a new issue [here](https://github.com/cherrylabstech/asista-sdk-android-demo/issues).
