package com.uceku.ucekustudy.file_manager;

import android.net.Uri;
import android.os.Environment;
import org.apache.commons.io.FileUtils;
import android.os.Handler;
import android.os.Message;

import com.uceku.ucekustudy.models.CourseContentType;
import com.uceku.ucekustudy.utility.DocType;

import java.io.File;
import java.io.IOException;

public class AppFileUtils {

    // file storage directories
    private static final String FILE_DIRECTORY_NOTES = "NOTES";
    private static final String FILE_DIRECTORY_PREVIOUS_PAPERS = "PREVIOUS_PAPERS";
    private static final String FILE_DIRECTORY_BOOKS = "BOOKS";
    private static final String FILE_DIRECTORY_SYLLABUS = "SYLLABUS";

    // file starting names for notes, previous_papers, books etc.
    private static final String FILE_START_NAME_NOTES = "nn";
    private static final String FILE_START_NAME_PREVIOUS_PAPERS = "pp";
    private static final String FILE_START_NAME_BOOKS = "bb";

    public static boolean isFilePresent(Uri uri) {
        if (uri == null ||uri.getPath() == null) return false;
        String path = uri.getPath();
        File file = new File(path);
        return file.exists();
    }

    public static File createTmpFileInCacheDir(String downloadFileName, DocType docType) {
         String suffix = null;
        if (docType == DocType.PDF) {
            suffix = ".pdf";
        }

        try {

            return File.createTempFile(downloadFileName, suffix, FileUtils.getTempDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveFileToExternalStorage(final File tempFile, final CourseContentType courseContentType, final String fileName, final Handler.Callback callback) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.arg1 = -1;
                String externalDir = getExternalDirPath(courseContentType);
                try {
                    copy(tempFile, new File(externalDir, fileName));
                    message = new Message();
                    message.arg1 = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    message = new Message();
                    message.arg1 = -1;
                }
                callback.handleMessage(message);
            }
        });
    }

    private static void copy(File src, File dst) throws IOException {
        FileUtils.copyFile(src, dst);
    }

    public static boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }

    public static String buildDocFileName(int id, String name, DocType type) {

        return String.valueOf(id) + "_" +name + "." + docNameFromType(type);
    }

    public static String docNameFromType(DocType type) {
        if (type == DocType.PDF) {
            return "pdf";
        }
        return "";
    }

    public static boolean isThisFileAlreadyPresent(CourseContentType contentType, String fileName) {
        if (fileName == null) throw new RuntimeException("File name is null");
        File file = new File(getExternalDirPath(contentType), fileName);
        return file.exists();
    }

    public static File getStoredNotesFile(String fileName) {
        return new File(getFileDirectoryNotesPath(), fileName);
    }

    public static File getStoredPreviousPapersFile(String fileName) {
        return new File(getFileDirectoryPreviousPapersPath(), fileName);
    }

    public static File getStoredBooksFile(String fileName) {
        return new File(getFileDirectoryBooksPath(), fileName);
    }


    // storage
    // Checks if a volume containing external storage is available
    // for read and write.
    private static boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
    }

    // Checks if a volume containing external storage is available to at least read.
    private static boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ||
                Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY;
    }


    private static String getExternalDirPath(CourseContentType courseContentType) {
        if (courseContentType == CourseContentType.NOTES) {
            return getFileDirectoryNotesPath();
        } else if (courseContentType == CourseContentType.PREVIOUS_PAPER) {
            return getFileDirectoryPreviousPapersPath();
        } else if (courseContentType == CourseContentType.BOOKS) {
            return getFileDirectoryBooksPath();
        } else if (courseContentType == CourseContentType.SYLLABUS) {
            return getFileDirectorySyllabusPath();
        } else return null;

    }

    private static String getFileDirectorySyllabusPath() {
        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + File.separator +FILE_DIRECTORY_SYLLABUS;
        File myDir = new File(path);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        return path;
    }

    private static String getFileDirectoryNotesPath() {
        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + File.separator +FILE_DIRECTORY_NOTES;
        File myDir = new File(path);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        return path;
    }

    private static String getFileDirectoryPreviousPapersPath() {
        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + File.separator +FILE_DIRECTORY_PREVIOUS_PAPERS;
        File myDir = new File(path);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        return path;
    }

    private static String getFileDirectoryBooksPath() {
        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + File.separator +FILE_DIRECTORY_BOOKS;
        File myDir = new File(path);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        return path;
    }

    public static File getStoredSyllabusFile(String fileName) {
        return new File(getFileDirectorySyllabusPath(), fileName);
    }
}
