# Bugs

1. Every .pej should contain an id

    Some buggy versions of PEJ allowed people to generate .pej files without ids.
    If this happened to you, your .per files didn't have a name, only the extension .per

2. Every unit (task) in a .pej should also contain a numeric id

    Some buggy versions of PEJ allowed people to generate .pej files without ids.
    If this happened to you, you will need to fix your .pej and your .per accordingly

3. If you are using explicit assessments, their ids should have been set to strings without spaces

    If you have spaces there, please use a regular expression to replace them by '_' or other non-blank char

# Parsing .per files


1. You can use r141-readper.pl

    You will need to make sure you have the XML::TreeBuilder and HTML::TreeBuilder installed:

        sudo cpan
        install HTML::TreeBuilder
        install XML::TreeBuilder

    Then you are ready to run the script:
        
        ./r141-readper.pl --per job.per --annotator A1 --track-changes --flagid

    If you have assessments such as: usability_before, usability_after and pe_effort:

        ./r141-readper.pl --per job.per --annotator Yvonne --assessment usability_before --assessment usability_after --assessment pe_effort --track-changes 2> log.txt > totals.txt

2. HTER: if you have TER installed on your machine, let r141-readper.pl known where it is (--ter)

3. The scripts generates some HTML files to ease navigation, you can choose where this files are produced with --html

# HTML

r141-readper.pl will create a bunch of HTML files, then you can generate an HTML summary:

        ./2html.pl MySummary html/ < job.per.summary > job.per.html

