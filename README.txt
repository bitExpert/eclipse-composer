README
======

What is Eclipse Composer?
----------------

Eclipse Composer offers an integration of the PHP Composer Dependency Management
Tool into the Eclipse IDE.
The plugin contributes a context menu to the project explorer view. Right click
on a composer.json file and choose External Tools > Composer > Install or
External Tools > Composer > Update

Notes
--------

By default the path to the PHP executable is guessed. On a Windows system php.exe
is used while on a Unix system php is used. If this does not work for you this
changed by changed on the Composer Plugin Preference Page.

Besides the composer.json file you will also need to install the composer.phar
file in the respective workspace project.To install composer.phar follow the
guideline on the Composer website:
http://getcomposer.org/doc/00-intro.md#installation

The plugin can be obtained at the following update site:
http://update.bitexpert.de/eclipse/3.5/