package com.threerings.getdown.tools;

import com.samskivert.util.StringUtil;

import java.util.Locale;

/**
 * Helper class that allows to determine type of OS and OS architecture for current running JVM
 * http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
 * compare to http://svn.terracotta.org/svn/tc/dso/tags/2.6.4/code/base/common/src/com/tc/util/runtime/Os.java
 * http://www.docjar.com/html/api/org/apache/commons/lang/SystemUtils.java.html
 * @author Karel Petranek, Pavel Janecka
 */
@SuppressWarnings("WeakerAccess")
public final class PlatformUtils {
    /**
     * types of Operating Systems
     */
    public enum OSType {
        Windows, MacOS, Linux, Other
    }

    /**
     * Architectures of Operating System
     */
    public enum OSArchitecture {
        x32("32-bit"), x64("64-bit");

        private String architectureString;

        OSArchitecture(String architectureString) {
            this.architectureString = architectureString;
        }

        @Override
        public String toString() {
            return architectureString;
        }
    }

    // cached result of OS detection
    private static OS detectedOS;

    /**
     * detect the operating system from the os.name System property and cache
     * the result
     *
     * @return {@link OSType} - the operating system detected
     */
    public static OS getOperatingSystem() {
        if (detectedOS == null) {
            detectedOS = new OS();
        }
        return detectedOS;
    }

    /**
     * Class contains basic info (type, architecture, ...) about OS on which is current JVM running
     *
     * @author Pavel Janecka
     */
    public static class OS {
        public final OSType type;
        public final String typeString;
        public final OSArchitecture architecture;
        public final String architectureString;
        public final String version;

        /**
         * Default constructor that determines which platform and architecture lies under running JVM
         */
        public OS() {
            String OSString = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OSString.contains("mac")) || (OSString.contains("darwin"))) {
                this.type = OSType.MacOS;
            } else if (OSString.contains("win")) {
                this.type = OSType.Windows;
            } else if (OSString.contains("nux")) {
                this.type = OSType.Linux;
            } else {
                this.type = OSType.Other;
            }

            // based on http://stackoverflow.com/questions/1856565/how-do-you-determine-32-or-64-bit-architecture-of-windows-using-java
            if (this.type == OSType.Windows) {
                this.architecture = (System.getenv("ProgramFiles(x86)") != null) ? OSArchitecture.x64 : OSArchitecture.x32;
            } else {
                this.architecture = (System.getProperty("os.arch").contains("64")) ? OSArchitecture.x64 : OSArchitecture.x32;
            }

            // getdown needs java based architecture string because its used inside config file
            // just detect windows proper architecture and fallback to default on other systems
            String properArchitecture = System.getProperty("os.arch");
            if (this.type == OSType.Windows) {
                if (this.architecture == OSArchitecture.x32)
                    properArchitecture = "x86";
                else
                    properArchitecture = "amd64";
            }
            this.architectureString = StringUtil.deNull(properArchitecture).toLowerCase();

            this.typeString = StringUtil.deNull(System.getProperty("os.name")).toLowerCase();
            this.version = StringUtil.deNull(System.getProperty("os.version"));
        }
    }
}