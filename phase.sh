# Receive CLI arguments (Phase one, two)
selection="$1"
check_time="$2"
csv="$3"

# Run phase one
if [[ "$selection" == "one" ]]; then
    # Display runtime
    if [[ "$check_time" == "-t" || "$check_time" == "--time" ]]; then
        # Format as CSV
        if [[ "$csv" == "-c" || "$csv" == "--csv" ]]; then
            # Print CSV headers
            echo "name,lines,nano"

            find -path "*Input Files/*" -not -path "./Schedule Input Files/*" -exec bash -c '
                # Run PhaseOne against each file
                file_content=$(cat "$1")
                file_name=$(basename "$1")
                let file_length=$(cat "$1" | sed "/^\s*$/d" | wc -l)-1

                cd Phases
                let avg_time=$(echo "$file_content" | java PhaseOne -p 16 -c 7 -t -n 100)
                cd ..

                # Print results
                echo "$file_name,$file_length,$avg_time"
            ' bash {} \;

        # Display default time output
        else
            find -path "*Input Files/*" -not -path "./Schedule Input Files/*" -exec bash -c '
                # Display current file
                echo ""
                echo "Processing File: $1"

                # Run PhaseOne against each file
                file_content=$(cat "$1")

                cd Phases
                time echo "$file_content" | java PhaseOne -p 16 -c 7 > /dev/null
                cd ..
            ' bash {} \;
        fi

    # Display solution
    else
        find -path "*Input Files/*" -not -path "./Schedule Input Files/*" -exec bash -c '
            # Display current file
            echo ""
            echo "Processing File: $1"

            # Run PhaseOne against each file
            file_content=$(cat "$1")

            cd Phases
            echo "$file_content" | java PhaseOne -p 16 -c 7
            cd ..
        ' bash {} \;
    fi

# Run phase two
elif [[ "$selection" == "two" ]]; then
    # Display runtime
    if [[ "$check_time" == "-t" || "$check_time" == "--time" ]]; then
        # Format as CSV
        if [[ "$csv" == "-c" || "$csv" == "--csv" ]]; then
            # Print CSV headers
            echo "name,lines,nano"

            find -path "./Schedule Input Files/*" -not -name "*Students*" -exec bash -c '
                # Run PhaseOne against each file
                file_content=$(cat "$1")
                file_name=$(basename "$1")
                let file_length=$(cat "$1" | sed "/^\s*$/d" | wc -l)-1

                cd Phases
                let avg_time=$(echo "$file_content" | java PhaseTwo -p 16 -c 7 -t -n 100)
                cd ..

                echo "$file_name,$file_length,$avg_time"
            ' bash {} \;

        # Display default time output
        else
            find -path "./Schedule Input Files/*" -not -name "*Students*" -exec bash -c '
                # Display current file
                echo ""
                echo "Processing File: $1"

                # Run PhaseOne against each file
                file_content=$(cat "$1")

                cd Phases
                time echo "$file_content" | java PhaseTwo -p 16 -c 7 > /dev/null
                cd ..
            ' bash {} \;
        fi

    # Display solution
    else
        find -path "./Schedule Input Files/*" -not -name "*Students*" -exec bash -c '
            # Display current file
            echo ""
            echo "Processing File: $1"

            # Run PhaseTwo against each file
            file_content=$(cat "$1")

            cd Phases
            echo "$file_content" | java PhaseTwo -p 16 -c 7
            cd ..
        ' bash {} \;
    fi

# Run phase three
elif [[ "$selection" == "three" ]]; then
    # Display runtime in CSV format
    file_content=$(cat "$2")
    file_name=$(basename "$2")
    let file_length=$(cat "$2" | sed "/^\s*$/d" | wc -l)-1

    # Print CSV headers
    echo "nano,radioactivity"

    # Test a range of radioactivity from 0 to .5
    let repeats=0
    found_one=false

    for radioactivity in $(seq 0 .01 .5); do
        # If no more tests are realistic, break the loop
        if [[ $found_one = true && repeats -ge 3 ]]; then
            break
        fi

        cd Phases
        avg_time=$(timeout 30 bash -c "echo \"$file_content\" | java PhaseOne -p 16 -c 7 -r $radioactivity -t -n 5")

        # Only print if there is data
        if [[ -n "$avg_time" ]]; then
            found_one=true
            echo "$avg_time,$radioactivity"

        # Otherwise, acknowlege that the last test was a dud
        else
            let repeats=$repeats+1
        fi

        cd ..
    done
fi
