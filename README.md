This program is my proposed solution to an exercise given by Ciril Group.

It simulates a forest fire propagation as a cell automaton.

# Installation

* Download [Java 21 JDK](https://www.oracle.com/java/technologies/downloads/#java21)
* Add the JDK path to your `%PATH%`/`$PATH` environment variable, e.g. `C:\Program Files\Java\jdk-21\bin`
* Download [JavaFX 21 SDK](https://gluonhq.com/products/javafx/)
* In a console, go to the project root and run:
`javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.web Main.java`,
replacing `%PATH_TO_FX%` with the path where you extracted the JavaFX SDK, or
setting that variable beforehand (`$PATH_TO_FX` on Unix) in your environment
variables.

# Usage

In a console, go to the project root and run:
 `java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.web Main`,
replacing `%PATH_TO_FX%` like previously.