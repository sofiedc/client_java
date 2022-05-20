package io.prometheus.client.exporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

/**
 * The MetricsServlet class exists to provide a simple way of exposing the metrics values.
 *
 */
public class MetricsServlet extends HttpServlet {

  private CollectorRegistry registry;

  /**
   * Construct a MetricsServlet for the default registry.
   */
  public MetricsServlet() {
    this(CollectorRegistry.defaultRegistry);
  }

  /**
   * Construct a MetricsServlet for the given registry.
   * @param registry collector registry
   */
  public MetricsServlet(CollectorRegistry registry) {
    this.registry = registry;
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
          throws ServletException, IOException {
    boolean includeExtendedDoc = Boolean.valueOf(req.getParameter("doc"));

    resp.setStatus(HttpServletResponse.SC_OK);
    resp.setContentType(TextFormat.CONTENT_TYPE_004);

    Writer writer = new BufferedWriter(resp.getWriter());
    try {
      TextFormat.write004(writer, registry.filteredMetricFamilySamples(parse(req)), includeExtendedDoc);
      writer.flush();
    } finally {
      writer.close();
    }
  }

  private Set<String> parse(HttpServletRequest req) {
    String[] includedParam = req.getParameterValues("name[]");
    if (includedParam == null) {
      return Collections.emptySet();
    } else {
      return new HashSet<String>(Arrays.asList(includedParam));
    }
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
          throws ServletException, IOException {
    doGet(req, resp);
  }

}
