package rosatech.api

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object RosaLog {
    var logger: Logger = LogManager.getLogger("RosaTech")
    var modLogger: Logger = LogManager.getLogger("RosaTech Mod Integration")
}
