# Usage

##Parsing post-editing effort indicators


    python perparse.py antonio examples/antonio.per --hter /home/waziz/tools/ter/tercom-0.7.25/tercom.7.25.jar --assessments necessity > antonio.table
    column -s$'\t' -n -t antonio.table | less -NS


Explaining:

    antonio - post-editor id
    examples/antonio.per - PET's output file (.per)
    --hter <path> - path to HTER (only relevant in tasks where MTs are provided)
    --assessments <list> - explicit assessment ids (only relevant if explicit assessments were defined for the task)
    > antonio.table - tab-separated lines


##Indicators:


    who - annotator
    type - type of post-editing unit (HT - human translation, PE - post-editing)
    src - source id
    sys - system id
    time - post-editing time
    slen - number of tokens in source
    mlen - number of tokens in MT
    plen - number of tokens in PE
    letters - letters typed
    digits - digits typed
    spaces - white chars typed
    symbols - special symbols typed
    navigation - navigation keystrokes
    erase - erasing keystrokes
    commands - commands entered (ctrl+c, etc.)
    visible - visible keys typed
    keystrokes - "total" keystrokes (except commands and navigation)
    allkeys - total keystrokes
    insertions - PET insertions
    deletions - PET deletions
    substitutions - PET substitutions
    shifts - PET shifts

With --hter option you also get

    hter - HTER score 
    hter_ins - HTER insertions
    hter_del - HTER deletions
    hter_sub - HTER subs
    hter_shift - HTER (phrase) shifts
    hter_wdsh - HTER (word) shifts
    hter_errors - HTER errors
    hter_words - HTER length

With --assessments you also get

    assessing - assessing time
    necessity - assessment (in this example I used --assessments necessity)

You always get

    S - source text
    MT - MT text
    PE - PE text


