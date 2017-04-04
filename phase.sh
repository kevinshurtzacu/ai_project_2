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
            echo "name,lines,real,user,sys"

            find -path "*Input Files/*" -not -path "./Schedule Input Files/*" -exec bash -c '
                # Run PhaseOne against each file
                file_content=$(cat "$1")
                file_name=$(basename "$1")
                let file_length=$(cat "$1" | sed "/^\s*$/d" | wc -l)-1

                cd Phases
                \time -f "$file_name,$file_length,%E,%U,%S" bash -c "
                    echo \"$file_content\" | java PhaseOne -o > /dev/null
                "
                cd ..

                # Move to the next argument
                shift
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
                \time echo "$file_content" | java PhaseOne -o > /dev/null
                cd ..

                # Move to the next argument
                shift
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
            echo "$file_content" | java PhaseOne -o
            cd ..

            # Move to the next argument
            shift
        ' bash {} \;
    fi

# Run phase two
elif [[ "$selection" == "two" ]]; then
    # Display runtime
    if [[ "$check_time" == "-t" || "$check_time" == "--time" ]]; then
        # Format as CSV
        if [[ "$csv" == "-c" || "$csv" == "--csv" ]]; then
            # Print CSV headers
            echo "name,lines,real,user,sys"

            find -path "./Schedule Input Files/*" -not -name "*Students*" -exec bash -c '
                # Run PhaseOne against each file
                file_content=$(cat "$1")
                file_name=$(basename "$1")
                let file_length=$(cat "$1" | sed "/^\s*$/d" | wc -l)-1

                cd Phases
                \time -f "$file_name,$file_length,%E,%U,%S" bash -c "
                    echo \"$file_content\" | java PhaseTwo -o > /dev/null
                "
                cd ..

                # Move to the next argument
                shift
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
                \time echo "$file_content" | java PhaseTwo -o > /dev/null
                cd ..

                # Move to the next argument
                shift
            ' bash {} \;
        fi

    # Display solution
    else
        find -path "./Schedule Input Files/*" -not -name "*Students*" -exec bash -c '
            # Display current file
            echo ""
            echo "Processing File: $1"

            # Run PhaseTwo against each file
            file=$(cat "$1")
            cd Phases
            echo "$file" | java PhaseTwo -o -p 16 -r .05 -v 7
            cd ..

            # Move to the next argument
            shift
        ' bash {} \;
    fi

# Run phase three
elif [[ "$selection" == "three" ]]; then
    # Display runtime in CSV format
    file_content=$(cat "$2")
    file_name=$(basename "$2")
    let file_length=$(cat "$2" | sed "/^\s*$/d" | wc -l)-1

    # Print CSV headers
    echo "name,lines,real,user,sys,radioactivity"

    # test a range of radioactivity from 0 to 1
    for radioactivity in $(seq 0 .01 .05); do
        cd Phases
        timeout 30 \time -f "$file_name,$file_length,%E,%U,%S,$radioactivity" bash -c "
            echo \"$file_content\" | java PhaseOne -o -r $radioactivity #> /dev/null
        "
        cd ..
    done

    # Move to the next argument
    shift
fi
