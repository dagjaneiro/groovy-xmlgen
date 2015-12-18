package com.github.xmlgen

import com.github.xmlgen.annotation.XmlAttr
import com.github.xmlgen.annotation.XmlElem
import com.github.xmlgen.annotation.XmlList
import com.github.xmlgen.annotation.XmlType
import groovy.xml.QName
import groovy.xml.XmlUtil

import java.nio.charset.StandardCharsets

/**
 * Created by daj on 21/07/2015.
 */
class XmlGenerator {

    def xmlParser
    Node rootNode
    def stack = []

    XmlGenerator(def obj) {
        stack.push([node: null, name: null, obj: obj])
        addXmlType()
    }

    private addXmlType() {
        def cur = stack.pop()
        def curObj = cur.obj
        def node = cur.node
        def rootName = cur.name

        if (curObj == null) return
        def xmlRoot = curObj.class.getAnnotation(XmlType)
        if (xmlRoot) {
            def newNode
            def elemName = rootName

            if (elemName == null) {
                elemName = xmlRoot.value() == "[name]" ? curObj.class.getSimpleName() : xmlRoot.value()
            }

            if(node == null) {
                rootNode = newNode = new Node(null , elemName)
            } else {
                newNode = node.appendNode( new QName(elemName), [])
            }

            def classes = []
            def clazz = curObj.class

            while(clazz != null) {
                classes.add(clazz)
                clazz = clazz.getSuperclass()
            }

            classes.each { c ->
                c.declaredFields.each { f ->
                    f.annotations.each { a ->

                        def type = a.annotationType()

                        String attrName = a.value() == "[name]" ? f.getName() : a.value()
                        def obj = curObj."${f.getName()}"

                        switch (type) {
                            case XmlList:
                                String itemName = a.item()
                                addXmlList(newNode, attrName, itemName, obj)
                                break
                            case XmlElem:
                                if (!isSimpleType(obj)) {
                                    stack.push([node: newNode, name: attrName, obj: obj])
                                    addXmlType()
                                }else {
                                    addXmlElement(newNode, attrName, obj)
                                }
                                break
                            case XmlAttr:
                                addXmlAttribute(newNode, attrName, obj)
                                break
                            case XmlType:
                                stack.push([node: newNode, name: attrName, obj: obj])
                                addXmlType()
                        }
                    }
                }
            }
        }
    }

    private static addXmlElement(def node, String key) {
        return node.appendNode( new QName(key) )
    }

    private static addXmlElement(def node, String key, def value) {
        return node.appendNode( new QName(key), value)
    }

    private static addXmlAttribute(def node, String key, def value) {
        if(value) node."@$key" = value
    }

    private addXmlList(def node, String key, String itemName, def array) {
        def newNode = node.appendNode( new QName(key), [] )
        array.each { obj ->
            if(isSimpleType(obj) ) {
                def item = addXmlElement(newNode, itemName)
                addXmlAttribute(item, "value", obj)
            } else {
                stack.push([node: newNode, name: itemName, obj: obj])
                addXmlType()
            }
        }
    }

    String toString() {
        if (rootNode == null) {
            return "Invalid root node"
        }
        def sw = new StringWriter()
        def xmlPrinter = new XmlNodePrinter(new PrintWriter(sw))
        xmlPrinter.preserveWhitespace = true
        xmlPrinter.print(rootNode)
        return sw.toString()
    }

    private static Boolean isSimpleType(def obj) {
        return (obj instanceof String || obj instanceof Integer || obj instanceof BigDecimal || obj instanceof Boolean)
    }

    void write(OutputStream os, String charsetName = StandardCharsets.UTF_8) {
        if (rootNode == null) {
            return
        }
        OutputStreamWriter osw = new OutputStreamWriter(os, charsetName)
        def xmlPrinter = new XmlNodePrinter(new PrintWriter(osw))
        xmlPrinter.preserveWhitespace = true
        xmlPrinter.namespaceAware = false
        xmlPrinter.print(rootNode)
    }
}
