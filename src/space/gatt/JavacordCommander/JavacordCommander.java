package space.gatt.JavacordCommander;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.permissions.Role;
import org.reflections.Reflections;
import space.gatt.JavacordCommander.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Zach G on 06-Apr-16.
 */
public class JavacordCommander {

	private static JavacordCommander instance;

	private DiscordAPI javacordInstance;

	/**
	 * Including the (String) dir variable, will automatically call the enableSnooper method.
	 * @param apiInstance The Discord API Instance
	 */
	public JavacordCommander(DiscordAPI apiInstance){
		this.javacordInstance = apiInstance;
		JavacordCommander.instance = this;
		javacordInstance.registerListener(new CommandListener());
		MessageManager.startManager(1000);
	}

	/**
	 * Including the (String) dir variable, will automatically call the enableSnooper method.
	 * @param apiInstance The Discord API Instance
	 * @param messageManagerDelay The delay to give the Timer Task
	 */
	public JavacordCommander(DiscordAPI apiInstance, Integer messageManagerDelay){
		this.javacordInstance = apiInstance;
		JavacordCommander.instance = this;
		javacordInstance.registerListener(new CommandListener());
		MessageManager.startManager(messageManagerDelay);
	}

	/**
	 * Including the (String) dir variable, will automatically call the enableSnooper method.
	 * @param apiInstance The Discord API Instance
	 * @param dir The Directory to look through
	 */
	public JavacordCommander(DiscordAPI apiInstance, String dir){
		this.javacordInstance = apiInstance;
		JavacordCommander.instance = this;
		enableSnooper(dir);
		javacordInstance.registerListener(new CommandListener());
		MessageManager.startManager(1000);
	}
	/**
	 * Including the (String) dir variable, will automatically call the enableSnooper method.
	 * @param apiInstance The Discord API Instance
	 * @param dir The Directory to look through
	 * @param messageManagerDelay The delay to give the Timer Task
	 */
	public JavacordCommander(DiscordAPI apiInstance, String dir, Integer messageManagerDelay){
		this.javacordInstance = apiInstance;
		JavacordCommander.instance = this;
		enableSnooper(dir);
		javacordInstance.registerListener(new CommandListener());
		MessageManager.startManager(messageManagerDelay);
	}

	/**
	 * Returns the JavacordCommander instance
	 * @return The JavacordCommander Instance
	 */
	public static JavacordCommander getInstance() {
		return instance;
	}

	private List<Class> listeners = new ArrayList<>();
	private List<Method> listeningMethods = new ArrayList<>();

	private HashMap<String, Class> commandRegistrar = new HashMap<>();
	private HashMap<String, Method> methodRegistrar = new HashMap<>();

	private List<String> commandList = new ArrayList<>();

	private HashMap<String, List<String>> helpLines = new HashMap<>();
	
	private ArrayList<String> adminUsers = new ArrayList<>();
	
	/**
	 * Returns the List of Listening Classes
	 * @return Listening Classes
	 */
	public List<Class> getListeners() {
		return listeners;
	}

	/**
	 * Returns the List of Listening Methods
	 * @return Listening Methods
	 */
	public List<Method> getListeningMethods() {
		return listeningMethods;
	}
	
	/**
	 * Returns the Command Registrar (Don't touch <3)
	 * @return Command Registrar
	 */
	public HashMap<String, Class> getCommandRegistrar() {
		return commandRegistrar;
	}
	/**
	 * Returns the Command Registrar (Don't touch)
	 * @return Command Registrar
	 */
	public HashMap<String, Method> getMethodRegistrar() {
		return methodRegistrar;
	}
	/**
	 * Returns the Command List
	 * @return Command List
	 */
	public List<String> getCommandList() {
		return commandList;
	}

	private boolean allowAdminBypass = false;

	/**
	 * Returns if the Admin Bypass is Enabled
	 * @return If the Admin Bypass is Enabled
	 */
	public boolean allowAdminBypass() {
		return allowAdminBypass;
	}

	/**
	 * If you want to allow the Admin Users to bypass the Permission Requirements, set this to true
	 * @param  allowAdminBypass  True or False value
	 */
	public void setAllowAdminBypass(boolean allowAdminBypass) {
		this.allowAdminBypass = allowAdminBypass;
	}
	/**
	 * Returns the Admin Users
	 * @return List of Admin Users
	 */
	public ArrayList<String> getAdminUsers() {
		return adminUsers;
	}

	/**
	 * Adds the User from ID as an admin
	 * @param id Discord ID of User
	 */
	public void addAdminUser(String id) {
		adminUsers.add(id);
	}


	/**
	 * @param dir The directory or package to look for classes in.
	 */
	public void enableSnooper(String dir){
		Reflections reflections = new Reflections(dir);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Command.class);
		for (final Class c : classes){
			Annotation[] annotations = c.getAnnotations();
			for (Annotation a : annotations) {
				if (a instanceof Command) {
					String cmd = ((Command)a).value();
					if (!commandList.contains(cmd)) {
						commandList.add(cmd);
						System.out.println("Registered command " + cmd + " for class " + c.getName());
						Method[] methods = c.getDeclaredMethods();
						for (Method method : methods) {
							if (method.isAnnotationPresent(IMethod.class)) {
								if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
									listeners.add(method.getDeclaringClass());
									listeningMethods.add(method);
									commandRegistrar.put(cmd, method.getDeclaringClass());
									methodRegistrar.put(cmd, method);
									System.out.println("Registered method " + method.getName() + " for command " + cmd);
									loadData(c, cmd);
								} else {
									throw new IllegalArgumentException(method.getName() + " in " + c.getSimpleName()
											+ " is not public static!");
								}
							}
						}
					}else{
						System.out.println("The class " + c.getName() + " tried to register the command " + cmd + " a second time!");
					}
				}
			}
		}
	}


	/**
	* <p>Returns the Built Help Message with all the Groups and stuff.</p>
	 * @return Built Help Message in List
	*/
	public List<String> buildHelpMessage(){
		List<String> parts = new ArrayList<>();

		for (String group : helpLines.keySet()){
			parts.add(Settings.getHelpMessageBreaker().replace("%group", group));
			String current = "";
			for (String msg : helpLines.get(group)){
				String s = msg;
				String currentStore = current;
				currentStore += "\n" + s;
				if (currentStore.length() >= 1990){
					parts.add(current);
					currentStore = s;
				}
				current = currentStore;
			}
			parts.add(current);
		}
		return parts;
	}

	private void loadData(Class c, String cmd){
		Annotation[] annotations = c.getAnnotations();
		String group = "null";
		String description = "nul";
		String syntax = "null";
		String[] aliases = new String[]{"null"};
		for (Annotation a : annotations) {
			if (a instanceof Group){
				group = ((Group)a).value();
			}
			if (a instanceof Description){
				description = ((Description)a).value();
			}
			if (a instanceof Syntax){
				syntax = ((Syntax)a).value();
			}
			if (a instanceof Aliases){
				aliases = ((Aliases)a).value();
			}
		}
		if (group != "null" && !group.equalsIgnoreCase("hidden")){
			List<String> list = new ArrayList<>();
			if (helpLines.get(group) != null){
				list = helpLines.get(group);
			}
			String msg = Settings.getHelpFormat();
			String aliasesMsg = "";
			if (!(aliases.length == 1 && !aliases[0].equalsIgnoreCase("null"))){
				for (String s : aliases){
					aliasesMsg = aliasesMsg + s + " ";
				}
				aliasesMsg.trim();
			}
			msg = msg.replace("%cmd", cmd).replace("%group", group).replace("%desc", description).replace("%syntax", syntax).replace("%aliases", aliasesMsg);
			list.add(msg);
			helpLines.put(group, list);
		}
	}

	/**
	 *
	 * @param user User to check
	 * @param server Server to check against
	 * @param roleName Role name
	 * @param caseSensitive Case Sensitive
	 * @return True or False
	 */
	public boolean hasRole(User user, Server server, String roleName, boolean caseSensitive){
		if (caseSensitive) {
			for (Role r : user.getRoles(server)) {
				if (r.getName().equalsIgnoreCase(roleName)) {
					return true;
				}
			}
			return false;
		}else{
			return hasRole(user, server, roleName);
		}
	}
	/**
	 *
	 * @param user User to check
	 * @param server Server to check against
	 * @param roleName Role name
	 * @return True or False
	 */
	public boolean hasRole(User user, Server server, String roleName){
		for (Role r : user.getRoles(server)){
			if (r.getName().equals(roleName)){
				return true;
			}
		}
		return false;
	}

}
