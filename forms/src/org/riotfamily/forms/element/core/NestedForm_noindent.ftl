<div id="${form.id}" class="nestedForm ${form.property?default('unbound')}">
<#if form.required || form.present>
	
	<#list elements.elements as element>
    	<#if element.compositeElement?if_exists>
			<#assign class = "composite" />
		<#else>
			<#assign class = "single" />
		</#if>
		<div class="form-element">
			<#if element.label?exists>
				<div class="title">
					<label>
						<#if element.label?has_content>
							${element.label}
						<#else>
							<span class="no-label"></span>
						</#if>
						<#if element.hint?exists>
							<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')"></span>
						</#if>
					</label>
				</div>
			</#if>
			<div class="${class}">
				<#if element.hint?exists>
					<div id="${element.id}-hint" class="hint">${element.hint}</div>
				</#if>
				${element.render()}
				${element.form.errors.renderErrors(element)}
			</div>
		</div>
	</#list>			
	
	<#if !form.required && form.present>
  		${toggleButton.render()}
	</#if>
	
<#else>
	<div class="setButton">${toggleButton.render()}</div>
</#if>
</div>