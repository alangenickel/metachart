
package org.metachart.xml.graph;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.metachart.org/graph}nodes"/&gt;
 *         &lt;element ref="{http://www.metachart.org/graph}edges"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nodes",
    "edges"
})
@XmlRootElement(name = "graph")
public class Graph
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected Nodes nodes;
    @XmlElement(required = true)
    protected Edges edges;

    /**
     * Gets the value of the nodes property.
     * 
     * @return
     *     possible object is
     *     {@link Nodes }
     *     
     */
    public Nodes getNodes() {
        return nodes;
    }

    /**
     * Sets the value of the nodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Nodes }
     *     
     */
    public void setNodes(Nodes value) {
        this.nodes = value;
    }

    public boolean isSetNodes() {
        return (this.nodes!= null);
    }

    /**
     * Gets the value of the edges property.
     * 
     * @return
     *     possible object is
     *     {@link Edges }
     *     
     */
    public Edges getEdges() {
        return edges;
    }

    /**
     * Sets the value of the edges property.
     * 
     * @param value
     *     allowed object is
     *     {@link Edges }
     *     
     */
    public void setEdges(Edges value) {
        this.edges = value;
    }

    public boolean isSetEdges() {
        return (this.edges!= null);
    }

}
