package io.github.manavpreet1809.sleeptracker.api;

/** Predicted quality from 1 to 10 and how many nights the model learned from. */
public record PredictionResponse(double predictedQuality, int basedOnNights) {
}
