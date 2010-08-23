package net.frontlinesms.plugins.scripter;

/**
 * @author Alex Anderson <alex@frontlinesms.com>
 */
public class ScripterScript {
	private static final String[] DEFAULT_SCRIPT = {
		"// Welcome to scripter",
		"// Scripts should be written in the Groovy language",
		"// Available variables (class:name:description):",
		"// * UiGeneratorController:ui:This is the ui controller and can be used to add or remove UI elements.",
		"// * FrontlineSMS:boss:This is the main core of the FrontlineSMS engine.",
		"// * FrontlineEventNotification:n:This is the notification which caused the script to be invoked.",
		"",
		"n",
	};
	
	private String script = join(DEFAULT_SCRIPT);
	
	public synchronized void setScript(String script) {
		this.script = script;
	}
	public synchronized String getScript() {
		return this.script;
	}

	private static String join(String[] lines) {
		StringBuilder bob = new StringBuilder();
		for(String line : lines) {
			bob.append(line);
			bob.append('\n');
		}
		return bob.toString();
	}
}
