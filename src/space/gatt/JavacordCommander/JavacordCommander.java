package space.gatt.JavacordCommander;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.MessageBuilder;
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
	 */
	public JavacordCommander(DiscordAPI apiInstance){
		this.javacordInstance = apiInstance;
		JavacordCommander.instance = this;
		javacordInstance.registerListener(new CommandListener());
		MessageManager.startManager(1000);
	}

	public JavacordCommander(DiscordAPI apiInstance, Integer messageManagerDelay){
		this.javacordInstance = apiInstance;
		JavacordCommander.instance = this;
		javacordInstance.registerListener(new CommandListener());
		MessageManager.startManager(messageManagerDelay);
	}

	/**
	 * @dir      The directory or package to look for classes in.
	 */
	public JavacordCommander(DiscordAPI apiInstance, String dir){
		this.javacordInstance = apiInstance;
		JavacordCommander.instance = this;
		enableSnooper(dir);
		javacordInstance.registerListener(new CommandListener());
		MessageManager.startManager(1000);
	}

	public JavacordCommander(DiscordAPI apiInstance, String dir, Integer messageManagerDelay){
		this.javacordInstance = apiInstance;
		JavacordCommander.instance = this;
		enableSnooper(dir);
		javacordInstance.registerListener(new CommandListener());
		MessageManager.startManager(messageManagerDelay);
	}

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
	
	public List<Class> getListeners() {
		return listeners;
	}

	public List<Method> getListeningMethods() {
		return listeningMethods;
	}

	public HashMap<String, Class> getCommandRegistrar() {
		return commandRegistrar;
	}

	public HashMap<String, Method> getMethodRegistrar() {
		return methodRegistrar;
	}

	public List<String> getCommandList() {
		return commandList;
	}

	private boolean allowAdminBypass = false;

	public boolean allowAdminBypass() {
		return allowAdminBypass;
	}

	/**
	 * <p>If you want to allow the Admin Users to bypass the Permission Requirements, set this to true</p>
	 */
	public void setAllowAdminBypass(boolean allowAdminBypass) {
		this.allowAdminBypass = allowAdminBypass;
	}

	public ArrayList<String> getAdminUsers() {
		return adminUsers;
	}

	public void addAdminUser(String id) {
		adminUsers.add(id);
	}


	/**
	 * @dir      The directory or package to look for classes in.
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
						throw new IllegalArgumentException("The class " + c.getName() + " tried to register the command " + cmd + " a second time!");
					}
				}
			}
		}
	}

	private static List<String> getParts(String string, int partitionSize) {
		List<String> parts = new ArrayList<String>();
		int len = string.length();
		for (int i=0; i<len; i+=partitionSize)
		{
		    parts.add("```" + Settings.getHelpMessageLanguage() + "\n" + string.substring(i, Math.min(len, i + partitionSize)) + "\n```");
		}
		return parts;
	}

	/**
	* <p>Returns the Built Help Message with all the Groups and stuff.</p>
	*/
	public List<String> buildHelpMessage(){
		MessageBuilder builder = new MessageBuilder();
		for (String group : helpLines.keySet()){
			builder.append(Settings.getHelpMessageBreaker().replace("%group", group));
			builder.appendNewLine();
			for (String msg : helpLines.get(group)){
				builder.append(msg).appendNewLine();
			}
		}
		List<String> parts = getParts(builder.build(), 1950);
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


}
