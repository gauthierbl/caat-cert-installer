package com.kcura.certificates;

import java.io.File;

public class CAATInstallDir {

    private static final String START_INI = "start.ini";

    private static final String SSL_INI = "start.d/ssl.ini";

    private static final String HTTPS_INI = "start.d/https.ini";

    private static final String WEB_XML = "webapps/nexus/WEB-INF/web.xml";

    private File installDir;

    private File startIni;

    private File sslIni;

    private File httpsIni;

    private File webXml;

    public CAATInstallDir(final String path) {
        this.installDir = new File(path);
        this.startIni = new File(installDir, START_INI);
        this.sslIni = new File(installDir, SSL_INI);
        this.httpsIni = new File(installDir, HTTPS_INI);
        this.webXml = new File(installDir, WEB_XML);
        assert (installDir.exists() && startIni.exists() && sslIni.exists() && httpsIni.exists() && webXml.exists());
    }

    public File getInstallDir() {
        return installDir;
    }

    public File getStartIni() {
        return startIni;
    }

    public File getSslIni() {
        return sslIni;
    }

    public File getHttpsIni() {
        return httpsIni;
    }

    public File getWebXml() {
        return webXml;
    }
}
