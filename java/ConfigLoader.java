import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A static class that loads simulation properties from a config.properties file
 */
public class ConfigLoader {
    /**
     * Load the properties from a config.properties file located in the class
     * path. If some of the properties have invalid syntax, the subsequent
     * properties are ignored and they keep their default values.
     * If a property value is not within its allowed bounds, its default value
     * is kept.
     */
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

    /**
     * Check if an integer property value is above its allowed minimum
     * and if not, report it
     * @param name The name of the property
     * @param value The value of the property
     * @param bound The allowed minimum
     * @return Whether the condition was respected
     */
    private static boolean validateMinInt(
            String name, int value, int bound) {
        if (value >= bound)
            return true;

        System.err.println(name + " < " + bound + " in the configuration file");
        System.err.println("Keeping its default value");
        return false;
    }

    /**
     * Check if an integer property value is below its allowed maximum
     * and if not, report it
     * @param name The name of the property
     * @param value The value of the property
     * @param bound The allowed maximum
     * @return Whether the condition was respected
     */
    private static boolean validateMaxInt(
            String name, int value, int bound) {
        if (value <= bound)
            return true;

        System.err.println(name + " > " + bound + " in the configuration file");
        System.err.println("Keeping its default value");
        return false;
    }

    /**
     * Check if a double property value is above its allowed minimum
     * and if not, report it
     * @param name The name of the property
     * @param value The value of the property
     * @param bound The allowed minimum
     * @return Whether the condition was respected
     */
    private static boolean validateMinDouble(
            String name, double value, double bound) {
        if (value >= bound)
            return true;

        System.err.println(name + " < " + bound + " in the configuration file");
        System.err.println("Keeping its default value");
        return false;
    }

    /**
     * Check if a double property value is below its allowed maximum
     * and if not, report it
     * @param name The name of the property
     * @param value The value of the property
     * @param bound The allowed maximum
     * @return Whether the condition was respected
     */
    private static boolean validateMaxDouble(
            String name, double value, double bound) {
        if (value <= bound)
            return true;

        System.err.println(name + " > " + bound + " in the configuration file");
        System.err.println("Keeping its default value");
        return false;
    }
}
