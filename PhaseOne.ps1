cd Phases
gci '..\Original Input Files\' | foreach {
    echo ''
    echo "Processing File: $_"
    $_ | get-content | java 'PhaseOne' -o
}

gci '..\Easy Input Files\' | foreach {
    echo ''
    echo "Processing File: $_"
    $_ | get-content | java 'PhaseOne' -o
}

gci '..\Hard Input Files\' | foreach {
    echo ''
    echo "Processing File: $_"
    $_ | get-content | java 'PhaseOne' -o
}
cd ..
