package com.kcura.certificates;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * This is a driver class that accepts a number of arguments that are used to create a new keystore/truststore for CAAT
 * using the provided values.  Some values in CAAT config files will be overridden depending upon the values provided.
 *
 * This class will provide support for intermediate certificates in the future.  For now, self-signed certs in PKCS12 format only.
 *
 * @author Michael Di Salvo
 */
public class CertInstaller {

    /**
     * The default alias for the keystore/truststore/cert combination
     */
    private static final String DEF_ALIAS = "contenanalyst";

    /**
     * The passed in path to the cert to import into CAAT
     */
    private static String certlocation;

    /**
     * The passed in path to the CAAT install to update
     */
    private static String caatlocation;

    /**
     * The passed in name of the new keystore to create
     */
    private static String keystorename;

    /**
     * The password of the keystore to create
     */
    private static String password;

    /**
     * The alias of the keystore/truststore/cert combination to create
     */
    private static String keystorealias;

    /**
     * The {@link File} that represents the CAAT install.  This is set after validation of the passed in location param
     */
    private static CAATInstallDir CAAT_INSTALL_DIR;

    /**
     * The {@link Options} object that is created in the static initializer block.
     */
    private static Options options;

    static {
        options = new Options();
        options.addOption(new Option("help", "Print this message."));
        options.addOption(Option.builder("certlocation")
                .argName("certlocation")
                .hasArg()
                .desc("The location of the certificate to install.")
                .build());
        options.addOption(Option.builder("caatlocation")
                .argName("caatlocation")
                .hasArg()
                .desc("The location of the caat install to install the cert to.")
                .build());
        options.addOption(Option.builder("keystorename")
                .argName("keystorename")
                .hasArg()
                .desc("The name of the newly generated keystore.")
                .build());
        options.addOption(Option.builder("password")
                .argName("password")
                .hasArg()
                .desc("The password for the cert private key.  This will be used for the new keystore as well.")
                .build());
        options.addOption(Option.builder("keystorealias")
                .argName("keystorealias")
                .hasArg()
                .desc("The alias to associate with this keystore/truststore/cert combination.")
                .build());
    }

    public static void main(String...args) {
        setValuesFromArgs(args);
        validateValues();
        createCAATDirFromValues();
        copyCertIntoCAATInstall();

        // TODO
        // Use the keytool to import into the trust chain ex
        // Update start.ini and ssl.ini files as needed
    }

    /**
     * Parses the provided arguments into a {@link CommandLine} using a {@link CommandLineParser}.  The
     * <code>CommandLine</code> object is then interrogated to set the static class variables that will be used to
     * install certs into a valid CAAT install.
     *
     * @param args The command line arguments to parse
     */
    private static void setValuesFromArgs(String[] args) {
        CommandLineParser clp = new DefaultParser();
        try {
            CommandLine cl = clp.parse(options, args);
            if (cl.hasOption("help")) { help(); }
            if (cl.hasOption("certlocation")) { certlocation = cl.getOptionValue("certlocation"); }
            if (cl.hasOption("caatlocation")) { caatlocation = cl.getOptionValue("caatlocation"); }
            if (cl.hasOption("keystorename")) { keystorename = cl.getOptionValue("keystorename"); }
            if (cl.hasOption("password")) { password = cl.getOptionValue("password"); }
            if (cl.hasOption("keystorealias")) { keystorealias = cl.getOptionValue("keystorealias"); }
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
        }
    }

    /**
     * A static utility method that is used to validate the arguments passed in via command line.
     * <pre>
     *     <table summary="CertInstaller Args">
     *          <tr>
     *              <td>Option</td><td>Requirement</td>
     *          </tr>
     *          <tr>
     *              <td>certlocation</td><td><code>!null</code></td>
     *          </tr>
     *          <tr>
     *              <td>caatlocation</td><td><code>!null</code></td>
     *          </tr>
     *          <tr>
     *              <td>keystorename</td><td><code>!null</code></td>
     *          </tr>
     *          <tr>
     *              <td>password</td><td><code>!null</code></td>
     *          </tr>
     *          <tr>
     *              <td>keystorealias</td><td><code>null || empty ? : "contentanalyst"</code></td>
     *          </tr>
     *     </table>
     * </pre>
     */
    private static void validateValues() {
        checkNotNull(certlocation, "A value must be provided for the certificate location.");
        checkNotNull(caatlocation, "A value must be provided for the CAAT location.");
        checkNotNull(keystorename, "A value must be provided for the new keystore name.");
        checkNotNull(password, "A value must be provided for the new keystore password.");

        if (isNullOrEmpty(keystorealias)) {
            System.out.println("No value was provided for keystore alias, setting to contentanalyst.");
            keystorealias = DEF_ALIAS;
        }

        if (!new File(certlocation).exists() || !certlocation.endsWith(".p12")) {
            System.err.println(
                    String.format(
                            "The value provided for the certlocation, %s, is not a valid cert in PKCS12 format.",
                            certlocation
                    )
            );
            System.exit(0);
        }

        if (!isValidCAAT(caatlocation)) {
            System.err.println(
                    String.format(
                            "The value provided for caatlocation, %s, is not a path to a CAAT install.", caatlocation
                    )
            );
            System.exit(0);
        };
    }

    /**
     * Creates a {@link CAATInstallDir} from the provided path.
     */
    private static void createCAATDirFromValues() {
        CAAT_INSTALL_DIR = new CAATInstallDir(caatlocation);
    }

    private static void copyCertIntoCAATInstall() {
        try {
            Files.copy(
                    new File(certlocation).toPath(),
                    new File(CAAT_INSTALL_DIR.getSslDir(), new File(certlocation).getName()).toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            System.err.println("Copying of the cert into the CAAT install failed.  Reason: " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Checks if the provided path parameter is a valid CAAT install dir by checking that the File created from the path
     * returns <code>true</code> for the following 3 conditions:
     *
     * <pre>
     *     File(path).exists()
     * </pre>
     * <pre>
     *     File(path).isDirectory()
     * </pre>
     * <pre>
     *     File(path, "start.ini").exists()
     * </pre>
     *
     * @param path The provided path to the CAAT install directory
     * @return <code>true</code> if a valid CAAT install directory
     */
    private static boolean isValidCAAT(String path) {
        checkNotNull(path);
        File caatInstallDir = new File(path);
        return (
                caatInstallDir.exists() &&
                        caatInstallDir.isDirectory() &&
                        new File(caatInstallDir, "start.ini").exists()

        );
    }

    /**
     * A static utility method to print the CL help based on the cert installers {@link #options}.
     */
    private static void help() {
        new HelpFormatter().printHelp("CAAT Certificate Installer", options);
        System.exit(0);
    }

}
