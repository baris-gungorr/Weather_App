# Weather_App

📸 Screenshots

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/baris-gungorr/Weather_App/blob/main/app/images/cloudy.jpg" alt="Hava Durumu" width="200">
    </td>
    <td align="center">
      <img src="https://github.com/baris-gungorr/Weather_App/blob/main/app/images/freeze.jpg" alt="Hava Durumu" width="200">
    </td>
    <td align="center">
      <img src="https://github.com/baris-gungorr/Weather_App/blob/main/app/images/rain.jpg" alt="Hava Durumu" width="200">
    </td>
    <td align="center">
     <img src="https://github.com/baris-gungorr/Weather_App/blob/main/app/images/Sun.jpg" alt="Hava Durumu" width="200">
  </tr>
</table>

👇 Structures Used
- Coroutine
- Retrofit
- View Binding | Data Binding

 ✏️ Dependency
 ```gradle
implementation 'com.google.android.gms:play-services-location:21.0.1'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
```
```groovy
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1'
```
app build.gradle:

```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

buildFeatures {
    dataBinding true
}
```
project build.gradle:

```groovy
plugins {
    id 'com.android.application' version '8.0.2' apply false
    id 'com.android.library' version '8.0.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.8.20' apply false
}
```

❗ Manifest File

```groovy
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>


```

```groovy

👇 API

- (https://openweathermap.org/)
```
```groovy

🔗 Play Store Link

 Download: [Play Store'dan indir]( https://play.google.com/store/apps/details?id=com.barisgungorr.weather_app).
```
