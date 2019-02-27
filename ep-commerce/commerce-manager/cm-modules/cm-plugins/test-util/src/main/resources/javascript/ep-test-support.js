/**
 * EP Test Utility object used for adding test Ids to widgets.
 */
var EPTest = (function () {
    "use strict";

    var
        ATTRIBUTE_WIDGET_ID = "widget-id",
        ATTRIBUTE_WIDGET_TYPE = "widget-type",
        ATTRIBUTE_RWT_WIDGETID = "wid",
        ATTRIBUTE_WIDGET_PARENT = "parent-widget-id",
        AUTOMATION_ID = "automation-id",
        APPEARANCE_ID = "appearance-id",
        EMPTY = "",
        minifiedJsonMap,
        BEGINNING_ENCODING_MARKER,
        END_ENCODING_MARKER,

        isDefined = function (object) {
            return (typeof(object) !== "undefined" ) && (object !== null);
        },

        waitUntil = function (condition, timeout, callback) {
            if (!condition()) {
                setTimeout(function () {
                    waitUntil(condition, timeout, callback);
                }, timeout);
            } else {
                callback();
            }
        },

        /**
         * create and send a request to server for test-id maps
         * @private
         */
        _requestMaps = function () {
            var mapReq = new XMLHttpRequest();
            var con = rwt.remote.Connection.getInstance();
            var cid = con.getConnectionId();
            var url = "/cm/?servicehandler=TestMapServiceHandler&cid=" + cid;
            mapReq.addEventListener("load", mapListener);
            mapReq.addEventListener("error", handleResponseError);
            mapReq.open("GET", url, false);
            mapReq.send();
        },

        handleResponseError = function () {
            console.log(this.statusText);
        },
        /**
         * function to be called when the requestMap receives a response from the server
         */
        mapListener = function () {
            var bothMaps = JSON.parse(this.responseText);
            minifiedJsonMap = bothMaps.minified;
        },

        _removeTestSequence = function (originalValue) {
            var matcher = BEGINNING_ENCODING_MARKER + "[\\w\\W]*?" + END_ENCODING_MARKER;
            var regex = new RegExp(matcher, 'g');

            return originalValue.replace(regex, "");
        },

        _getIdFromMap = function (text) {
            //Labels might have other symbols added to the encoded text ex: *SD: (id: SD)
            var start = text.indexOf(BEGINNING_ENCODING_MARKER);
            var end = text.indexOf(END_ENCODING_MARKER);

            var slice = text.slice(start + 1, end);
            if (start == -1 && end == -1) {
                return -1;
              }
            //TODO: following line could be reomoved?
            var clean = _removeEncodingMarkers(slice);

            return minifiedJsonMap[clean];
        },

        applyAutomationIds = function (widget, method, text, withRetry) {
            if (isDefined(text)) {
                var automationId = _getIdFromMap(text);

                //Map doesn't have id and retry option is specified, call this function again
                if (!automationId && withRetry) {
                    _requestMaps();
                    applyAutomationIds(widget, method, text, false);
                } else {
                    //Execute original method
                    var decodedValue = _removeTestSequence(text);
                    method.call(widget, decodedValue);

                    //Apply ids
                    if ((widget.getHtmlAttribute(AUTOMATION_ID) === "") && (automationId !== undefined ) && (automationId !== -1)) {
                        widget.setHtmlAttribute(AUTOMATION_ID, automationId);
                    }

                    widget.setHtmlAttribute(ATTRIBUTE_WIDGET_ID, decodedValue);
                }
            }
        },

        waitForWidget = function (wid, callback) {
            var counter = 10,
                waitCheck = function () {
                    var element = document.getElementById(wid);
                    counter = counter - 1;
                    if (counter <= 0) {
                        return true;
                    }
                    return !!element;
                };
            waitUntil(waitCheck, 250, callback);
        },

        addClassToItem = function (row, parent_id, classToAdd) {
            if (typeof(row) !== "undefined" && row !== null) {
                var rwtId;
                if (typeof (row._htmlAttributes ) !== "undefined") {
                    rwtId = row._htmlAttributes.id;
                }
                if (typeof (rwtId) === "undefined") {
                    rwtId = row._rwtId;
                }

                var rapObject = rap.getObject(rwtId);
                var element;
                if (typeof(rapObject) !== "undefined") {
                    element = rapObject.$el;
                }
                addAttributes(element, parent_id, classToAdd)
            }
        },

        addClassToTableRows = function (wid, parentTestId, classToAdd) {
            try {
                waitForWidget(wid, function () {
                    var manager = new rwt.remote.WidgetManager(),
                        table = manager.findWidgetById(wid);
                    var counter = 10;
                    waitUntil(function () {
                        counter--;
                        return !!table._getTopItem() || counter < 0;
                    }, 250, function () {
                        var item = table._getTopItem();
                        while (typeof(item) !== "undefined" && item !== null) {
                            addClassToItem(item, parentTestId, classToAdd);
                            item = item.getNextItem();
                        }
                    });
                });
            } catch (err) {
                console.log(err)
            }
        },

        addClassToComboItems = function (wid, parentTestId, classToAdd) {
            try {
                waitForWidget(wid, function () {
                    var manager = new rwt.remote.WidgetManager();
                    var widget = manager.findWidgetById(wid);
                    var element = widget._element;
                    element.onclick = function () {

                        var rowContainer = manager.findWidgetById(wid)._list._.popup._children[0]._rowContainer;
                        var items = rowContainer._items;
                        items.forEach(function (item) {
                            if (typeof (item) !== "undefined" && item !== null) {
                                item.setHtmlAttribute(ATTRIBUTE_WIDGET_TYPE, classToAdd);
                                item.setHtmlAttribute(ATTRIBUTE_WIDGET_PARENT, parentTestId);
                            }
                        });
                    };
                });
            } catch (err) {
                console.log(err)
            }
        },

        addAttributes = function (element, parent_id, classToAdd) {
            if (typeof (element) !== "undefined" && element !== null) {
                element.attr(ATTRIBUTE_WIDGET_TYPE, classToAdd);
                element.attr(ATTRIBUTE_WIDGET_PARENT, parent_id);
            }
        },

        addAttributesToComplexWidgets = function (rapWidget, widgetType, widgetId, testId) {
            if (widgetType.toLowerCase() === "ccombo") {
                addClassToComboItems(widgetId, testId, "dropdown_row");
            }

            if (widgetType.toLowerCase() === "table") {
                addClassToTableRows(widgetId, testId, "table_row");
            }
        },

        /**
         * Attach test id to RAP widget given its rap widget id.
         * If object cannot be found try to attach test id later, eventually it will be set.
         */
        _mapWidgetIdToTestId = function (widgetId, encodedId, widgetType) {

            try {
                var rapWidget = rap.getObject(widgetId);

                var testId = _removeTestSequence(encodedId);

                if (rapWidget) {
                    var rwtQuery;

                    if (rapWidget.$el) {
                        rwtQuery = rapWidget.$el;
                        addAttributesToComplexWidgets(rapWidget, widgetType, widgetId, testId);
                    }
                    else if (rapWidget.$input) {
                        rwtQuery = rapWidget.$input;
                    }

                    if (rwtQuery) {
                        var automationId = _getIdFromMap(encodedId);
                        if (automationId && automationId !== -1) {
                            rwtQuery.attr(AUTOMATION_ID, automationId);
                        }
                        rwtQuery.attr(ATTRIBUTE_WIDGET_ID, testId);
                        rwtQuery.attr(ATTRIBUTE_WIDGET_TYPE, widgetType);
                        rwtQuery.attr(ATTRIBUTE_RWT_WIDGETID, widgetId);
                    }
                }
            }
            catch (error) {
                console.log(error)
            }
        },

        /**
         * Same as mapWidgetIdToTestId but adds automation-id attribute instead of widget-id.
         */
        _setAutomationIdFromJava = function (widgetId, automationId) {
            try {
                var rapWidget = rap.getObject(widgetId);

                if (rapWidget) {
                    var rwtQuery;

                    if (rapWidget.$el) {
                        rwtQuery = rapWidget.$el;
                    }
                    else if (rapWidget.$input) {
                        rwtQuery = rapWidget.$input;
                    }

                    if (rwtQuery) {
                        rwtQuery.attr(AUTOMATION_ID, automationId);
                        rwtQuery.attr(ATTRIBUTE_RWT_WIDGETID, widgetId);
                    }
                }
            }
            catch (error) {
                console.log(error)
            }
        },

        /**
         * Finds the RWT Widget Id for the given selector
         * @param selector The Selector
         * @returns  string Widget Id if found, otherwise undefined
         */
        findRwtIdForSelector = function (selector) {
            var widget = document.querySelector(selector);
            if (typeof (widget) !== "undefined" && widget !== null) {
                if (typeof(widget.rwtWidget) !== "undefined") {
                    return widget.rwtWidget._rwtId;
                } else if (typeof (widget._item) !== "undefined") {
                    return widget._item._rwtId;
                }
                return widget.id;
            }
        },

        /**
         finds the parent prototype which contains the function func
         */
        _findParentProtoTypeForFunction = function (proto, func) {
            while (typeof(proto) !== "undefined" && proto !== null) {
                if (typeof(proto[func]) !== "undefined") {
                    return proto;
                }
                proto = proto.__proto__;
            }
        },

        _setActiveTab = function () {
            //instantiate a new CTabItem because without at least 1 generate,d the setSelected function is undefined.
            try {
                new rwt.widgets.CTabItem(null, null);
            } catch (err) {
                console.log(err)
            }

            //override CTabItem's _afterDisappear function to remove the active-tab attribute we assigned if the CTabItem is not Seeable
            var proto = _findParentProtoTypeForFunction(rwt.widgets.CTabItem.prototype, "_afterDisappear");
            if (typeof(proto._afterDisappear_cti_original) === "undefined") {
                proto._afterDisappear_cti_original = proto._afterDisappear;
                proto._afterDisappear = function () {
                    this._afterDisappear_cti_original();
                    if (this.__user$appearance === "ctab-item") {
                        try {
                            this.$el.removeAttr("active-tab")
                        } catch (err) {
                            console.log(err)
                        }

                    }
                    _setSeeableAttr(this);
                };
            }

            //code needed for the special case when RAP is wrongly calling the setSelected function and marking a tab as selected even when it's not
            //selected and is not seeable(). This happens when you edit a product and click the Merchandising Associations.
            proto = _findParentProtoTypeForFunction(rwt.widgets.CTabItem.prototype, "_afterInsertDom");
            if (typeof(proto._afterInsertDom_cti_original) === "undefined") {
                proto._afterInsertDom_cti_original = proto._afterInsertDom;
                proto._afterInsertDom = function () {
                    this._afterInsertDom_cti_original();
                    if (this.__user$appearance === "ctab-item") {
                        if (this.isSeeable() && this.isSelected()) {
                            this.setHtmlAttribute("active-tab", true);
                        }
                        else {
                            if (typeof(this.$el) !== "undefined") {
                                this.$el.removeAttr("active-tab");
                            }
                        }
                    }
                };
            }

            //makes sure that only the tabs that are seeable will be marked as active-tab if they are selected and the rest of its sibling ctabs will be unmarked as active-tab
            //this is just for good measure because occasionally rap will wrongly call setselected method on tabs that are not even seeable
            proto = _findParentProtoTypeForFunction(rwt.widgets.CTabItem.prototype, "_afterAppear");
            if (typeof(proto._afterAppear_cti_original) === "undefined") {
                proto._afterAppear_cti_original = proto._afterAppear;
                proto._afterAppear = function () {
                    this._afterAppear_cti_original();
                    if (this.__user$appearance === "ctab-item") {
                        if (this.isSelected()) {
                            this.setHtmlAttribute("active-tab", true);
                            //get the parent of this CTabItem and remove the active-tab attribute from
                            //all children that is not this object (aka that is not the selected CtabItem)
                            var parent = this.getParent();
                            var rwtId = this._rwtId;
                            parent._mapItems(function (item) {
                                try {
                                    if (item._rwtId !== rwtId && typeof(item.$el) !== "undefined") {
                                        item.$el.removeAttr("active-tab");
                                    }
                                } catch (err) {
                                    console.log(err)
                                }
                            });
                        }
                    }
                    _setSeeableAttr(this);
                };
            }

            //override the setSelected func of CTabItem to add active-tab attribute to the selected CTabItem
            //this code will only work once at least one CtabItem has been instantiated otherwise the prototype
            //will return setSelected func as undefined
            proto = rwt.widgets.CTabItem.prototype;
            if (typeof(proto.setSelected_cti_original) === "undefined") {
                proto.setSelected_cti_original = proto.setSelected;

                proto.setSelected = function (isSelected) {
                    this.setSelected_cti_original(isSelected);
                    //__user$overflow = "hidden"

                    if (isSelected) {
                        this.setHtmlAttribute("active-tab", true);
                    }
                    else {
                        if (typeof(this.$el) !== "undefined") {
                            this.$el.removeAttr("active-tab");
                        }
                    }
                }
            }
        },

        _setStructuralPaneLocations = function () {
            var defaultWM = rwt.widgets.base.Window.getDefaultWindowManager();
            var candidates = defaultWM.getActiveWindow()._children;
            var fattestChild = {height: -1};
            candidates.forEach(function (candidate) {
                var candidateHeight = candidate.getHeight();
                if (candidateHeight > fattestChild.height) {
                    fattestChild.height = candidateHeight;
                    fattestChild.entry = candidate;
                }
            });
            var viewPort = fattestChild.entry.getChildren()[0];

            var viewPortId = viewPort._rwtId;
            var panes = [];

            viewPort.setHtmlAttribute("viewPort");
            viewPort.getChildren().forEach(function (child) {
                if (typeof(child._afterDisappear_child_original) === "undefined") {
                    child._afterDisappear_child_original = child._afterDisappear;
                    child._afterDisappear = function () {
                        this._afterDisappear_child_original();
                        this.__user$element.removeAttribute("pane-location");
                        _setSeeableAttr(this);
                        setTimeout(_setStructuralPaneLocations, 100);
                    }
                }

                if (!!child.__user$element) {
                    child.__user$element.removeAttribute("viewport-child");
                    if (child.isSeeable() && ((child.__user$appearance === "composite") || (child.__user$appearance === "ctabfolder"))) {
                        child.setHtmlAttribute("viewport-child", true);
                        _setSeeableAttr(child);
                        panes.push(child);
                    }
                }

            });

            var rightPanes = [];

            //go through the panes, and add left-pane, center-pane, editor-pane etc. based on position
            panes.forEach(function (pane) {
                if (pane.getLeft() <= 40) {
                    if (pane.__user$appearance === "ctabfolder") {
                        pane.setHtmlAttribute("pane-location", "left-pane-outer");
                    }
                    if (pane.__user$appearance === "composite") {
                        pane.setHtmlAttribute("pane-location", "left-pane-inner");
                    }
                } else if (pane.getLeft() > 100) {
                    rightPanes.push(pane);
                }
            });

            rightPanes.forEach(function (pane) {
                if (pane.getTop() < 50) {
                    if (pane.__user$appearance === "composite") {
                        pane.setHtmlAttribute("pane-location", "center-pane-inner");
                    }
                    if (pane.__user$appearance === "ctabfolder") {
                        pane.setHtmlAttribute("pane-location", "center-pane-outer");
                    }
                } else if (pane.getTop() > 100) {
                    pane.setHtmlAttribute("pane-location", "editor-pane");
                }
            });

            if ((typeof(rwt.widgets.Composite.prototype.setParent_composite_old) === "undefined") && defaultWM.getActiveWindow().getHtmlAttribute("window-id") === "post-login-window") {
                rwt.widgets.Composite.prototype.setParent_composite_old = rwt.widgets.Composite.prototype.setParent;
                rwt.widgets.Composite.prototype.setParent = function (parent) {
                    this.setParent_composite_old(parent);
                    if (parent != null) {
                        if ((parent._rwtId !== undefined ) && (viewPortId !== undefined)) {
                            if (parent._rwtId === viewPortId) {
                                setTimeout(_setStructuralPaneLocations, 100);
                            }
                        }
                    }
                }
            }

        },

        _isButtonEnabled = function (selector) {
            var element = document.querySelector(selector);
            if (isDefined(element) && isDefined(element.rwtWidget)
                && isDefined(element.rwtWidget.isEnabled)) {
                return (!!element.rwtWidget.isEnabled());
            }
            return null;
        },

        //sets the current editor inside the editor-pane as the active-editor
        _setActiveEditor = function () {
            var proto = rwt.widgets.ScrolledComposite.prototype;
            if (typeof(proto._afterAppear) !== "undefined" &&
                typeof(proto._afterAppear_sc_original) === "undefined") {
                proto._afterAppear_sc_original = proto._afterAppear;
                proto._afterAppear = function () {
                    this._afterAppear_sc_original();
                    this.setHtmlAttribute("active-editor", "true");
                    _setSeeableAttr(this);
                }
            }

            if (typeof(proto._afterDisappear) !== "undefined" &&
                typeof(proto._afterDisappear_sc_original) === "undefined") {
                proto._afterDisappear_sc_original = proto._afterDisappear;
                proto._afterDisappear = function () {
                    this._afterDisappear_sc_original();
                    this.__user$element.removeAttribute("active-editor");
                    _setSeeableAttr(this);
                }
            }

        },

        _setPostLoginWindowId = function () {
            var defaultWM = rwt.widgets.base.Window.getDefaultWindowManager();
            defaultWM.getActiveWindow().setHtmlAttribute("window-id", "post-login-window");
        },

        _overrideButtonSetText = function () {
            try {
                new rwt.widgets.Button(null);
            } catch (err) {
                console.log(err)
            }

            var pr = rwt.widgets.Button.prototype;
            if (typeof(pr.setText) !== "undefined" &&
                typeof(pr.setText_button_original) === "undefined") {
                pr.setText_button_original = pr.setText;
                pr.setText = function (value) {
                    applyAutomationIds(this, this.setText_button_original, value, true);
                };
            }
        },

        _overrideWidgetSetToolTipText = function () {
                    var proto = _findParentProtoTypeForFunction(rwt.widgets.base.Widget.prototype, "setToolTipText");
                    if (typeof(proto.setToolTipText) !== "undefined" &&
                        typeof(proto.setToolTipText_Widget_original) === "undefined") {
                        proto.setToolTipText_Widget_original = proto.setToolTipText;
                        proto.setToolTipText = function (value) {
                            if(value) {
                                applyAutomationIds(this, this.setToolTipText_Widget_original, value, true);
                            }
                        };
                    }
                },

        /**
         * this method is needed to translate the combo-field otherwise the comboField will have hashed ids.
         * Combo contains a combo-field inside it which is a BasicText.
         * @private
         */
        _overrideBasicTextSetValue = function () {
            var pr = rwt.widgets.base.BasicText.prototype;
            if (typeof(pr.setValue) !== "undefined" &&
                typeof(pr.setValue_BasicText_original) === "undefined") {
                pr.setValue_BasicText_original = pr.setValue;
                pr.setValue = function (value) {
                    applyAutomationIds(this, this.setValue_BasicText_original, value, true);
                };
            }
        },

        _overrideWindowsetCaption = function () {
            var pr = rwt.widgets.base.Window.prototype;
            if (typeof(pr.setCaption) !== "undefined" &&
                typeof(pr.setCaption_Window_original) === "undefined") {
                pr.setCaption_Window_original = pr.setCaption;
                pr.setCaption = function (value) {
                    applyAutomationIds(this, this.setCaption_Window_original, value, true);
                };
            }
        },

        /**
         * This method is needed for the scrollToComboItemWithText method. This method translates the
         * list._items inside the combo to the actual values
         * @private
         */
        _overrideComboSetItems = function () {
            try {
                new rwt.widgets.Combo(null);
            } catch (err) {
                console.log(err)
            }

            var pr = rwt.widgets.Combo.prototype;
            if (typeof(pr.setItems) !== "undefined" &&
                typeof(pr.setItems_Combo_original) === "undefined") {
                pr.setItems_Combo_original = pr.setItems;
                pr.setItems = function (items) {
                    var tempItems = [];
                    items.forEach(function (item, index) {
                        tempItems[index] = _removeTestSequence(item);
                    });

                    this.setItems_Combo_original(tempItems);
                }
            }
        },

        _overrideBasicButtonSetText = function () {
            var pr = rwt.widgets.base.BasicButton.prototype;
            if (typeof(pr.setText) !== "undefined" &&
                typeof(pr.setText_BasicButton_original) === "undefined") {
                pr.setText_BasicButton_original = pr.setText;
                pr.setText = function (value) {
                    applyAutomationIds(this, this.setText_BasicButton_original, value, true);
                };
            }
        },

        _overrideGridColumnSetText = function () {
            try {
                new rwt.widgets.GridColumn(null, null);
            } catch (err) {
                console.log(err)
            }

            var pr = rwt.widgets.GridColumn.prototype;
            if (typeof(pr.setText) !== "undefined" &&
                typeof(pr.setText_GridColumn_original) === "undefined") {
                pr.setText_GridColumn_original = pr.setText;
                pr.setText = function (value) {
                    applyAutomationIds(this, this.setText_GridColumn_original, value, true);
                };
            }
        },

        _overrideGroupSetLegend = function () {
            try {
                new rwt.widgets.Group();
            } catch (err) {
                console.log(err)
            }

            var pr = rwt.widgets.Group.prototype;
            if (typeof(pr._setLegend) !== "undefined" &&
                typeof(pr._setLegend_Group_original) === "undefined") {
                pr._setLegend_Group_original = pr._setLegend;
                pr._setLegend = function (value) {
                    applyAutomationIds(this, this._setLegend_Group_original, value, true);
                };
            }
        },

        _overrideLabelSetText = function () {
            var pr = rwt.widgets.Label.prototype;
            if (typeof(pr.setText) !== "undefined" &&
                typeof(pr.setText_Label_original) === "undefined") {
                pr.setText_Label_original = pr.setText;
                pr.setText = function (value) {
                    applyAutomationIds(this, this.setText_Label_original, value, true);
                }
            }
        },

        _overrideMenuItemSetText = function () {
            try {
                new rwt.widgets.MenuItem(null);
            } catch (err) {
                console.log(err)
            }

            var pr = rwt.widgets.MenuItem.prototype;
            if (typeof(pr.setText) !== "undefined" &&
                typeof(pr.setText_MenuItem_original) === "undefined") {
                pr.setText_MenuItem_original = pr.setText;
                pr.setText = function (value) {
                    applyAutomationIds(this, this.setText_MenuItem_original, value, true);
                };
            }
        },

        _overrideToolItemSetText = function () {
            //instantiate a new ToolItem because without at least 1 generate,d the setToolTipText function is undefined.
            try {
                new rwt.widgets.ToolItem();
            } catch (err) {
                console.log(err)
            }

            var pr = rwt.widgets.ToolItem.prototype;
            if (typeof(pr.setText) !== "undefined" &&
                typeof(pr.setText_ToolItem_original) === "undefined") {
                pr.setText_ToolItem_original = pr.setText;
                pr.setText = function (value) {
                    applyAutomationIds(this, this.setText_ToolItem_original, value, true);
                };
            }
        },

        _overrideCTabItemSetText = function () {
            try {
                new rwt.widgets.CTabItem(null, null);
            } catch (err) {
                console.log(err)
            }
            var pr = rwt.widgets.CTabItem.prototype;
            if (typeof(pr.setText) !== "undefined" && typeof(pr.setText_CTabItem_original) === "undefined") {

                pr.setText_CTabItem_original = pr.setText;
                pr.setText = function (value) {
                    applyAutomationIds(this, this.setText_CTabItem_original, value, true);
                };
            }
        },

        _overrideHyperlinksetText = function(){
            try {
                new org.eclipse.ui.forms.widgets.Hyperlink(null);
            } catch (err) {
                console.log(err)
            }

            var pr = org.eclipse.ui.forms.widgets.Hyperlink.prototype;
            if (typeof(pr.setText) !== "undefined" && typeof(pr.setText_Hyperlink_original) === "undefined") {
                pr.setText_Hyperlink_original = pr.setText;

                pr.setText = function (value) {
                    applyAutomationIds(this, this.setText_Hyperlink_original,  value, true);
                };
            }
        },

        /**
         * remove Bell char before the lookup because the map does not contain bell chars in the ids
         * because adding string with bell char to map as key does not retain the bell char in the key
         * @param text
         * @returns {*}
         * @private
         */
        _removeEncodingMarkers = function(text){
            return text
                .replace(new RegExp(BEGINNING_ENCODING_MARKER, "g"), EMPTY)
                .replace(new RegExp(END_ENCODING_MARKER, "g"), EMPTY);
        },

        _override_called = false,
        /**
         * Overrides the default RWT widget appearance to add an appearance-id.
         */
        _overrideSetAppearances = function () {
            console.log("Adding EP Test overrides");
            if (!_override_called) {
                _override_called = true;
                _setStructuralPaneLocations();
                _setActiveTab();
                _setActiveEditor();
                _overrideWidgetAppearFunctions();
                _overrideRowContainerRenderRow();
                _overrideWidgetSetToolTipText();
                _overrideButtonSetText();
                _overrideBasicButtonSetText();
                _overrideGroupSetLegend();
                _overrideLabelSetText();
                _overrideMenuItemSetText();
                _overrideToolItemSetText();
                _overrideHyperlinksetText();
                _overrideCTabItemSetText();
                _overrideBasicTextSetValue();
                _overrideGridColumnSetText();
                _overrideWindowsetCaption();
                _overrideComboSetItems();

                var widgetClasses = [
                    {
                        widget: rwt.widgets.ToolItem, callback: function (id, widget) {
                        if(widget.getHtmlAttribute(ATTRIBUTE_WIDGET_TYPE) === ""){
                            widget.setHtmlAttribute(ATTRIBUTE_WIDGET_TYPE, "ToolItem");
                        }
                    }
                    },
                    {widget: rwt.widgets.base.Widget},
                    {widget: rwt.widgets.Combo},
                    {widget: rwt.widgets.DropDown},
                    {widget: rwt.widgets.base.HorizontalBoxLayout},
                    {widget: rwt.widgets.base.VerticalBoxLayout},
                    {widget: rwt.widgets.GridItem},
                    {
                        widget: rwt.widgets.CTabItem, callback: function (id, widget) {
                        var parent = widget._parent;
                        if (typeof(parent.getHtmlAttribute(APPEARANCE_ID)) === "undefined") {
                            parent.setHtmlAttribute(APPEARANCE_ID, id);
                        }
                    }
                    },
                    {
                        widget: rwt.widgets.CTabFolder
                    },
                    {
                        widget: rwt.widgets.base.Parent
                    },

                    {
                        widget: rwt.widgets.MenuItem
                    },
                    {
                        widget: rwt.widgets.Label
                    },
                    {
                        widget:rwt.widgets.base.BasicText
                    },
                    {
                        widget:rwt.widgets.base.MultiCellWidget
                    },
                    {
                        widget: rwt.widgets.Group
                    }
                ];

                widgetClasses.forEach(function (widgetClassContainer) {
                    var widgetClass = widgetClassContainer.widget;
                    var pr = widgetClass.prototype;
                    var callback = widgetClassContainer.callback;
                    while (typeof(pr) !== "undefined" && pr !== null) {
                        var fnct = pr.setAppearance;
                        if (typeof(fnct) !== "undefined" && fnct !== null &&
                            typeof(pr.setAppearance_old) === "undefined") {
                            pr.setAppearance_old = pr.setAppearance;
                            pr.setAppearance = function (appearance) {
                                this.setAppearance_old(appearance);
                                try {
                                    this.setHtmlAttribute(APPEARANCE_ID, appearance);
                                    if (typeof(callback) !== "undefined") {
                                        callback(appearance, this);
                                    }
                                } catch (err) {
                                    console.log(err)
                                }
                            };
                            break;
                        }
                        //no setAppearance found on proto, check its parent
                        pr = pr.__proto__;
                    }
                });
            }
            _requestMaps();
        },

        /**
         * set ids to table and tree rows and columns by overriding renderRow func of GridRowContainer
         * @private
         */
        _overrideRowContainerRenderRow = function () {
            try {
                new rwt.widgets.base.GridRowContainer();
            } catch (err) {
                console.log(err)
            }
            rwt.widgets.base.GridRowContainer.prototype._renderRow_GridRowCont_original = rwt.widgets.base.GridRowContainer.prototype._renderRow;
            rwt.widgets.base.GridRowContainer.prototype._renderRow = function (row, item, contentOnly) {
                this._renderRow_GridRowCont_original(row, item, contentOnly);

                if (row && row.$el && row.$cellLabels && row.$cellLabels.length > 0) {

                    row.$cellLabels.forEach(function (cellLabel, columnNum) {
                        if (cellLabel) {
                            var text = cellLabel.text();
                            var value = _removeTestSequence(text);
                            cellLabel.text(value);
                            cellLabel.attr("column-id", value);
                            cellLabel.attr("column-num", columnNum);
                            var automationId = _getIdFromMap(text)
                            if(automationId && automationId !== -1) {
                                cellLabel.attr(AUTOMATION_ID, automationId);
                            }
                        }
                    });

                    //find the index of the first cellLabel that's not undefined and not empty
                    var i = 0;
                    while (!row.$cellLabels[i]) {
                        i++
                    }

                    //TODO row-id should be removed (deprecated)
                    row.$el.attr("row-id", row.$cellLabels[i].text());
                    row.$el.attr(ATTRIBUTE_WIDGET_ID, row.$cellLabels[i].text());
                    if (!row.$el.attr(ATTRIBUTE_WIDGET_TYPE)) {
                        row.$el.attr(ATTRIBUTE_WIDGET_TYPE, "row");
                    }

                    if (this._items) {
                        var index = this._items.indexOf(item);
                        if (index !== null) {
                            row.$el.attr("row-num", index);
                        }
                    }

                    if (row.$expandIcon) {
                        row.$expandIcon.attr("expand-icon", EMPTY);
                    }
                }
            }
        },

        _setSeeableAttr = function (widget) {
            if (widget.isSeeable()) {
                widget.setHtmlAttribute("seeable", true);
            }
            else {
                widget.setHtmlAttribute("seeable", false);
            }
        },

        /**
         * when a widget appears, set a seeable attr if isSeeable() is true and set text-id to the widget
         * when it disappears, remove the seeable attr
         * Note: rwt's default afterAppear function sets is isSeeable() to true
         * Note; rwt's default afterDisappear function sets is isSeeable() to false
         */
        _overrideWidgetAppearFunctions = function () {
            //afterAppear function is called by rwt when the element appears on screen
            if (typeof(rwt.widgets.base.Widget.prototype._afterAppear_widget_original) === "undefined") {
                rwt.widgets.base.Widget.prototype._afterAppear_widget_original = rwt.widgets.base.Widget.prototype._afterAppear;
                rwt.widgets.base.Widget.prototype._afterAppear = function () {
                    this._afterAppear_widget_original();
                    _setSeeableAttr(this);
                    //also set widget-id to widgets
                    _setWidgetID(this);
                }
            }
            //afterDisappear function is called by rwt when the element disappears from screen, but could exist in the DOM
            if (typeof(rwt.widgets.base.Widget.prototype._afterDisappear_widget_original) === "undefined") {
                rwt.widgets.base.Widget.prototype._afterDisappear_widget_original = rwt.widgets.base.Widget.prototype._afterDisappear;
                rwt.widgets.base.Widget.prototype._afterDisappear = function () {
                    this._afterDisappear_widget_original();
                    _setSeeableAttr(this);
                }
            }

            //need this because parent's class overrides widget's afterAappear function and so overriding afterAppear of widget class does not affect the parent class
            if (typeof(rwt.widgets.base.Parent.prototype._afterAppear_parent_original) === "undefined") {
                rwt.widgets.base.Parent.prototype._afterAppear_parent_original = rwt.widgets.base.Parent.prototype._afterAppear;
                rwt.widgets.base.Parent.prototype._afterAppear = function () {
                    this._afterAppear_parent_original();
                    _setSeeableAttr(this);
                    //also set widget-id to widgets
                    _setWidgetID(this);
                }
            }
            //need this because parent's class overrides widget's afterDisappear function and so overriding afterDisappear of widget class does not affect the parent class
            if (typeof(rwt.widgets.base.Parent.prototype._afterDisappear_parent_original) === "undefined") {
                rwt.widgets.base.Parent.prototype._afterDisappear_parent_original = rwt.widgets.base.Parent.prototype._afterDisappear;
                rwt.widgets.base.Parent.prototype._afterDisappear = function () {
                    this._afterDisappear_parent_original();
                    _setSeeableAttr(this);
                }
            }
        },

        /**
         * Sets widget-id to given widget if it doesn't already have it.
         * @param widget
         * @private
         */
        _setWidgetID = function (widget) {
            if (widget._rawText && widget.getHtmlAttribute(ATTRIBUTE_WIDGET_ID) === "") {
                widget.setHtmlAttribute(ATTRIBUTE_WIDGET_ID, widget._rawText);
            }
            else if ((widget.getHtmlAttribute(ATTRIBUTE_WIDGET_ID) === "") && widget.getToolTipText()) {
                widget.setHtmlAttribute(ATTRIBUTE_WIDGET_ID, widget.getToolTipText());
            }
        },

        /**
         * Scroll the CCombo for the given selector to the row with text .
         * And Select the item.
         * @param selector The selector for the CCombo.
         * @param text the text to match.
         */
        _scrollToComboItemWithText = function (selector, text) {
            var element = document.querySelector(selector);
            if (isDefined(element) && isDefined(element.rwtWidget)) {
                var widget = element.rwtWidget;
                var items = widget._list.getItems();
                items.forEach(function (item, index) {
                    if (item.trim() === text.trim()) {
                        widget.select(index);
                    }
                });
            }

        },

        /**
         * Scroll the Table for the given selector to the row with text in the given column.
         * And Select the item.
         * @param selector The selector for the table.
         * @param text the text to match.
         * @param columnName
         * @return boolean Trueif the table scrolls to the row. False otherwise.
         */
        _scrollToTableItemWithText = function (selector, text, columnName) {
            var parent_wid = findRwtIdForSelector(selector);
            var manager = new rwt.remote.WidgetManager();
            var table = manager.findWidgetById(parent_wid);
            if (typeof (table) === "undefined" || table === null) {
                return false;
            }
            var columns = table._columns;
            var columnIndex = -1;
            for (var key in columns) {
                var column = columns[key];
                if (column._text === columnName) {
                    columnIndex = column._index;
                }
            }
            if (columnIndex < 0) {
                return false;
            }
            var item = table._getTopItem();

            var matchTextToAnyString = function (strings, text) {

                return typeof (item._texts[columnIndex]) !== "undefined" && item._texts[columnIndex] !== null
                    && item._texts[columnIndex].toLowerCase() === text.toLowerCase();

            };

            while (typeof(item) !== "undefined" && item !== null) {
                if (item._texts !== null && typeof(item._texts) !== "undefined"
                    && matchTextToAnyString(item._texts, text)) {
                    table.scrollItemIntoView(item);
                    table.deselectAll();
                    table.selectItem(item);
                    return true;
                }
                item = item.getNextItem();
            }
            return false;
        },

        _scrollWidgetIntoView = function (selector) {
            var element = document.querySelector(selector);
            if (isDefined(element) && isDefined(element.rwtWidget) && isDefined(element.rwtWidget.scrollIntoView)) {
                element.rwtWidget.scrollIntoView();
                element.rwtWidget.focus();
            }
        },

        _storeMinifiedMap = function(minJsonMap){
            minifiedJsonMap = minJsonMap;
        },

    _setEncodingMarkers = function (beginningMarker, endMarker) {
        BEGINNING_ENCODING_MARKER = beginningMarker;
        END_ENCODING_MARKER = endMarker;
    },

    _isElementInteractable = function (selector) {
        var isParent = function (element, candidate) {
            if (element.parentElement === candidate) {
                return true;
            } else if (element.parentElement === null) {
                return false;
            } else {
                return isParent(element.parentElement, candidate)
            }
        };

        var element = document.querySelector(selector);
        if (element) {
            var rect = element.getBoundingClientRect();
            var topElement = document.elementFromPoint(rect.left+1, rect.top+1);
            if(topElement) {
                return element === topElement || isParent(topElement, element);
            }
        }
        return false;
    };

    return {
        isButtonEnabled: _isButtonEnabled,
        scrollWidgetIntoView: _scrollWidgetIntoView,
        scrollToTableItemWithText: _scrollToTableItemWithText,
        scrollToComboItemWithText: _scrollToComboItemWithText,
        overrideSetAppearances: _overrideSetAppearances,
        mapWidgetIdToTestId: _mapWidgetIdToTestId,
        setAutomationIdFromJava: _setAutomationIdFromJava,
        setPostLoginWindowId: _setPostLoginWindowId,
        isElementInteractable: _isElementInteractable,
        storeMinifiedMap: _storeMinifiedMap,
        requestMaps:_requestMaps,
        setEncodingMarkers: _setEncodingMarkers
    };
})
();
