package me.ste.stevesseries.fancydrops;

import java.util.concurrent.ThreadLocalRandom;

public class NumberRange {
    double min, max;

    public NumberRange(double value) {
        this(value, value);
    }

    public NumberRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static NumberRange parse(String string) {
        if (string == null) {
            return null;
        }
        boolean isDouble = true;
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            isDouble = false;
        }
        if (isDouble) {
            return new NumberRange(Double.parseDouble(string));
        } else {
            if (string.matches("([0-9]+(\\.[0-9]+)?)-([0-9]+(\\.[0-9]+)?)")) {
                String[] split = string.split("-");
                return new NumberRange(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
            } else {
                return null;
            }
        }
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public boolean match(double num) {
        return num >= min && num <= max;
    }

    public double random() {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}