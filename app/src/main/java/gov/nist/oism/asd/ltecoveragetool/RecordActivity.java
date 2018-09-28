/*
 * This software was developed by employees of the National Institute of Standards and Technology (NIST), an agency of the Federal Government
 * and is being made available as a public service. Pursuant to title 17 United States Code Section 105, works of NIST employees are not
 * subject to copyright protection in the United States.  This software may be subject to foreign copyright.  Permission in the United States
 * and in foreign countries, to the extent that NIST may hold copyright, to use, copy, modify, create derivative works, and distribute
 * this software and its documentation without fee is hereby granted on a non-exclusive basis, provided that this notice and disclaimer of
 * warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO,
 * ANY WARRANTY THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND FREEDOM FROM INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL
 * BE ERROR FREE.  IN NO EVENT SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT, INDIRECT, SPECIAL OR CONSEQUENTIAL
 * DAMAGES, ARISING OUT OF, RESULTING FROM, OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY, CONTRACT, TORT, OR OTHERWISE,
 * WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT OF THE RESULTS OF,
 * OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */
package gov.nist.oism.asd.ltecoveragetool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gov.nist.oism.asd.ltecoveragetool.util.LteLog;

public class RecordActivity extends AppCompatActivity {

    private static final String TAG = RecordActivity.class.getSimpleName();
    private static final Object MUTEX = new Object();

    public static final String DATA_READINGS_KEY = "data_readings_key";
    public static final String OFFSET_KEY = "offset_key";

    private Button mPauseRecordButton;
    private ImageView mRecordingImage;
    private TextView mRecordingImageLabel;
    private AlphaAnimation mRecordingImageAnimation;
    private SignalStrengthListener mSignalStrengthListener;
    private TelephonyManager mTelephonyManager;
    private TextView mRsrpText, mRsrqText, mPciText, mDataPointsText, mOffsetText, mSignalStrengthText;
    private DataReading mCurrentReading;
    private double mOffset;
    private Timer mTimer;
    private List<DataReading> mDataReadings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mCurrentReading = new DataReading();
        mDataReadings = new ArrayList<>();

        mOffset = getIntent().getDoubleExtra(NewRecordingActivity.OFFSET_KEY, 0.0);

        mPauseRecordButton = findViewById(R.id.activity_record_pause_resume_button_ui);
        mRecordingImage = findViewById(R.id.activity_record_record_image_ui);
        mRecordingImageLabel = findViewById(R.id.activity_record_record_image_label_ui);
        mRsrpText = findViewById(R.id.activity_record_lte_rsrp_text_ui);
        mRsrqText = findViewById(R.id.activity_record_lte_rsrq_text_ui);
        mPciText = findViewById(R.id.activity_record_lte_pci_text_ui);
        mDataPointsText = findViewById(R.id.activity_record_data_points_text_ui);
        mOffsetText = findViewById(R.id.activity_record_offset_text_ui);
        mSignalStrengthText = findViewById(R.id.activity_record_signal_strength_text_ui);

        // Make part of text clickable.
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordActivity.this, UncertaintyNoticeActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setColor(getResources().getColor(R.color.activity_record_clickable_color));
            }
        };
        TextView rsrpLabel = findViewById(R.id.activity_record_lte_rsrp_label_ui);
        SpannableString rsrpSpan = new SpannableString(getString(R.string.activity_record_lte_rsrp_label_text));
        rsrpSpan.setSpan(clickableSpan, rsrpSpan.length() - 6, rsrpSpan.length(), 0);
        rsrpLabel.setMovementMethod(LinkMovementMethod.getInstance());
        rsrpLabel.setText(rsrpSpan);

        TextView rsrqLabel = findViewById(R.id.activity_record_lte_rsrq_label_ui);
        SpannableString rsrqSpan = new SpannableString(getString(R.string.activity_record_lte_rsrq_label_text));
        rsrqSpan.setSpan(clickableSpan, rsrqLabel.length() - 6, rsrqLabel.length(), 0);
        rsrqLabel.setMovementMethod(LinkMovementMethod.getInstance());
        rsrqLabel.setText(rsrqSpan);

        mRecordingImageAnimation = new AlphaAnimation(1, 0);
        mRecordingImageAnimation.setDuration(750);
        mRecordingImageAnimation.setInterpolator(new LinearInterpolator());
        mRecordingImageAnimation.setRepeatCount(Animation.INFINITE);
        mRecordingImageAnimation.setRepeatMode(Animation.REVERSE);

        mRecordingImage.startAnimation(mRecordingImageAnimation);

        mSignalStrengthListener = new SignalStrengthListener();
        ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(mSignalStrengthListener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                final DataReading dataReadingCopy;
                synchronized (MUTEX) {

                    // To be used on the UI thread.
                    dataReadingCopy = new DataReading(mCurrentReading);
                }
                if (isRecording()) {
                    mDataReadings.add(new DataReading(dataReadingCopy));
                }

                // To be used on the UI thread.
                final int numDataReadings = mDataReadings.size() == 0 ? 1 : mDataReadings.size();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (dataReadingCopy.getRsrp() >= -95) {
                            mSignalStrengthText.setText(getResources().getString(R.string.activity_record_signal_strength_excellent));
                        }
                        else if (-95 > dataReadingCopy.getRsrp() && dataReadingCopy.getRsrp() >= -110) {
                            mSignalStrengthText.setText(getResources().getString(R.string.activity_record_signal_strength_good));
                        }
                        else if (-110 > dataReadingCopy.getRsrp() && dataReadingCopy.getRsrp() >= -140) {
                            mSignalStrengthText.setText(getResources().getString(R.string.activity_record_signal_strength_poor));
                        }
                        else {
                            mSignalStrengthText.setText(getResources().getString(R.string.activity_record_signal_strength_no_signal));
                        }
                        mRsrpText.setText(dataReadingCopy.getRsrp() + "");
                        mRsrqText.setText(dataReadingCopy.getRsrq() + "");
                        mPciText.setText(dataReadingCopy.getPci() == -1 ? "N/A" : dataReadingCopy.getPci() + "");
                        mDataPointsText.setText(numDataReadings + "");
                        mOffsetText.setText(mOffset + "");
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mSignalStrengthListener != null) {
                mTelephonyManager.listen(mSignalStrengthListener, SignalStrengthListener.LISTEN_NONE);
            }
        }
        catch (Exception caught) {
            LteLog.e(TAG, caught.getMessage(), caught);
        }

        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mSignalStrengthListener != null) {
                mTelephonyManager.listen(mSignalStrengthListener, SignalStrengthListener.LISTEN_NONE);
            }
        }
        catch (Exception caught) {
            LteLog.e(TAG, caught.getMessage(), caught);
        }
    }

    public void pauseRecordButtonClicked(View view) {
        if (isRecording()) {
            pauseRecording();
        }
        else {
            resumeRecording();
        }
    }

    public void stopButtonClicked(View view) {
        Intent intent = new Intent(this, DisplayResultsActivity.class);
        intent.putExtra(DATA_READINGS_KEY, (ArrayList<DataReading>) mDataReadings);
        intent.putExtra(OFFSET_KEY, mOffset);
        startActivity(intent);
        finish();
    }

    public void uncertaintyStatementButtonClicked(View view) {
        Intent intent = new Intent(this, UncertaintyNoticeActivity.class);
        startActivity(intent);
    }

    private boolean isRecording() {
        return getString(R.string.activity_record_pause_button_text).equals(mPauseRecordButton.getText());
    }

    private void pauseRecording() {
        mPauseRecordButton.setText(getString(R.string.activity_record_resume_button_text));
        mPauseRecordButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_new_recording), null, null, null);
        mRecordingImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_paused));
        mRecordingImageLabel.setText(R.string.activity_record_recording_paused_image_label_text);
        mRecordingImage.clearAnimation();
    }

    private void resumeRecording() {
        mPauseRecordButton.setText(getString(R.string.activity_record_pause_button_text));
        mPauseRecordButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_pause), null, null, null);
        mRecordingImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording));
        mRecordingImageLabel.setText(R.string.activity_record_record_image_label_text);
        mRecordingImage.startAnimation(mRecordingImageAnimation);
    }

    private class SignalStrengthListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            LteLog.i(TAG, "onSignalStrengthsChanged: " + signalStrength.toString());
            String[] values = signalStrength.toString().split(" ");
            if (values != null && values.length > 12) {
                int rsrp = Integer.parseInt(values[9]);
                if (rsrp == 2147483647) {
                    rsrp = -141;
                }
                else {
                    rsrp = rsrp + (int) mOffset;
                }

                int rsrq = Integer.parseInt(values[10]);
                if (rsrq == 2147483647) {
                    rsrq = -20;
                }

                int cqi = Integer.parseInt(values[12]);
                if (cqi == 2147483647) {
                    cqi = -20;
                }

                int pci = -1; // "N/A"
                mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    if (ActivityCompat.checkSelfPermission(RecordActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        for (CellInfo cellInfo : mTelephonyManager.getAllCellInfo()) {
                            if (cellInfo instanceof CellInfoLte) {

                                // Gets the LTE PCI: (returns Physical Cell Id 0..503, Integer.MAX_VALUE if unknown)
                                pci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();
                            }
                        }
                    }
                }
                catch (Exception caught) {
                    LteLog.e(TAG, caught.getMessage(), caught);
                }

                synchronized (MUTEX) {
                    mCurrentReading.setRsrp(rsrp);
                    mCurrentReading.setRsrq(rsrq);
                    mCurrentReading.setCqi(cqi);
                    mCurrentReading.setPci(pci);
                }
                LteLog.i(TAG, "rsrp: " + rsrp + ", rsrq: " + rsrq);
            }

            super.onSignalStrengthsChanged(signalStrength);
        }
    }
}
