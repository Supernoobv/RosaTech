package rosatech.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class RosaLog {
    public static Logger logger = LogManager.getLogger("RosaTech");
    public static Logger modLogger = LogManager.getLogger("RosaTech Mod Integration");

    private RosaLog() {
    }
}
