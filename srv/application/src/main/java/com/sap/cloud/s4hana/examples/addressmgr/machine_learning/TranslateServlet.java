package com.sap.cloud.s4hana.examples.addressmgr.machine_learning;

import com.google.common.base.Strings;
import com.sap.cloud.s4hana.examples.addressmgr.machine_learning.commands.MlLanguageDetectionCommand;
import com.sap.cloud.s4hana.examples.addressmgr.machine_learning.commands.MlTranslationCommand;
import com.sap.cloud.s4hana.examples.addressmgr.util.HttpServlet;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.services.scp.machinelearning.LeonardoMlServiceType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import rx.Observable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

@WebServlet("/api/translate")
public class TranslateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = CloudLoggerFactory.getLogger(TranslateServlet.class);

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String input = request.getParameter("input");
        String sourceLang = request.getParameter("sourceLang");

        response.setContentType("text/utf-8");
        final PrintWriter responseWriter = response.getWriter();

        try {
            String output;
            if(Strings.isNullOrEmpty(sourceLang))
                output = determineLanguageAndTranslate(input, "en");
            else
                output = translate(input, sourceLang, "en");
            responseWriter.write("Translation: " + output);
        } catch (Exception e) {
            logger.error("Problem in translation service access", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private String determineLanguageAndTranslate(final String input, final String targetLang) throws Exception {
        final String sourceLang = new MlLanguageDetectionCommand(
                MlService.createFromCfServicesConfig(LeonardoMlServiceType.LANG_DETECTION), input).execute()
                .getLangCode();
        if (StringUtils.isBlank(sourceLang)) {
            logger.debug("Could not translate because language could not be determined: '{}'", input);
            return "";
        }

        return translate(input, sourceLang, targetLang);
    }

    private String translate(final String input, final String sourceLang, final String targetLang) {
        final List<String> results = new MlTranslationCommand(
                MlService.createFromCfServicesConfig(LeonardoMlServiceType.TRANSLATION),
                sourceLang, targetLang, Collections.singletonList(input)).execute();

        if (results.isEmpty()) {
            logger.error("No results from translation 1's input - should not happen");
            return "";
        }
        return results.get(0);
    }
}
