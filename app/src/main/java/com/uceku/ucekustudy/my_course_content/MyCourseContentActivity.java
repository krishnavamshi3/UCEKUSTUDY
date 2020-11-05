package com.uceku.ucekustudy.my_course_content;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.squareup.okhttp.OkHttpClient;
import com.uceku.ucekustudy.models.Syllabus;
import com.uceku.ucekustudy.network.NetworkConfig;
import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.constants.IntentConstants;
import com.uceku.ucekustudy.file_manager.AppFileUtils;
import com.uceku.ucekustudy.models.CourseContentType;
import com.uceku.ucekustudy.models.NoteOverview;
import com.uceku.ucekustudy.realm_db.Reads;
import com.uceku.ucekustudy.utility.DocType;

import java.io.File;

import javax.annotation.Nonnull;

import io.realm.Realm;

public class MyCourseContentActivity extends AppCompatActivity implements Connectable, Disconnectable, Bindable {

    private static final String TAG = MyCourseContentActivity.class.getSimpleName();

    PDFView pdfView;
    LinearLayout loadingRootView;
    LinearLayout errorLoadingRootView;
    ImageView docErrorIV;
    TextView docErrorTV;
    Toolbar toolbar;

    File tmpFile;

    private final OkHttpClient client = new OkHttpClient();
    Realm realm;
    NoteOverview mNoteOverview;
    Syllabus mSyllabus;
    Merlin merlin;


    DocType docType;
    int contentTypeOrdinal;
    int contentId;

    Uri docUri;
    String docUrl;
    String docTitle;

    boolean showDownloadAction = false;
    boolean showRefreshAction = false;

    FirebaseStorage storage;
    double progress = 0.0;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_content);

        pdfView = findViewById(R.id.pdfView);
        loadingRootView = findViewById(R.id.loading_doc_root);
        errorLoadingRootView = findViewById(R.id.doc_error_root);
        docErrorIV = findViewById(R.id.doc_error_iv);
        docErrorTV = findViewById(R.id.doc_error_tv);
progressDialog = new ProgressDialog(this);
        // get Intent
        Intent intent = getIntent();
        if (intent != null) {
            docType = (DocType) intent.getSerializableExtra(IntentConstants.DOCTYPE);
            contentTypeOrdinal = intent.getIntExtra(IntentConstants.CONTENTTYPE, -1);
            contentId = intent.getIntExtra(IntentConstants.CONTENTID, -1);

        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();



        merlin = new Merlin.Builder().withAllCallbacks().build(getBaseContext());
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);
        merlin.bind();

        realm = Realm.getDefaultInstance();

        if (contentTypeOrdinal == CourseContentType.NOTES.ordinal()) {
            mNoteOverview = realm.where(NoteOverview.class).equalTo("id", contentId).findFirst();
            if (mNoteOverview == null) {
                showEndPoint();
                return;
            } else {
                String _localUrl = mNoteOverview.getNoteLocalFileUrl();
                docUri = _localUrl == null || _localUrl.isEmpty() ? null : Uri.fromFile(new File(_localUrl));
                docUrl = mNoteOverview.getNoteCloudFullFileUrl();
                docTitle = mNoteOverview.getNoteName();
            }
        } else if (contentTypeOrdinal == CourseContentType.PREVIOUS_PAPER.ordinal()) {
            // ToDo :
        } else if (contentTypeOrdinal == CourseContentType.BOOKS.ordinal()) {
            // ToDo :
        } else if (contentTypeOrdinal == CourseContentType.SYLLABUS.ordinal()) {
            mSyllabus = Reads.getSyllabusByID(contentId, realm);
            if (mSyllabus == null) {
                showEndPoint();
                return;
            } else {
                String _localUrl = mSyllabus.getFileLocalURL();
                docUri = _localUrl == null || _localUrl.isEmpty() ? null : Uri.fromFile(new File(_localUrl));
                docUrl = mSyllabus.getFileCloudURL();
                docTitle = mSyllabus.getName();
            }
        }

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(docTitle);

        if (AppFileUtils.isFilePresent(docUri)) {
            showPdfOnUI();
            loadPDFView(pdfView.fromUri(docUri));
            showMenuItem(R.id.action_refresh);
        } else if (TextUtils.isEmpty(docUrl)) {
            showFileNotPresentInCloud();
        } else {
            showFetchingPdf();
            if (NetworkConfig.isNetworkConnected()) {
                downloadFileAndStoreInTemp(docUrl, docType, new OnDocFileDownload() {
                    @Override
                    public void onDownloaded(File tmpDownloadedFile) {
                        showPdfOnUI();
                        loadPDFView(pdfView.fromFile(tmpDownloadedFile));
                        showMenuItem(R.id.action_download);
                        showMenuItem(R.id.action_refresh);
                    }

                    @Override
                    public void onFailure() {
                        showErrorLoadingDocument();
                    }
                }, new Handler());
            } else {
                showErrorOnNetworkFailure();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
            realm = null;
        }
        merlin.unbind();
//        boolean isCacheEmpty = AppFileUtils.deleteTempFiles(getCacheDir());
//        Log.d("Check : ", isCacheEmpty + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_document, menu);
        MenuItem downloadMenuItem = menu.findItem(R.id.action_download);
        downloadMenuItem.setVisible(showDownloadAction);
        MenuItem refreshMenuItem = menu.findItem(R.id.action_refresh);
        refreshMenuItem.setVisible(showRefreshAction);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        } else if (item.getItemId() == R.id.action_download) {
            if (isStoragePermissionGranted()) {
                onSave();
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSave() {
        if (tmpFile == null) {
            Toast.makeText(this, "Cannot save the document. please click refresh to again download it.", Toast.LENGTH_SHORT).show();
            return;
        }
        final String _fileName = AppFileUtils.buildDocFileName(contentId, docTitle, docType);

        AppFileUtils.saveFileToExternalStorage(tmpFile, getCourseContentType(contentTypeOrdinal), _fileName, new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.arg1 == 0) {
                    updateSaveContentInDB(_fileName);
                    Toast.makeText(MyCourseContentActivity.this, "File saved successfully", Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == -1) {
                    Toast.makeText(MyCourseContentActivity.this, "File not saved", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    /**
     * ToDo;
     *
     * @param fileName fileName with which we saved.
     */
    private void updateSaveContentInDB(final String fileName) {
        if (realm == null || realm.isClosed()) {
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                File file;
                if (contentTypeOrdinal == CourseContentType.NOTES.ordinal()) {
                    file = AppFileUtils.getStoredNotesFile(fileName);
                    mNoteOverview.setNoteLocalFileUrl(file.getAbsolutePath());
                    realm.insertOrUpdate(mNoteOverview);
                } else if (contentTypeOrdinal == CourseContentType.PREVIOUS_PAPER.ordinal()) {
                    file = AppFileUtils.getStoredPreviousPapersFile(fileName);
                } else if (contentTypeOrdinal == CourseContentType.BOOKS.ordinal()) {
                    file = AppFileUtils.getStoredBooksFile(fileName);
                } else if (contentTypeOrdinal == CourseContentType.SYLLABUS.ordinal()) {
                    file = AppFileUtils.getStoredSyllabusFile(fileName);
                    mSyllabus.setFileLocalURL(file.getAbsolutePath());
                    realm.insertOrUpdate(mSyllabus);
                }
            }
        });

    }

    private void onRefresh() {
        showFetchingPdf();
        if (NetworkConfig.isNetworkConnected()) {
            downloadFileAndStoreInTemp(docUrl, docType, new OnDocFileDownload() {
                @Override
                public void onDownloaded(File tmpDownloadedFile) {
                    showPdfOnUI();
                    loadPDFView(pdfView.fromFile(tmpDownloadedFile));
                    showMenuItem(R.id.action_download);
                    showMenuItem(R.id.action_refresh);
                }

                @Override
                public void onFailure() {
                    showErrorLoadingDocument();
                }
            }, new Handler());
        } else {
            showErrorOnNetworkFailure();
        }
    }


    public void downloadFileAndStoreInTemp(String url, final DocType docType, final OnDocFileDownload onDocFileDownload, final Handler handler) {

        // Create a reference from an HTTPS URL
        // Note that in the URL, characters are URL escaped!
        StorageReference httpsReference = storage.getReferenceFromUrl(url);

        tmpFile = AppFileUtils.createTmpFileInCacheDir("temp_", docType);

        if (tmpFile == null) {
            onDocFileDownload.onFailure();
            return;
        }

        httpsReference.getFile(tmpFile).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        onDocFileDownload.onDownloaded(tmpFile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                onDocFileDownload.onFailure();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //displaying percentage in progress dialog
                progressDialog.setMessage("Fetching document " + ((int) progress) + "%...");
            }
        });

    }

    @Override
    public void onConnect() {
        NetworkConfig.setNetworkConnected(true);
    }

    @Override
    public void onDisconnect() {
        NetworkConfig.setNetworkConnected(false);
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        NetworkConfig.setNetworkConnected(networkStatus.isAvailable());
    }

    private interface OnDocFileDownload {
        void onDownloaded(File tmpDownloadedFile);

        void onFailure();
    }

    private void showFileNotPresentInCloud() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        loadingRootView.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        errorLoadingRootView.setVisibility(View.VISIBLE);
        docErrorTV.setText("File Not Uploaded by the Admin.");
        hideMenuItem(R.id.action_refresh);
        hideMenuItem(R.id.action_download);
    }

    private void showEndPoint() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        loadingRootView.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        errorLoadingRootView.setVisibility(View.VISIBLE);
        docErrorTV.setText("There is no document to show!");
        hideMenuItem(R.id.action_refresh);
        hideMenuItem(R.id.action_download);
    }

    private void showErrorLoadingDocument() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        loadingRootView.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        errorLoadingRootView.setVisibility(View.VISIBLE);
        docErrorIV.setImageResource(R.drawable.ic_picture_as_pdf_primary_24dp);
        docErrorTV.setText("Couldn't load Document. Please Refresh!");
        hideMenuItem(R.id.action_download);
        showMenuItem(R.id.action_refresh);
    }

    private void showErrorOnNetworkFailure() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        loadingRootView.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        errorLoadingRootView.setVisibility(View.VISIBLE);
        docErrorIV.setImageResource(R.drawable.ic_portable_wifi_off_black_24dp);
        docErrorTV.setText(R.string.please_check_network_message);
        hideMenuItem(R.id.action_download);
    }

    private void showPdfOnUI() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        errorLoadingRootView.setVisibility(View.GONE);
        loadingRootView.setVisibility(View.GONE);
        pdfView.setVisibility(View.VISIBLE);
    }

    private void showFetchingPdf() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        errorLoadingRootView.setVisibility(View.GONE);
        loadingRootView.setVisibility(View.GONE);
        ProgressBar progressBar = loadingRootView.findViewById(R.id.progressBar);
        progressBar.setProgress((int) progress);
        Log.i("progress", String.valueOf(progress));
        pdfView.setVisibility(View.GONE);
    }

    public void showMenuItem(int resourceId) {
        if (R.id.action_download == resourceId) showDownloadAction = true;
        if (R.id.action_refresh == resourceId) showRefreshAction = true;
        invalidateOptionsMenu();
    }

    public void hideMenuItem(int resourceId) {
        if (R.id.action_download == resourceId) showDownloadAction = false;
        if (R.id.action_refresh == resourceId) showRefreshAction = false;
        invalidateOptionsMenu();
    }


    private CourseContentType getCourseContentType(int contentTypeOrdinal) {
        return CourseContentType.values()[contentTypeOrdinal];
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nonnull String[] permissions, @Nonnull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            onSave();
        } else {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            Toast.makeText(this, "Storage permission is not granted! Please grant the permission to save the file :) ", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPDFView(PDFView.Configurator configurator) {
        configurator
                .swipeHorizontal(true)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        Log.d(TAG, "load Complete");
                    }
                }).onError(new OnErrorListener() {
            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "on Error");
                showErrorLoadingDocument();
            }
        }).onPageError(new OnPageErrorListener() {
            @Override
            public void onPageError(int page, Throwable t) {
                Log.d(TAG, "on page error listener");
            }
        }).load();
    }


}
