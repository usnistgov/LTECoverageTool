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

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class GradeFragment extends Fragment {

    private static final String DATA_READINGS_ARG = "data_readings_arg";
    private static final String FILENAME_ARG = "filename_arg";

    private ArrayList<DataReading> mDataReadings;
    private String mFilename;

    public GradeFragment() {
        // Required empty public constructor
    }

    public static GradeFragment newInstance(ArrayList<DataReading> dataReadings, String filename) {
        GradeFragment fragment = new GradeFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATA_READINGS_ARG, dataReadings);
        args.putString(FILENAME_ARG, filename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilename = getArguments().getString(FILENAME_ARG);
            mDataReadings = (ArrayList<DataReading>) getArguments().getSerializable(DATA_READINGS_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);
        TextView filenameText = view.findViewById(R.id.fragment_grade_filename_text_ui);
        filenameText.setText(mFilename);

        double top = 0.0, middle = 0.0, low = 0.0;
        double topProbability = 0.0, middleProbability = 0.0, lowProbability = 0.0, grade = 0.0;
        if (mDataReadings != null) {
            for (DataReading dataReading : mDataReadings) {
                int rsrp = dataReading.getRsrp();
                if (rsrp >= -95) {
                    top++;
                }
                else if (rsrp < -95 && rsrp >= -110) {
                    middle++;
                }
                else {
                    low++;
                }
            }

            int numReadings = mDataReadings.size() == 0 ? 1 : mDataReadings.size();
            topProbability = top / numReadings;
            middleProbability = middle / numReadings;
            lowProbability = low / numReadings;

            grade = 1 * lowProbability + 5.5 * middleProbability + 10 * topProbability;
        }

        TextView gradeText = view.findViewById(R.id.fragment_grade_grade_text_ui);
        gradeText.setText(new DecimalFormat("#.###").format(grade));
        TextView gradeLabel = view.findViewById(R.id.fragment_grade_grade_label_ui);

        if (top >= middle && top > low) {
            gradeText.setTextColor(getResources().getColor(R.color.fragment_grade_grade_pie_top));
            gradeLabel.setTextColor(getResources().getColor(R.color.fragment_grade_grade_pie_top));
        }
        else if (middle > low) {
            gradeText.setTextColor(getResources().getColor(R.color.fragment_grade_grade_pie_mid));
            gradeLabel.setTextColor(getResources().getColor(R.color.fragment_grade_grade_pie_mid));
        }
        else {
            gradeText.setTextColor(getResources().getColor(R.color.fragment_grade_grade_pie_low));
            gradeLabel.setTextColor(getResources().getColor(R.color.fragment_grade_grade_pie_low));
        }

        PieChart pieChart = view.findViewById(R.id.fragment_grade_pie_chart_ui);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setDrawHoleEnabled(false);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (top != 0) {
            entries.add(new Entry((float) topProbability, 1));
            labels.add(getResources().getString(R.string.fragment_grade_pie_chart_label_top));
            colors.add(getResources().getColor(R.color.fragment_grade_grade_pie_top));
        }
        if (middle != 0) {
            entries.add(new Entry((float) middleProbability, 2));
            labels.add(getResources().getString(R.string.fragment_grade_pie_chart_label_mid));
            colors.add(getResources().getColor(R.color.fragment_grade_grade_pie_mid));
        }
        if (low != 0) {
            entries.add(new Entry((float) lowProbability, 3));
            labels.add(getResources().getString(R.string.fragment_grade_pie_chart_label_low));
            colors.add(getResources().getColor(R.color.fragment_grade_grade_pie_low));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        PieData pieData = new PieData(labels, pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(16f);
        pieData.setValueTextColor(getResources().getColor(R.color.fragment_grade_pie_chart_text));
        pieChart.setData(pieData);
        Legend legend = pieChart.getLegend();
        legend.setFormSize(20f);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setTextSize(16f);
        legend.setTextColor(getResources().getColor(R.color.fragment_grade_pie_chart_text_legend));
        legend.setXEntrySpace(20f);
        pieDataSet.setColors(colors);

        return view;
    }
}
