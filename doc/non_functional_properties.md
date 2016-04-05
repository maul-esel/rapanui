## 7 Non-Functional Properties

### 7.1 Performance

As expected, the suggestion generation using the rule system poses the biggest performance problem. Especially considering the algorithm recurses, the complexity is exponential in the recursion depth. To achieve reasonable performance, the recursion depth had to be heavily restricted. Of course, this also restricts the results produced by the algorithm. While the restriction makes sense since the application's intention is not to find the entire proof, better results might still be achieved with deeper recursion.

One approach to avoid the performance problem in this scenario would be to replace the recursion depth counter with a recursion *cost*, where rule searches have a higher cost. Alternatively, the rule search algorithm could be configured to only recurse to the other algorithms (or a more complex recursion structure).

The rule search itself may be optimized in several ways:

* Instead of iterating through all rules' conclusions, a data structure could be devised to locate relevant conclusions faster.
* When justifying a rule's premises, those that can be completely or nearly completely translated should be justified first.
* Once the translation of a rule is completely known, one justification per premise suffices.

In general it would be interesting to see if caching justification searches has any beneficial effect.

### 7.2 Project Management

Development was tracked in a git repository [hosted on github](https://github.com/maul-esel/rapanui). In addition to this documentation, extensive javadoc is also provided.

Build management is handled with gradle. The three projects `rapanui.dsl`, `rapanui.core` and `rapanui.ui` each have a `build.gradle` script, plus there's a `settings.gradle` in `src/` to manage the multi-project build.

**All gradle commands should be run from within `src/`!**

#### Setup

After cloning the repository, you first have to build the eclipse files with `./gradlew eclipse`. This is also required for building the projects (an eclipse installation is not required).

**Note:** Running `./gradlew eclipse` again may result in a build path problem in `rapanui.dsl` because there are duplicate entries for the source folders. To fix this, remove the source folder entries from the project file and add them again in Eclipse.

#### Build

To build the projects, simply run `./gradlew build`, or to do a clean build, `./gradlew clean build`. The entire application can also be packaged into a so-called "fat jar" that contains all necessary dependencies by running `./gradlew shadowJar`. `./gradlew javadoc` generates the javadoc documentation and places it in the `build/docs/javadoc` subfolder of each project.

#### Run

The application can also be executed without creating a "fat jar" by simply running `./gradlew run`.
