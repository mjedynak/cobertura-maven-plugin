package org.codehaus.mojo.cobertura.tasks;

import org.codehaus.mojo.cobertura.configuration.ConfigInstrumentation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class ExcludesListPopulatorTest {

    private ExcludesListPopulator excludesListPopulator;
    private ConfigInstrumentation config;

    @Before
    public void setUp() {
        excludesListPopulator = new ExcludesListPopulator();
        config = new ConfigInstrumentation();
    }


    @Test
    public void shouldNotModifyExcludesListWhenFileNotFound() {
        // given
        config.setAdditionalExcludesConfig("notExistingFile.xml");

        // when
        excludesListPopulator.populateExcludesList(config);

        // then
        List<String> excludes = config.getExcludes();
        assertThat(excludes, is(notNullValue()));
        assertThat(excludes.size(), is(0));
    }

    @Test
    public void shouldNotModifyExcludesListWhenFileHasNotCorrectFormat() {
        // given
        config.setAdditionalExcludesConfig("src/test/resources/notCorrectExcludes.xml");

        // when
        excludesListPopulator.populateExcludesList(config);

        // then
        List<String> excludes = config.getExcludes();
        assertThat(excludes, is(notNullValue()));
        assertThat(excludes.size(), is(0));
    }

    @Test
    public void shouldModifyExcludesList() {
        // given
        config.setAdditionalExcludesConfig("src/test/resources/excludes.xml");

        // when
        excludesListPopulator.populateExcludesList(config);

        // then
        List<String> excludes = config.getExcludes();
        assertThat(excludes, is(notNullValue()));
        assertThat(excludes.size(), is(2));
        assertThat(excludes, hasItem("pl/mjedynak/excluded/*.class"));
        assertThat(excludes, hasItem("pl/mjedynak/alsoExcluded/*.class"));
    }
}
