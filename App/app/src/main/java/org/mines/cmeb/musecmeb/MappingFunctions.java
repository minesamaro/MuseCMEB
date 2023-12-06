package org.mines.cmeb.musecmeb;

// This class contains the mapping functions used to map the stress indexes to new values
public class MappingFunctions {
    // Returns the sigmoid value, to a range of choice (caps naturally at range)
    public static double sigmoid(double x, double range, double hor_shift, double mult, double vert_shift ){
        return (range/(1+Math.exp(-(mult*x-hor_shift)))) + vert_shift;
    }
    // Returns the sigmoid value, with the parameters we discussed
    public static double sigmoid(double x){
        return (100/(1+Math.exp(-(5*x-5))));
    }

    // Linear mapping function (caps at max_y and min_y)
    public static double lin(double x, double min_x, double max_x, double min_y, double max_y, double b){
        double slope = (max_y - min_y) / (max_x - min_x);
        double result = (slope * x) + b;

        if (result > max_y)
            return max_y;
        else
            return Math.max(result, min_y);
    }
    // Same thing as before, but for the linear case
    public static double lin(double x){
        double result = 50 * x;

        if (result > 100)
            return 100;
        else
            return Math.max(result, 0);
    }
}
