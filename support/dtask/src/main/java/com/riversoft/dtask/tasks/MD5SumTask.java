package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5SumTask extends BaseRiverTask {

    private static MessageDigest digest;
    private FileSet files;
    private boolean verify = false;

    private StringBuffer nonMatchingFiles;
    private String propertyName;

    public void setAddproperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setVerify(String verify) {
        this.verify = Boolean.parseBoolean(verify);
    }

    public void addFileset(FileSet files) {
        this.files = files;
    }

    public void setChecksumFile(String filename) {
        overridePropertyFileName(filename);
    }

    /**
     * Hex digits.
     */
    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Encode (16 bytes) MD5 bytearray into a 32 character String.
     *
     * @param bytes Array containing the digest
     * @return Encoded MD5, or null if encoding failed
     */
    public String encode(byte[] bytes) {

        if (bytes.length != 16) {
            return null;
        }

        char[] buffer = new char[32];

        for (int i = 0; i < 16; i++) {
            int low = (bytes[i] & 0x0f);
            int high = ((bytes[i] & 0xf0) >> 4);
            buffer[i * 2] = HEX[high];
            buffer[i * 2 + 1] = HEX[low];
        }

        return new String(buffer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.drutt.ant.tasks.BaseDruttTask#doExecute()
     */
    @Override
    protected void doExecute() throws BuildException {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                //
            }
        }

        if (verify) {
            getProject().log("==================== MD5 Verify =======================", DRUTT_LOG_LVL);
            nonMatchingFiles = new StringBuffer();
        }
        DirectoryScanner ds = files.getDirectoryScanner(getProject());
        String[] fileNames = ds.getIncludedFiles();

        File baseDir = ds.getBasedir();

        int percentCompleted = 0;
        int filesCompleted = 0;
        int numberOfFiles = fileNames.length;

        getProject().log("Files to process: " + numberOfFiles, DRUTT_LOG_LVL);

        for (int i = 0; i < numberOfFiles; i++) {
            filesCompleted++;
            int pComplete = (int) ((float) filesCompleted * 100.0f / (float) numberOfFiles) + 1;
            if (pComplete > percentCompleted) {
                for (int j = 0; j < (pComplete - percentCompleted); j++) {
                    getProject().log(
                            (percentCompleted + j) % 10 == 0 ? "" + (percentCompleted + j)
                                    : ((percentCompleted + j) % 2 == 0 ? "." : ""), DRUTT_CONT_LOG_LVL);
                }
                percentCompleted = pComplete;
            }

            File file2sum = new File(baseDir, fileNames[i]);
            String md5sum = null;

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file2sum);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = fis.read(bytes)) > 0) {
                    digest.update(bytes, 0, len);
                }
            } catch (Exception e) {
                throw new BuildException(e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                    }
                }
            }

            // digest is reset after call to digest()
            md5sum = encode(digest.digest());

            if (verify) {
                boolean sumMatch = md5sum.equals(getProperty(fileNames[i]));
                if (!sumMatch) {
                    if (nonMatchingFiles.length() != 0) {
                        nonMatchingFiles.append(NEW_LINE);
                    }
                    nonMatchingFiles.append(fileNames[i]);
                }
            } else {
                setProperty(fileNames[i], md5sum);
            }
        }
        getProject().log("", DRUTT_LOG_LVL);
        if (!verify) {
            saveProperties();
        } else {
            if (propertyName != null) {
                setProjectProperty(propertyName, nonMatchingFiles.toString());
            }
        }
    }
}
