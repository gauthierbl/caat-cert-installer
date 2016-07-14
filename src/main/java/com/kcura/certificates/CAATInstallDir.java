package com.kcura.certificates;

import java.io.File;

public class CAATInstallDir {

    public static final String SSL_INI = "start.d/ssl.ini";

    public static final String SSL_DIR = "etc/ssl";

    public static final String ETC_DIR = "etc";

    private final File installDir;

    private final File sslIni;

    private final File sslDir;

    private final File etcDir;

    public CAATInstallDir(final String path) {
        this.installDir = new File(path);
        this.sslIni = new File(installDir, SSL_INI);
        this.sslDir = new File(installDir, SSL_DIR);
        this.etcDir = new File(installDir, ETC_DIR);
        assert (installDir.exists() && sslIni.exists() && sslDir.exists() && sslDir.isDirectory());
    }

    public File getInstallDir() {
        return installDir;
    }

    public File getSslIni() {
        return sslIni;
    }

    public File getSslDir() {
        return sslDir;
    }

    public File getEtcDir() {
        return etcDir;
    }
}
