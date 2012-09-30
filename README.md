# PET

Post-Editing Tool.

## Build

1. ant compile jar javadoc

## Usage

For a standard run:

    ./run.bat

Overloading pec.meta:

    ./run.bat path-to-my-pec.meta


# PEJ

1. Graphical interface

    ./pej.bat

2. Command line interface

    For instructions:

        ./pej.bat -h

    For an example:

        ./pej.bat -id example-job -pej example/demo/A1/example-job -s example/plain/source.txt europarl -R example/plain/pe.txt example/plain/pe.attr -T example/plain/target.txt example/plain/target.attr -units example/plain/units.attr -Dwhy=showing-how-to

3. API

    pet.pej.PEJBuilder
