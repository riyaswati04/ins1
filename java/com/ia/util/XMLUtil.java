package com.ia.util;

import static java.lang.String.format;
import static org.jdom.output.Format.getPrettyFormat;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.XMLFormatter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalNameException;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public final class XMLUtil {
    public static final String DEFAULT_SAX_PARSER = "org.apache.xerces.parsers.SAXParser";

    private static final String ATTR_NAME_VALUE_COUNT_MISMATCH =
            "Number of attribute names does not match number of attribute values";

    private static final String FEATURE_VALIDATE_SCHEMA =
            "http://apache.org/xml/features/validation/schema";

    private static final String ILLEGAL_ATTRIBUTE_ELEMENT_NAME =
            "%s cannot be used as the attribute name of an XML element.";

    private static final String PROPERTY_EXTERNAL_SCHEMA_LOCATION =
            "http://apache.org/xml/properties/schema/external-schemaLocation";

    private final SAXBuilder builder;

    private final XMLOutputter xmlOutputter;

    /**
     * Initialises the class and sets it to use the {@link #DEFAULT_SAX_PARSER} for building.
     */
    public XMLUtil() {
        this(DEFAULT_SAX_PARSER);
    }

    /**
     * Initialises the class and sets it to use the <code>saxParser</code> for building.
     *
     * @param saxParser Fully qualified class name of a SAX Parser to use.
     */
    public XMLUtil(final String saxParser) {
        super();
        builder = new SAXBuilder(saxParser);
        builder.setExpandEntities(false);
        xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    }

    /**
     * Initialises the class and sets it to use the given <code>nameSpace</code> and
     * <code>schemaLocation</code> to validate the document.
     *
     * @param nameSpace XML name space for schema validation.
     * @param schemaLocation URL of the schema.
     */
    public XMLUtil(final String nameSpace, final String schemaLocation) {
        this(DEFAULT_SAX_PARSER, nameSpace, schemaLocation);
    }

    /**
     * Initialises the class and sets it to use the given <code>nameSpace</code> and
     * <code>schemaLocation</code> to validate the document.
     *
     * @param saxParser Fully qualified class name of a SAX Parser to use.
     * @param nameSpace XML name space for schema validation.
     * @param schemaLocation URL of the schema.
     */
    public XMLUtil(final String saxParser, final String nameSpace, final String schemaLocation) {
        this(saxParser);
        builder.setValidation(true);
        builder.setFeature(FEATURE_VALIDATE_SCHEMA, true);
        builder.setProperty(PROPERTY_EXTERNAL_SCHEMA_LOCATION,
                format("%s %s", nameSpace, schemaLocation));
    }

    /**
     * Builds a {@link Document} instance from <code>file</code>.
     *
     * @param file Input XML file.
     * @return A {@link Document} instance from <code>file</code>.
     * @throws JDOMException
     * @throws IOException
     */
    public Document build(final File file) throws JDOMException, IOException {
        synchronized (builder) {
            return builder.build(file);
        }
    }

    /**
     * Builds a {@link Document} instance from <code>inputXML</code>.
     *
     * @param inputXML Valid XML string.
     * @return A {@link Document} instance from <code>inputXML</code>.
     * @throws JDOMException
     * @throws IOException
     */
    public Document build(final String inputXML) throws JDOMException, IOException {
        synchronized (builder) {
            return builder.build(new StringReader(inputXML));
        }
    }

    /**
     * Builds a {@link Document} instance from <code>url</code>.
     *
     * @param url URL of an XML file.
     * @return A {@link Document} instance from <code>url</code>.
     * @throws JDOMException
     * @throws IOException
     */
    public Document build(final URL url) throws JDOMException, IOException {
        synchronized (builder) {
            return builder.build(url);
        }
    }

    /**
     * <p>
     * Returns an XML representation of the input <code>resultSet</code>.
     * </p>
     *
     * <p>
     * The data contained in the <code>resultSet</code> is wrapped in an XML snippet with a root tag
     * specified by <code>rootTagName</code>. Each row of data will be wrapped in an element with
     * name <code>rowTagName</code>. If <code>customAttributeNames</code> are supplied they will be
     * used as the attribute names for each row element. If not the names of the columns are used as
     * attribute names.
     * </p>
     *
     * <p>
     * The value of each attribute of each row element will be the string representation of the
     * value of that column in the result set row.
     * </p>
     *
     * @param resultSet
     * @param rootTagName Name of the root element.
     * @param rowTagName Name of each row element.
     * @param customAttributeNames Optional names of attributes to use for each row element.
     * @return An XML representation of the input <code>resultSet</code>.
     * @throws SQLException
     */
    public String convertToXml(final ResultSet resultSet, final String rootTagName,
            final String rowTagName, final String... customAttributeNames) throws SQLException {
        final Element rootElement = makeElement(rootTagName);
        List<String> listOfColumnNames = new ArrayList<String>();

        if (customAttributeNames.length == 0) {
            listOfColumnNames = getColumnNames(resultSet);
        }
        else {
            listOfColumnNames = Arrays.asList(customAttributeNames);
        }

        while (resultSet.next()) {
            final Element rowElement = makeElement(rowTagName);

            for (int index = 0; index < listOfColumnNames.size(); index++) {
                final String name = listOfColumnNames.get(index);
                final String value = resultSet.getString(index + 1);
                setAttributeValue(rowElement, name, value);
            }

            rootElement.addContent(rowElement);
        }

        final Document document = new Document(rootElement);
        return toPrettyXML(document);
    }

    /**
     * Returns an XML snippet representing an element with name <code>tagName</code> and attributes
     * and values supplied in <code>attrNamesAndValues</code>.
     *
     * @see #makeElement(String, Object...)
     *
     * @param tagName Name of the XML element.
     * @param attrNamesAndValues Map of attribute names and values.
     * @return An XML snippet representing an element with name <code>tagName</code> and attributes
     *         and values supplied in <code>attrNamesAndValues</code>.
     */
    public String formXMLString(final String tagName, final Object... attrNamesAndValues) {
        final Element element = makeElement(tagName, attrNamesAndValues);
        return xmlOutputter.outputString(element);
    }

    /**
     * Returns an XML snippet representing an element with name <code>tagName</code> and attributes
     * and values supplied in <code>attrNamesAndValues</code>.
     *
     * @see #makeElement(String, Object...)
     *
     * @param tagName Name of the XML element.
     * @param attrNamesAndValues Map of attribute names and values.
     * @return An XML snippet representing an element with name <code>tagName</code> and attributes
     *         and values supplied in <code>attrNamesAndValues</code>.
     */
    public String formXMLStringUsingMap(final String tagName,
            final Map<String, String> attrNamesAndValues) {
        final Element element = new Element(tagName);

        for (final Map.Entry<String, String> me : attrNamesAndValues.entrySet()) {
            setAttributeValue(element, me.getKey(), me.getValue());
        }

        return xmlOutputter.outputString(element);
    }

    /**
     * Returns the names of columns from the input {@code resultSet}.
     *
     * @param resultSet
     * @return The names of columns from the input {@code resultSet}.
     * @throws SQLException
     */
    private List<String> getColumnNames(final ResultSet resultSet) throws SQLException {
        final ResultSetMetaData rsmd = resultSet.getMetaData();
        final int numberOfColumns = rsmd.getColumnCount();
        final List<String> listOfColumnNames = new ArrayList<String>();

        for (int index = 0; index < numberOfColumns; index++) {
            final String columnName = rsmd.getColumnName(index + 1);
            listOfColumnNames.add(columnName);
        }

        return listOfColumnNames;
    }

    public Object getElementMatchingPath(final Document document, final String path,
            final Map<String, String> nameSpacePrefixToUri) throws JDOMException {
        final XPath xpath = makeXPath(path, nameSpacePrefixToUri);
        return xpath.selectSingleNode(document);
    }

    /**
     * Returns a single element matching <code>path</code> in <code>document</code>.
     *
     * @param document An {@link Element} or {@link Document} representing whole or part of a parsed
     *        XML tree.
     * @param path A valid XPath expression.
     * @return A single element matching <code>path</code> in <code>document</code>.
     * @throws JDOMException
     */
    public Object getElementMatchingPath(final Object document, final String path)
            throws JDOMException {
        return makeXPath(path).selectSingleNode(document);
    }

    /**
     * Returns a single element matching <code>path</code> in <code>inputXML</code>.
     *
     * @param inputXML A valid XML snippet.
     * @param path A valid XPath expression.
     * @return A single element matching <code>path</code> in <code>inputXML</code>.
     * @throws JDOMException
     * @throws IOException
     */
    public Object getElementMatchingPath(final String inputXML, final String path)
            throws JDOMException, IOException {
        final Document document = build(inputXML);
        return makeXPath(path).selectSingleNode(document);
    }

    public Object getElementMatchingPath(final String inputXML, final String path,
            final Map<String, String> nameSpacePrefixToUri) throws JDOMException, IOException {
        final Document document = build(inputXML);
        final XPath xpath = makeXPath(path, nameSpacePrefixToUri);
        return xpath.selectSingleNode(document);
    }

    /**
     * Returns a list of elements matching <code>path</code> in <code>document</code>.
     *
     * @param document An {@link Element} or {@link Document} representing whole or part of a parsed
     *        XML tree.
     * @param path A valid XPath expression.
     * @return A list of elements matching <code>path</code> in <code>document</code>.
     * @throws JDOMException
     */
    public List<?> getElementsMatchingPath(final Object document, final String path)
            throws JDOMException {
        return makeXPath(path).selectNodes(document);
    }

    /**
     * Returns a list of elements matching <code>path</code> in <code>inputXML</code>.
     *
     * @param inputXML A valid XML snippet.
     * @param path A valid XPath expression.
     * @return A list of elements matching <code>path</code> in <code>inputXML</code>.
     * @throws JDOMException
     * @throws IOException
     */
    public List<?> getElementsMatchingPath(final String inputXML, final String path)
            throws JDOMException, IOException {
        final Document document = build(inputXML);
        return makeXPath(path).selectNodes(document);
    }

    /**
     * <p>
     * Returns an {@link Element} instance with the given {@code tagName}, attributes and values. If
     * no attributes and values are supplied then the element is created without any.
     * </p>
     *
     * @see #makeElement(String, Object[])
     *
     * @param tagName Name of the element.
     * @param attrNamesAndValues List of attribute names and values with each attribute name
     *        followed by corresponding value .
     * @return An {@link Element} instance with the given {@code tagName}, attributes and values.
     * @throws KuberaRuntimeException If the number of attributes do not match the number of values.
     */
    public Element makeElement(final String tagName, final List<?> attrNamesAndValues) {
        return makeElement(tagName, attrNamesAndValues.toArray());
    }

    /**
     * <p>
     * Returns an {@link Element} instance with the given {@code tagName}, attributes and values. If
     * no attributes and values are supplied then the element is created without any.
     * </p>
     *
     * <p>
     * For e.g.
     * </p>
     *
     * <p>
     * (1) <code>makeElement("Grid")</code> returns an element equivalent to
     * <code>&lt;Grid /&gt;</code>.
     * </p>
     *
     * <p>
     * (2) <code>makeElement("Grid", "id", 99, "label", "Total")</code> returns an element
     * equivalent to <code>&lt;Grid id="99"
     * label="Total"/&gt;</code>.
     * </p>
     *
     * <p>
     * (3) <code>makeElement("Grid", "id", 99, "label")</code> will trigger a
     * {@link KuberaRuntimeException} as the number of attributes (two, namely {@code id} and
     * {@code label}) does not match the number of values (one, namely {@literal 99})
     * </p>
     *
     * <p>
     * Note that this method <b>does not</b> escape the attribute names or values. Clients are
     * expected to escape both before passing them to this method.
     * </p>
     *
     * @see XMLFormatter#escapeString(String)
     *
     * @param tagName Name of the element.
     * @param attrNamesAndValues Array of attribute names and values with each attribute name
     *        followed by corresponding value .
     * @return An {@link Element} instance with the given {@code tagName}, attributes and values.
     * @throws KuberaRuntimeException If the number of attributes do not match the number of values.
     */
    public Element makeElement(final String tagName, final Object... attrNamesAndValues) {
        final Element element = new Element(tagName);
        final int numberOfAttrsAndValues = attrNamesAndValues.length;

        if (numberOfAttrsAndValues % 2 != 0) {
            throw new IARuntimeException(ATTR_NAME_VALUE_COUNT_MISMATCH);
        }

        for (int attrIndex = 0; attrIndex < numberOfAttrsAndValues; attrIndex = attrIndex + 2) {
            final int valueIndex = attrIndex + 1;
            final Object attribute = attrNamesAndValues[attrIndex];
            final Object value = attrNamesAndValues[valueIndex];
            setAttributeValue(element, attribute, value);
        }

        return element;
    }

    /**
     * <p>
     * Returns an {@link XPath} instance corresponding to the string {@code path}.
     * </p>
     *
     * @param path String representing a valid XPath.
     * @return An {@link XPath} instance corresponding to the string {@code path}.
     * @throws JDOMException If the input is not a valid XPath.
     */
    private XPath makeXPath(final String path) throws JDOMException {
        return XPath.newInstance(path);
    }

    private XPath makeXPath(final String path, final Map<String, String> nameSpacePrefixToUri)
            throws JDOMException {
        final XPath xpath = XPath.newInstance(path);

        for (final Map.Entry<String, String> me : nameSpacePrefixToUri.entrySet()) {
            xpath.addNamespace(me.getKey(), me.getValue());
        }

        return xpath;
    }

    /**
     * <p>
     * Sets an {@code attribute} of {@code element} to the string representation of input
     * {@code value}.
     * </p>
     *
     * <p>
     * Note that this method <b>does not</b> escape the attribute name or the value. Clients are
     * expected to escape both before passing them to this method.
     * </p>
     *
     * @see XMLFormatter#escapeString(String)
     *
     * @param element
     * @param attribute Name of the XML attribute.
     * @param value Value of the XML attribute.
     * @throws KuberaRuntimeException If <code>attribute.toString()</code> is not a valid XML
     *         attribute name.
     */
    private void setAttributeValue(final Element element, final Object attribute,
            final Object value) {
        try {
            final String attributeName = attribute.toString();
            final String attributeValue = value == null ? "" : value.toString();
            element.setAttribute(attributeName, attributeValue);
        }
        catch (final IllegalNameException e) {
            final String template = ILLEGAL_ATTRIBUTE_ELEMENT_NAME;
            throw new IARuntimeException(format(template, attribute), e);
        }
    }

    /**
     * <p>
     * Returns a string representation of the input {@code document}.
     * </p>
     *
     * @param document
     * @return A string representation of the input {@code document}.
     */
    public String toPrettyXML(final Document document) {
        return xmlOutputter.outputString(document);
    }

    public String toPrettyXML(final Document document, final String encoding) {
        final Format format = getPrettyFormat();
        format.setEncoding(encoding);
        final XMLOutputter outputter = new XMLOutputter(format);
        return outputter.outputString(document);
    }

    /**
     * <p>
     * Returns a string representation of the input {@code element}. This will not contain the &lt;?
     * xml ?&gt; header.
     * </p>
     *
     * @param element
     * @return A string representation of the input {@code element}.
     */
    public String toPrettyXML(final Element element) {
        return xmlOutputter.outputString(element);
    }

    /**
     * Returns an XML snippet of the form &lt;Result success="yes" /&gt; or &lt;Result success="no"
     * /&gt; depending on whether {@code result} is true or not.
     *
     * @param result
     * @return An XML snippet of the form &lt;Result success="yes" /&gt; or &lt;Result success="no"
     *         /&gt; depending on whether {@code result} is true or not.
     */
    public String translateResultToXML(final boolean result) {
        final Element root = new Element("Result");
        setAttributeValue(root, "success", result ? "yes" : "no");
        final Document document = new Document(root);
        return toPrettyXML(document);
    }

    /**
     * Returns an XML snippet of the form
     *
     * <pre>
     * <Result success="yes" attr="value"/>
     * </pre>
     *
     * or <br/>
     *
     * <pre>
     * <Result success="no" attr="value"/>
     * </pre>
     *
     * depending on whether {@code result} is true or not. <br/>
     *
     * For example:
     * <ul>
     * <li><code>translateResultToXML(true, "id", 2)</code> returns <br/>
     *
     * <pre>
     * <Result success="yes" id="2"/>;
     * </pre>
     *
     * </li>
     * <li><code>translateResultToXML(false, "a", "b")</code> returns <br/>
     *
     * <pre>
     * <Result success="no" a="b"/>
     * </pre>
     *
     * </li>
     * </ul>
     *
     * @param result
     * @param attrNamesAndValues List of attribute names and values.
     * @return An XML snippet of the form &lt;Result success="yes" /&gt; or &lt;Result success="no"
     *         /&gt; depending on whether {@code result} is true or not.
     */
    public String translateResultToXML(final boolean result, final Object... attrNamesAndValues) {
        final Element root = makeElement("Result", attrNamesAndValues);
        setAttributeValue(root, "success", result ? "yes" : "no");
        final Document document = new Document(root);
        return toPrettyXML(document);
    }
}
