cd Phases
gci '..\Original Input Files\' | foreach {
    echo ''
    echo "Processing File: $_"
    $_ | get-content | java 'PhaseOne'
}

gci '..\Easy Input Files\' | foreach {
    echo ''
    echo "Processing File: $_"
    $_ | get-content | java 'PhaseOne'
}

gci '..\Hard Input Files\' | foreach {
    echo ''
    echo "Processing File: $_"
    $_ | get-content | java 'PhaseOne'
}
cd ..
