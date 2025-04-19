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
     * Analyzes sleep consistency by calculating normalized variance scores.
     * Returns a consistency score from 0-100 where higher is more consistent.
     *
     * @param userData The sleep data to analyze
     * @return SleepConsistencyReport with scores for different consistency metrics
     */
    public static SleepConsistencyReport analyzeSleepConsistency(ArrayList<double[]> userData) {
        if (userData.size() < 3) {
            return new SleepConsistencyReport(); // Return default report for insufficient data
        }

        // Create normalized copies of data for time values that cross midnight
        ArrayList<double[]> normalizedData = new ArrayList<>();
        for (double[] log : userData) {
            double[] normalizedLog = log.clone();

            // Normalize sleep time (if after 12pm, it's likely afternoon nap not nighttime sleep)
            if (normalizedLog[Factor.SLEEP_TIME.getArrayIndex()] < 12) {
                normalizedLog[Factor.SLEEP_TIME.getArrayIndex()] += 24;
            }

            // Similarly normalize wake time if needed
            if (normalizedLog[Factor.WAKE_TIME.getArrayIndex()] < 12) {
                normalizedLog[Factor.WAKE_TIME.getArrayIndex()] += 24;
            }

            normalizedData.add(normalizedLog);
        }

        // Calculate variances
        double sleepTimeVariance = sampleVariance(normalizedData, Factor.SLEEP_TIME.getArrayIndex());
        double wakeTimeVariance = sampleVariance(normalizedData, Factor.WAKE_TIME.getArrayIndex());
        double durationVariance = sampleVariance(userData, Factor.SLEEP_DURATION.getArrayIndex());

        // Calculate consistency scores (0-100 scale, lower variance = higher consistency)
        // Sleep time: variance > 4 hours² is bad, < 0.25 hours² is excellent
        int sleepTimeScore = calculateConsistencyScore(sleepTimeVariance, 0.25, 4.0);

        // Wake time: variance > 4 hours² is bad, < 0.25 hours² is excellent
        int wakeTimeScore = calculateConsistencyScore(wakeTimeVariance, 0.25, 4.0);

        // Duration: variance > 6 hours² is bad, < 0.5 hours² is excellent
        int durationScore = calculateConsistencyScore(durationVariance, 0.5, 6.0);

        // Overall score is weighted average
        int overallScore = (int)((sleepTimeScore * 0.4) + (wakeTimeScore * 0.4) + (durationScore * 0.2));

        return new SleepConsistencyReport(
                sleepTimeScore,
                wakeTimeScore,
                durationScore,
                overallScore,
                sleepTimeVariance,
                wakeTimeVariance,
                durationVariance
        );
    }

    /**
     * Calculates a consistency score based on variance.
     *
     * @param variance The calculated variance
     * @param excellentThreshold Variance below this is considered excellent (100 score)
     * @param poorThreshold Variance above this is considered poor (0 score)
     * @return Consistency score from 0-100
     */
    private static int calculateConsistencyScore(double variance, double excellentThreshold, double poorThreshold) {
        if (variance <= excellentThreshold) return 100;
        if (variance >= poorThreshold) return 0;

        // Linear mapping from variance to score
        return 100 - (int)((variance - excellentThreshold) / (poorThreshold - excellentThreshold) * 100);
    }

    /**
     * Class to hold sleep consistency analysis results
     */
    public static class SleepConsistencyReport {
        private final int sleepTimeConsistency;
        private final int wakeTimeConsistency;
        private final int durationConsistency;
        private final int overallConsistency;
        private final double sleepTimeVariance;
        private final double wakeTimeVariance;
        private final double durationVariance;

        // Default constructor for insufficient data
        public SleepConsistencyReport() {
            this(0, 0, 0, 0, 0, 0, 0);
        }

        public SleepConsistencyReport(
                int sleepTimeConsistency,
                int wakeTimeConsistency,
                int durationConsistency,
                int overallConsistency,
                double sleepTimeVariance,
                double wakeTimeVariance,
                double durationVariance) {

                    this.sleepTimeConsistency = sleepTimeConsistency;

                 this.wakeTimeConsistency = wakeTimeConsistency;

                this.durationConsistency = durationConsistency;

                    this.overallConsistency = overallConsistency;

             this.sleepTimeVariance = sleepTimeVariance;

                    this.wakeTimeVariance = wakeTimeVariance;

                  this.durationVariance = durationVariance;
        }

        // Getters
        public int getSleepTimeConsistency()
        { return sleepTimeConsistency; }
        public int getWakeTimeConsistency()
        { return wakeTimeConsistency; }
        public int getDurationConsistency()
        { return durationConsistency; }
        public int getOverallConsistency()
        { return overallConsistency; }
        public double getSleepTimeVariance()
        { return sleepTimeVariance; }
        public double getWakeTimeVariance()
        { return wakeTimeVariance; }
        public double getDurationVariance()
        { return durationVariance; }

        public String getConsistencyRating() {
            if (overallConsistency >= 90) return "Excellent";
            if (overallConsistency >= 75) return "Very Good";
            if (overallConsistency >= 60) return "Good";
            if (overallConsistency >= 40) return "Fair";
            if (overallConsistency >= 20) return "Poor";
            return "Very Poor";
        }

        @Override
        public String toString() {
            return String.format(
                    "Sleep Consistency Report:\n" +
                            "Overall Consistency: %d/100 (%s)\n" +
                            "Sleep Time Consistency: %d/100\n" +
                            "Wake Time Consistency: %d/100\n" +
                            "Sleep Duration Consistency: %d/100",
                    overallConsistency, getConsistencyRating(),
                    sleepTimeConsistency,
                    wakeTimeConsistency,
                    durationConsistency
            );
        }
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
     * Gets the standard deviation for a sleep factor, with option to exclude extreme values.
     *
     * @param sleepData All recorded sleep logs
     * @param factor Which sleep factor to analyze
     * @param removeOutliers Set true to ignore unusual values that might skew results
     * @return Standard deviation value
     */
    public static double enhancedStandardDeviation(ArrayList<double[]> sleepData, Factor factor, boolean removeOutliers) {
        int index = factor.getArrayIndex();
        int dataPoints = sleepData.size();

        // Need at least two points to calculate standard deviation
        if (dataPoints <= 1)
            return 0;

        // First get the average
        double mean = calculateAverages(sleepData)[index];

        // Grab all the values we want to analyze
        ArrayList<Double> allValues = new ArrayList<>();
        for (double[] night : sleepData) {
            allValues.add(night[index]);
        }

        // Filter out weird values if requested (need enough data points)
        if (removeOutliers && dataPoints > 3) {
            // Quick and dirty initial standard deviation
            double sumSquaredDiffs = 0;
            for (Double val : allValues) {
                double diff = val - mean;
                sumSquaredDiffs += diff * diff;
            }
            double roughStdDev = Math.sqrt(sumSquaredDiffs / dataPoints);

            // Keep only the reasonable values
            ArrayList<Double> normalValues = new ArrayList<>();
            double newSum = 0;
            int removed = 0;

            for (Double val : allValues) {
                // Within 2 standard deviations is considered normal
                if (Math.abs(val - mean) <= 2 * roughStdDev) {
                    normalValues.add(val);
                    newSum += val;
                } else {
                    removed++;
                }
            }

            // Only use filtered data if we have enough points left
            if (normalValues.size() > 1) {
                allValues = normalValues;
                mean = newSum / normalValues.size();
                dataPoints = normalValues.size();
            }
        }

        // Final calculation using the possibly filtered dataset
        double sumSquares = 0;
        for (Double val : allValues) {
            double diff = val - mean;
            sumSquares += diff * diff;
        }

        // Sample standard deviation formula
        return Math.sqrt(sumSquares / (dataPoints - 1));
    }


    /**
     * Analyzes and displays the factors that positively or negatively affect sleep quality based on user's recorded sleep data.
     *
     * @param userData The list of user's recorded sleep logs,each entry is a double array containing sleep-related variables.
     */
    public static String sleepQualityFactors(ArrayList<double[]> userData) {
        StringBuilder sb = new StringBuilder();
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
        sb.append("Factors that improved sleep quality:");
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
        return sb.toString();
    }

    /**
     * Analyzes and displays the factors that affect sleep duration based on the recorded sleep data.
     *
     * @param userData The list of user's recorded sleep logs, where each entry is a double array containing sleep related variables.
     */
    public static String sleepDurationFactors(ArrayList<double[]> userData) {
        StringBuilder sb = new StringBuilder();
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
        return sb.toString();
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

    /**
     * Predicts sleep quality based on previous data using a simple regression model.
     *
     * @param userData The sleep data history
     * @param currentFactors The factors for the night to predict
     * @return Predicted sleep quality (1-10 scale)
     */
    public static double predictSleepQuality(ArrayList<double[]> userData, double[] currentFactors) {
        if (userData.size() < 5) {
            // Not enough data for reliable prediction
            return -1;
        }

        // Initialize coefficients to measure impact of each factor
        double[] coefficients = calculateFactorCoefficients(userData);

        // Start with baseline (average sleep quality)
        double baseQuality = calculateAverages(userData)[Factor.SLEEP_QUALITY.getArrayIndex()];

        // Adjust baseline using coefficients and current factors
        double predictedQuality = baseQuality;

        // Apply each coefficient to the difference between current factor and average
        double[] averages = calculateAverages(userData);

        // Apply factor impacts
        for (Factor factor : new Factor[] {
                Factor.ROOM_TEMPERATURE,
                Factor.SLEEP_DURATION,
                Factor.CARDIO_DURATION,
                Factor.NAPPING_DURATION,
                Factor.SCREEN_TIME,
                Factor.WATER_INTAKE
        }) {
            int idx = factor.getArrayIndex();
            double avgValue = averages[idx];
            double currentValue = currentFactors[idx];
            double impact = coefficients[idx] * (currentValue - avgValue);
            predictedQuality += impact;
        }

        // Ensure prediction is within valid range (1-10)
        predictedQuality = Math.max(1, Math.min(10, predictedQuality));

        return predictedQuality;
    }

    /**
     * Calculates correlation coefficients between each factor and sleep quality
     *
     * @param userData The sleep data to analyze
     * @return Array of coefficients for each factor
     */
    private static double[] calculateFactorCoefficients(ArrayList<double[]> userData) {
        int qualityIdx = Factor.SLEEP_QUALITY.getArrayIndex();
        double[] coefficients = new double[11]; // Match data array size

        // For each factor, calculate correlation coefficient with sleep quality
        for (Factor factor : Factor.values()) {
            int factorIdx = factor.getArrayIndex();

            // Skip sleep quality itself
            if (factorIdx == qualityIdx) continue;

            // Calculate correlation (simplified)
            double correlation = calculateCorrelation(userData, factorIdx, qualityIdx);

            // Convert correlation to coefficient (empirically determined scale factors)
            switch (factor) {
                case ROOM_TEMPERATURE:
                    // Negative correlation - lower temp often means better sleep
                    coefficients[factorIdx] = correlation * -0.2;
                    break;
                case SLEEP_DURATION:
                    // Positive correlation - more sleep often means better quality
                    coefficients[factorIdx] = correlation * 0.5;
                    break;
                case CARDIO_DURATION:
                    // Positive correlation up to a point
                    coefficients[factorIdx] = correlation * 0.01;
                    break;
                case NAPPING_DURATION:
                    // Negative correlation - more napping can disrupt night sleep
                    coefficients[factorIdx] = correlation * -0.005;
                    break;
                case SCREEN_TIME:
                    // Negative correlation - more screen time often means worse sleep
                    coefficients[factorIdx] = correlation * -0.3;
                    break;
                case WATER_INTAKE:
                    // Positive correlation up to a point, then negative (bathroom trips)
                    coefficients[factorIdx] = correlation * 0.0005;
                    break;
                default:
                    coefficients[factorIdx] = 0;
            }
        }

        return coefficients;
    }

    /**
     * Calculates Pearson correlation coefficient between two variables in the dataset
     *
     * @param userData The sleep data to analyze
     * @param factorIdx Index of the first variable
     * @param qualityIdx Index of the second variable (typically sleep quality)
     * @return Correlation coefficient (-1 to 1)
     */
    private static double calculateCorrelation(ArrayList<double[]> userData, int factorIdx, int qualityIdx) {
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        int n = userData.size();

        // Calculate sums for correlation formula
        for (double[] entry : userData) {
            double x = entry[factorIdx];
            double y = entry[qualityIdx];

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }

        // Calculate Pearson correlation coefficient
        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        if (denominator == 0) return 0;
        return numerator / denominator;
    }
}
