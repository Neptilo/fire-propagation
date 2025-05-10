import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    public static void load() {
        FireSimulator simulator = FireSimulator.instance;
        try (InputStream input = simulator.getClass().getResourceAsStream(
                "/config.properties")) {
            Properties props = new Properties();
            props.load(input);
            String propName, propValueStr;

            propName = "width";
            propValueStr = props.getProperty(propName);
            if (propValueStr != null) {
                int value = Integer.parseInt(propValueStr);
                if (validateMinInt(propName, value, 1))
                    simulator.setWidth(value);
            }

            propName = "height";
            propValueStr = props.getProperty(propName);
            if (propValueStr != null) {
                int value = Integer.parseInt(propValueStr);
                if (validateMinInt(propName, value, 1))
                    simulator.setHeight(value);
            }

            propName = "startingPointNum";
            propValueStr = props.getProperty(propName);
            if (propValueStr != null) {
                int value = Integer.parseInt(propValueStr);
                int numTiles = simulator.getWidth() * simulator.getHeight();
                if (validateMinInt(propName, value, 1) &&
                        validateMaxInt(propName, value, numTiles))
                    simulator.setStartingPointNum(value);
            }

            propName = "propagationFactor";
            propValueStr = props.getProperty(propName);
            if (propValueStr != null) {
                double value = Double.parseDouble(propValueStr);
                if (validateMinDouble(propName, value, 0) &&
                        validateMaxDouble(propName, value, 1))
                    simulator.setPropagationFactor(value);
            }

            propName = "timeStepMs";
            propValueStr = props.getProperty(propName);
            if (propValueStr != null) {
                int value = Integer.parseInt(propValueStr);
                if (validateMinInt(propName, value, 1))
                    simulator.setTimeStepMs(value);
            }
        } catch (IOException e) {
            System.err.println("Could not find a config.properties file");
            System.err.println("Keeping default values");
        } catch (NumberFormatException e) {
            System.err.println("Some properties have a wrong number format");
            System.err.println("Keeping default values");
        } catch (IllegalArgumentException e) {
            System.err.println("Malformed syntax in config.properties file");
            System.err.println("Keeping default values");
        }
    }

    private static boolean validateMinInt(
            String name, int value, int bound) {
        if (value >= bound)
            return true;

        System.err.println(name + " < " + bound + " in the configuration file");
        System.err.println("Keeping its default value");
        return false;
    }

    private static boolean validateMaxInt(
            String name, int value, int bound) {
        if (value <= bound)
            return true;

        System.err.println(name + " > " + bound + " in the configuration file");
        System.err.println("Keeping its default value");
        return false;
    }

    private static boolean validateMinDouble(
            String name, double value, double bound) {
        if (value >= bound)
            return true;

        System.err.println(name + " < " + bound + " in the configuration file");
        System.err.println("Keeping its default value");
        return false;
    }

    private static boolean validateMaxDouble(
            String name, double value, double bound) {
        if (value <= bound)
            return true;

        System.err.println(name + " > " + bound + " in the configuration file");
        System.err.println("Keeping its default value");
        return false;
    }
}
