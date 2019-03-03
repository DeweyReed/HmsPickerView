[![](https://jitpack.io/v/DeweyReed/HmsPickerView.svg)](https://jitpack.io/#DeweyReed/HmsPickerView)

# HmsPickerView

A beautiful little Android view to pick hours, minutes, seconds.

<img src="./images/image.webp" alt="Screenshot" style="width:256px;"/>

## Installation

Step 1. Add it in your root build.gradle at the end of repositories:

```Groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```Groovy
dependencies {
    implementation 'com.github.DeweyReed:HmsPickerView:${version}'
}
```

[![](https://jitpack.io/v/DeweyReed/HmsPickerView.svg)](https://jitpack.io/#DeweyReed/HmsPickerView)

## Usage

```XML
<xyz.aprildown.hmspickerview.HmsPickerView
    android:id="@+id/hmsPickerView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

## License

[LICENSE](./LICENSE)