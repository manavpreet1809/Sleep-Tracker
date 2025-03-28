import java.util.ArrayList;

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
        return (int) (lastEntry[3] * 2 / 3);
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
}
