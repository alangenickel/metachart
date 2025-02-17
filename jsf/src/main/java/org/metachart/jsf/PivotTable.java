package org.metachart.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(value="org.metachart.jsf.PivotTable")
@ListenerFor(systemEventClass=PostAddToViewEvent.class)
public class PivotTable extends UINamingContainer
{
final static Logger logger = LoggerFactory.getLogger(PivotTable.class);
	
	private static enum Attribute {data}
	
	private String data;
	
	@Override
	public void processEvent(ComponentSystemEvent event) throws AbortProcessingException
	{
		if(event instanceof PostAddToViewEvent)
		{
			// Include Pivot.js JavaScript
			UIOutput js = new UIOutput();
			js.setRendererType("javax.faces.resource.Script");
			js.getAttributes().put("library", "jsMetaChart");
			js.getAttributes().put("name", "pivot.js");
			FacesContext context = this.getFacesContext();
			context.getViewRoot().addComponentResource(context, js, "head");
			
			// Include FileSaver.js JavaScript
			UIOutput jsfs = new UIOutput();
			jsfs.setRendererType("javax.faces.resource.Script");
			jsfs.getAttributes().put("library", "jsMetaChart");
			jsfs.getAttributes().put("name", "fileSaver.js");
			context = this.getFacesContext();
			context.getViewRoot().addComponentResource(context, jsfs, "head");
			
			// Include Export Renderers for Excel Export JavaScript
			UIOutput jse = new UIOutput();
			jse.setRendererType("javax.faces.resource.Script");
			jse.getAttributes().put("library", "jsMetaChart");
			jse.getAttributes().put("name", "export_renderers.js");
			context = this.getFacesContext();
			context.getViewRoot().addComponentResource(context, jse, "head");
			
			// Include CSS
	        UIOutput css = new UIOutput();
			css.setRendererType("javax.faces.resource.Stylesheet");
			css.getAttributes().put("library", "cssMetaChart");
			css.getAttributes().put("name", "pivot.css");
			context.getViewRoot().addComponentResource(context, css, "head");
		}
		super.processEvent(event);
	}
	
	@Override
	public void encodeAll(FacesContext ctx) throws IOException
	{
		Map<String,Object> map = this.getAttributes();
		this.data         = (String) map.get(Attribute.data.toString());
		
		ResponseWriter writer = ctx.getResponseWriter();
		writer.startElement("script", this);
		// Prepare the column and row default configurations
		ArrayList<String> columns    = new ArrayList<String>();
		ArrayList<String> rows       = new ArrayList<String>();
                ArrayList<String> renderers  = new ArrayList<String>();
                
		String            colDef  = "";
		String            rowDef  = "";
		Boolean           hasAgg  = false;
		logger.debug("Pivot Table has " +this.getChildCount() +" Children");
		for (UIComponent child : this.getChildren())
		{
			logger.debug("class " +child.getClass().toString());
			if (child.getClass().getSimpleName().equals("PivotFields"))
			{
				PivotFields field = (PivotFields) child;
				if (field.getCol()) {columns.add(field.getName());logger.debug("Adding " +field.getName() +" to Column definitions");}
				if (field.getRow()) {   rows.add(field.getName());logger.debug("Adding " +field.getName() +" to Row    definitions");}
			}
                        else if (child.getClass().getSimpleName().equals("PivotRenderer"))
			{
				PivotRenderer renderer = (PivotRenderer) child;
				renderers.add(renderer.getType());
			}
                        else if (child.getClass().getSimpleName().equals("PivotAggregator"))
			{
				hasAgg = true;
			}
		}
		
		if (!columns.isEmpty())
		{
			for (String parameter : columns)
			{
				colDef += "'" +parameter +"',";
			}
			colDef = colDef.substring(0, colDef.lastIndexOf(","));
		}
		
		if (!rows.isEmpty())
		{
			for (String parameter : rows)
			{
				rowDef += "'" +parameter +"',";
			}
			rowDef = rowDef.substring(0, rowDef.lastIndexOf(","));
		}
		
		
		writer.write("$(function(){");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("     var derivers =     $.pivotUtilities.derivers;");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("     var renderers =    $.extend($.pivotUtilities.renderers, $.pivotUtilities.export_renderers);");
		writer.writeText(System.getProperty("line.separator"), null);
		
		writer.write("     var tpl =          $.pivotUtilities.aggregatorTemplates;");
		writer.writeText(System.getProperty("line.separator"), null);
		
		writer.write("     var numberFormat = $.pivotUtilities.numberFormat;");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("     var usFmt = numberFormat();");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("     var usFmtInt = numberFormat({");
		writer.writeText(System.getProperty("line.separator"), null);
	    writer.write("       digitsAfterDecimal: 0");
	    writer.writeText(System.getProperty("line.separator"), null);
	    writer.write("     });");
	    writer.writeText(System.getProperty("line.separator"), null);

	    writer.write("$('#output').pivotUI(");
        writer.write("    " +data +","); 
        writer.writeText(System.getProperty("line.separator"), null);
        writer.write("    {");
        writer.writeText(System.getProperty("line.separator"), null);
        
        
        
        
        // Aggregators can be inserted here
        if (hasAgg)
        {
        	writer.write("    aggregators: {");
        		this.encodeChildren(ctx);
        	writer.write("    },");
        }
        writer.writeText(System.getProperty("line.separator"), null);
        // If Renderer definitions are given, add them here
        if (renderers.size()>0)
        {
            	writer.write("    renderers: {");
                    StringBuffer buffer = new StringBuffer();
                    for (String renderer : renderers)
                    {
                        buffer.append("\"" +renderer +"\": renderers['" +renderer +"']");
                        buffer.append(",");
                    }
                    String jsRendererCommand = buffer.toString();
                    jsRendererCommand = jsRendererCommand.substring(0, jsRendererCommand.lastIndexOf(","));
                writer.write(jsRendererCommand);
        	writer.write("},");
                writer.writeText(System.getProperty("line.separator"), null);
        }
        else
        {
            // Add the renderers incl Exporter
            writer.write("    renderers:   renderers," );
            writer.writeText(System.getProperty("line.separator"), null);
        
        
            writer.writeText(System.getProperty("line.separator"), null);
        }
        
        // Columns and row setup need to be done here because they are not standalone renderable
		writer.write("    cols:        [" +colDef +"]," );
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("    rows:        [" +rowDef +"]," );
		writer.writeText(System.getProperty("line.separator"), null);
        writer.write("    });");
        writer.writeText(System.getProperty("line.separator"), null);
        writer.write("  });");   
        writer.writeText(System.getProperty("line.separator"), null);
        
		// Now add some Excel export magic
		writer.write("function exportToTSVFile() {");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("    console.log('Selected ' +this.value);");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("    if (this.value=='TSV Export'){");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("    var data = $('.pvtRendererArea').find('textarea').text();");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("    var blob = new Blob([data], {type: 'data:text/tsv;charset=utf-8'});");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("    var fileName = 'data.tsv';");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("    saveAs(blob, fileName);");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("    console.log('TSV Selected');}");
		writer.writeText(System.getProperty("line.separator"), null);
		writer.write("};");
		writer.writeText(System.getProperty("line.separator"), null);
		
		// Connect this logic to the selection of the TSV export
		writer.write("$('.pvtRenderer').on('change',exportToTSVFile);");
		writer.writeText(System.getProperty("line.separator"), null);
		
        writer.endElement("script");
        writer.writeText(System.getProperty("line.separator"), null);
        writer.startElement("div", this);
        writer.write("<div id='output' style='margin: 10px;'></div>");
		
		
	}
	
	public String getData() {return data;}
	public void setData(String data) {this.data = data;}

	@Override
	public String getFamily() {
		return null;
	}
}