# PET

Post-Editing Tool.

## Build

Requires `maven2` and `jdk 1.6`.

    maven compile
    maven package

This will create a jar with dependencies under `target`.
Use `run.sh (run.bat)` and `pej.sh (pej.bat)` to start the tools.

IMPORTANT: although `nbproject` has not been removed yet (just so people can open the project with NetBeans), building with `ant compile jar` (which will use `build.xml` in the root directory) is not recommended, unless you know enough to compose the correct command lines (with all dependencies) that replace `run.sh (run.bat)` and `pej.sh (pej.bat)`.

## Usage

For a standard run:

    ./run.sh

Overloading pec.meta:

    ./run.sh path-to-my-pec.meta

On Windows, double-click `run.bat`.


# PEJ

1. Graphical interface

        ./pej.sh

On Windows, double-click `pej.bat`.

2. Command line interface

    For instructions:

        ./pej.sh -h

    For an example:

        ./pej.sh -id example-job -pej example/demo/A1/example-job -s example/plain/source.txt europarl -R example/plain/pe.txt example/plain/pe.attr -T example/plain/target.txt example/plain/target.attr -units example/plain/units.attr -Dwhy=showing-how-to

3. API

        pet.pej.PEJBuilder
