public class ScheduleGene extends Gene {
    public int startTime;
    public int endTime;
    public String days;
    public String season;
    public int year;

    public ScheduleGene(String n, int st, int et, String d, String s, int y) {
        super(n);
        startTime = st;
        endTime = et;
        days = d;
        season = s;
        year = y;
    }

    // checks if other schedule conflicts
    public boolean conflicts(int otherStartTime, int otherEndTime, String otherDays, String otherSeason, int otherYear) {
        // if the years and seasons are the same
        if (otherYear == year && otherSeason.equals(season)) {
            // and the days intersect
            boolean daysIntersect = false;

            for (char day : days.toCharArray()) {
                if (otherDays.contains(Character.toString(day)))
                    daysIntersect = true;
            }

            if (daysIntersect) {
                // and the times overlap
                if (otherStartTime > startTime && otherStartTime < endTime)
                    return true;

                if (otherEndTime > startTime && otherEndTime < endTime)
                    return true;
            }
        }

        return false;
    }

    // checks if other schedule conflicts
    public boolean conflicts(ScheduleGene other) {
        return conflicts(other.startTime, other.endTime, other.days, other.season, other.year);
    }

    // checks for redundancy
    public boolean isRedundant(String otherName) {
        if (otherName.equals(name))
            return true;

        return false;
    }

    // checks for redundancy
    public boolean isRedundant(ScheduleGene other) {
        return isRedundant(other.name);
    }
}
