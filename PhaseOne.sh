find -path "*Input Files/*" -not -path "./Schedule Input Files/*" -exec bash -c '
    while (( "$#" != "0" )); do
        # Display current file
        echo ""
        echo "Processing File: $1"
        
        # Run PhaseOne against each file
        file=$(cat "$1")
        cd Phases
        echo "$file" | java PhaseOne -o
        cd ..
        
        # Move to the next argument
        shift
    done
' bash {} +
