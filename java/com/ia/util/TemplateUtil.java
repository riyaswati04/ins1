package com.ia.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.github.mustachejava.Mustache;
import com.google.gson.Gson;

public final class TemplateUtil {
    private static void markAsExpiredAndSetContentType(final HttpServletResponse response,
            final String contentType) {
        response.setHeader("Cache-Control", "no-cache,no-store,max-age=0,must-revalidate");
        response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
        response.setContentType(contentType);
    }

    public static void renderExpiredJSONToResponse(final HttpServletResponse response,
            final String json) throws IOException {
        markAsExpiredAndSetContentType(response, "application/json");
        writeToResponse(response, json);
    }

    public static void renderExpiredJSONToResponse(final HttpServletResponse response,
            final String json, final String contentType) throws IOException {
        markAsExpiredAndSetContentType(response, contentType);
        writeToResponse(response, json);
    }

    public static void renderExpiredPage(final HttpServletResponse response,
            final Mustache template, final Map<String, ?> context) throws IOException {
        markAsExpiredAndSetContentType(response, "text/html");

        final PrintWriter writer = response.getWriter();
        template.execute(writer, context).close();
    }

    public static void renderExpiredXMLToResponse(final HttpServletResponse response,
            final String xml) throws IOException {
        markAsExpiredAndSetContentType(response, "text/xml");
        writeToResponse(response, xml);
    }

    public static String renderTemplate(final Mustache template, final Map<String, Object> context)
            throws IOException {
        final StringWriter writer = new StringWriter();
        template.execute(writer, context).close();
        return writer.toString();
    }

    public static String toJSON(final Object object) {
        return new Gson().toJson(object);
    }

    private static void writeToResponse(final HttpServletResponse response, final String string)
            throws IOException {
        final PrintWriter writer = response.getWriter();
        writer.println(string);
        writer.flush();
    }
}
