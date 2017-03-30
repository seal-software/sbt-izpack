Custom Panel
============

This test project shows howto add custom IzPack panels to the installer.

With IzPack 5 it is now possible to configure a fully qualified class name instead of the awkward requirement to put panels in a specially named JAR file.

The idea is to have a separate SBT project dedicated for custom, IzPack related classes. The generated artifact of this project is then loaded into the installer via the `<jar/>` Element in the installer XML file.