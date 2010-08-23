/**
 * 
 */
package net.frontlinesms.plugins.scripter;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * @author Alex Anderson <alex@frontlinesms.com>
 */
@PluginControllerProperties(i18nKey="plugins.scripter", iconPath="icons/forms_large.png", springConfigLocation=PluginControllerProperties.NO_VALUE, hibernateConfigPath=PluginControllerProperties.NO_VALUE, name="Scripter ALPHA")
public class ScripterPluginController extends BasePluginController implements EventObserver {
	private static final String UI_FILE_TAB = "/ui/plugins/scripter/scripterTab.xml";
	private final ScripterScript script = new ScripterScript();
	private EventBus eventBus;
	private ScripterThinletTabController tabController;
	private UiGeneratorController ui;

	/** @see net.frontlinesms.plugins.BasePluginController#initThinletTab(net.frontlinesms.ui.UiGeneratorController) */
	@Override
	protected Object initThinletTab(UiGeneratorController ui) {
		this.ui = ui;
		this.tabController = new ScripterThinletTabController(this, ui);

		Object tab = ui.loadComponentFromFile(UI_FILE_TAB, tabController);
		tabController.setTabComponent(tab);
		
		return tab;
	}

	/** @see net.frontlinesms.plugins.PluginController#deinit() */
	public void deinit() {
		this.eventBus.unregisterObserver(this);
	}

	/** @see net.frontlinesms.plugins.PluginController#init(net.frontlinesms.FrontlineSMS, org.springframework.context.ApplicationContext) */
	public void init(FrontlineSMS frontlinesms, ApplicationContext arg1)
			throws PluginInitialisationException {
		this.eventBus = frontlinesms.getEventBus();
		this.eventBus.registerObserver(this);
	}

	public void notify(FrontlineEventNotification n) {
		// Run the script IN ITS CURRENT FORM
		Runnable runnable = getRunnable(n);
		if(runnable != null) {
			new Thread(runnable, "ScripterThread").start();
		}
	}

	public ScripterScript getScript() {
		return this.script;
	}
	
	private Runnable getRunnable(final FrontlineEventNotification n) {
		final String currentScript = script.getScript();
		if(currentScript == null || currentScript.trim().length() == 0) {
			return null;
		} else {
			return new Runnable() {
				public void run() {
					// invoke Groovy script
					log("Running da script.");
					
					Binding binding = new Binding();
					binding.setVariable("ui", ui);
					binding.setVariable("boss", ui.getFrontlineController());
					binding.setVariable("n", n);
					
					GroovyShell shell = new GroovyShell(binding);
					try {
						Object returned = shell.evaluate(currentScript);
						log("returned: " + returned);
					} catch(CompilationFailedException ex) {
						log("Compilation failed: " + ex.getMessage());
					} catch(GroovyRuntimeException ex) {
						log("Script execution failed: " + ex.getMessage());
					}
				}
				
				private void log(String message) {
					if(tabController != null) {
						tabController.log(message);
					}
				}
			};
		}
	}
}
