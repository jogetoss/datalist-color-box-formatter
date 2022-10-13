package org.joget.marketplace;

import java.util.Map;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormatDefault;
import org.joget.apps.form.model.FormBinder;
import org.joget.apps.form.model.FormLoadBinder;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.util.WorkflowUtil;
import javax.servlet.http.HttpServletRequest;


public class DatalistColorFormatter extends DataListColumnFormatDefault {
    private final static String MESSAGE_PATH = "messages/DatalistColorFormatter";
    
    @Override
    public String getName() {
        return "Datalist Color Formatter";
    }
    
    @Override
    public String getVersion() {
        return "7.0.0";
    }

    @Override
    public String getDescription() {
        return AppPluginUtil.getMessage("org.joget.marketplace.DatalistColorFormatter.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getLabel() {
        return AppPluginUtil.getMessage("org.joget.marketplace.DatalistColorFormatter.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/DatalistColorFormatter.json", null, true, MESSAGE_PATH);
    }
    
    @Override
    public String format(DataList dataList, DataListColumn column, Object row, Object value) {
        String result = "";
        
        //calling defaults settings from the user/json file
        String defaultBackgroundColor = getPropertyString("DefaultBackgroundColor");
        String defaultFontColor = getPropertyString("DefaultFontColor");
        String defaultFontHorizontalAlignment = getPropertyString("DefaultFontHorizontalAlignment");
        String defaultFontVerticalAlignment = getPropertyString("DefaultFontVerticalAlignment");
        String defaultPadding = getPropertyString("DefaultPadding");
        String defaultBorderRadius = getPropertyString("DefaultBorderRadius");
        String defaultMargin = getPropertyString("DefaultMargin");
        
        HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
        
        if (request != null && request.getAttribute(getClassName()) == null) {
        String html = ".colorFormatterDiv{" + "color:" + defaultFontColor + ";" + "border-radius:" + defaultBorderRadius + "px" + ";" + "margin:" + defaultMargin + "px" + ";" + "margin-top:" + "15" + "px" + ";" + "margin-bottom:" + "15" + "px" + ";" + "padding:" + defaultPadding + "px" + ";" + "text-align:" + defaultFontHorizontalAlignment + ";" + "vertical-align:" + defaultFontVerticalAlignment + ";" + "white-space: nowrap" + ";" + "display:" + "inline-block"+ ";" + "}";
        html = "<style type=\'text/css\'>" + html + "</style>";
            result += html;
        }
        
        Map optionsBinderProperties = (Map) getProperty("optionsBinder");
            if (optionsBinderProperties != null && optionsBinderProperties.get("className") != null && !optionsBinderProperties.get("className").toString().isEmpty()) {
                PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
                FormBinder optionBinder = (FormBinder) pluginManager.getPlugin(optionsBinderProperties.get("className").toString());
                
                if (optionBinder != null) {
                    boolean found = false;
                    optionBinder.setProperties((Map) optionsBinderProperties.get("properties"));
                    FormRowSet rowSet = ((FormLoadBinder)optionBinder).load(null,null,null);
                    //returning the optionColor and the label if the id matches
                    for (FormRow r: rowSet){
                        String optionLabel = r.getProperty("label");
                        String optionId = r.getProperty("value");
                        String optionColor = r.getProperty("grouping");
                        
                        String values[] = ((String)value).split(";");
                        for(String v : values){
                            if(v.equalsIgnoreCase(optionId)){
                                found = true;
                                if(optionColor.isEmpty()){
                                    optionColor = defaultBackgroundColor;
                                }
                                result += "<div class =\"colorFormatterDiv\" style=\"" +"background-color:"+ optionColor + ";" + "\">" + optionLabel + "</div>";
                            }
                        }
                        
                        request.setAttribute(getClassName(), true);
                    }
                    if(!found){
                        result += "<div class =\"colorFormatterDiv\" style=\"" +"background-color:"+ defaultBackgroundColor + ";" + "\">" + value + "</div>";
                    }
                }
            }
        return result;
    }
}
