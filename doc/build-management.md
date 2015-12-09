# Build Management

Build management is handled with gradle. The three projects `rapanui.dsl`, `rapanui.core` and `rapanui.ui` each have a `build.gradle` script, plus there's a `settings.gradle` in `src/` to manage the multt-project build.

**All gradle commands should be run from within `src/`!**

## Setup

After cloning the repository, you first have to build the eclipse files with `gradle eclipse`. This is also required for building the projects.

**Note:** Running `gradle eclipse` again may result in a build path problem in `rapanui.dsl` because there are duplicate entries for the source folders. To fix this, remove the source folder entries and add them again in Eclipse.

## Build

To build the projects, simply run `gradle build`, or to do a clean build, `gradle clean build`. The entire application can also be packaged into a so-called "fat jar" that contains all necessary dependencies by running `gradle shadowJar`.

## Run

The application can also be executed without creating a "fat jar" by simply running `gradle run`.