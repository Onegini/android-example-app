package com.onegini.mobile.exampleapp.view.activity;

import java.io.IOException;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.view.action.qrcodeidentityprovider.QrCodeRegistrationAction;
import com.onegini.mobile.exampleapp.view.helper.AlertDialogFragment;

public class QrCodeScanActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = QrCodeScanActivity.class.getSimpleName();

  private static final int CAMERA_PERMISSION_REQUEST_CODE = 12345;

  SurfaceView qrCodeScanner;
  Button cancelButton;

  private CameraSource cameraSource;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qr_code_registration);
    initUI();
    initQrCodeScanner();
  }

  private void initUI() {
    qrCodeScanner = findViewById(R.id.qr_code_scanner);
    cancelButton = findViewById(R.id.qr_code_cancel_button);
  }

  private void initQrCodeScanner() {
    final BarcodeDetector barcodeDetector = buildBarcodeDetector();
    cameraSource = buildCameraSource(barcodeDetector);

    barcodeDetector.setProcessor(new QrCodeDetectorProcessor());
    qrCodeScanner.getHolder().addCallback(new QrCodeSurfaceHolderCallback());
  }

  private BarcodeDetector buildBarcodeDetector() {
    return new BarcodeDetector.Builder(this)
        .setBarcodeFormats(Barcode.QR_CODE)
        .build();
  }

  private CameraSource buildCameraSource(final BarcodeDetector barcodeDetector) {
    return new CameraSource.Builder(this, barcodeDetector)
        .setAutoFocusEnabled(true)
        .setRequestedPreviewSize(640, 480)
        .setFacing(CameraSource.CAMERA_FACING_BACK)
        .build();
  }

  @Override
  public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        startCamera();
      } else {
        onScanningCanceled(new Exception("Camera permission denied by the user"));
      }
    }
  }

  @SuppressLint("MissingPermission")
  private void startCamera() {
    try {
      cameraSource.start(qrCodeScanner.getHolder());
    } catch (IOException e) {
      showError("Couldn't start the camera. See logcat for more details.");
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public void onCancelButtonClicked() {
    onScanningCanceled(new Exception("Registration canceled"));
  }

  private void onScanningCanceled(final Exception exception) {
    if (QrCodeRegistrationAction.CALLBACK != null) {
      QrCodeRegistrationAction.CALLBACK.returnError(exception);
    } else {
      setResult(RESULT_CANCELED);
    }
    finish();
  }

  private void onScanningCompleted(final String qrCode) {
    if (QrCodeRegistrationAction.CALLBACK != null) {
      QrCodeRegistrationAction.CALLBACK.returnSuccess(qrCode);
    } else {
      final Intent intent = new Intent();
      intent.setData(Uri.parse(qrCode));
      setResult(RESULT_OK, intent);
    }
    finish();
  }

  private void showError(final String message) {
    final DialogFragment dialog = AlertDialogFragment.newInstance(message);
    dialog.show(getFragmentManager(), "alert_dialog");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    cameraSource.stop();
    cameraSource.release();
  }

  @Override
  public void onClick(final View view) {
    if (R.id.qr_code_cancel_button == view.getId()) {
      onCancelButtonClicked();
    }
  }

  private class QrCodeDetectorProcessor implements Detector.Processor<Barcode> {

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(final Detector.Detections<Barcode> detections) {
      final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
      if (qrCodes.size() > 0) {
        onScanningCompleted(qrCodes.valueAt(0).displayValue);
      }
    }
  }

  private class QrCodeSurfaceHolderCallback implements SurfaceHolder.Callback {

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
      if (ActivityCompat.checkSelfPermission(QrCodeScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(QrCodeScanActivity.this, new String[]{ Manifest.permission.CAMERA }, CAMERA_PERMISSION_REQUEST_CODE);
      } else {
        startCamera();
      }
    }

    @Override
    public void surfaceChanged(final SurfaceHolder surfaceHolder, final int i, final int i1, final int i2) {

    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
      cameraSource.stop();
    }
  }
}
