package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class ToggleRequestStatisticsCommand extends AbstractCommand  {

	private RequestCountFilterPlugin requestCountFilterPlugin;
	
	public RequestCountFilterPlugin getRequestCountFilterPlugin() {
		return requestCountFilterPlugin;
	}

	public void setRequestCountFilterPlugin(RequestCountFilterPlugin requestCountFilterPlugin) {
		this.requestCountFilterPlugin = requestCountFilterPlugin;
	}

	@Override
	protected boolean isEnabled(CommandContext context, String action) {
		return true;
	}

	@Override
	protected String getStyleClass(CommandContext context, String action) {
		return requestCountFilterPlugin.isEnabled() ?
				"switchOn" : "switchOff";
	}
	
	public CommandResult execute(CommandContext context) {
		requestCountFilterPlugin.setEnabled(!requestCountFilterPlugin.isEnabled());
		GotoUrlResult result = new GotoUrlResult(context, context.getListDefinition().getEditorUrl(null, context.getParentId(), context.getParentEditorId()));
		result.setTarget("top.frames.editor");
		return result;
	}


}
