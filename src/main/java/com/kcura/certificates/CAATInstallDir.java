package com.kcura.certificates;

import java.io.File;

public class CAATInstallDir {

    public static final String SSL_INI = "start.d/ssl.ini";

    public static final String SSL_DIR = "etc/ssl";

    private final File installDir;

    private final File sslIni;

    private final File sslDir;

    public CAATInstallDir(final String path) {
        this.installDir = new File(path);
        this.sslIni = new File(installDir, SSL_INI);
        this.sslDir = new File(installDir, SSL_DIR);
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
}
