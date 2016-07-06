package com.kcura.certificates;

import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public class Main {

    private static final String DEF_ALIAS = "contenanalyst";

    private static String certlocation;

    private static String caatlocation;

    private static String keystorename;

    private static String keystorepwd;

    private static String keystorealias;

    private static List<Option> optionsList;

    private static File CAAT_INSTALL_DIR;

    private static Options options;

    static {
        optionsList = new ArrayList<>(6);
        options = new Options();
        optionsList.add(new Option("help", "Print this message."));
        optionsList.add(Option.builder("certlocation")
                .argName("certlocation")
                .hasArg()
                .desc("The location of the certificate to install.")
                .build());
        optionsList.add(Option.builder("caatlocation")
                .argName("caatlocation")
                .hasArg()
                .desc("The location of the caat install to install the cert to.")
                .build());
        optionsList.add(Option.builder("keystorename")
                .argName("keystorename")
                .hasArg()
                .desc("The name of the newly generated keystore.")
                .build());
        optionsList.add(Option.builder("keystorepwd")
                .argName("keystorepwd")
                .hasArg()
                .desc("The new keystorepwd for the keystore.")
                .build());
        optionsList.add(Option.builder("keystorealias")
                .argName("keystorealias")
                .hasArg()
                .desc("The alias to associate with this keystore/truststore/cert combination.")
                .build());
        optionsList.forEach(o -> options.addOption(o));
    }

    public static void main(String...args) {
        setValuesFromArgs(args);
        validateValues();
        setCAATInstallDirFromValues();

        // Accept path to cert, path to caat install, keystorepwd for keystore/truststore, keystorepwd, keystorealias
        // Take self-signed cert, package it into PKCS12 format
        // Use the keytool to import into the trust chain ex
        // Update start.ini and ssl.ini files as needed
    }

    private static void setValuesFromArgs(String[] args) {
        CommandLineParser clp = new DefaultParser();
        try {
            CommandLine cl = clp.parse(options, args);
            if (cl.hasOption("help")) { help(); }
            if (cl.hasOption("certlocation")) { certlocation = cl.getOptionValue("certlocation"); }
            if (cl.hasOption("caatlocation")) { caatlocation = cl.getOptionValue("caatlocation"); }
            if (cl.hasOption("keystorename")) { keystorename = cl.getOptionValue("keystorename"); }
            if (cl.hasOption("keystorepwd")) { keystorepwd = cl.getOptionValue("keystorepwd"); }
            if (cl.hasOption("keystorealias")) { keystorealias = cl.getOptionValue("keystorealias"); }
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
        }
    }

    private static void validateValues() {
        checkNotNull(certlocation, "A value must be provided for the certificate location.");
        checkNotNull(caatlocation, "A value must be provided for the CAAT location.");
        checkNotNull(keystorename, "A value must be provided for the new keystore name.");
        checkNotNull(keystorepwd, "A value must be provided for the new keystore password.");
        if (isNullOrEmpty(keystorealias)) {
            System.out.println("No value was provided for keystore alias, setting to contentanalyst.");
            keystorealias = DEF_ALIAS;
        }
        isValidCAAT(caatlocation);
    }

    private static void setCAATInstallDirFromValues() {
        CAAT_INSTALL_DIR = new File(caatlocation);
    }

    private static boolean isValidCAAT(String path) {
        checkNotNull(path);
        File caatInstallDir = new File(path);
        return (
                caatInstallDir.exists() &&
                        caatInstallDir.isDirectory() &&
                        new File(caatInstallDir, "start.ini").exists()

        );
    }

    private static void help() {
        new HelpFormatter().printHelp("CAAT Certificate Installer", options);
        System.exit(0);
    }

}
