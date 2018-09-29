# wat

Native, C-based Bukkit plugin API. Please don't actually use this.

## Building

### General

This plugin requires Java 11. This is purely to hopefully keep the number of people who try to use this lower. I
recommend [Azule Zulu](https://www.azul.com/downloads/zulu/).

The `JAVA_HOME` environment variable must be set to point to the JDK 11 properly for `cmake` to properly pick up the
correct JDK location at build time.

### Linux

You need the JDK installed and setup as described above, and `cmake`, `make`, and `gcc`. These should all be easy to
install with your package manager.

`./gradlew clean build`

### macOS

You need the JDK installed and setup as described above, and `cmake`, `make`, and `gcc`:

`brew install cmake`

Assuming you have XCode installed already:

`xcode-select --install`

The you should be able to build the plugin:

`./gradlew clean build`

### Windows (MSVC)

Windows is the most complicated setup process, due to its strange environment. You need to have
[Visual Studio 2017](https://visualstudio.microsoft.com/downloads/) installed with the MSVC toolchain. Selecting
`Desktop development with C++` in the Visual Studio installer will select all of the necessary packages.

Open the `x64 Native Tools Command Prompt for VS 2017` Command Prompt configuration from the Start Menu. Visual Studio
creates this to provide a shell with the developer tools pre-defined on the path. Once open, run the command `set`. This
will output all of the environment variables, many of which are not set in a standard Windows environment and are
required for MSBuild. Copy the output of the `set` command and paste it into a file called `env.txt` in the root of this
project. The Gradle build script will read it and use it to set the environment when calling `cmake` and `MSBuild`
commands.

Note you'll need to re-do this setup any time Visual Studio updates.

Make sure the `JAVA_HOME` environment variable is set properly in the above `env.txt`.

Check to make sure you don't have any registry keys in `HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\` pointing to any non Java
11 JDK. This is handled by the `JAVA_HOME` environment variable, so you don't need these registry keys at all, and you
should delete them. Some JDK installers add these keys. Unfortunately, though it shouldn't behave this way, `cmake`'s
`FindJNI` will take the values in the registry above all other configurations and paths. If you don't delete these keys,
it won't select the right JDK and won't build.

It is also important in your `env.txt` file to list your JDK 11 path first, since `cmake` will also take the output of
calling `java -version` above the `JAVA_HOME` environment variable. This isn't what the documentation for `FindJNI` says
or how it should behave, but that is what it does on Windows. The easiest way of doing this is something like the
following:

```
JAVA_HOME=C:\Program Files\Zulu\zulu-11
Path=%JAVA_HOME%\bin;%Path%
```

in your `env.txt`. You'll need to expand those manually for that file, so just use the above as a reference for
structure.

Once all of the above is setup, you should have all of the dependencies necessary to build:

`gradlew clean build`

## Building Native Plugins

Coming soon
