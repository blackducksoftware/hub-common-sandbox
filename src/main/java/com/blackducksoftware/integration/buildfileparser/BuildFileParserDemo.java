package com.blackducksoftware.integration.buildfileparser;

import java.io.File;
import java.io.FileNotFoundException;

import com.synopsys.integration.buildfileparser.BuildFileParser;
import com.synopsys.integration.buildfileparser.ParseResult;
import com.synopsys.integration.buildfileparser.exception.BuildFileContextNotFoundException;
import com.synopsys.integration.buildfileparser.exception.PomXmlParserInstantiationException;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;

public class BuildFileParserDemo {
    public static final String BUILD_GRADLE_PATH = "/Users/ekerwin/working/bfp-demo/build.gradle";
    public static final String POM_XML_PATH = "/Users/ekerwin/working/bfp-demo/pom.xml";
    public static final String PACKAGE_LOCK_JSON_PATH = "/Users/ekerwin/working/bfp-demo/package-lock.json";
    public static final String GEMFILE_LOCK_PATH = "/Users/ekerwin/working/bfp-demo/Gemfile.lock";

    public static void main(final String[] args) throws PomXmlParserInstantiationException {
        final BuildFileParser buildFileParser;
        try {
            buildFileParser = BuildFileParser.createDefault();
        } catch (final PomXmlParserInstantiationException e) {
            System.out.println("Is a SAX parser available? " + e.getMessage());
            throw e;
        }

        try {
            System.out.println("GRADLE RESULTS:");
            performParsing(buildFileParser, BUILD_GRADLE_PATH);
        } catch (BuildFileContextNotFoundException | FileNotFoundException e) {
            System.out.println(String.format("Are you sure that %s is a file named 'build.gradle'? %s", BUILD_GRADLE_PATH, e.getMessage()));
        }

        try {
            System.out.println("MAVEN RESULTS:");
            performParsing(buildFileParser, POM_XML_PATH);
        } catch (BuildFileContextNotFoundException | FileNotFoundException e) {
            System.out.println(String.format("Are you sure that %s is a file named 'pom.xml'? %s", POM_XML_PATH, e.getMessage()));
        }

        try {
            System.out.println("NPM RESULTS:");
            performParsing(buildFileParser, PACKAGE_LOCK_JSON_PATH);
        } catch (BuildFileContextNotFoundException | FileNotFoundException e) {
            System.out.println(String.format("Are you sure that %s is a file named 'package-lock.json'? %s", PACKAGE_LOCK_JSON_PATH, e.getMessage()));
        }

        try {
            System.out.println("RUBYGEMS RESULTS:");
            performParsing(buildFileParser, GEMFILE_LOCK_PATH);
        } catch (BuildFileContextNotFoundException | FileNotFoundException e) {
            System.out.println(String.format("Are you sure that %s is a file named 'Gemfile.lock'? %s", GEMFILE_LOCK_PATH, e.getMessage()));
        }
    }

    public static void performParsing(final BuildFileParser buildFileParser, final String filename) throws BuildFileContextNotFoundException, FileNotFoundException {
        final ParseResult parseResult = buildFileParser.parseFile(new File(filename));
        printParseResult(parseResult);
    }

    public static void printParseResult(final ParseResult parseResult) {
        System.out.println("Parse Result Success: " + parseResult.isSuccess());

        if (parseResult.getNameVersion().isPresent()) {
            System.out.println(parseResult.getNameVersion().toString());
        }

        if (parseResult.getDependencyGraph() != null && parseResult.getDependencyGraph().getRootDependencyExternalIds() != null) {
            for (final ExternalId externalId : parseResult.getDependencyGraph().getRootDependencyExternalIds()) {
                System.out.println(externalId.createExternalId());
            }
        }
        System.out.println("---------------------------------------\n\n\n");
    }

}
