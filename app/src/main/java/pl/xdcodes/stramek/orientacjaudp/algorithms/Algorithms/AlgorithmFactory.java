package pl.xdcodes.stramek.orientacjaudp.algorithms.Algorithms;

/**
 * Created by Stramek on 04.01.2016.
 */
public class AlgorithmFactory {
    public Algorithm getAlgorithm(String algorithm) {

        if(algorithm == null) {
            return null;
        }

        if (algorithm.equalsIgnoreCase("COMPLEMENTARY")) {
            return new Complementary();
        } else if (algorithm.equalsIgnoreCase("MADGWICKAMG")) {
            return new MadgwickAMG();
        } else if (algorithm.equalsIgnoreCase("MADGWICKAG")) {
            return new MadgwickAG();
        }

        return null;
    }
}