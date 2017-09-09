package org.crypticmission.gurpslack.model

import org.eclipse.persistence.jaxb.JAXBContext
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.InputStream
import javax.xml.bind.Unmarshaller
import javax.xml.bind.annotation.XmlElement
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType


/**
 */

class Parent(val kPath: KPath) {
    val children: MutableList<Child> by kPath.list("child")
    val fooChildren: MutableList<Child> by kPath.list("foo/child")
}

class Child(@XmlElement val a: String, @XmlElement val b: String) {
    constructor() : this("default-a", "default-b")
}

fun NodeList.toList() : List<Node> {
    val nodes = mutableListOf<Node>()
    for (i in 0..this.length) {
        nodes += this.item(i)
    }
    return nodes
}

class XProperty<R, T>(val xmlDocument: Document, val unmarshaller: Unmarshaller, val xPathExpression: XPathExpression) :
        ReadOnlyProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        val returnType = (property.getter.returnType.javaType.javaClass)
        val nodeList = xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET) as NodeList
        val node = nodeList.item(0)
        return unmarshaller.unmarshal(node, returnType) as T
    }
}

class XListProperty<R, T>(val xmlDocument: Document, val unmarshaller: Unmarshaller, val xPathExpression: XPathExpression) :
        ReadOnlyProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        val clazz = property.javaField?.type
        println(clazz)
        val nodeList = xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET) as NodeList
        return nodeList.toList().map { unmarshaller.unmarshal(it, clazz)} as T
    }
}

class KPath(val inputStream: InputStream) {
    val builderFactory = DocumentBuilderFactory.newInstance()
    val builder = builderFactory.newDocumentBuilder()
    var xmlDocument = inputStream.use { builder.parse(it) }
    var xPath = XPathFactory.newInstance().newXPath()
    var jc = JAXBContext.newInstance()
    val unmarshaller: Unmarshaller = jc.createUnmarshaller()


    fun <R, T> element(expression: String) = XProperty<R, T> (xmlDocument, unmarshaller, xPath.compile(expression))
    fun <R, T> list(expression: String) = XListProperty<R, T> (xmlDocument, unmarshaller, xPath.compile(expression))
}
class JXMLTest {

    fun loadParent() = Parent(KPath(this::class.java.getResourceAsStream("/test.xml")))

        @Test
        @Ignore
        fun shouldLoad2WhenLoadingRoot() {
            // given
            val parent = loadParent()

            // when

            // then
            assertEquals(1, parent.children.size)
            parent.children.forEachIndexed { i, it ->
                assertEquals("root-${i}-a", it.a)
                assertEquals("root-${i}-b", it.b)
            }
            assertEquals(2, parent.fooChildren.size)
            parent.fooChildren.forEachIndexed { i, it ->
                assertEquals("foo-${i}-a", it.a)
                assertEquals("foo-${i}-b", it.b)
            }

        }

//    @Test
//    fun shouldLoad2WhenLoadingFoo() {
//        // given
//        val parent = loadParent()
//
//        // when
//        val size = parent.fooChildren?.size ?: throw AssertionFailure("no foo children")
//
//        // then
//        assertEquals(2, size)
//
//    }
//
//    @Test
//    fun shouldLoad2WhenLoadingBar() {
//        // given
//        val parent = loadParent()
//
//        // when
//        val size = parent.barChildren?.size ?: throw AssertionFailure("no bar children")
//
//        // then
//        assertEquals(2, size)
//
//    }
//
//    @Test
//    fun shouldLoad4WhenLoadingBarAndBaz() {
//        // given
//        val parent = loadParent()
//
//        // when
//        val size = parent.barAndBazChildren?.size ?: throw AssertionFailure("no baz children")
//
//        // then
//        assertEquals(4, size)
//
//    }
//    @Test
//    fun shouldLoad2WhenLoadingBaz() {
//        // given
//        val parent = loadParent()
//
//        // when
//        val size = parent.bazChildren?.size ?: throw AssertionFailure("no baz children")
//
//        // then
//        assertEquals(2, size)
//
//    }
//
//    @Test
//    fun shouldLoad8WhenLoadingAll() {
//        // given
//        val parent = loadParent()
//
//        // when
//        val size = parent.allChildren?.size ?: throw AssertionFailure("no baz children")
//
//        // then
//        assertEquals(8, size)
//
//    }
//    }
}