# LifecycleKt [![](https://jitpack.io/v/TinoGuo/LifecycleKt.svg)](https://jitpack.io/#TinoGuo/LifecycleKt)

this library help developers to handle lifecycle management in coroutines of Android

inspire from [here](https://hellsoft.se/simple-asynchronous-loading-with-kotlin-coroutines-f26408f97f46)

## Installation
```gradle
allprojects {
	repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```gradle
implementation 'com.github.TinoGuo:LifecycleKt:v1.0'
```

## Usage
```kotlin
load {
    someIOThings()
    //of course you can get the state of coroutines from here
} then {
    someUIThins()
}
```
code is very simple, just enjoy it!

Welcom PR!

License
-------

    Copyright 2018 TinoGuo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
