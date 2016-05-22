package space.gatt.JavacordCommander;

/**
 * Settings. Change things about Javacord Commander here.
 */
public class Settings {

    private static String defaultGroup = "";
    private static String commandStarter = "";
    private static String msgStarter = "";

    private static String game = "";

    private static String helpMessageBreaker = "~~~~ %group ~~~~";
    private static String helpMessageLanguage = "xml";
    private static String helpFormat = "< %cmd > - %desc - %group";

	/**
     * @return Game
     */
    public static String getGame() {
        return game;
    }

    /**
     * Set's the game. Only included because Gasai Bot needed it.
     * @param game Value to set the game to
     */
    public static void setGame(String game) {
        Settings.game = game;
    }

	/**
     * @return Help Message Breaker
     */
    public static String getHelpMessageBreaker() {
        return helpMessageBreaker;
    }

    /**
     * Set's the breaker line that will be used for definined new sections of the Help Message based on the groups.
     * <p>The Placeholder %group will be replaced with the name of the group.</p>
     *
     * @param helpMessageBreaker The Help Message Breaker
     *
     */

    public static void setHelpMessageBreaker(String helpMessageBreaker) {
        Settings.helpMessageBreaker = helpMessageBreaker;
    }
    /**
     * @return Help Message Language
     */
    public static String getHelpMessageLanguage() {
        return helpMessageLanguage;
    }

	/**
     * Set's the markup that will be used to format the help message. Defaults to XML
     * @param helpMessageLanguage The Message Language (CSS, Java, XML, ect)
     */
    public static void setHelpMessageLanguage(String helpMessageLanguage) {
        Settings.helpMessageLanguage = helpMessageLanguage;
    }

	/**
     * @return The help message format
     */
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
     * </p>
     * @param helpFormat The help format.
     */
    public static void setHelpFormat(String helpFormat) {
        Settings.helpFormat = helpFormat;
    }

	/**
     * @return Message Starter
     */
    public static String getMsgStarter() {
        return msgStarter;
    }

	/**
     * The starting symbol (or string) for all of JavacordCommanders' messages.
     * @param msgStarter The Message Starter
     */
    public static void setMsgStarter(String msgStarter) {
        Settings.msgStarter = msgStarter;
    }
    /**
     * @return Command Starter
     */
    public static String getCommandStarter() {
        return commandStarter;
    }
    /**
     * @param str Command Starter
     */
    public static void setCommandStarter(String str) {
        commandStarter = str;
    }
    /**
     * @return Default Group
     */
    public static String getDefaultGroup() {
        return defaultGroup;
    }
    /**
     * @param str Default Group
     */
    public static void setDefaultGroup(String str) {
        defaultGroup = str;
    }

    private static boolean loadedSettings = false;

    private static String buildString(){
        return "";
    }
}
