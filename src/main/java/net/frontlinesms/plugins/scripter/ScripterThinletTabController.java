/**
 * 
 */
package net.frontlinesms.plugins.scripter;

import java.awt.EventQueue;

import net.frontlinesms.plugins.BasePluginThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * @author Alex Anderson <alex@frontlinesms.com>
 */
public class ScripterThinletTabController extends BasePluginThinletTabController<ScripterPluginController> implements ScripterLog {
	private static final String COMPONENT_SCRIPT_TEXTAREA = "tfScript";
	private static final String COMPONENT_SAVE_BUTTON = "btSave";
	private static final String COMPONENT_REVERT_BUTTON = "btRevert";
	private static final String COMPONENT_LOG_LIST = "lsLog";

	public ScripterThinletTabController(ScripterPluginController controller, UiGeneratorController ui) {
		super(controller, ui);
	}
	
	@Override
	public void setTabComponent(Object tabComponent) {
		super.setTabComponent(tabComponent);
		
		// Update the script text and ensure buttons are en/disabled
		revert();
	}
	
//> LOGGING METHOD
	public void log(final String message) {
		EventQueue.invokeLater(
			new Runnable() {
				public void run() {
					Object logItem = ui.createListItem(message, null);
					ui.add(getLogList(), logItem);
				}
			});
	}
	
//> UI EVENT METHODS
	public void save() {
		getPluginController().getScript().setScript(ui.getText(getScriptTextArea()));
		updateButtons(false);
	}

	public void revert() {
		ui.setText(getScriptTextArea(), getPluginController().getScript().getScript());
		updateButtons(false);
	}
	
	public void textChanged() {
		updateButtons(true);
	}
	
//> UI HELPER METHODS
	private void updateButtons(boolean enable) {
		ui.setEnabled(getSaveButton(), enable);
		ui.setEnabled(getRevertButton(), enable);	
	}
	private Object getScriptTextArea() {
		return super.find(COMPONENT_SCRIPT_TEXTAREA);
	}
	private Object getSaveButton() {
		return super.find(COMPONENT_SAVE_BUTTON);
	}
	private Object getRevertButton() {
		return super.find(COMPONENT_REVERT_BUTTON);
	}
	private Object getLogList() {
		return super.find(COMPONENT_LOG_LIST);
	}
}
