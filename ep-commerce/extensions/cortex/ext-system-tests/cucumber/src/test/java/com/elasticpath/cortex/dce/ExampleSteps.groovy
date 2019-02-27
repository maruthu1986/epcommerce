package com.elasticpath.cortex.dce

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks


this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


When(~'^I search for an item name (.+)$') { itemName ->
	CommonMethods.searchAndOpenItemWithKeyword(itemName)
}

