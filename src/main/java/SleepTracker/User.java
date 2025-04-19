package SleepTracker;

import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private ArrayList<double[]> userData;

    public User(ArrayList<double[]> userData) {
        this.userData = userData;
    }

    public ArrayList<double[]> getUserData() {
        return this.userData;
    }

    public void setUserData(ArrayList<double[]> userData) {
        this.userData = userData;
    }

    /**
     * Multiplies last sleep duration by 2/3 and truncates to get whole number of sleep cycles.
     * @return Number of sleep cycles.
     */
    public static int sleepCycles(ArrayList<double[]> userData) {
        double[] lastEntry = userData.getLast();
        return (int) (lastEntry[3] / 1.5);
    }

    /**
     * Uses the number of sleep cycles and multiplies that number by 30 minutes to get the total duration
     * of rapid eye movement.
     * @return Duration of rapid eye movement
     */
    public static int REM_Duration(ArrayList<double[]> userData) {
        double[] lastEntry = userData.getLast();
        return (int) (((lastEntry[3] * 2 / 3) * 30));
    }

    /**
     * Enhanced sleep cycle analysis that accounts for sleep quality and interruptions.
     * Standard sleep cycles are 90-110 minutes, but quality affects the calculation.
     *
     * @param userData The sleep data to analyze
     * @return SleepCycleInfo object containing detailed sleep cycle information
     */
    public static SleepCycleInfo analyzeSleepCycles(ArrayList<double[]> userData) {
        if (userData.isEmpty()) {
            return new SleepCycleInfo(0, 0, 0, 0);
        }

        double[] lastEntry = userData.getLast();
        double sleepDuration = lastEntry[Factor.SLEEP_DURATION.getArrayIndex()];
        double sleepQuality = lastEntry[Factor.SLEEP_QUALITY.getArrayIndex()];

        // Base calculation: average cycle is 90 minutes (1.5 hours)
        double cycleLength = 1.5;

        // Adjust cycle length based on sleep quality (higher quality = more efficient cycles)
        if (sleepQuality >= 7) {
            cycleLength = 1.4; // Shorter, more efficient cycles
        } else if (sleepQuality <= 3) {
            cycleLength = 1.7; // Longer, less efficient cycles
        }

        // Calculate number of sleep cycles
        int completeCycles = (int)(sleepDuration / cycleLength);

        // Calculate REM sleep (typically 20-25% of total sleep in healthy adults)
        // Quality affects REM percentage
        double remPercentage = 0.2 + (sleepQuality / 50); // Ranges from 0.2 to 0.4
        double remDuration = sleepDuration * remPercentage;

        // Calculate deep sleep (typically 10-20% of total sleep)
        double deepSleepPercentage = 0.1 + (sleepQuality / 100); // Ranges from 0.1 to 0.2
        double deepSleepDuration = sleepDuration * deepSleepPercentage;

        return new SleepCycleInfo(
                completeCycles,
                remDuration * 60, // Convert to minutes
                deepSleepDuration * 60, // Convert to minutes
                cycleLength * 60 // Convert to minutes
        );
    }

    /**
     * Class to hold detailed sleep cycle information
     */
    public static class SleepCycleInfo {
        private final int numberOfCycles;
        private final double remMinutes;
        private final double deepSleepMinutes;
        private final double averageCycleMinutes;

        public SleepCycleInfo(int numberOfCycles, double remMinutes, double deepSleepMinutes, double averageCycleMinutes) {
            this.numberOfCycles = numberOfCycles;
            this.remMinutes = remMinutes;
            this.deepSleepMinutes = deepSleepMinutes;
            this.averageCycleMinutes = averageCycleMinutes;
        }

        public int getNumberOfCycles() { return numberOfCycles; }
        public double getRemMinutes() { return remMinutes; }
        public double getDeepSleepMinutes() { return deepSleepMinutes; }
        public double getAverageCycleMinutes() { return averageCycleMinutes; }

        @Override
        public String toString() {
            return String.format(
                    "Sleep Cycles: %d\nREM Sleep: %.1f minutes\nDeep Sleep: %.1f minutes\nAverage Cycle Length: %.1f minutes",
                    numberOfCycles, remMinutes, deepSleepMinutes, averageCycleMinutes
            );
        }
    }

    /**
     * Analyzes and displays the factors that affect sleep duration based on the recorded sleep data.
     *
     * @param userData The list of user's recorded sleep logs, where each entry is a double array containing sleep related variables.
     */
    public static double[] calculateAverages(ArrayList<double[]> userData) {
        double[] avg = new double[userData.getFirst().length];
        for (double[] log : userData)
            for (int i = 0; i < log.length; i++)
                avg[i] += log[i];
        int numDays = userData.size();
        for (int i = 0; i < avg.length; i++)
            avg[i] /= numDays;
        return avg;
    }

    /**
     * Calculates a weighted moving average giving more importance to recent data.
     *
     * @param userData The sleep data to analyze
     * @param factor The Factor enum to calculate the weighted average for
     * @param daysToConsider Number of recent days to include, or 0 for all days
     * @return The weighted average value
     */
    public static double weightedMovingAverage(ArrayList<double[]> userData, Factor factor, int daysToConsider) {
        int row = factor.getArrayIndex();
        int dataSize = userData.size();

        // If no data or invalid factor, return 0
        if (dataSize == 0) return 0;

        // Limit days to consider if specified
        int startIndex = (daysToConsider > 0 && daysToConsider < dataSize)
                ? dataSize - daysToConsider
                : 0;

        double sum = 0;
        double weightSum = 0;

        for (int i = startIndex; i < dataSize; i++) {
            // Calculate weight: more recent data gets higher weight
            // Linear weight function: (i + 1 - startIndex) / total
            double weight = (double)(i + 1 - startIndex) / (dataSize - startIndex);
            sum += userData.get(i)[row] * weight;
            weightSum += weight;
        }

        return weightSum > 0 ? sum / weightSum : 0;
    }

    /**
     * Allows user of the class to add a row to userData.
     * @param row Row to add.
     */
    public void addRow(double[] row) {
        this.userData.add(row);
    }

    /**
     * Calculates expected value of squared deviation from the mean using a known mathematical formula.
     * @param dataset User data.
     * @param row Row to calculate variance on.
     * @return Squared deviation.
     */
    public static double sampleVariance(ArrayList<double[]> dataset, int row) {
        double avg = calculateAverages(dataset)[row];
        double sum = 0;
        for (double[] log : dataset)
            sum += Math.pow(avg - log[row], 2);
        return sum / (dataset.size() - 1);
    }

    /**
     * Calculates the standard deviation using another known mathematical formula
     * @param dataset User data
     * @param row the row to calculate standard deviation on, up the users choice!
     * @return standard deviation
     */
    public static double standardDeviation(ArrayList<double[]> dataset, int row) {
        double count = dataset.size();
        double sum = 0;
        double avg = calculateAverages(dataset)[row];
        for (double[] log : dataset){
            sum += log[row];}
        double variancesum = 0;
        for (double[] log : dataset){
            variancesum += Math.pow(log[row] - avg, 2);}
        double variance = variancesum / count;
        return Math.sqrt(variance);
    }

    /**
     * Calculates the standard deviation for a specific factor across all sleep logs.
     * Enhanced to handle different types of standard deviations and extreme values.
     *
     * @param dataset The sleep data to analyze
     * @param factor The Factor enum to calculate standard deviation for
     * @param excludeOutliers Whether to exclude statistical outliers from calculation
     * @return The standard deviation value
     */
    public static double enhancedStandardDeviation(ArrayList<double[]> dataset, Factor factor, boolean excludeOutliers) {
        int row = factor.getArrayIndex();
        double count = dataset.size();
        if (count <= 1) return 0;

        // Calculate the average first
        double avg = calculateAverages(dataset)[row];

        // Collect all values and optionally filter outliers
        ArrayList<Double> values = new ArrayList<>();
        for (double[] log : dataset) {
            values.add(log[row]);
        }

        // Optionally remove outliers (values more than 2 standard deviations from mean)
        if (excludeOutliers && count > 3) {
            // Calculate initial standard deviation to identify outliers
            double initialVariance = 0;
            for (Double value : values) {
                initialVariance += Math.pow(value - avg, 2);
            }
            double initialStdDev = Math.sqrt(initialVariance / count);

            // Filter values to exclude outliers
            ArrayList<Double> filteredValues = new ArrayList<>();
            double filteredSum = 0;
            for (Double value : values) {
                if (Math.abs(value - avg) <= 2 * initialStdDev) {
                    filteredValues.add(value);
                    filteredSum += value;
                }
            }

            // Recalculate average without outliers
            if (!filteredValues.isEmpty()) {
                values = filteredValues;
                avg = filteredSum / filteredValues.size();
                count = filteredValues.size();
            }
        }

        // Calculate final variance and standard deviation
        double varianceSum = 0;
        for (Double value : values) {
            varianceSum += Math.pow(value - avg, 2);
        }

        // Use n-1 for sample standard deviation (Bessel's correction)
        double variance = varianceSum / (count - 1);
        return Math.sqrt(variance);
    }

    /**
     * Analyzes and displays the factors that positively or negatively affect sleep quality based on user's recorded sleep data.
     *
     * @param userData The list of user's recorded sleep logs,each entry is a double array containing sleep-related variables.
     */
    public static void sleepQualityFactors(ArrayList<double[]> userData) {
        int cardioUpCount = 0, cardioDownCount = 0;
        int tempUpCount = 0, tempDownCount = 0;
        int screenTimeUpCount = 0, screenTimeDownCount = 0;
        int waterUpCount = 0, waterDownCount = 0;
        int napUpCount = 0, napDownCount = 0;
        int sleep_durationUpCount = 0, sleep_durationDownCount = 0;
        //Up and Down in the naming represents the number of times a certain variable causes
        //an increase or decrease in sleep quality
        for (double[] log : userData) { //Sleep quality always starts at index 1
            double sleepQuality = log[1];
            double cardio = log[4];
            double temperature = log[2];
            double screenTime = log[6];
            double water = log[8];
            double nap_duration = log[5];
            double sleep_duration = log[3];
            if (cardio >= 60) {
                if (sleepQuality >= 5) {
                    cardioUpCount++; //7.5 out of 10 sleep quality indicates a solid rest
                } else if (sleepQuality < 5) {
                    cardioDownCount++; //3.5 out of 10 sleep quality indicates a restless night
                }
            } else if (cardio < 60) {
                if (sleepQuality >= 5) {
                    cardioUpCount++;
                } else if (sleepQuality < 5) {
                    cardioDownCount++; //Ends here for cardio
                }
            }
            if (temperature <= 18.5) {
                if (sleepQuality >= 5) {
                    tempUpCount++;
                } else if (sleepQuality < 5) {
                    tempDownCount++;
                }
            } else if (temperature > 18.5) {
                if (sleepQuality >= 5) {
                    tempUpCount++;
                } else if (sleepQuality < 5) {
                    tempDownCount++;
                }
            }
            if (screenTime <= 4) {
                if (sleepQuality >= 5) {
                    screenTimeUpCount++;
                } else if (sleepQuality < 5) {
                    screenTimeDownCount++;
                }
            } else if (screenTime > 4) {
                if (sleepQuality >= 5) {
                    screenTimeUpCount++;
                } else if (sleepQuality < 5) {
                    screenTimeDownCount++;
                }
            }
            if (water >= 2000) {
                if (sleepQuality >= 5) {
                    waterUpCount++;
                } else if (sleepQuality < 5) {
                    waterDownCount++;
                }
            } else if (water < 2000) {
                if (sleepQuality >= 5) {
                    waterUpCount++;
                } else if (sleepQuality < 5) {
                    waterDownCount++;
                }
            }
            if (nap_duration <= 30) {
                if (sleepQuality >= 5) {
                    napUpCount++;
                } else if (sleepQuality < 5) {
                    napDownCount++;
                }
            } else if (nap_duration > 30) {
                if (sleepQuality >= 5) {
                    napUpCount++;
                } else if (sleepQuality < 5) {
                    napDownCount++;
                }
            }
            if (sleep_duration >= 8) {
                if (sleepQuality >= 5) {
                    sleep_durationUpCount++;
                } else if (sleepQuality < 5) {
                    sleep_durationDownCount++;
                }
            } else if (sleep_duration < 8) {
                if (sleepQuality >= 5) {
                    sleep_durationUpCount++;
                } else if (sleepQuality < 5) {
                    sleep_durationDownCount++;
                }
            }
        }
        System.out.println("Factors that improved sleep quality:");
        System.out.println(" - More than 60 min cardio: " + cardioUpCount);
        System.out.println(" - Room temp below 18.5°C: " + tempUpCount);
        System.out.println(" - Screen time less than 4 hours: " + screenTimeUpCount);
        System.out.println(" - Drinking 2L+ water: " + waterUpCount);
        System.out.println(" - Short naps (<30 min): " + napUpCount);
        System.out.println(" - Enough sleep (>8 hours): " + sleep_durationUpCount);

        System.out.println("Factors that reduced sleep quality:");
        System.out.println(" - Less than 60 min cardio: " + cardioDownCount);
        System.out.println(" - Room temp above 18.5°C: " + tempDownCount);
        System.out.println(" - Screen time 4+ hours: " + screenTimeDownCount);
        System.out.println(" - Low water intake: " + waterDownCount);
        System.out.println(" - Long naps (>30 min): " + napDownCount);
        System.out.println(" - Insufficient sleep (<8 hours): " + sleep_durationDownCount);

    }

    /**
     * Analyzes and displays the factors that affect sleep duration based on the recorded sleep data.
     *
     * @param userData The list of user's recorded sleep logs, where each entry is a double array containing sleep related variables.
     */
    public static void sleepDurationFactors(ArrayList<double[]> userData) {
        {

            int increaseCardio = 0, decreaseCardio = 0;
            int increaseTemp = 0, decreaseTemp = 0;
            int increaseScreenTime = 0, decreaseScreenTime = 0;
            int increaseWater = 0, decreaseWater = 0;
            int increaseNap = 0, decreaseNap = 0;

            for (double[] log : userData) {
                double sleepHours = log[3];
                double cardioMinutes = log[4];
                double roomTemp = log[2];
                double screenHours = log[6];
                double waterIntake = log[8];
                double napMinutes = log[5];

                if (cardioMinutes >= 60) {
                    if (sleepHours >= 8) {
                        increaseCardio++;
                    } else if (sleepHours < 8) {
                        decreaseCardio++;
                    }
                } else if (cardioMinutes < 60) {
                    if (sleepHours >= 8) {
                        increaseCardio++;
                    } else if (sleepHours < 8) {
                        decreaseCardio++;
                    }
                }
                if (roomTemp <= 18.5) {
                    if (sleepHours >= 8) {
                        increaseTemp++;
                    } else if (sleepHours < 8) {
                        decreaseTemp++;
                    }
                } else if (roomTemp > 18.5) {
                    if (sleepHours >= 8) {
                        increaseTemp++;
                    } else if (sleepHours < 8) {
                        decreaseTemp++;
                    }
                }
                if (screenHours <= 4) {
                    if (sleepHours >= 8) {
                        increaseScreenTime++;
                    } else if (sleepHours < 8) {
                        decreaseScreenTime++;
                    }
                } else if (screenHours > 4) {
                    if (sleepHours >= 8) {
                        increaseScreenTime++;
                    } else if (sleepHours < 8) {
                        decreaseScreenTime++;
                    }
                }
                if (waterIntake >= 2000) {
                    if (sleepHours >= 8) {
                        increaseWater++;
                    } else if (sleepHours < 8) {
                        decreaseWater++;
                    }
                } else if (waterIntake < 2000) {
                    if (sleepHours >= 8) {
                        increaseWater++;
                    } else if (sleepHours < 8) {
                        decreaseWater++;
                    }
                }
                if (napMinutes <= 30) {
                    if (sleepHours >= 8) {
                        increaseNap++;
                    } else if (sleepHours < 8) {
                        decreaseNap++;
                    }
                } else if (napMinutes > 30) {
                    if (sleepHours >= 8) {
                        increaseNap++;
                    } else if (sleepHours < 8) {
                        decreaseNap++;
                    }
                }
            }

            System.out.println("Factors that improved sleep duration:");
            System.out.println(" - More than 60 min cardio: " + increaseCardio);
            System.out.println(" - Room temp below 18.5°C: " + increaseTemp);
            System.out.println(" - Screen time less than 4 hours: " + increaseScreenTime);
            System.out.println(" - Drinking 2L+ water: " + increaseWater);
            System.out.println(" - Short naps (<30 min): " + increaseNap);

            System.out.println("Factors that reduced sleep duration:");
            System.out.println(" - Less than 60 min cardio: " + decreaseCardio);
            System.out.println(" - Room temp above 18.5°C: " + decreaseTemp);
            System.out.println(" - Screen time 4+ hours: " + decreaseScreenTime);
            System.out.println(" - Low water intake: " + decreaseWater);
            System.out.println(" - Long naps (>60 min): " + decreaseNap);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true; // pointer equality
        }
        if (!(obj instanceof User user)) { // pattern variable
            return false; // wrong type
        }
        if (this.userData.size() != user.getUserData().size()) {
            return false;
        }
        for (int i = 0; i < this.userData.size(); i++) {
            double[] curr1 = this.userData.get(i);
            double[] curr2 = user.getUserData().get(i);
            if (!Arrays.equals(curr1, curr2))
                return false;
        }
        return true;
    }
}
