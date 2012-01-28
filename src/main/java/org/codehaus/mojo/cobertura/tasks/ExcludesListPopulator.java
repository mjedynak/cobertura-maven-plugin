package org.codehaus.mojo.cobertura.tasks;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.mojo.cobertura.configuration.ConfigInstrumentation;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ExcludesListPopulator {

    private static final String EXCLUDE_TAG_NAME = "exclude";
    private static final String EXCLUDES_TAG_NAME = "excludes";

    private Log log = new SystemStreamLog();

    public void populateExcludesList(ConfigInstrumentation config) {
        String additionalExcludesConfig = config.getAdditionalExcludesConfig();
        if (additionalExcludesConfig != null) {
            modifyList(config, additionalExcludesConfig);
        }
    }

    private void modifyList(ConfigInstrumentation config, String additionalExcludesConfig) {
        try {
            Xpp3Dom dom = getDom(additionalExcludesConfig);
            if (dom.getName().equals(EXCLUDES_TAG_NAME)) {
                traverseThroughChildren(config, dom, EXCLUDE_TAG_NAME);
            }
        } catch (FileNotFoundException e) {
            log.warn("Cannot find file: " + additionalExcludesConfig + ", excludes not extended");
            printStackTrace(e);
        } catch (XmlPullParserException e) {
            handleParsingException(additionalExcludesConfig, e);
        } catch (IOException e) {
            handleParsingException(additionalExcludesConfig, e);
        }
    }

    private Xpp3Dom getDom(String additionalExcludesConfig) throws XmlPullParserException, IOException {
        FileReader reader = new FileReader(additionalExcludesConfig);
        return Xpp3DomBuilder.build(reader);
    }

    private void traverseThroughChildren(ConfigInstrumentation config, Xpp3Dom dom, String excludeTagName) {
        for (Xpp3Dom xpp3Dom : dom.getChildren()) {
            addExclude(config, excludeTagName, xpp3Dom);
        }
    }

    private void addExclude(ConfigInstrumentation config, String excludeTagName, Xpp3Dom xpp3Dom) {
        if (xpp3Dom.getName().equals(excludeTagName)) {
            config.addExclude(xpp3Dom.getValue());
        }
    }

    private void handleParsingException(String additionalExcludesConfig, Exception e) {
        log.warn("Exception during parsing " + additionalExcludesConfig + ": " + e.getMessage());
        printStackTrace(e);
    }

    private void printStackTrace(Exception e) {
        if (log.isDebugEnabled()) {
            log.debug(e);
        }
    }

}
