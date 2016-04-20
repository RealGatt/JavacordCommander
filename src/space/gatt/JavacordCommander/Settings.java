package space.gatt.JavacordCommander;

/**
 * Created by Zach on 5/03/2016.
 */
public class Settings {

    private static String defaultGroup = "";
    private static String commandStarter = "";
    private static String msgStarter = "";

    private static String helpMessageLanguage = "xml";
    private static String helpFormat = "< %cmd > - %desc - %group";

    public static String getHelpMessageLanguage() {
        return helpMessageLanguage;
    }

	/**
     * Set's the markup that will be used to format the help message. Defaults to XML
     */
    public static void setHelpMessageLanguage(String helpMessageLanguage) {
        Settings.helpMessageLanguage = helpMessageLanguage;
    }

    public static String getHelpFormat() {
        return helpFormat;
    }

    /**
     * Set's the format that help messages will be formatted with.
     * <p>
     * %cmd will be replaced with the command
     * %desc will be replaced with the description
     * %group will be replaced with the group
     * %syntax will be replaced with the syntax
     * %aliases will be replaced with the list of aliases
     * <p>
     */
    public static void setHelpFormat(String helpFormat) {
        Settings.helpFormat = helpFormat;
    }

    public static String getMsgStarter() {
        return msgStarter;
    }

	/**
     * The starting symbol (or string) for all of JavacordCommanders' messages.
     */
    public static void setMsgStarter(String msgStarter) {
        Settings.msgStarter = msgStarter;
    }

    public static String getCommandStarter() {
        return commandStarter;
    }

    public static void setCommandStarter(String str) {
        commandStarter = str;
    }

    public static String getDefaultGroup() {
        return defaultGroup;
    }

    public static void setDefaultGroup(String str) {
        defaultGroup = str;
    }

    private static boolean loadedSettings = false;

    private static String buildString(){
        return "";
    }
}
