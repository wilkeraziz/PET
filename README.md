# PET

Post-Editing Tool.

## Build

    ant compile jar javadoc


Note that on windows ant echos a command line with the classpath set. On windows you can use run.bat and pej.bat to run PET/PEJ since those files already set the classpath variable for you.

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
