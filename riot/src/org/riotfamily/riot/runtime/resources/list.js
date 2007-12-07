var RiotList = Class.create();
RiotList.prototype = {

	initialize: function(key) {
		this.key = key;
	},

	render: function(target, commandTarget, filterForm) {
		this.table = RBuilder.node('table', {parent: $(target)},
			RBuilder.node('thead', null,
				this.headRow = RBuilder.node('tr')
			),
			this.tbody = RBuilder.node('tbody')
		);
		this.pager = new Pager(RBuilder.node('div', {parent: $(target)}), this.gotoPage.bind(this));
		if (filterForm) {
			this.filterForm = $(filterForm);
		}
		Event.observe(window, 'resize', this.resizeColumns.bind(this));
		ListService.getModel(this.key, this.renderTable.bind(this, commandTarget));
	},

	renderFormCommands: function(objectId, target) {
		var item = {objectId: objectId};
		ListService.getFormCommands(this.key, objectId, this.appendCommands.bind(this, target, true, item));
	},

	renderTable: function(commandTarget, model) {
		this.columns = [];
		this.headings = {};
		this.table.className = model.cssClass;
		model.columns.each(this.addColumn.bind(this));
		var th = RBuilder.node('th', {className: 'commands', parent: this.headRow,
			style: { width: model.itemCommandCount * 34 + 'px' }});

		this.appendCommands(commandTarget, true, null, model.listCommands);
		this.updateFilter(model);
	},

	updateColsAndRows: function(model) {
		model.columns.each(this.updateSortIndicator.bind(this));
		this.updateRows(model);
	},

	updateRowsAndPager: function(model) {
		this.updateRows(model);
		this.pager.update(model.currentPage, model.pages);
	},

	addColumn: function(col) {
		var label;
		var className = 'col-' + (this.columns.length + 1) + ' ' + col.cssClass;
		var th = RBuilder.node('th', {property: col.property, className: className},
			label = RBuilder.node('span', {innerHTML: col.heading})
		);
		this.columns.push(th);
		this.headings[col.property] = label;
		if (col.sortable) {
			th.addClassName('sortable');
			Event.observe(th, 'click', this.sort.bindAsEventListener(this));
			this.updateSortIndicator(col);
		}
		this.headRow.appendChild(th);
	},

	updateSortIndicator: function(col) {
		var e = this.headings[col.property];
		if (col.sorted) {
			e.toggleClassName('sorted-asc', col.ascending);
			e.toggleClassName('sorted-desc', !col.ascending);
		}
		else {
			e.removeClassName('sorted-asc');
			e.removeClassName('sorted-desc');
		}
	},

	sort: function(event) {
		var property = Event.findElement(event, 'th').property;
		ListService.sort(this.key, property, this.updateColsAndRows.bind(this));
	},

	filter: function(filter) {
		ListService.filter(this.key, filter, this.updateRowsAndPager.bind(this));
	},

	reset: function() {
		ListService.filter(this.key, null, this.updateFilter.bind(this));
	},

	updateFilter: function(model) {
		this.updateRowsAndPager(model);
		if (this.filterForm) {
			this.filterForm.update(model.filterFormHtml);
		}
	},

	resizeColumns: function() {
		if (this.columnsSized) {
			this.columns.each(function(th) {
				th.style.width = 'auto';
			});
			this.columnsSized = false;
		}
	},

	gotoPage: function(page) {
		if (!this.columnsSized) {
			this.columns.each(function(th) {
				var colWidth = th.offsetWidth - parseInt(th.getStyle('padding-left'))
					- parseInt(th.getStyle('padding-right'));

				th.style.width = colWidth + 'px';
			});
			this.columnsSized = true;
		}
		ListService.gotoPage(this.key, page, this.updateRowsAndPager.bind(this));
	},

	updateRows: function(model) {
		this.tbody.update();
		model.items.each(this.addRow.bind(this, model));
	},

	addRow: function(model, row) {
		var tr = RBuilder.node('tr');
		Event.observe(tr, 'mouseover', tr.addClassName.bind(tr, 'highlight'));
		Event.observe(tr, 'mouseout', tr.removeClassName.bind(tr, 'highlight'));
		if (row.cssClass) {
			tr.addClassName(row.cssClass);
		}
		if (row.lastOnPage) {
			tr.addClassName('last');
		}
		if (row.defaultCommandId) {
			Event.observe(tr, 'click', this.execCommand.bind(this, row, row.defaultCommandId, false));
		}

		for (var i = 0; i < row.columns.length; i++) {
			RBuilder.node('td', {innerHTML: row.columns[i], parent: tr, className: this.columns[i].className});
		}

		var td = RBuilder.node('td', {className: 'commands highlight-default'});
		Event.observe(td, 'mouseover', td.removeClassName.bind(td, 'highlight-default'));
		Event.observe(td, 'mouseout', td.addClassName.bind(td, 'highlight-default'));

		this.appendCommands(td, false, row, row.commands);

		row.commands.each(function(command) {
			if (command.itemStyleClass) {
				tr.addClassName(command.itemStyleClass);
			}
		});

		tr.appendChild(td);
		this.tbody.appendChild(tr);
	},

	appendCommands: function(el, renderLabel, item, commands) {
		el = $(el);
		var handler = this.execItemCommand.bindAsEventListener(this);
		commands.each(function(command) {
			var a = RBuilder.node('a', {href: '#', item: item,
					className: 'action action-' + command.styleClass});

			if (command.enabled) {
				a.command = command;
				a.addClassName('enabled');
				if (item && item.defaultCommandId == command.id) {
					a.addClassName('default');
				}
				Event.observe(a, 'click', handler);
			}
			else {
				a.addClassName('disabled');
				a.onclick = Event.stop;
			}
			if (renderLabel) {
				RBuilder.node('span', {className: 'label', parent: a, innerHTML: command.label});
			}
			else {
				a.title = command.label.stripTags();
			}
			el.appendChild(a);
		});
	},

	execItemCommand: function(event) {
		var a = Event.findElement(event, 'a');
		this.execCommand(a.item, a.command.id, false);
		Event.stop(event);
	},

	execCommand: function(item, commandId, confirmed) {
		ListService.execCommand(this.key, item, commandId, confirmed,
				this.processCommandResult.bind(this));
	},

	processCommandResult: function(result) {
		if (!result) return;
		if (result.action == 'confirm') {
			if (confirm(result.message)) {
				this.execCommand(result.item, result.commandId, true);
			}
		}
		else if (result.action == 'gotoUrl') {
			var win = eval(result.target);
			if (result.replace) {
				win.location.replace(result.url);
			}
			else {
				win.location.href = result.url;
			}
		}
		else if (result.action == 'popup') {
			var win = window.open(result.url, result.windowName || 'commandPopup');
			if (!win) {
				alert(result.popupBlockerMessage || 'The Popup has been blocked by the browser.');
			}
			else {
				if (win.focusLost) {
					win.close();
					win = window.open(result.url, result.windowName || 'commandPopup');
				}
				win.focus();
				win.onblur = function() {
					this.focusLost = true;
				}
			}
		}
		else if (result.action == 'reload') {
			window.location.reload();
		}
		else if (result.action == 'eval') {
			eval(result.script);
		}
		else if (result.action == 'setRowStyle') {
			alert(result.objectId + ': ' + result.rowStyle);
		}
	}

}

dwr.engine.setErrorHandler(function(err, ex) {
	if (ex.javaClassName == 'org.riotfamily.riot.list.ui.ListSessionExpiredException') {
		location.reload();
	}
	else {
		alert(err);
	}
});

dwr.engine.setPreHook(function() {
	if (top.setLoading) top.setLoading(true);
});

dwr.engine.setPostHook(function() {
   if (top.setLoading) top.setLoading(false);
}); 
